package root.includes.proxyrepo;

import root.database.*;
import root.includes.logger.Logger;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static root.database.TableNameSanitizer.checkSafeTableName;

class RepositoryProxyImpl<T> implements InvocationHandler {
    private static final String DEFAULT_ID_FIELD_NAME = "id";
    private static final boolean DEBUG_SQL = true;

    @FunctionalInterface
    interface ThrowingFn {
        Object apply(Object[] args) throws Exception;
    }

    static Function<Object[], Object> wrap(ThrowingFn fn) {
        return args -> {
            try {
                return fn.apply(args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private final Map<Method, Function<Object[], Object>> HANDLE_CACHE = new ConcurrentHashMap<>();

    private final Object target;
    private final Map<String, Object> options;
    //private static MethodHandles.Lookup lookup = MethodHandles.lookup();

    private final String __TABLE_NAME;
    private final Class<?> __MODEL_CLASS;

    SqlQueryMethodNameScanner methodNameScanner = null;

    boolean checkArgumentInstanceOf(Object obj, Class<?> expected) {
        if(!expected.isInstance(obj)) {
            Class<?> actual = (obj != null) ? obj.getClass() : null;
            throw new IllegalArgumentException("Expected instance of " + expected.getName() + " but got " + String.valueOf(actual));
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    RepositoryProxyImpl(Object target, Map<String, Object> options) {
        this.target = target;
        this.options = options != null ? options : new HashMap<String, Object>();

        if (!this.options.containsKey("tableName"))
            throw new IllegalArgumentException("Missing tableName");

        if (!this.options.containsKey("modelClass"))
            throw new IllegalArgumentException("Missing model class");

        __TABLE_NAME = (String) this.options.get("tableName");
        __MODEL_CLASS = (Class<T>) this.options.get("modelClass");

        checkSafeTableName(__TABLE_NAME);
        checkArgumentInstanceOf(__MODEL_CLASS, Class.class);

    }

    private Method findMethod(Class<?> c, Method m) {
        for (Class<?> cls = c; cls != null; cls = cls.getSuperclass()) {
            for (var mm : cls.getDeclaredMethods()) {
                if (mm.getName().equals(m.getName()) &&
                    (mm.getParameterCount() == m.getParameterCount()) &&
                    (Arrays.equals(mm.getParameterTypes(), m.getParameterTypes()))) {
                    return mm;
                }
            }
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            // invoke default method (e.g. default method in repo interface)
            if (method.isDefault()) {
                return MethodHandles.lookup()
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(proxy)
                    .invokeWithArguments(args);
            }

            // invoke implemented method (if exists (e.g. custom repo method implemented in a separate class))
            if (target != null) {
                Method impl = findMethod(target.getClass(), method);
                if (impl != null) {
                    impl.setAccessible(true);
                    //Logger.log("Invoked implemented method: " + impl.getDeclaringClass().getName() + "." + impl.getName());
                    //Logger.log("Invoke Args: " + Arrays.toString(args));
                    return impl.invoke(target, args);
                }
            }
        }catch(Throwable e) {
            throw new RuntimeException("Error invoking method " + method.getName() + ": " + e.getMessage(), e);
        }


        // handle generated methods
        String methodName = method.getName();

        methodNameScanner = new SqlQueryMethodNameScanner();
        methodNameScanner.scan(methodName, SqlQueryMethodNameScanner.FLAG_NONE);

//        try(var ignore = Logger.scope("RepoProxy.invoke (" + methodName + ")")) {
//            Logger.log("Source: " + root.common.utils.StringUtils.quotedString(methodNameScanner.sourceString));
//            Logger.log("Type: " + root.common.utils.StringUtils.quotedString(methodNameScanner.methodType));
//            Logger.log("Where Clause: " + root.common.utils.StringUtils.quotedString(methodNameScanner.whereStr));
//            Logger.log("Param count: " + methodNameScanner.paramCount);
//        }

        return HANDLE_CACHE.computeIfAbsent(method, m -> {
            String name = m.getName();

            // handle default repo method(s)
            if(name.equals("create")) return wrap((method_args) -> handleCreate(m, method_args));
            if(name.equals("update")) return wrap((method_args) -> handleUpdate(m, method_args));
            if(name.equals("save")) return wrap((method_args) -> handleSave(m, method_args));

            // find
            //if(name.equals("findById")) return wrap((method_args) -> handleFindById(m, method_args));
            if(name.equals("findById")) return wrap((method_args) -> handleFindById(m, method_args));
            if(name.startsWith("findBy")) return wrap((method_args) -> handleFindBy(m, method_args));
            if(name.equals("findAll")) return wrap((method_args) -> handleFindAll(m, method_args));
            if(name.startsWith("findFirstBy")) return wrap((method_args) -> handleFindFirstById(m, method_args));

            // delete
            if(name.equals("deleteById")) return wrap((method_args) -> handleDeleteById(m, method_args));
            if(name.startsWith("deleteBy")) return wrap((method_args) -> handleDeleteBy(m, method_args));
            if(name.equals("delete")) return wrap((method_args) -> handleDelete(m, method_args));

            // delete all methods
            if(name.equals("deleteAll")) return wrap((method_args) -> handleDeleteAll(m, method_args));
            if(name.equals("deleteAllInBatch")) return wrap((method_args) -> handleDeleteAllInBatch(m, method_args));
            if(name.equals("deleteAllById")) return wrap((method_args) -> handleDeleteAllById(m, method_args));
            if(name.equals("deleteAllByIdInBatch")) return wrap((method_args) -> handleDeleteAllInBatch(m, method_args));

            // count
            if(name.equals("count")) return wrap((method_args) -> handleCount(m, method_args));
            if(name.startsWith("countBy")) return wrap((method_args) -> handleCountBy(m, method_args));

            // exists
            if(name.equals("existsById")) return wrap((method_args) -> handleExistsById(m, method_args));
            if(name.startsWith("existsBy")) return wrap((method_args) -> handleExistsBy(m, method_args));

            throw new UnsupportedOperationException("Unsupported method: " + methodName);
        }).apply(args);
    }



    /*
        * Utility methods to set / get entity id
     */
    /*
    private static final ConcurrentHashMap<Class<?>, MethodHandle> CACHED_ENTITY_ID_GETTER = new ConcurrentHashMap<>();

    private static Field findField(Class<?> c, String name) {
        for (Class<?> cur = c; cur != null; cur = cur.getSuperclass()) {
            try {
                Field f = cur.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {}
        }

        throw new RuntimeException("Field '" + name + "' not found in " + c);
    }
*/

    private Object getIntReturnValue(Class<?> returnType, int affectedRows) {
        if(returnType == int.class || returnType == Integer.class) return affectedRows;
        if(returnType == long.class || returnType == Long.class) return (long) affectedRows;

        throw new RuntimeException("Unsupported return type " + returnType.getSimpleName());
    }

    private Object getAffectedRowsReturnValue(Class<?> returnType, int affectedRows) {
        if(returnType == int.class || returnType == Integer.class) return affectedRows;
        if(returnType == long.class || returnType == Long.class) return (long) affectedRows;
        if(returnType == boolean.class || returnType == Boolean.class) return affectedRows > 0;
        if(returnType == void.class || returnType == Void.class) return null;

        throw new RuntimeException("Unsupported return type " + returnType.getSimpleName());
    }

    private Object getBooleanReturnValue(Class<?> returnType, boolean booleanValue) {
        if(returnType == int.class || returnType == Integer.class) return booleanValue ? 1: 0;
        if(returnType == long.class || returnType == Long.class) return booleanValue ? 1L : 0L;
        if(returnType == boolean.class || returnType == Boolean.class) return booleanValue;
        if(returnType == void.class || returnType == Void.class) return null;

        throw new RuntimeException("Unsupported return type " + returnType.getSimpleName());
    }

    private static <T> TreeSet<T> toTreeSet(Collection<T> c) {
        try {
            return new TreeSet<>(c);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Elements must be Comparable", e);
        }
    }

    private static long convertToLongValueExactOrThrow(Object arg) {
        if (arg instanceof Long l) return l;
        if (arg instanceof Integer i) return i.longValue();
        if (arg instanceof Short s) return s.longValue();
        if (arg instanceof Byte b) return b.longValue();

        throw new IllegalArgumentException();
    }



    /**
     * Handles the "save" method by determining whether to perform an insert (create) or update operation based on the presence of an ID in the entity.
     * @param method
     * @param args
     * @return
     * @throws Exception
     */

    private Object handleSave(Method method, Object[] args) {
        Object entityId = FSQLUtils.getEntityId(args[0], DEFAULT_ID_FIELD_NAME);

        if (entityId == null || convertToLongValueExactOrThrow(entityId) == 0L) {
            return handleCreate(method, args);
        } else {
            return handleUpdate(method, args);
        }
    }



    /**
     * Our method handles incorrecly when no generated keys are returned from the insert statement (e.g. when
     * using a non auto-increment primary key or when the database doesn't support generated keys).
     *
     * @param method
     * @param args
     * @return
     * @throws Exception
     */

    private Object handleCreate(Method method, Object[] args)  {
        Object entity = args[0];

        DataSourceManager.withVoid(conn -> {
            var oldDebugValue = GenericEntityPersistence.setDebugSql(DEBUG_SQL);
            GenericEntityPersistence.genericInsertAndUpdateId(conn, __TABLE_NAME, entity, "id");
            GenericEntityPersistence.setDebugSql(oldDebugValue);
        });

        // return result
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(__MODEL_CLASS)) return entity;
        if (returnType == void.class || returnType == Void.class) return null;
        if (returnType == int.class || returnType == long.class) return FSQLUtils.getEntityId(entity, DEFAULT_ID_FIELD_NAME);
        if (returnType == Integer.class || returnType == Long.class) return FSQLUtils.getEntityId(entity, DEFAULT_ID_FIELD_NAME);

        throw new RuntimeException("Unsupported return type " + returnType);
    }

    private Object handleUpdate(Method method, Object[] args) {
        Object entity = args[0];

        int affectedRows = DataSourceManager.with(conn -> {
            var oldDebugValue = GenericEntityPersistence.setDebugSql(DEBUG_SQL);
            int n = GenericEntityPersistence.genericUpdate(conn, __TABLE_NAME, entity, "id");
            GenericEntityPersistence.setDebugSql(oldDebugValue);
            return n;
        });

        // return result
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(entity.getClass())) return entity;

        if (returnType == int.class || returnType == Integer.class) return (int) affectedRows;
        if (returnType == long.class || returnType == Long.class) return (long) affectedRows;

        if (returnType == boolean.class || returnType == Boolean.class) return affectedRows > 0;
        if (returnType == void.class || returnType == Void.class) return null;

        throw new RuntimeException("Unsupported return type " + returnType + "(" + returnType.getSimpleName() + "), found (" + ((Object) entity).getClass().getSimpleName() + ")");
    }



    /*
    handle find methods
     */

    private Object handleFindBy(Method method, Object[] args) {
        String sql = "SELECT * FROM " + __TABLE_NAME + " WHERE " + methodNameScanner.whereStr;

        // create query & return result(s) based on return type
        FSQLQuery q = FSQLQuery.create(sql)
            .debug(DEBUG_SQL)
            .bindArray(args);

        // return result
        Class<?> returnType = method.getReturnType();
        // TODO: support more return types (e.g. Stream, Iterable, array, etc.)
        if (Collection.class.isAssignableFrom(returnType)) return q.fetchAll(__MODEL_CLASS);
        if (returnType == Optional.class) return q.fetchOne(__MODEL_CLASS);
        if (returnType.isAssignableFrom(__MODEL_CLASS)) return q.fetchOne(__MODEL_CLASS).orElse(null);

        throw new RuntimeException("Unsupported return type " + returnType.getSimpleName());
    }

    private Object handleFindAll(Method method, Object[] args) {
        // create query & return result(s) based on return type
        var list = FSQLQuery.create("SELECT * FROM " + __TABLE_NAME)
            .debug(DEBUG_SQL)
            .fetchAll(__MODEL_CLASS);

        Class<?> returnType = method.getReturnType();
        if (returnType == HashSet.class) return new HashSet<>(list);
        if (returnType == LinkedHashSet.class) return new LinkedHashSet<>(list);
        if (returnType == TreeSet.class) return toTreeSet(list);
        if (returnType == Set.class) return new LinkedHashSet<>(list);
        if (Collection.class.isAssignableFrom(returnType)) return list;
        if (Iterable.class.isAssignableFrom(returnType)) return list;
        if (returnType.isArray()) return list.toArray( (Object[]) java.lang.reflect.Array.newInstance(__MODEL_CLASS, list.size()) );
        if (returnType == java.util.stream.Stream.class) return list.stream();

        throw new RuntimeException("Unsupported return type " + returnType.getSimpleName());
    }

    public Object handleFindById(Method method, Object[] args) {
        long entityId = convertToLongValueExactOrThrow(args[0]);

        var result = FSQLQuery.create("SELECT * FROM " + __TABLE_NAME + " WHERE id = ?")
            .bind( entityId )
            .debug(DEBUG_SQL)
            .fetchOne(__MODEL_CLASS);

        Class<?> returnType = method.getReturnType();
        if(returnType == Optional.class) return result;
        if(returnType == __MODEL_CLASS) return result.orElse(null);
        if(returnType == void.class || returnType == Void.class) return null;

        throw new RuntimeException("Unsupported return type " + returnType.getSimpleName());
    }

    public Object handleFindFirstById(Method method, Object[] args) {
        String sql = "SELECT * FROM " + __TABLE_NAME + " WHERE " + methodNameScanner.whereStr + " LIMIT 1";

        // create query & return result(s) based on return type
        FSQLQuery q = FSQLQuery.create(sql)
            .debug(DEBUG_SQL)
            .bindArray(args);

        // return result
        Class<?> returnType = method.getReturnType();
        // TODO: support more return types (e.g. Stream, Iterable, array, etc.)
        if (Collection.class.isAssignableFrom(returnType)) return q.fetchAll(__MODEL_CLASS);
        if (returnType == Optional.class) return q.fetchOne(__MODEL_CLASS);
        if (returnType.isAssignableFrom(__MODEL_CLASS)) return q.fetchOne(__MODEL_CLASS).orElse(null);

        throw new RuntimeException("Unsupported return type " + returnType.getSimpleName());
    }



    /*
    handle count methods
     */

    public Object handleCount(Method method, Object[] args) {
        String sql = "SELECT COUNT(*) FROM " + __TABLE_NAME;

        long found = FSQLQuery.create(sql)
            .debug(DEBUG_SQL)
            .selectCount();

        Class<?> returnType = method.getReturnType();
        return getIntReturnValue(returnType, (int) found);
    }

    public Object handleCountBy(Method method, Object[] args) {
        if(methodNameScanner.whereStr == null || methodNameScanner.whereStr.isEmpty()) {
            throw new IllegalArgumentException("Invalid countBy method name: " + method.getName());
        }

        String sql = "SELECT COUNT(*) FROM " + __TABLE_NAME + " WHERE " + methodNameScanner.whereStr;

        long found = FSQLQuery.create(sql)
            .bindArray(args)
            .debug(DEBUG_SQL)
            .selectCount();

        Class<?> returnType = method.getReturnType();
        return getIntReturnValue(returnType, (int) found);
    }



    /*
    handle exists methods
     */

    public Object handleExistsById(Method method, Object[] args) {
        long entityId = convertToLongValueExactOrThrow(args[0]);

        String sql = "SELECT * FROM " + __TABLE_NAME + " WHERE id = ?";

        boolean exists = FSQLQuery.create(sql)
            .bind(entityId)
            .debug(DEBUG_SQL)
            .selectExists();

        Class<?> returnType = method.getReturnType();
        return getBooleanReturnValue(returnType, exists);
    }

    public Object handleExistsBy(Method method, Object[] args) {
        String sql = "SELECT * FROM " + __TABLE_NAME + " WHERE " + methodNameScanner.whereStr;

        boolean exists = FSQLQuery.create(sql)
            .bindArray( args )
            .debug(DEBUG_SQL)
            .selectExists();

        Class<?> returnType = method.getReturnType();
        return getBooleanReturnValue(returnType, exists);
    }



    /*
    handle delete methods
     */

    public Object handleDeleteById(Method method, Object[] args) {
        long entityId = convertToLongValueExactOrThrow(args[0]);

        String sql = "DELETE FROM " + __TABLE_NAME + " WHERE id = ?";

        int affectedRows = FSQLQuery.create(sql)
            .bind( entityId )
            .debug(DEBUG_SQL)
            .delete();

        Class<?> returnType = method.getReturnType();
        return getAffectedRowsReturnValue(returnType, affectedRows);
    }

    private Object handleDelete(Method method, Object[] args) {
        checkArgumentInstanceOf(args[0], __MODEL_CLASS);

        // setup
        Object entity = args[0];
        long entityId = convertToLongValueExactOrThrow(FSQLUtils.getEntityId(entity, DEFAULT_ID_FIELD_NAME));

        String sql = "DELETE FROM " + __TABLE_NAME + " WHERE id = ?";
        int affectedRows = FSQLQuery.create(sql)
            .bind( entityId )
            .debug(DEBUG_SQL)
            .delete();

        Class<?> returnType = method.getReturnType();
        return getAffectedRowsReturnValue(returnType, affectedRows);
    }

    private Object handleDeleteBy(Method method, Object[] args) {
        String sql = "DELETE FROM " + __TABLE_NAME + " WHERE " + methodNameScanner.whereStr;

        int affectedRows = FSQLQuery.create(sql)
            .bindArray( args )
            .debug(DEBUG_SQL)
            .delete();

        Class<?> returnType = method.getReturnType();
        return getAffectedRowsReturnValue(returnType, affectedRows);
    }



    /*
    multiple delete methods (e.g. deleteAll, deleteAllById, etc.)
     */

    private Object handleDeleteAll(Method method, Object[] args) {
        return handleDeleteAllInBatch(method, args);
    }

    private Object handleDeleteAllInBatch(Method method, Object[] args) {
        @SuppressWarnings("unchecked")
        Iterable<T> entities = (Iterable<T>) args[0];
        Long[] ids = FSQLUtils.extractEntityIds(entities);

        //Logger.log("deleteAllInBatch args: " + String.valueOf(args[0]) + ", extracted ids: " + Arrays.toString(ids));

        // execute query
        String sql = "DELETE FROM " + __TABLE_NAME + " WHERE (id IN " + SqlFactory.createParenPlaceholdersSql(ids.length) + ")";

        //Logger.log("sql: " + sql);
        //Logger.log("args: " + Arrays.toString(ids));

        int affectedRows = FSQLQuery.create(sql)
            .bindArray(ids)
            .debug(DEBUG_SQL)
            .delete();

        // return result
        Class<?> returnType = method.getReturnType();
        return getAffectedRowsReturnValue(returnType, affectedRows);
    }

    private Object handleDeleteAllById(Method method, Object[] args) {
        return handleDeleteAllByIdInBatch(method, args);
    }

    private Object handleDeleteAllByIdInBatch(Method method, Object[] args) {
        @SuppressWarnings("unchecked")
        Iterable<T> ids = (Iterable<T>) args[0];

        // execute query
        String sql = "DELETE FROM " + __TABLE_NAME + " WHERE (id IN " + SqlFactory.createParenPlaceholdersSql(args.length) + ")";

        int affectedRows = FSQLQuery.create(sql)
            .bind(ids)
            .debug(DEBUG_SQL)
            .delete();

        // return result
        Class<?> returnType = method.getReturnType();
        return getAffectedRowsReturnValue(returnType, affectedRows);
    }
}