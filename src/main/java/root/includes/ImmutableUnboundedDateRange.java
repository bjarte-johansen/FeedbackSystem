package root.includes;

import java.time.LocalDate;

import static root.common.utils.Preconditions.checkArgument;

public class ImmutableUnboundedDateRange<T extends LocalDate> {
    private final T start;
    private final T end;

    public ImmutableUnboundedDateRange(T start, T end) {
        this.start = start;
        this.end = end;

        if(isBounded()) {
            checkArgument(isOrdered(), "Start date must be before or equal to end date");
        }
    }

    ImmutableUnboundedDateRange<T> withStart(T start) {
        return new ImmutableUnboundedDateRange<>(start, this.end);
    }

    ImmutableUnboundedDateRange<T> withEnd(T end) {
        return new ImmutableUnboundedDateRange<>(this.start, end);
    }

    public T getStart(){
        return start;
    }
    public T getEnd(){
        return end;
    }

    public boolean isOrdered(){
        return start != null && end != null && !start.isAfter(end);
    }

    public boolean isValid(){
        return isOrdered();
    }

    public boolean hasStart(){
        return start != null;
    }

    public boolean hasEnd() {
        return end != null;
    }

    public boolean hasEither(){
        return hasStart() || hasEnd();
    }

    public boolean isBounded(){
        return hasStart() && hasEnd();
    }
}