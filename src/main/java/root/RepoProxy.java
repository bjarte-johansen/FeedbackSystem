package root;

import root.database.CaseConverter;
import root.database.DB;
import root.database.FSQLQuery;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static root.database.CaseConverter.camelToSnake;

class RepoProxy<T> implements InvocationHandler {
    private final Object target;
    private final Map<String, Object> options;
    //private static MethodHandles.Lookup lookup = MethodHandles.lookup();


    RepoProxy(Object target, Map<String, Object> options) {
        this.target = target;
        this.options = options != null ? options : new HashMap<String, Object>();

        if (!this.options.containsKey("tableName"))
            throw new IllegalArgumentException("Missing tableName");

        if (!this.options.containsKey("modelClass"))
            throw new IllegalArgumentException("Missing model class");

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        // handle find method(s)
        if (methodName.startsWith("find")) {

            if (methodName.startsWith("findBy")) {
                return handleFindBy(method, args);
            }

            if (methodName.equals("findAll")) {
                return handleFindAll(method, args);
            }
        }

        // handle create method(s)
        if(methodName.equals("create")) {
            return handleCreate(method, args);
        }

        // handle delete method(s)
        if (methodName.startsWith("delete")) {

            if(methodName.startsWith("deleteBy")) {
                return handleDeleteBy(method, args);
            }

            if(methodName.equals("deleteAll")) {
                return handleDeleteAll(method, args);
            }

            return handleDelete(method, args);
        }

        return method.invoke(target, args);
    }

    private String buildAndClause(String[] parts) {
        StringBuilder sql = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sql.append(" AND ");

            sql.append(camelToSnake(parts[i]));
            sql.append(" = ?");
        }

        return sql.toString();
    }

    private Object getEntityId(Object entity) throws Exception {
        return entity.getClass().getMethod("getId").invoke(entity);
    }

    private void setEntityId(Object entity, Object id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
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
    @SuppressWarnings("unchecked")
    private Object handleCreate(Method method, Object[] args) throws Exception {
        // setup
        Object entity = args[0];
        Class<T> modelClass = (Class<T>) options.get("modelClass");
        Class<T> returnType = (Class<T>) method.getReturnType();
        String methodName = method.getName();

        // validate
        if (!modelClass.isInstance(args[0]))
            throw new IllegalArgumentException("Expected entity of type " + modelClass.getName());

        // resolve
        ArrayList<String> colNames = new ArrayList<>(100);
        ArrayList<Object> colValues = new ArrayList<>(100);

        Field[] fields = modelClass.getDeclaredFields();
        for(Field f : fields) {
            int mod = f.getModifiers();
            if(Modifier.isStatic(mod) || Modifier.isTransient(mod)) continue;

            // do not serailize id field
            if(f.getName().equals("id")) continue;

            f.setAccessible(true);
            Object v = f.get(entity);

            colNames.add(CaseConverter.camelToSnake(f.getName()));
            colValues.add(v);
        }

        // build sql and execute query
        String sql = "INSERT INTO " + options.get("tableName")
            + " (" +String.join(", ", colNames) + ")"
            + " VALUES (" + String.join(", ", Collections.nCopies(colNames.size(), "?")) + ")";

        Long id = FSQLQuery.create(DB.getConnection(), sql)
            .bind(colValues.toArray())
            .insertAndGetId();

        // set id if entity has field "id"
        setEntityId(entity, id);

        if(returnType == int.class || returnType == long.class || returnType == Integer.class || returnType == Long.class) {
            return id;
        }

        if(returnType == boolean.class || returnType == Boolean.class){
            return ((Number) id).longValue() > 0;
        }

        if(modelClass.isInstance(entity)) {
            return entity;
        }

        if(returnType == void.class || returnType == Void.class) {
            return null;
        }

        throw new Exception("Unsupported return type " + returnType);
    }



    private Object handleDeleteAll(Method method, Object[] args) throws Exception {

        // setup
        String methodName = method.getName();                             // deleteAll()

        // resolve
        if(methodName.equals("deleteAll")) {
            return FSQLQuery.create(DB.getConnection(), "DELETE FROM " + options.get("tableName"))
                .delete();
        }

        // unsupported return type
        throw new RuntimeException("Unable to match pattern");
    }

    @SuppressWarnings("unchecked")
    private Object handleDelete(Method method, Object[] args) throws Exception {

        // setup
        Class<T> modelClass = (Class<T>) options.get("modelClass");
        String methodName = method.getName();                             // delete(entity)

        // resolve
        if(methodName.equals("delete")) {
            if(!modelClass.isInstance(args[0]))  throw new IllegalArgumentException("Expected argument of type " + modelClass.getName());

            return FSQLQuery.create(DB.getConnection(), "DELETE FROM " + options.get("tableName") + " WHERE id = ?")
                .bind( getEntityId(args[0]) )
                .delete();
        }

        // unsupported return type
        throw new RuntimeException("Unable to match pattern");
    }

    private Object handleDeleteBy(Method method, Object[] args) throws Exception {
        // setup
        String fullMethodName = method.getName();                             // deleteByEmailAndTenantId ex

        // resolve
        if(fullMethodName.startsWith("deleteBy")) {
            // build sql
            String methodName = fullMethodName.substring("deleteBy".length());                    // EmailAndTenantId
            String[] parts = methodName.split("And");

            return FSQLQuery.create(DB.getConnection(), "DELETE FROM " + options.get("tableName") + " WHERE " + buildAndClause(parts))
                .bind(args[0])
                .delete();
        }

        // unsupported return type
        throw new RuntimeException("Unable to match pattern");
    }



    @SuppressWarnings("unchecked")
    private Object handleFindBy(Method method, Object[] args) throws Exception {
        // setup
        Class<T> modelClass = (Class<T>) options.get("modelClass");
        Class<?> returnType = method.getReturnType();
        boolean isCollectionReturnType = Collection.class.isAssignableFrom(returnType);

        // dissect and build sql
        String methodName = method.getName();
        String methodCondStr = methodName.substring("findBy".length());

        String sql = "SELECT * FROM " + ((String) options.get("tableName"))
            + " WHERE "
            + buildAndClause( methodCondStr.split("And") )
            + ((!isCollectionReturnType) ? " LIMIT 1" : "");

        // create query & return result(s) based on return type
        FSQLQuery q = FSQLQuery.create(DB.getConnection(), sql)
            .bind(args);

        if (isCollectionReturnType)
            return q.fetchAll(modelClass);

        if (returnType == Optional.class)
            return q.fetchOne(modelClass);

        if (returnType.isAssignableFrom(modelClass))
            return q.fetchOne(modelClass).orElse(null);

        // unsupported return type
        throw new RuntimeException("Unsupported return type");
    }

    @SuppressWarnings("unchecked")
    private Object handleFindAll(Method method, Object[] args) throws Exception {
        // setup
        Class<T> modelClass = (Class<T>) options.get("modelClass");
        Class<?> returnType = method.getReturnType();
        boolean isCollectionReturnType = Collection.class.isAssignableFrom(returnType);

        // dissect and build sql
        String tableName = (String) options.get("tableName");
        if (tableName == null || tableName.isEmpty())
            throw new IllegalArgumentException("Missing table name in options");

        if (isCollectionReturnType){
            // create query & return result(s) based on return type
            return FSQLQuery.create(DB.getConnection(), "SELECT * FROM " + tableName)
                .fetchAll(modelClass);
        }

        // unsupported return type
        throw new RuntimeException("Unsupported return type");
    }
}