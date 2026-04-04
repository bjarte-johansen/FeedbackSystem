package root.app;

import root.app.includes.PageCursor;
import root.models.Review;

public class ReviewQueryOptions {
    public static final int OPTION_ORDER_BY_ID_ASC = 1;
    public static final int OPTION_ORDER_BY_ID_DESC = 2;
    public static final int OPTION_ORDER_BY_CREATED_AT_ASC = 3;
    public static final int OPTION_ORDER_BY_CREATED_AT_DESC = 4;
    public static final int OPTION_ORDER_BY_PENDING_FIRST = 5;
    public static final int OPTION_ORDER_BY_APPROVED_FIRST = 6;
    public static final int OPTION_ORDER_BY_REJECTED_FIRST = 7;

    private PageCursor pageCursor;
    private int orderByEnum;
    private int statusEnum;

    public ReviewQueryOptions() {
        this(new PageCursor(), -1, -1);
    }

    public ReviewQueryOptions(PageCursor pageCursor, Integer statusEnum, Integer orderByEnum) {
        this.pageCursor = pageCursor;
        this.statusEnum = statusEnum;
        this.orderByEnum = orderByEnum;
    }

    /*
    public int getOffset(){ return offset; }
    public void setOffset(int offset){ this.offset = offset; }

    public int getLimit(){ return limit; }
    public void setLimit(int limit){ this.limit = limit; }
    */

    public PageCursor getPageCursor() { return pageCursor; }
    public void setPageCursor(PageCursor pageCursor) { this.pageCursor = pageCursor; }

    public int getOrderByEnum(){ return orderByEnum; }
    public void setOrderByEnum(int orderByEnum){ this.orderByEnum = orderByEnum; }

    public int getStatusEnum(){ return statusEnum; }
    public void setStatusEnum(int statusEnum){ this.statusEnum = statusEnum; }

    public String buildLimitOffsetSql() {
        return pageCursor.buildLimitOffsetSql();
    }

    public String buildOrderBySql() {
        // TODO: by status should by id DESC?
        switch (orderByEnum) {
            case OPTION_ORDER_BY_ID_ASC:
                return "id ASC";
            case OPTION_ORDER_BY_ID_DESC:
                return "id DESC";
            case OPTION_ORDER_BY_CREATED_AT_ASC:
                return "createdAt ASC";
            case OPTION_ORDER_BY_CREATED_AT_DESC:
                return "createdAt DESC";
            case OPTION_ORDER_BY_PENDING_FIRST:
                return "status = " + Review.REVIEW_STATUS_PENDING + " DESC, id ASC";
            case OPTION_ORDER_BY_APPROVED_FIRST:
                return "status = " + Review.REVIEW_STATUS_APPROVED + " DESC, id ASC";
            case OPTION_ORDER_BY_REJECTED_FIRST:
                return "status = " + Review.REVIEW_STATUS_REJECTED + " DESC, id ASC";
            default:
                return "id ASC"; // default order
        }
    }

    @Override
    public String toString(){
        return "ReviewQueryOptions{" +
            "cursor=" + pageCursor.toString() +
            ", orderByEnum=" + orderByEnum +
            ", statusEnum=" + statusEnum +
            '}';
    }
}
