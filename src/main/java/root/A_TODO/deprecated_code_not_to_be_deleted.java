package root.A_TODO;

public class deprecated_code_not_to_be_deleted {
    /*
    lambda helper methods
    deprecated, no longer used since we do client side rendering
     */

    /*
    // add a simple function to format double values to 2 decimals for display in JSP
    public static Function<Double, String> dblFormatRoundToHalfDotToDash = (v) -> {
        v = Math.round(v * 2.0) / 2.0;
        String s = String.format(Locale.US, "%.1f", v);
        return s.replace(".", "-");
    };

    public static Function<Double, String> dblFormatWithSingleDecimal = (v) -> String.format(Locale.US, "%.1f", v);
    //private static Function<Double, String> DOUBLE_FORMATTER_2 = (v) -> String.format(Locale.US, "%.1f", v);

    // add a simple function to format double values to 2 decimals for display in JSP
    public static Function<Instant, String> dateFormatter = v -> {
        if (v == null) return "";

        return DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault())
            .format(v);
    };

    // add a simple function to format Instant values to "days ago" format for display in JSP
    public static Function<Instant, String> daysAgoFormatter = v -> {
        if (v == null) return "";

        long days = ChronoUnit.DAYS.between(v, Instant.now());
        return String.valueOf(Math.max(0, days));
    };
    */


    /***************************************************************************/





    /*
    old code for using jsp, we do not delete it
     */

//    /**
//     * API endpoint to generate HTML for a list of reviews based on the given filters. This is used for the "Load more"
//     * functionality on the frontend, where the frontend can call this endpoint with the appropriate filters and
//     * pagination cursor to get the next page of reviews as HTML to append to the existing list. This allows us to reuse
//     * the same HTML rendering logic for both the initial page load and subsequent "Load more" requests, ensuring
//     * consistency in the review display and reducing code duplication.
//     *
//     * @param externalId
//     * @param encodedCursor
//     * @param orderByEnum
//     * @param scoreFilter
//     * @param model
//     * @param req
//     * @return
//     * @throws Exception
//     */
//
//    @GetMapping("/api/reviews/build-html")
//    public String renderReviewsAsHtml(
//        @RequestParam String externalId,
//        @RequestParam(name = "cursor", defaultValue = "") String encodedCursor,
//        @RequestParam(name = "orderByEnum", defaultValue = ("" + ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC)) int orderByEnum,
//        @RequestParam(name = "scoreFilter", defaultValue = "-1") String scoreFilter,
//        Model model,
//        HttpServletRequest req
//    ) throws Exception {
//        Map<String, Object> vm = reviewPageService.buildReviewListingPage(
//            externalId, encodedCursor, orderByEnum, scoreFilter, // filters
//            req,
//            false // do not include stats
//        );
//
//        model.addAllAttributes(vm);
//
//        return "client/pretty-review-list.partial";
//    }


//    /**
//     * API endpoint to generate HTML for a single review by id.
//     *
//     * @param reviewId
//     * @param model
//     * @return
//     * @throws Exception
//     */
//
//    @GetMapping("/api/review/{reviewId}/build-html")
//    @Deprecated
//    public String makeReviewHtml(@PathVariable long reviewId, Model model) throws Exception {
//        Review review = reviewRepo.findById(reviewId).orElse(null);
//        if (review == null) return "error";
//
//        model.addAttribute("review", review);
//
//        // add formatters to model for display in JSP
//        //model.addAttribute("daysAgoFormatter", ReviewPageService.daysAgoFormatter);
//
//        return "client/pretty-review.partial";
//    }
}
