package root.database;

import root.logger.Logger;
import root.models.Review;

import java.sql.*;
import java.util.*;

import static root.database.SPIImpl.insert_review;

public class DBTest {
    public static void clean() throws Exception{
        // delete all reviews for tenant_id = 1
        SPIImpl.delete_all_reviews_by_tenant_id(1L);
    }

    public static void run() throws Throwable {
        // insert demo ratings for tenant_id = 1
        DBTest.insertDemoRatings(DB.getConnection(), 1L);

        // fetch and print ratings for tenant_id = 1
        DBTest.fetchAndPrintRatings(1);

        // end print
        System.out.println("OK");
    }


/*
    static LinkedHashMap<String, Object> makeLinkedMap(Object... values) {
        return linkedMap(values);
    }
 */

    static void insertDemoRatings(Connection conn, long tenantId) throws SQLException, Exception {
        insert_review(tenantId, "1", 1L, "Great resource!", 5);
        insert_review(tenantId, "2", 2L, "Not bad", 4);
        insert_review(tenantId, "3", 3L, "Could be better", 3);
        insert_review(tenantId, "someUrlOrProductId", 3L, "Average", 4);

        FSQLQuery.create(conn, "UPDATE reviews SET comment = ?, score = ? WHERE tenant_id = ? AND external_id = ?")
            .bind("Updated text!!", 4, 1L, "someUrlOrProductId")
            .update();
    }



    //

    static void fetchAndPrintRatings(long tenantId) throws Throwable
    {
        List<Review> reviews = FSQLQuery.create(DB.getConnection(),"SELECT id, tenant_id, external_id, external_id_hash, author_id, comment, score FROM reviews WHERE tenant_id = ?")
            .bind(tenantId)
            .fetchAll(Review.class);

        printList("Reviews (" + reviews.size() + ")", reviews);
    }

    static <T> void printList(String title, List<T> elements)
    {
        Logger.log();
        if(title != null && !title.isEmpty()) {
            Logger.log(title);
        }

        for (var e : elements) {
            Logger.log(e);
        }
    }
}