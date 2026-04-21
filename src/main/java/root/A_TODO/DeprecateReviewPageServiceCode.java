package root.A_TODO;

public class DeprecateReviewPageServiceCode {
//
//    private static Set<Integer> parseScoreFilter(String scoreFilter) {
//        if (scoreFilter == null || scoreFilter.isBlank()) {
//            return Collections.emptySet();
//        }
//
//        if(scoreFilter.equals("-1") || scoreFilter.equals("0")) {
//            return Collections.emptySet();
//        }
//
//        Set<Integer> scoreFilterSet = new HashSet<>(Utils.parseCsvIntList(scoreFilter));
//        scoreFilterSet.remove(0); // remove 0 if present, as it is not a valid score
//
//        return scoreFilterSet;
//    }
//
//    private static @NotNull ReviewQueryOptions getReviewQueryOptions(Integer orderByEnum, String scoreFilter, PageCursor decodedCursor) {
//        // parse orderByEnum, default to OPTION_ORDER_BY_ID_DESC if not provided or invalid
//        orderByEnum = (orderByEnum != null) ? orderByEnum : ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC;
//
//        // build query options
//        ReviewQueryOptions o = new ReviewQueryOptions();
//        o.setPageCursor(decodedCursor);
//        o.getStatusFilterSet().add(Review.REVIEW_STATUS_APPROVED);
//        o.setOrderByEnum(orderByEnum);
//
//        // add score filter to options if set
//        if (scoreFilter != null && !scoreFilter.isBlank()) {
//            Set<Integer> scoreFilterSet = new HashSet<>(Utils.parseCsvIntList(scoreFilter));
//
//            /*
//            // remove 0 and -1 because of old code (TODO: fix this if score can ever be 0)
//            if(scoreFilterSet.size() == 1 && scoreFilterSet.contains(-1) || scoreFilterSet.contains(0)){
//                throw new RuntimeException("Invalid score filter value: " + scoreFilter);
//                //scoreFilterSet.clear();
//            }
//             */
//
//            o.getScoreFilterSet().addAll(scoreFilterSet);
//        }
//
//        return o;
//    }
//
//    public Map<String, Object> buildReviewListingPage(
//        String externalId,
//        String cursorStr,
//        Integer orderByEnum,
//        String scoreFilter,
//        boolean includeStats
//    ) throws Exception {
//        Map<String, Object> modelMap = new HashMap<>();
//
//        // extract external id
//        externalId = (externalId == null || externalId.isEmpty()) ? AppConfig.DEFAULT_INVALID_EXTERNAL_ID : externalId;
//        modelMap.put("externalId", externalId);
//
//        // parse orderByEnum, default to OPTION_ORDER_BY_ID_DESC if not provided or invalid
//        orderByEnum = (orderByEnum != null) ? orderByEnum : ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC;
//
//        // decode cursor
//        PageCursor decodedCursor = PageCursorEncoder.parseOrDefault(cursorStr, AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS);
//        modelMap.put("pageCursor", decodedCursor.encode());
//
//        // create query options
//        var options = getReviewQueryOptions(orderByEnum, scoreFilter, decodedCursor);
//
//        return buildReviewListingPage(externalId, options, includeStats);
//    }
}
