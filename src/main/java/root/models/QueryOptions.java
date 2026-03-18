package root.models;

import java.util.List;

interface QueryOrder{
    String getField();
    boolean getAsc();
}

public interface QueryOptions{
    int getOffset();
    int getLimit();
    List<QueryOrder> getOrder();
}

//

