package root.repositories;

import org.springframework.stereotype.Repository;
import root.Project;
import root.database.*;
import root.logger.Logger;
import root.models.FNV1A64HashGenerator;
import root.models.IReview;
import root.models.QueryOptions;
import root.models.Review;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

@Repository
public class ReviewRepositoryCustomImpl {
    //protected static Connection getConnection() throws Exception { return DB.getConnection(); }
/*
	public IReview save(IReview review) throws Exception {
        // Implementation to save review to the database

        if (review.getId() == null) {
            return create(review);
        } else {
            return update(review);
        }
    }
 */

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

    private static <T, R> R[] arrayMap(T[] arr, Function<T, R> fn){
        R[] result = (R[]) Array.newInstance(arr.getClass().getComponentType(), arr.length);

        for(int i = 0; i < arr.length; i++){
            try {
                result[i] = fn.apply(arr[i]);
            }catch (Exception e){}
        }

        return result;
    }

    private Object[] extractFieldValuesFromColumnMappings(Object o, FSQLColumnMapping[] mappings) throws Exception {
        Object[] values = new Object[mappings.length];

        for(int i = 0; i < mappings.length; i++){
            values[i] = mappings[i].field.get(o);
        }

        return values;
    }
/*
	public IReview create(IReview review) throws Exception {
        return DB.with(conn -> {
            // Implementation to save review to the database


            var meta = root.database.EntityMeta.create(Review.class);
            //var pair = meta.extractColumnNamesAndValuesArrayAsPair(review, meta.nonId);
            var columnNames = meta.getNonIdColumnNames();
            var columnValues = meta.getFieldValues(review, false);

            //var fieldValues = meta.extractFieldValuesFromColumnMappings(review, meta.nonId);
            //Logger.log("using meta to extract fields for insert: " + Arrays.toString(meta.nonIdColumnNames));
            //var fields = extractWritableEntityProperties((Review) review, Project.DEFAULT_ENTITY_ID_NAME, false);

            Long id = FSQLQuery.create(conn, SqlFactory.createInsertSql("reviews", columnNames))
                .bindArray( columnValues )
                .insertAndGetId();

            review.setId(id);

            return review;
        });
    }


	public IReview update(IReview review) throws Exception {
        return DB.with(conn -> {
            var fields = extractWritableEntityProperties((Review) review, Project.DEFAULT_ENTITY_ID_NAME, false);

            //var meta = root.database.EntityMeta.getCached(Review.class);
            //var pair = meta.extractColumnNamesAndValuesArrayAsPair(review, meta.all);

            // FSQLQuery implementation example
            FSQLQuery.create(conn, SqlFactory.createUpdateSql("reviews", fields, new Object[]{"id =", null}))
                .bind(fields)
                .bind(review.getId())
                .insertAndGetId();

            return review;
        });
    }

	public Optional<Review> findById(long tenantId, long reviewId) throws Exception {
        // Implementation to find review by ID from the database

        return DB.with(conn -> {
            return FSQLQuery.create(conn, "SELECT * FROM reviews WHERE (id = ? AND tenant_id = ?)")
                .bind(reviewId, tenantId)
                .fetchOne(Review.class);
        });
    }
 */

	public List<IReview> findByExternalId(long tenantId, String externalId, Long externalIdHash, QueryOptions queryOptions) throws Exception {
        // Implementation to find reviews by resource ID from the database

        long hashToUse = (externalIdHash == null) ? FNV1A64HashGenerator.generate(externalId) : externalIdHash;

        return FSQLQuery.create("SELECT * FROM reviews WHERE tenant_id = ? AND external_id_hash = ? AND external_id = ?")
            .bind(tenantId, hashToUse, externalId)
            .fetchAll(Review.class)
            .stream()
            .map(review -> (IReview) review)
            .toList();
    }
/*
	public void delete(IReview review) throws Exception {
        // Implementation to delete review from the database

        deleteById(review.getTenantId(),  review.getId());
    }
 */

	public void deleteById(Long tenantId, Long reviewId) throws Exception {
        DB.with(conn -> {
            // Implementation to delete review by ID from the database

            String sql = SqlFactory.createDeleteSql("reviews", FSQL.makeArr("tenant_id =", "?", "id =", "?"));

            FSQLQuery.create(conn, sql)
                .bind(tenantId, reviewId)
                .delete();

            return null;
        });
    }

	public void delete(IReview review) throws Exception {
        DB.with(conn -> {
            // Implementation to delete review from the database

            String sql = SqlFactory.createDeleteSql("reviews", FSQL.makeArr("id =", "?"));

            FSQLQuery.create(conn, sql)
                .bind(review.getId())
                .delete();

            return null;
        });
    }


	public <T> void deleteAll(Iterable<? extends T> entities) throws Exception {
        DB.with(conn -> {
            Long[] ids = FSQLUtils.extractEntityIds(entities);
            String sql = "DELETE FROM reviews WHERE id IN (" + SqlFactory.createParenPlaceholdersSql(ids.length) + ")";

            FSQLQuery.create(conn, sql)
                .bindArray(ids)
                .delete();

            return null;
        });
    }

	public List<Review> findByAuthorIdAndExternalId(long authorId, String path, QueryOptions queryOptions) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public List<Review> findByAuthorIdAndExternalId(long authorId, String path) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public List<Review> findAll() throws Exception {
        return DB.with(conn -> {
            return FSQLQuery.create(conn, "SELECT * FROM reviews")
                //.debug()
                .fetchAll(Review.class);
        });
    }

	public long count() throws Exception {
        return DB.with(conn -> {
            return FSQLQuery.create(conn, SqlFactory.createCountSql("reviews", null))
                .selectCount();
        });
    }

	public long countByExternalId(long tenantId, String externalId, Long externalIdHash) throws Exception {
        return DB.with(conn -> {
            long hashToUse = (externalIdHash == null) ? FNV1A64HashGenerator.generate(externalId) : externalIdHash;

            return FSQLQuery.create(conn, SqlFactory.createCountSql("reviews", new Object[]{"tenant_id =", "?", "external_id_hash = ", "?", "external_id = ", "?"}))
                .bind(tenantId, hashToUse, externalId)
                //.debug(true)
                .selectCount();
        });
    }

	public List<Review> findByTenantId(long tenantId) throws Exception {
        return DB.with(conn -> {
            return FSQLQuery.create(conn, "SELECT * FROM reviews WHERE tenant_id = ?")
                .bind(tenantId)
                .fetchAll(Review.class);
        });
    }
}