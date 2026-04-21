
/*
PS: we no longer pull data from UI
 * Reads the current state of the review list UI (like sorting and filtering options) and updates the corresponding
 * data attributes on the .review--list element. This ensures that when reloadReviewList is called, it uses the
 * latest user-selected options to construct the search parameters for the API call.
 */
/*
updateReviewListOptionsFromUI: function () {
    console.log("Skipping updateReviewListOptionsFromUI, using SSOT");

    const rvl = Review.getReviewListing(true);

    const $reviewList = $(".review--list");
    if (!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

    const $orderByEnumSelect = $reviewList.find("select[name='orderByEnum']");
    if ($orderByEnumSelect.length !== 0) {
        const val = $orderByEnumSelect.val();
        const empty = (val === null || val === "");
        rvl.setOrderByEnum(empty ? null : Number(val));
    }

    const $scoreFilter = $reviewList.find("select[name='scoreFilter']");
    if ($scoreFilter.length !== 0) {
        const val = $scoreFilter.val();
        const empty = (val === null || val === "");
        rvl.setScoreFilter(empty ? [] : [Number(val)]);
    }

    const $numberOfDaysFilter = $reviewList.find("select[name='numberOfDaysFilter']");
    if($numberOfDaysFilter.length !== 0){
        const val = $numberOfDaysFilter.val();
        const empty = (val === null || val === "");
        rvl.setNumberOfDaysFilter(empty ? null : Number(val));
    }
},
 */