package root.app;

public record QueryOrder(String field, boolean asc) {
    @Override
    public String toString() {
        return "QueryOrder{field=" + field() + ", asc=" + (asc() ? " ASC" : " DESC") + "}";
    }
}
