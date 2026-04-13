package root.includes;

public class ImmutableRange <T extends Comparable<T>> {
    private final T start;
    private final T end;

    public ImmutableRange(T start, T end) {
        this.start = start;
        this.end = end;

        if(isBounded() && start.compareTo(end) >= 0) {
            throw new IllegalArgumentException("Start must be less than end");
        }
    }

    public T getStart() {
        return start;
    }
    public T getEnd() {
        return end;
    }

    public boolean hasStart() {
        return start != null;
    }

    public boolean hasEnd(){
        return end != null;
    }

    public boolean isStrictlyOrdered(){
        return isBounded() && start.compareTo(end) < 0;
    }

    public boolean isWeaklyOrdered(){
        return isBounded() && start.compareTo(end) <= 0;
    }

    public boolean isBounded(){
        return hasStart() && hasEnd();
    }

    public boolean isUnbounded(){
        return !hasStart() && !hasEnd();
    }

    public boolean isPartiallyBounded() {
        return hasStart() || hasEnd();
    }
}
