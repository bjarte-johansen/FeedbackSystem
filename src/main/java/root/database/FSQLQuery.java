package root.database;

import root.includes.logger.logger.Logger;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.lang.reflect.Array;


/**
 * FSQLQuery is a utility class for building and executing SQL queries with support for both positional and named
 * parameters. It provides methods for binding parameters, executing queries, and mapping results to Java objects.
 * <p>
 * Example usage:
 * <p>
 * using positional parameters: Optional<User> user = FSQLQuery query = new FSQLQuery(conn, "SELECT * FROM users WHERE
 * id = ?") .bind(123) .fetchOne(User.class);
 * <p>
 * using named parameters: Optional<User> user = FSQLQuery query = new FSQLQuery(conn, "SELECT * FROM users WHERE id =
 * :id") .bindNamed("id", 123) .fetchOne(User.class);
 * <p>
 * Note: This class is designed to be flexible and can be extended with additional features as needed.
 */

public class FSQLQuery {
    @FunctionalInterface
    interface QueryExecutor<T> {
        T run(Connection conn) throws Exception;
    }

    public static class InsertResult {
        private final Long _id;
        private final int _count;

        public InsertResult(Long id, int count) {
            this._id = id;
            this._count = count;
        }

        public Long getId() {
            return _id;
        }

        public int getCount() {
            return _count;
        }
    }

    private static final boolean OVERRIDE_DEBUG_SQL = false;
    private static final int DEFAULT_CAPACITY = 32;
    private static final String REPLACED_WITH_INTERNAL_SQL = null;

    private Connection conn;
    private final String sql;
    private final List<Object> args = new ArrayList<>(DEFAULT_CAPACITY);
    private final HashMap<String, Object> namedArgs = new HashMap<String, Object>(DEFAULT_CAPACITY);
    private boolean ownsConnection = false;

    private boolean debugSql = false;

    //public ThreadLocal<BiFunction<FSQLQuery, String, String>> onThreadBeforeQueryExecution;
    //public ThreadLocal<Consumer<FSQLQuery>> onAfterQueryExecution;


    private FSQLQuery(Connection conn, String sql, boolean ownsConnection) {
        this.conn = conn;
        this.sql = sql;
        this.ownsConnection = ownsConnection;
    }

    public static FSQLQuery create(Connection conn, String sql) {
        return new FSQLQuery(conn, sql, false);
    }

    public static FSQLQuery create(String sql) {
        try {
            return new FSQLQuery(null, sql, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create FSQLQuery with new connection", e);
        }
    }



    /*
     * Bind values to the query. This can be used for both positional and named parameters.
     * For named parameters, use the bindNamed method.
     */

    public FSQLQuery bind(Object... values) {
        if (values != null && values.length > 1) {
            throw new RuntimeException("More than one argument provided to bind method. If you want to bind multiple values, use bindArray or bind(Iterable) methods.");
        }

        if (values != null && values.length > 0) {
            bind(Arrays.asList(values));
        }
        return this;
    }

    public FSQLQuery bindIf(boolean cond, Object... values) {
        if (!cond) return this;
        return bind(values);
    }

    public FSQLQuery bindArray(Object[] values) {
        if (values != null && values.length > 0) {
            bind(Arrays.asList(values));
        }
        return this;
    }

    public FSQLQuery bindArrayIf(boolean cond, Object[] values) {
        if (!cond) return this;
        return bindArray(values);
    }

    public FSQLQuery bind(Iterable<?> values) {
        if (values != null) {
            values.forEach(value -> bind(value));
        }
        return this;
    }

    public FSQLQuery bindIf(boolean cond, Iterable<?> values) {
        if (!cond) return this;
        return bind(values);
    }

    public FSQLQuery bind(List<?> values) {
        if (values != null) {
            args.addAll(values);
        }
        return this;
    }

    public FSQLQuery bindIf(boolean cond, List<?> values) {
        if (!cond) return this;
        return bind(values);
    }

    public FSQLQuery bindNamed(String name, Object value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Parameter name cannot be null or blank");
        }

        namedArgs.put(name, value);
        return this;
    }

    public FSQLQuery bindNamedIf(boolean cond, String name, Object value) {
        if (!cond) return this;
        return bindNamed(name, value);
    }

    public FSQLQuery bindNamed(Map<String, Object> values) {
        if (values != null) {
            namedArgs.putAll(values);
        }

        return this;
    }

