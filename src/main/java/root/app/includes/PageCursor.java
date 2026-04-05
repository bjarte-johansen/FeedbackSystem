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
    public PageCursor next(int maxOffset) {
        return new PageCursor(Math.min(maxOffset, this.offset + this.limit), this.limit);
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