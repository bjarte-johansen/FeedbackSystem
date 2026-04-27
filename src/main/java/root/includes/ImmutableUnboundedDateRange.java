package root.includes;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Immutable data range class. Just a holder for start and end dates.
 * TODO: it does not make sense to do T extends LocalDate as LocalDate is final
 *
 * @param <T>
 */

public class ImmutableUnboundedDateRange<T extends LocalDate> {
    private final T start;
    private final T end;


    /**
     * public constructor, checks if start and end are ordered if both are not null
     *
     * @param start
     * @param end
     */

    public ImmutableUnboundedDateRange(T start, T end) {
        this.start = start;
        this.end = end;

        if(isBounded()) {
            checkArgument(isOrdered(), "Start date must be before or equal to end date when set");
        }
    }

    /** return new range with new start and same end */
    public ImmutableUnboundedDateRange<T> withStart(T start) { return new ImmutableUnboundedDateRange<>(start, this.end); }

    /** return new range with same start and new end */
    public ImmutableUnboundedDateRange<T> withEnd(T end) { return new ImmutableUnboundedDateRange<>(this.start, end); }

    /** @return get start */
    public T getStart(){
        return start;
    }

    /** @return get end */
    public T getEnd(){
        return end;
    }

    /** @return true if start <= end */
    public boolean isOrdered(){
        return (start != null) && (end != null) && !start.isAfter(end);
    }

    /** @return true if ordered */
    public boolean isValid(){
        return isOrdered();
    }

    /** @return true if start != null */
    public boolean hasStart(){
        return start != null;
    }

    /** @return true if end != null */
    public boolean hasEnd() {
        return end != null;
    }

    /** @return true if either start or end is not null */
    public boolean hasAnyBound(){
        return hasStart() || hasEnd();
    }

    /** @return true if both start and end is not null */
    public boolean isBounded(){
        return hasStart() && hasEnd();
    }
}