    public FSQLQuery bindNamedIf(boolean cond, Map<String, Object> values) {
        if (!cond) return this;
        return bindNamed(values);
    }


    protected static void __bindArgs(PreparedStatement ps, Object[] args) throws SQLException {
        __bindArgs(ps, Arrays.asList(args));
    }

    //@SuppressWarnings("unchecked")
    protected static void __bindArgs(PreparedStatement ps, List<Object> args) throws SQLException {
        int i = 1;
        for (var arg : args) {

            if (arg instanceof Objects[] arr) {
                for (var val : arr) FSQL.bind(ps, i++, val);
            } else if (arg instanceof int[] arr) {
                for (var val : arr) FSQL.bind(ps, i++, val);
            } else if (arg instanceof long[] arr) {
                for (var val : arr) FSQL.bind(ps, i++, val);
            } else if (arg instanceof List<?> list) {
                for (Object val : list) FSQL.bind(ps, i++, val);
            } else if (arg instanceof LinkedHashMap<?, ?> map) {
                for (Object val : map.values()) FSQL.bind(ps, i++, val);
            } else if (arg != null && arg.getClass().isArray()) {
                for (int idx = 0, n = Array.getLength(arg); idx < n; idx++) {
                    Object val = Array.get(arg, idx);
                    FSQL.bind(ps, i++, val);
                }
            } else {
                if (arg == null) {
                    FSQL.bindNull(ps, i++);
                } else {
                    FSQL.bind(ps, i++, arg);
                }
            }
        }
    }

    protected Long getGeneratedKey(PreparedStatement ps, boolean required) throws SQLException {
        ResultSet keys = ps.getGeneratedKeys();

        if (!keys.next()) {
            if (required)
                throw new SQLException("Insert succeeded but no ID obtained.");

            return null;
        }

        return keys.getLong(1);
    }

    public FSQLQuery debug() {
        this.debugSql = true;
        return this;
    }

    public FSQLQuery debug(boolean activate) {
        this.debugSql = activate;
        return this;
    }

    private <T> T execute(QueryExecutor<T> executor) throws Exception {
        if (ownsConnection) {
            T result = null;

            Connection oldConn = conn;
            try {
                conn = DataSourceManager.getConnection();
                result = executor.run(conn);
            } finally {
                conn.close();
                conn = oldConn;
            }

            return result;
        } else {
            return executor.run(conn);
        }
    }

    private PreparedStatement prepareSql(String inputSql, Object... options) throws Exception {
        // set activeSql to input if provided, otherwise use original
        String activeSql = (inputSql != null) ? inputSql : this.sql;
/*
        if(debugSql || OVERRIDE_DEBUG_SQL) {
            try (var ignore = Logger.scope("Attempt Query::prepareSql")) {
                Logger.log("Raw: " + activeSql);
                Logger.log("Arguments: " + args.toString());
            }
        }
 */

        // parse sql
        NamedSql.Parsed parsed = NamedSql.parse(activeSql, this.namedArgs, args);

        // log
        if (debugSql || OVERRIDE_DEBUG_SQL) {
            try (var ignore = Logger.scope("Parsed Query::prepareSql")) {
                //Logger.log("Raw: " + activeSql);
                //Logger.log("Parsed: " + parsed.sql);
                Logger.log("Interpolated: " + FSQLQueryInterpolator.interpolate(parsed.sql, parsed.args));
                Logger.log("Args: " + Arrays.toString(parsed.args));
            }
        }

        // prepare statement
        PreparedStatement ps;
        if (options != null && options.length > 0) {
            if (!Integer.class.isAssignableFrom(options[0].getClass())) {
                throw new IllegalArgumentException("Expected first option to be of type Integer for Statement options");
            }

            ps = conn.prepareStatement(parsed.sql, ((Number) options[0]).intValue());
        } else {
            ps = conn.prepareStatement(parsed.sql);
        }

        // bind args
        ArgumentBinder binder = new ArgumentBinder(ps);
        binder.bind(parsed.args);

        return ps;
    }

