package root.database;

import root.logger.Logger;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

public class FSQLQuery {
    public static class InsertResult{
        private final Long _id;
        private final int _count;

        public InsertResult(Long id, int count) {
            this._id = id;
            this._count = count;
        }

        public Long getId(){
            return _id;
        }
        public int getCount(){
            return _count;
        }
    }

    private static final int DEFAULT_CAPACITY = 32;
    private static final String REPLACED_WITH_INTERNAL_SQL = null;

    private final Connection conn;
    private final String sql;
    private final List<Object> args = new ArrayList<>(DEFAULT_CAPACITY);
    private final HashMap<String, Object> namedArgs = new HashMap<String,Object>(DEFAULT_CAPACITY);
    //private final List<Object> finalArgs = new ArrayList<>(DEFAULT_CAPACITY);

    //private final LinkedHashMap<String, Object> setValues = new LinkedHashMap<>();
    //public String tableName = null;


    public FSQLQuery(Connection conn, String sql) {
        this.conn = conn;
        this.sql = sql;
    }

    public static FSQLQuery create(Connection conn, String sql) {
        return new FSQLQuery(conn, sql);
    }

    public FSQLQuery bind(Object... values) {
        args.addAll(Arrays.asList(values == null ? new Object[0] : values));
        return this;
    }

    public FSQLQuery bindNamed(String name, Object value) {
        namedArgs.put(name, value);
        return this;
    }

    public FSQLQuery bindNamed(Map<String, Object> values) {
        if(values != null) {
            namedArgs.putAll(values);
        }

        return this;
    }



    @SuppressWarnings("unchecked")
    protected static void __bindArgs(PreparedStatement ps, List<Object> args) throws SQLException {
        int i = 1;
        for(var arg : args){
            /*
            if(arg instanceof Collection){
                for (Object value : (Collection<Object>) arg) {
                    FSQL.bind(ps, i++, value);
                }
            }else*/
            if(arg instanceof LinkedHashMap){
                Collection<Object> values = ((LinkedHashMap<String, Object>) arg).values();
                for (Object value : values) {
                    FSQL.bind(ps, i++, value);
                }
            }else {
                FSQL.bind(ps, i++, arg);
            }
        }
    }

    protected Long getGeneratedKey(PreparedStatement ps, boolean required) throws SQLException {
        ResultSet keys = ps.getGeneratedKeys();

        if (!keys.next()) {
            if(required)
                throw new SQLException("Insert succeeded but no ID obtained.");

            return null;
        }

        return keys.getLong(1);
    }

    public PreparedStatement prepareSql(String inputSql, Object... options) throws Exception {
        // set activeSql to input if provided, otherwise use original
        String activeSql = (inputSql != null && !inputSql.isBlank()) ? inputSql : this.sql;

        // parse sql
        NamedSql.Parsed parsed = NamedSql.parse(this.sql, this.namedArgs, args.toArray());

        // log
        try(var ignore = Logger.scope("Query::prepareSql")) {
            Logger.log("Raw: " + activeSql);
            Logger.log("Parsed: " + parsed.sql);
            Logger.log("NewArgs: " + Arrays.toString(parsed.args));
            Logger.log("Interpolated: " + FSQLQueryInterpolator.interpolate(parsed.sql, parsed.args));
        }

        // prepare statement
        PreparedStatement ps;
        if(options != null && options.length > 0) {
            if(!Integer.class.isAssignableFrom(options[0].getClass())){
                throw new IllegalArgumentException("Expected first option to be of type Integer for Statement options");
            }

            ps = conn.prepareStatement(parsed.sql, ((Number) options[0]).intValue());
        }else {
            ps = conn.prepareStatement(parsed.sql);
        }

        // bind args
        __bindArgs(ps, Arrays.asList(parsed.args));

        return ps;
    }

    protected InsertResult insert(boolean requireResult, boolean resolveEntityId) throws Exception {
        try(PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL, Statement.RETURN_GENERATED_KEYS)) {
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                if (requireResult) {
                    throw new SQLException("Insert failed, no rows affected.");
                } else {
                    return new InsertResult(null, 0);
                }
            }

            Long insertedId = getGeneratedKey(ps, false);

            return new InsertResult(insertedId, affectedRows);
        }
    }

    public long insertAndGetCount() throws Exception {
        InsertResult result = insert(true, false);
        return result.getCount();
    }

    public boolean insertAndGetStatus() throws Exception {
        return insertAndGetCount() > 0;
    }

    public Long insertAndGetId() throws Exception {
        InsertResult result = insert(true, true);
        return result.getId();
    }

    public Long insertAndGetId(Consumer<Long> after) throws Exception {
        InsertResult result = insert(true, true);

        if(after != null)
            after.accept(result.getId());

        return result.getId();
    }

    public Long insertAndSetId(Object instance, String idField) throws Exception {
        // perform insert and get generated id
        Long entityId = insertAndGetId();

        // set id field if possible
        if(entityId != null) {
            Class<?> clazz = instance.getClass();

            try {
                var field = clazz.getDeclaredField(idField);
                field.setAccessible(true);
                field.set(instance, entityId);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set ID field: " + idField, e);
            }
        }

        return entityId;
    }

    public int delete() throws Exception {
        try(PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
            return ps.executeUpdate();
        }
    }

    public int update() throws Exception {
        try(PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
            return ps.executeUpdate();
        }
    }

    public <T> List<T> fetchAll(Class<T> clazz) throws Exception {
        try(PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
            ps.executeQuery();

            try(ResultSet rs = ps.getResultSet()) {
                // build class mapping
                FSQLClassMapping<T> classMapping = new FSQLClassMapping<>(
                    clazz.getDeclaredConstructor(),
                    FSQLCachedFieldColumnLookup.build(clazz, rs)
                );

                // hydrate
                List<T> items = new ArrayList<>(32);
                while (rs.next()) {
                    items.add (FSQLDefaultRowMapper.map(rs, classMapping) );
                }
                return items;
            }
        }
    }

    public <T> Optional<T> fetchOne(Class<T> clazz) throws Exception {
        try(PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
            ps.executeQuery();

            try(ResultSet rs = ps.getResultSet()) {
                // build class mapping
                FSQLClassMapping<T> classMapping = new FSQLClassMapping<>(
                    clazz.getDeclaredConstructor(),
                    FSQLCachedFieldColumnLookup.build(clazz, rs)
                );

                // hydrate
                return rs.next()
                    ? Optional.of( FSQLDefaultRowMapper.map(rs, classMapping) )
                    : Optional.empty();
            }
        }
    }

    public long selectCount() throws Exception {
        System.out.println("@EXPERIMENTAL, Executing selectCount with SQL: " + this.sql);

        try(PreparedStatement ps = prepareSql("SELECT COUNT(*) FROM (" + this.sql + ") q")) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);
        }
    }

    public boolean selectExists() throws Exception {
        System.out.println("@EXPERIMENTAL, Executing selectExists with SQL: " + this.sql);

        try(PreparedStatement ps = prepareSql("SELECT EXISTS (" + this.sql + ")")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        }
    }
}