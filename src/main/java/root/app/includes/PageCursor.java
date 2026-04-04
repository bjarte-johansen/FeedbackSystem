package root.app.includes;

public class PageCursor {
    //private static final boolean NORMALIZE_LIMIT = true;

    private int offset;
    private int limit;

    public PageCursor() {
        this(0, Integer.MAX_VALUE);
    }
    public PageCursor(int offset, int limit) {
        this.setOffset(offset);
        this.setLimit(limit);
    }

    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public PageCursor next() {
        return new PageCursor(this.offset + this.limit, this.limit);
    }
    public PageCursor previous() {
        int newOffset = Math.max(0, this.offset - this.limit);
        return new PageCursor(newOffset, this.limit);
    }

    public String buildLimitOffsetSql() {
        String s = "";
        if(limit > 0) {
            s += "LIMIT " + this.limit;
        }
        if(offset > 0) {
            if(!s.isEmpty()) s += " ";
            s += "OFFSET " + this.offset;
        }
        return s;
    }

    @Override
    public String toString() {
        return "PageCursor{" +
            "offset=" + offset +
            ", limit=" + limit +
            '}';
    }
}

/*
public class PageCursor {
    private long firstId;
    private long lastId;
    private int limit;
    private int direction;

    PageCursor() {}
    PageCursor(int firstId, int lastId, int direction, int limit) {
        this.firstId = firstId;
        this.lastId = lastId;
        this.direction = direction;
        this.limit = limit;
    }
    PageCursor(Long firstId, Long lastId, int direction, int limit) {
        this.firstId = firstId;
        this.lastId = lastId;
        this.direction = direction;
        this.limit = limit;
    }

    public long getLastId() {
        return lastId;
    }
    public void setLastId(long id) {
        this.lastId = id;
    }

    public long getFirstId() {
        return firstId;
    }
    public void setFirstId(long id) {
        this.firstId = id;
    }

    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }

    @Override
    public String toString() {
        return "PageCursor{" +
                "firstId=" + firstId +
                ", lastId=" + lastId +
                ", direction=" + direction +
                '}';
    }
}
*/