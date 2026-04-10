package root.database;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static root.common.utils.Preconditions.checkArgument;


public class GenericEntityPersistence {
    @FunctionalInterface
    public interface FieldGetter {
        Object get(Object entity) throws Exception;
    }


    public static boolean DEBUG_SQL = false;

    public static String DEFAULT_ID_NAME = "id";
    private final static ConcurrentHashMap<Class<?>, LinkedHashMap<String, FieldGetter>> PROPERTY_MAP_CACHE = new ConcurrentHashMap<>();


    /**
     * Enable or disable debug logging of generated SQL statements in the generic persistence methods. When enabled, the
     * generated SQL statements will be printed to the console for debugging purposes. The method returns the previous
     * debug state before the change.
     *
     * @param debug
     * @return
     */

    public static boolean setDebugSql(boolean debug) {
        boolean prev = DEBUG_SQL;
        DEBUG_SQL = debug;
        return prev;
    }


    /**
     * Set the default ID field name to use when null is passed as the id_field_name parameter in the generic
     * persistence methods.
     *
     * @param id_name
     */

    public static void setDefaultIdName(String id_name) {
        DEFAULT_ID_NAME = id_name;
    }


    /**
     * Get the current default ID field name used when null is passed as the id_field_name parameter in the generic
     * persistence methods.
     *
     * @return the current default ID field name
     */

    public static String getDefaultIdName() {
        return DEFAULT_ID_NAME;
    }


    /*
    private static LinkedHashMap<String, FieldGetter> buildPropertyMapWithoutId(Object entity, String id_field_name, boolean requireNonEmpty) {
        return PROPERTY_MAP_CACHE.computeIfAbsent(entity.getClass(), clazz -> buildPropertyMapWithoutIdImpl(entity, id_field_name, requireNonEmpty));
    }

    private static LinkedHashMap<String, FieldGetter> buildPropertyMapWithoutIdImpl(Object entity, String id_field_name, boolean requireNonEmpty) {
        id_field_name = (id_field_name == null) ? DEFAULT_ID_NAME : id_field_name;
        LinkedHashMap<String, FieldGetter> props = new LinkedHashMap<>();

        try {
            for (var field : entity.getClass().getDeclaredFields()) {
                // check modifiers to skip static, transient, and synthetic fields
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isTransient(mod) || field.isSynthetic())
                    continue;

                // skip ID field
                String name = field.getName();
                if ((id_field_name != null) && name.equals(id_field_name)) {
                    id_field_name = null;
                    continue;
                }

                // make field accessible
                field.setAccessible(true);

                // modify case and map getter
                name = CachedCaseConverter.camelToSnake(name);
                props.put(name, field::get);
            }
        } catch (Exception e) {
            throw new NoSuchElementException("Failed to access field value", e);
        }

        if (requireNonEmpty && props.isEmpty())
            throw new IllegalStateException("Entity must have at least one non-ID field");

        return props;
    }

    private static Object[] extractPropertyValues(Object entity, Map<String, FieldGetter> props) throws Exception {
        Object[] prop_val_arr = new Object[props.size()];
        int i = 0;
        for (var getter : props.values()) {
            prop_val_arr[i++] = getter.get(entity);
        }
        return prop_val_arr;
    }
    */

    private static void setEntityId(Object entity, String id_field_name, Object id) throws Exception {
        var actualField = entity.getClass().getDeclaredField(id_field_name);
        actualField.setAccessible(true);
        actualField.set(entity, id);
    }

    private static Object getEntityId(Object entity, String id_field_name) throws Exception {
        var actualField = entity.getClass().getDeclaredField(id_field_name);
        actualField.setAccessible(true);
        return actualField.get(entity);
    }


    /**
     * Generic method to insert an entity into the specified table and update the ID field of the entity with the
     * generated ID.
     *
     * @param conn the database connection to use for the operation
     * @param table_name the name of the table to insert into
     * @param entity the entity object to insert, which should have fields corresponding to the table columns (except
     * the ID field)
     * @param id_field_name the name of the ID field in the entity, which will be updated with the generated ID after
     * insertion (if null, defaults to "id")
     * @param <T> the type of the entity being inserted, which should have a field for the ID and other fields
     * corresponding to the table columns
     * @return the same entity object passed in, but with the ID field updated to the generated ID from the database
     * @throws Exception if any error occurs during the database operation or reflection access to the entity fields
     */

