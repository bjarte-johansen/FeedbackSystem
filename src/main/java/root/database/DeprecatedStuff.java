package root.database;

import static root.database.FSQL.makeArr;

@Deprecated
public class DeprecatedStuff {
    static void oldUpdateOne(int tenantId) throws Exception{
        FSQL.table_update_where(DB.getConnection(), "reviews",
            FSQL.linkedNameValueMap(
                "comment", ("Updated text"),
                "score", (4L)
            ),
            makeArr(
                "tenant_id =", 1L,
                "external_id =", "someUrlOrProductId"
            ));
    }
    static void oldInsertOne(int tenantId) throws Exception{
        FSQL.table_insert_one(DB.getConnection(), "reviews", FSQL.linkedNameValueMap(
            "tenant_id", tenantId,
            "external_id", 14L,
            "author_id", 3L,
            "comment", "Average",
            "score", 4
        ));
    }
}
