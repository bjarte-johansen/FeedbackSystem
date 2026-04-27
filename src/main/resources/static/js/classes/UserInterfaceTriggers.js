class UserInterfaceTriggers{
    static triggerClientStatusFilterChange(select) {
        const $select = $(select);
        const value = Review.utils.parseIntOrIntArrayOr($select.val(), ",", []);
        const empty = (value === null || value.length === 0);
        const text = "Filter: " + $select.find(':selected').text()
            .replace(/\s*\(\d+\)/g, "");

        Review.activeFiltersHelper.toggleFilter("order-by-enum", text, true, !empty);
        UserInterfaceTriggers.#updateFilterValue("setStatusFilter", empty ? [] : value);
        UserInterfaceTriggers.reloadReviewList({resetCursor: true, reloadStats: true});
    }

    static triggerClientOrderByEnumChange(select) {
        const $select = $(select);
        const value = Review.utils.parseIntOr($select.val(), null);
        const empty = (value === null);
        const text = $select.find(':selected').text();

        Review.activeFiltersHelper.toggleFilter("order-by-enum", text, true, !empty);
        UserInterfaceTriggers.#updateFilterValue("setOrderByEnum", empty ? null : Number(value));
        UserInterfaceTriggers.reloadReviewList({resetCursor: true});
    }

    static triggerClientScoreFilterChange(select) {
        const $select = $(select);
        const value = $select.val();
        const text = $select.find(':selected').text();
        const empty = (value == null || value === "");

        Review.activeFiltersHelper.toggleFilter("score-filter-enum", text, true, !empty);
        UserInterfaceTriggers.#updateFilterValue("setScoreFilter", empty ? [] : [Number(value)]);
        UserInterfaceTriggers.reloadReviewList({resetCursor: true});
    }

    static triggerClientScoreFilterPresetChange(newValue) {
        const $reviewList = Review.getReviewListingDomElement(true);
        const $select = $reviewList.find('select[name=scoreFilter]');
        $select.val(newValue);

        UserInterfaceTriggers.triggerClientScoreFilterChange($select);
    }

    static triggerStartDateFilterChange(sender) {
        const dt = Review.utils.parseDateOr($(sender).val(), null);
        UserInterfaceTriggers.#updateFilterValue("setStartDateFilter", dt);
        UserInterfaceTriggers.reloadReviewList({resetCursor: true, reloadStats: true});
    }

    static triggerEndDateFilterChange(sender) {
        const dt = Review.utils.parseDateOr($(sender).val(), null);
        UserInterfaceTriggers.#updateFilterValue("setEndDateFilter", dt);
        UserInterfaceTriggers.reloadReviewList({resetCursor: true, reloadStats: true});
    }

    static triggerNumberOfDaysFilterChange(sender) {
        const days = Review.utils.parseIntOr($(sender).val(), null);
        const text = days !== null ? "Siden: " + $(sender).find(":selected").text() : "";
        const empty = days === null;

        Review.activeFiltersHelper.toggleFilter("number-of-days-filter-enum", text, true, !empty);
        UserInterfaceTriggers.#updateFilterValue("setNumberOfDaysFilter", days);
        UserInterfaceTriggers.reloadReviewList({resetCursor: true, reloadStats: true});
    }

    static #updateFilterValue(setter, value){
        const qo = Review.reviewListing.getQueryOptions();
        qo[setter](value);
    }

    static reloadReviewList(options = {}){
        Review.reloadReviewList(options);
    }
}