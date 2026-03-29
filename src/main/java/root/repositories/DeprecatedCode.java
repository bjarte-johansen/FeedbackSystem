package root.repositories;

@Deprecated
public class DeprecatedCode {
    //protected static Connection getConnection() throws Exception { return DB.getConnection(); }

/*
    private static FSQLColumnMapping[] extractEntityFieldNames(Review entity, String id_field_name, boolean include_id) throws Exception {
        Logger.error("[FML ERROR], extractEntityFieldNames used, remove this method and use reflection or code generation instead");

        EntityMeta meta = EntityMeta.create(Review.class);

        if(include_id) {
            return meta.all;
        }else{
            return meta.nonId;
        }
    }

    // returns pair with first element being map of field names and values, and second element being the id of the entity
    private static LinkedHashMap<String, Object> extractWritableEntityProperties(Review entity, String id_field_name, boolean include_id) {
        Logger.error("[FML ERROR], extractWritableEntityProperties used, remove this method and use reflection or code generation instead");

        // TODO: get class mapping of getters and return as array field names and/or of values based on wether
        //  on should build sql or bind values, also should be able to exclude id field for insert and include for
        //  update, also should be able to exclude null values for update

        var map = FSQL.linkedNameValueMap(
            "tenant_id", entity.getTenantId(),
            "external_id", entity.getExternalId(),
            "external_id_hash", entity.getExternalIdHash(),
            "author_id", entity.getAuthorId(),
            "author_name", entity.getAuthorName(),
            "score", entity.getScore(),
            "comment", entity.getComment(),
            "title", entity.getTitle(),
            "created_at", entity.getCreatedAt()
        );

        if(include_id && entity.getId() != null){
            map.put(id_field_name, entity.getId());
        }

        return map;
    }
*/
    /*
    private Object[] extractFieldValuesFromColumnMappings(Object o, FSQLColumnMapping[] mappings) throws Exception {
        Object[] values = new Object[mappings.length];

        for(int i = 0; i < mappings.length; i++){
            values[i] = mappings[i].field.get(o);
        }

        return values;
    }
     */
}
