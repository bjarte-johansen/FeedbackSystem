package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import root.app.ReviewQueryOptions;
import root.includes.ImmutableUnboundedDateRange;
import root.includes.PageCursor;
import root.includes.PageCursorEncoder;
import root.includes.Utils;
import root.includes.logger.Logger;

import java.time.LocalDate;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Class to parse options from request-parameters, used for parameters for listing reviews This class is used in
 * conjunction with #ReviewQueryOptions (name subject to change)
 */

public class ReviewQueryOptionsParser {
    private static boolean LOG_PARSED_OPTIONS = false;

    private static LocalDate parseDateOrDefault(String v, LocalDate def) {
        if (v == null || v.isBlank()) return def;

        try {
            return LocalDate.parse(v.trim()); // ISO: yyyy-MM-dd
        } catch (Exception e) {
            return def;
        }
    }

    private static Integer parseIntOrDefault(String v, Integer def) {
        if (v == null || v.isBlank()) return def;

        try {
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static List<Integer> parseIntListOrEmptyList(String v) {
        if (v == null || v.isBlank()) return Collections.emptyList();

        return root.includes.Utils.parseCsvIntList(v, ",", true);
    }

    private static Set<Integer> parseIntSetOrEmptySet(String v) {
        if (v == null || v.isBlank()) return Collections.emptySet();

        List<Integer> list = root.includes.Utils.parseCsvIntList(v, ",", true);
        return new HashSet<>(list);
    }


    /**
     * Parse the request parameters and return a ReviewQueryOptions object containing the parsed options. The parameters
     * that are parsed include: - cursor: a string representing the pagination cursor, which is parsed into a PageCursor
     * - orderByEnum: an integer representing the sorting option, which is parsed into an Integer - scoreFilter: a
     * string representing a comma-separated list of integers for filtering by score, which is parsed into a
     * Set<Integer> - statusFilter: a string representing a comma-separated list of integers for filtering by status,
     * which is parsed into a Set<Integer> - startDateFilter: a string representing the start date for filtering, which
     * is parsed into a LocalDate - endDateFilter: a string representing the end date for filtering, which is parsed
     * into a LocalDate - numberOfDaysFilter: a string representing the number of days for filtering, which is parsed
     * into an Integer
     * <p>
     * Note: comments might diverge from truth as we are in beta-testing
     *
     * @param req The request servlet to get values from
     * @param defaultCursorLimit paginator default limit if not supplied in request
     * @return ReviewQueryOptions object containing parsed values, if any, or default values where applicable
     */

    public static ReviewQueryOptions parseRequest(
        HttpServletRequest req,
        int defaultCursorLimit
    ) {
        ReviewQueryOptions options = new ReviewQueryOptions();

        //@RequestParam(required = false) String encodedCursor,
        PageCursor cursor = PageCursorEncoder.parseOrDefault(req.getParameter("cursor"), defaultCursorLimit);
        options.setPageCursor(cursor);

        //@RequestParam(required = false) int orderByEnum,

        // because of an earlier bug, we have to check for both "orderBy" and "orderByEnum" parameters to maintain
        // backward compatibility with old clients that might still be sending "orderBy" instead of "orderByEnum"
        String correctOrderByEnumStr = req.getParameter("orderBy");
        if (correctOrderByEnumStr == null) {
            correctOrderByEnumStr = req.getParameter("orderByEnum");
        }

        List<Integer> orderByEnumList = parseIntListOrEmptyList(correctOrderByEnumStr);
        if (orderByEnumList.isEmpty()) {
            orderByEnumList = new ArrayList<>();
            orderByEnumList.add(ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC);
        }
        Integer orderByEnum = orderByEnumList.getFirst();
        options.setOrderByEnum(orderByEnum);

        //@RequestParam(required = false) String scoreFilter,
        String s_scoreFilter = req.getParameter("scoreFilter");
        Set<Integer> scoreSetFilter = parseIntSetOrEmptySet(s_scoreFilter);
        options.getScoreFilterSet().addAll(scoreSetFilter);

        //@RequestParam(required = false) String scoreFilter,
        String s_statusSetFilter = req.getParameter("statusFilter");
        Set<Integer> statusSetFilter = parseIntSetOrEmptySet(s_statusSetFilter);

        checkArgument(!statusSetFilter.contains(-1), "Status filter should never contain -1");
        options.getStatusFilterSet().addAll(statusSetFilter);

        // parse start/end date filter
        //@RequestParam(required = false) LocalDate startDateFilter,
        //@RequestParam(required = false) LocalDate endDateFilter,

        LocalDate startDateFilter = parseDateOrDefault(req.getParameter("startDateFilter"), null);
        LocalDate endDateFilter = parseDateOrDefault(req.getParameter("endDateFilter"), null);

        if (startDateFilter != null || endDateFilter != null) {
            options.setDateFilterRange(new ImmutableUnboundedDateRange<>(startDateFilter, endDateFilter));
        }

        //@RequestParam(required = false) Integer numDaysFilter,
        Integer numberOfDaysFilter = parseIntOrDefault(req.getParameter("numberOfDaysFilter"), null);
        options.setNumberOfDaysFilter(numberOfDaysFilter);

        if(LOG_PARSED_OPTIONS) Logger.log("Parsed options: " + options);

        return options;
    }


    /**
     * Encode the ReviewQueryOptions object into a LinkedHashMap, which can be used for encoding into a cursor or for
     * other purposes. The keys in the map correspond to the fields in the ReviewQueryOptions object, and the values are
     * the corresponding values from the object. This method is useful for converting the options into a format that can
     * be easily serialized or used in other contexts, such as encoding into a pagination cursor for maintaining state
     * across requests.
     *
     * This MUST match what you parse or expect in javascript
     *
     * @param options
     * @return
     */

    public static LinkedHashMap<String, Object> encodeOptions(ReviewQueryOptions options) {
        return Utils.linkedMap(
            "scoreFilter", options.getScoreFilterSet(),
            "statusFilter", options.getStatusFilterSet(),
            "startDateFilter", options.getDateFilterRange() != null ? options.getDateFilterRange().getStart() : null,
            "endDateFilter", options.getDateFilterRange() != null ? options.getDateFilterRange().getEnd() : null,
            "numberOfDaysFilter", options.getNumberOfDaysFilter() != null ? options.getNumberOfDaysFilter() : null,
            "orderByEnum", options.getOrderByEnum()
        );
    }
}
