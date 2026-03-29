package root.database;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static root.common.utils.Preconditions.checkArgument;


/**
 * Helper class for binding arguments to a PreparedStatement. It maintains an internal index that tracks the next
 * parameter position to bind to, allowing for sequential binding of multiple arguments without needing to manually
 * manage the index.
 *
 * Has a fluent API design, allowing for chaining of bind calls. For example:
 */

public class ArgumentBinder {
    private int argumentIndex = 1;
    private final PreparedStatement ps;

    ArgumentBinder(PreparedStatement ps) {
        this(ps, 1);
    }

    ArgumentBinder(PreparedStatement ps, int argumentIndex) {
        this.argumentIndex = argumentIndex;
        this.ps = ps;
    }

    public static ArgumentBinder create(PreparedStatement ps) {
        return new ArgumentBinder(ps, 0);
    }
    public static ArgumentBinder create(PreparedStatement ps, int argumentIndex) {
        return new ArgumentBinder(ps, argumentIndex);
    }

    public int getIndex() {
        return argumentIndex;
    }

    public ArgumentBinder setIndex(int argumentIndex) {
        this.argumentIndex = argumentIndex;
        return this;
    }

    public ArgumentBinder bind(Collection<?> coll) throws SQLException {
        checkArgument(coll != null, "Argument cannot be null");

        int i = argumentIndex;

        for (var arg : coll) {
            if (arg instanceof Object[] arr) {
                for (var val : arr) FSQL.bind(ps, i++, val);
            } else if (arg instanceof int[] arr) {
                for (var val : arr) FSQL.bind(ps, i++, val);
            } else if (arg instanceof long[] arr) {
                for (var val : arr) FSQL.bind(ps, i++, val);
            } else if (arg instanceof List<?> list) {
                for (var val : list) FSQL.bind(ps, i++, val);
            } else if (arg instanceof LinkedHashMap<?, ?> map) {
                for (var val : map.values()) FSQL.bind(ps, i++, val);
            } else if (arg != null && arg.getClass().isArray()) {
                for (int idx = 0, n = Array.getLength(arg); idx < n; idx++) FSQL.bind(ps, i++, Array.get(arg, idx));
            } else {
                FSQL.bind(ps, i++, arg);
            }
        }

        argumentIndex = i;

        return this;
    }

    public ArgumentBinder bind(Map<?, ?> args) throws SQLException {
        checkArgument(args != null, "Argument cannot be null");

        return bind( (Collection<?>) args.values() );
    }

    public ArgumentBinder bind(Object[] args) throws SQLException {
        checkArgument(args != null, "Argument cannot be null");

        bind( (Collection<?>) Arrays.asList(args) );
        return this;
    }

    public ArgumentBinder bind(List<Object> args) throws SQLException {
        checkArgument(args != null, "Argument cannot be null");

        bind( (Collection<?>) args );
        return this;
    }

    public ArgumentBinder bindOdd(Object[] args) throws SQLException {
        return bindNth(args, 1, 2);
    }

    public ArgumentBinder bindEven(Object[] args) throws SQLException {
        return bindNth(args, 0, 2);
    }

    public ArgumentBinder bindNth(Object[] args, int offset, int step) throws SQLException {
        checkArgument(args != null, "Argument cannot be null");

        if(step <= 0 || offset < 0 || offset >= args.length) {
            throw new IllegalArgumentException("Invalid arguments for bindNth: offset=" + offset + ", step=" + step + ", args length=" + ((args == null) ? "null" : args.length));
        }

        for (int i = offset; i < args.length; i += step) {
            FSQL.bind(ps, argumentIndex++, args[i]);
        }
        return this;
    }
}