    protected InsertResult insert(boolean requireResult) throws Exception {
        return execute(conn -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL, Statement.RETURN_GENERATED_KEYS)) {
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
        });
    }

    public long insertAndGetCount() throws Exception {
        InsertResult result = insert(true);
        return result.getCount();
    }

    /*
    public boolean insertAndGetStatus() throws Exception {
        return insertAndGetCount() > 0;
    }
     */

    public Long insertAndGetId() throws Exception {
        InsertResult result = insert(true);
        return result.getId();
    }

    public Long insertAndGetId(Consumer<Long> after) throws Exception {
        InsertResult result = insert(true);

        if (after != null)
            after.accept(result.getId());

        return result.getId();
    }

    public Long insertAndSetId(Object instance, String idField) throws Exception {
        // no execute pattern as this method uses other method that already handles connection closing and error handling

        // perform insert and get generated id
        Long entityId = insertAndGetId();

        // set id field if possible
        if (entityId != null) {
            FSQLUtils.setEntityId(instance, entityId, idField);
        }

        return entityId;
    }

    public int delete() throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
                return ps.executeUpdate();
            }
        });
    }

    public int update() throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
                return ps.executeUpdate();
            }
        });
    }

    public <T> List<T> fetchAll(Class<T> clazz) throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
                ps.executeQuery();

                try (ResultSet rs = ps.getResultSet()) {
                    // build class mapping
                    FSQLClassMapping<T> classMapping = new FSQLClassMapping<>(
                        clazz.getDeclaredConstructor(),
                        FSQLCachedFieldColumnLookup.build(clazz, rs)
                    );

                    // hydrate
                    List<T> items = new ArrayList<>(32);
                    while (rs.next()) {
                        items.add(FSQLDefaultRowMapper.map(rs, classMapping));
                    }
                    return items;
                }
            }
        });
    }

    public <T> Optional<T> fetchOne(Class<T> clazz) throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
                ps.executeQuery();

                try (ResultSet rs = ps.getResultSet()) {
                    // build class mapping
                    FSQLClassMapping<T> classMapping = new FSQLClassMapping<>(
                        clazz.getDeclaredConstructor(),
                        FSQLCachedFieldColumnLookup.build(clazz, rs)
                    );

                    // hydrate
                    return rs.next()
                        ? Optional.of(FSQLDefaultRowMapper.map(rs, classMapping))
                        : Optional.empty();
                }
            }
        });
    }


    @FunctionalInterface
    public interface ResultSetConsumer<R> {
        R accept(ResultSet rs) throws Exception;
    }

    @FunctionalInterface
    public interface ResultSetRowConsumer<R> {
        R accept(ResultSet rs, int rowIndex) throws Exception;
    }

    public <R> R fetchCallback(ResultSetConsumer<R> resultSetConsumer) throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
                ps.executeQuery();

                try (ResultSet rs = ps.getResultSet()) {
                    return resultSetConsumer.accept(rs);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public <R> List<R> fetchCallback(ResultSetRowConsumer<R> resultSetConsumer) throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
                ps.executeQuery();

                try (ResultSet rs = ps.getResultSet()) {
                    List<R> res = new ArrayList<R>();
                    int i = 0;
                    while (rs.next()) {
                        res.add(resultSetConsumer.accept(rs, i++));
                    }
                    return res;
                }
            }
        });
    }

    public long selectCount() throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL)) {
                ResultSet rs = ps.executeQuery();
                if(!rs.next()) {
                    throw new RuntimeException("Expected count query to return a result");
                }
                return rs.getLong(1);
            }
        });
    }

    public boolean selectExists() throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql("SELECT EXISTS (" + this.sql + ")")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if(!rs.next()) {
                        throw new RuntimeException("Expected exists query to return a result");
                    }
                    return rs.getBoolean(1);
                }
            }
        });
    }


    /*
     * Fetch a single column value from the first row of the result set. This is useful for queries that return a
     * single value, such as COUNT(*) or MAX(column).
     */

    public <R> Optional<R> fetchColumn(Class<R> t) throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return Optional.empty();

                return Optional.ofNullable(rs.getObject(1, t));
            }
        });
    }

    public <R> Optional<R> fetchColumn(int colIndex, Class<R> t) throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return Optional.empty();

                return Optional.ofNullable(rs.getObject(colIndex, t));
            }
        });
    }

    public <R> Optional<R> fetchColumn(String colIndex, Class<R> t) throws Exception {
        return execute(r -> {
            try (PreparedStatement ps = prepareSql(REPLACED_WITH_INTERNAL_SQL);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return Optional.empty();

                return Optional.ofNullable(rs.getObject(colIndex, t));
            }
        });
    }

}