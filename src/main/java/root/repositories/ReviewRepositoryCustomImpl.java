package root.repositories;

import org.springframework.stereotype.Repository;
import root.AppConfig;
import root.database.*;
import root.models.FNV1A64HashGenerator;
import root.models.QueryOptions;
import root.models.Review;

import java.util.*;

@Repository
public class ReviewRepositoryCustomImpl {
    /*
	public List<Review> findByExternalId(String externalId) throws Exception {
        // Implementation to find reviews by resource ID from the database

        return FSQLQuery.create("SELECT * FROM " + AppConfig.REVIEW_TABLE_NAME + " WHERE external_id = ?")
            .bind(externalId)
            .fetchAll(Review.class);
    }
*/

    /*
	public List<Review> findByAuthorIdAndExternalId(long authorId, String path) throws Exception {
        throw new Exception("Method not implemented yet");
    }
     */
/*
	public List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception {
        throw new Exception("Method not implemented yet");
    }
 */
/*
	public long countByExternalId(String externalId) throws Exception {
        String sql = SqlFactory.createCountSql(AppConfig.REVIEW_TABLE_NAME, FSQL.makeArr("external_id = ", "?"));

        return FSQLQuery.create(sql)
            .bind(externalId)
            .selectCount();
    }

	public List<Review> findAll() throws Exception {
        return FSQLQuery.create("SELECT * FROM " + AppConfig.REVIEW_TABLE_NAME)
            .fetchAll(Review.class);
    }

 */
}