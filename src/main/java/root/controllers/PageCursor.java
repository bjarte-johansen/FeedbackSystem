package root.controllers;

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
