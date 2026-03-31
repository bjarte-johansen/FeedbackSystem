package root.app;

import java.util.ArrayList;
import java.util.List;

public class QueryOptions{
    private int paginatorOffset;
    private int paginatorLimit;
    private ArrayList<QueryOrder> order = new ArrayList<>();

    int getPaginatorOffset(){
        return paginatorOffset;
    };
    void setPaginatorOffset(int offset){
        this.paginatorOffset = offset;
    };

    int getPaginatorLimit(){
        return paginatorLimit;
    };
    void setPaginatorLimit(int limit){
        this.paginatorLimit = limit;
    };

    List<QueryOrder> getOrder(){
        return order;
    };
    void addOrder(QueryOrder order){
        this.order.add(order);
    };
}

//

