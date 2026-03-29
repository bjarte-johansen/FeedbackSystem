package root.models;

import java.util.List;

public interface QueryOptions{
    int getOffset();
    int getLimit();
    List<QueryOrder> getOrder();
}

//