    public static <T> T genericInsertAndUpdateId(Connection conn, String table_name, T entity, String id_field_name) throws Exception {
        Objects.requireNonNull(conn);
        Objects.requireNonNull(table_name);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(id_field_name);
        TableNameSanitizer.validateSafeTableName(table_name);

        String[] columnNameArr;
        Object[] columnValueArr;

        String parenColumnListSql;
        String parenPlaceholdersSql;

        // extract column names and values from entity using EntityMeta
        EntityMeta meta = EntityMeta.create(entity.getClass());
        columnNameArr = meta.getNonIdColumnNames();
        columnValueArr = meta.getNonIdPropertyValues(entity);

        // Build the SQL insert statement
        parenColumnListSql = "(" + String.join(", ", columnNameArr) + ")";
        parenPlaceholdersSql = SqlFactory.createParenPlaceholdersSql(columnNameArr.length);

        /*
        // Build a map of property names to their getter functions, excluding the ID field
        var props = buildPropertyMapWithoutId(entity, id_field_name, true);

        columnNameArr = props.keySet().toArray(new String[0]);
        columnValueArr = extractPropertyValues(entity, props);

        // Build the SQL insert statement
        parenColumnListSql = "(" + String.join(", ", columnNameArr) + ")";
        parenPlaceholdersSql = "(" + String.join(", ", Collections.nCopies(columnNameArr.length, "?")) + ")";
        */

        String sql = "INSERT INTO " + table_name + parenColumnListSql + " VALUES " + parenPlaceholdersSql;

        // Execute the insert and get the generated ID
        Long id = FSQLQuery.create(conn, sql)
            .bindArray(columnValueArr)
            //.bindArray(extractPropertyValues(entity, props))
            .debug(DEBUG_SQL)
            .insertAndSetId(entity, id_field_name);

        if (id == null) throw new IllegalStateException("Generated ID must be a number");

        // Set the generated ID back to the entity
        setEntityId(entity, id_field_name, id);

        // return result
        return entity;
    }


    /**
     * Generic method to update an entity in the specified table based on its ID field. The method builds an SQL UPDATE
     * statement using the non-ID fields of the entity and executes it against the database. The ID field is used in the
     * WHERE clause to identify which record to update. The method returns the number of affected rows.
     *
     * @param conn
     * @param table_name
     * @param entity
     * @param id_field_name
     * @param <T>
     * @return
     * @throws Exception
     */

    public static <T> int genericUpdate(Connection conn, String table_name, T entity, String id_field_name) throws Exception {
        Objects.requireNonNull(conn);
        Objects.requireNonNull(table_name);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(id_field_name);
        TableNameSanitizer.validateSafeTableName(table_name);

        String[] columnNameArr;
        Object[] columnValueArr;
        String setClauseSql;

        Object entityId = getEntityId(entity, id_field_name);
        checkArgument(entityId instanceof Number, "Entity must have numeric ID field for update operation");

        // extract column names and values from entity using EntityMeta
        EntityMeta meta = EntityMeta.create(entity.getClass());
        columnNameArr = meta.getNonIdColumnNames();
        columnValueArr = meta.getNonIdPropertyValues(entity);

        StringBuilder setClauseBuilder = new StringBuilder(512);
        for (int i = 0; i < columnNameArr.length; i++) {
            if (i > 0) setClauseBuilder.append(", ");
            setClauseBuilder.append(columnNameArr[i]).append(" = ?");
        }
        setClauseSql = setClauseBuilder.toString();

        /*
        // Build a map of property names to their getter functions, excluding the ID field
        var props = buildPropertyMapWithoutId(entity, id_field_name, true);

        // Build the SQL insert statement
        setClauseSql = props.keySet().stream()
            .map(key -> key + " = ?")
            .collect(Collectors.joining(", "));

        columnValueArr = extractPropertyValues(entity, props);
        */

        var sql = "UPDATE " + table_name + " SET " + setClauseSql + " WHERE " + id_field_name + " = ?";

        // Execute the insert and get the generated ID
        int affectedRows = FSQLQuery.create(conn, sql)
            .bindArray(columnValueArr)
            .bind(entityId)
            .debug(DEBUG_SQL)
            .update();

        // return result
        return affectedRows;
    }


    /**
     * Generic method to delete an entity from the specified table based on its ID field. The method builds an SQL
     * DELETE statement using the ID field of the entity and executes it against the database. The ID field is used in
     * the WHERE clause to identify which record to delete. The method returns the number of affected rows.
     *
     * @param conn
     * @param table_name
     * @param entity
     * @param id_field_name
     * @param <T>
     * @return
     * @throws Exception
     */

    public static <T> int genericDelete(Connection conn, String table_name, T entity, String id_field_name) throws Exception {
        Objects.requireNonNull(conn);
        Objects.requireNonNull(table_name);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(id_field_name);
        TableNameSanitizer.validateSafeTableName(table_name);

        // cache variables
        Object entityId = getEntityId(entity, id_field_name);
        checkArgument(entityId instanceof Number, "Entity must have numeric ID field for update operation");

        // Build the SQL insert statement
        var sql = "DELETE FROM " + table_name + " WHERE (" + id_field_name + " = ?)";

        // Execute the insert and get the generated ID
        int affectedRows = FSQLQuery.create(conn, sql)
            .bind(entityId)
            .debug(DEBUG_SQL)
            .delete();

        // return result
        return affectedRows;
    }
}