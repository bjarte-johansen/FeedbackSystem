// import { initClientRoutes, initAdminRoutes } from "./routes.js";

let Review = {
    client: {},
    admin: {},

    reviewListing: null,

    utils: Utils,



    setReviewListing: function (newReviewListing) {
        Review.reviewListing = newReviewListing;
        console.log("ReviewListing class populated with data from element:", Review.reviewListing);
    },
    getReviewListing: function (require = false) {
        if (require && !Review.reviewListing) {
            throw new Error("ReviewListing instance not found in Review.reviewListing");
        }
        // console.log("Reloading review list with options:", Review.reviewListing);
        return Review.reviewListing;
    },

    // oldKey: activeFilterDisplay
    activeFiltersHelper: {
        toggleFilter: function (type, text, replace, forced = true) {
            if(!forced && this.hasFilter(type)){
                this.removeFilter(type);
            }else {
                this.addFilter(type, text, replace);
            }

            this.updateVisibility();
        },

        hasFilter(type){
            const $container = this.getContainer();
            const $items = $container.find('.items');

            return $items.find(`.btn.${type}`).length > 0;
        },

        addFilter: function (type, text, replace) {
            const $reviewList = Review.getReviewListingDomElement(true);
            const $container = $reviewList.find('.active-filters');
            const $items = $container.find(".items");
            if(!$items.length) throw new Error(".items not found");

            if (replace && $items.find(`.btn.${type}`).length) {
                $items.find(`.btn.${type}`).text(text);
            } else {
                const $btn = $container.find('.templates .btn')
                    .clone()
                    .removeClass(".templates")
                    .addClass(type)
                    .text(text);
                $items.append($btn);
            }

            this.updateVisibility($container);
        },

        removeFilter: function(type) {
            const $reviewList = Review.getReviewListingDomElement(true);
            const $container = $reviewList.find('.active-filters');
            const $items = $container.find('.items');

            $items.find(`.btn.${type}`).remove();

            this.updateVisibility($container);
        },

        getContainer(){
            const $reviewList = Review.getReviewListingDomElement(true);
            const $container = $reviewList.find('.active-filters');
            return $container;
        },

        updateVisibility: function($container = null){
            $container = $container || this.getContainer();

            const numFilters = $container.find('.items').children().length;
            if(numFilters > 0){
                $container.removeClass('d-none').show();
            } else {
                $container.find('.items').empty();
                $container.addClass('d-none').hide();
            }
        },

        clear: function () {
            const $reviewList = $(".review--list");
            if (!$reviewList.length) return console.warn("No .review--list element found");

            const $container = $reviewList.find('.active-filters');
            $container.find(".items").empty();
            this.updateVisibility($container);

            $reviewList.find("select[name='scoreFilter']").val("");
            $reviewList.find("select[name='orderByEnum']").val("");
            $reviewList.find("select[name='statusFilterEnum']").val("");
            $reviewList.find("input[name='startDateFilter']").val("");
            $reviewList.find("input[name='endDateFilter']").val("");
            $reviewList.find("select[name='numberOfDaysFilter']").val("");

            const rvlc = Review.getReviewListing(true);
            rvlc.loadFilterSnapshot();
            /*
            rvlc.setOrderByEnum(null);
            rvlc.setStatusFilter([]);
            rvlc.setScoreFilter([]);
            rvlc.setStartDateFilter(null);
            rvlc.setEndDateFilter(null);
            rvlc.setNumberOfDaysFilter(null);

             */

            Review.reloadReviewList({resetCursor: true, reloadStats: true});
        }
    },

    getReviewListingDomElement(required = false) {
        const $reviewList = $('.review--list');
        if (required && $reviewList.length === 0) throw new Error("No .review--list element found");
        return $reviewList;
    },



    /*
    UI triggering stuff
     */

    triggerClientStatusFilterChange(select) {
        const $select = $(select);
        //const value = Review.utils.parseIntOr($select.val(), null);
        const value = Review.utils.parseIntOrIntArrayOr($select.val(), ",", []);
        const empty = value === null || value.length === 0;
        const text = "Filter: " + $select.find(':selected').text();

        Review.activeFiltersHelper.toggleFilter("order-by-enum", text, true, !empty);

        const rc = Review.getReviewListing(true);
        rc.setStatusFilter(empty ? [] : value);

        //Review.updateAdminStatusFilterButtons(rc.getStatusFilter());

        Review.reloadReviewList({resetCursor: true, reloadStats: true});
    },

    triggerClientOrderByEnumChange(select) {
        const $select = $(select);
        const value = Review.utils.parseIntOr($select.val(), null);
        const empty = (value === null);
        const text = /*"Sorter: "*/ $select.find(':selected').text();

        Review.activeFiltersHelper.toggleFilter("order-by-enum", text, true, !empty);

        const rc = Review.getReviewListing(true);
        rc.setOrderByEnum(empty ? null : Number(value));

        Review.reloadReviewList({resetCursor: true});
    },

    triggerClientScoreFilterChange(select) {
        const $select = $(select);
        const value = $select.val();
        const empty = (value === null || value === "" || value === "-1");
        const text = $select.find(':selected').text();
        if($select.val() === "0" || $select.val() === "-1") throw new Error("Value can not be 0 or -1");

        Review.activeFiltersHelper.toggleFilter("score-filter-enum", text, true, !empty);

        const rc = Review.getReviewListing(true);
        rc.setScoreFilter(empty ? [] : [Number(value)]);

        Review.reloadReviewList({resetCursor: true});
    },

    triggerClientScoreFilterPresetChange(newValue) {
        const $reviewList = Review.getReviewListingDomElement(true);
        const $select = $reviewList.find('select[name=scoreFilter]');
        $select.val(newValue);

        this.triggerClientScoreFilterChange($select);
    },

    triggerStartDateFilterChange(sender){
        const dt = Review.utils.parseDateOr($(sender).val(), null);

        // push
        const rc = Review.getReviewListing(true);
        rc.setStartDateFilter(dt);

        Review.reloadReviewList({resetCursor: true, reloadStats: true});
    },

    triggerEndDateFilterChange(sender){
        const dt = Review.utils.parseDateOr($(sender).val(), null);

        const rc = Review.getReviewListing(true);
        rc.setEndDateFilter(dt);

        Review.reloadReviewList({resetCursor: true, reloadStats: true});
    },

    triggerNumberOfDaysFilterChange(sender){
        const days = Review.utils.parseIntOr($(sender).val(), null);

        const rc = Review.getReviewListing(true);
        rc.setNumberOfDaysFilter(days);

        Review.reloadReviewList({resetCursor: true, reloadStats: true});
    },



    /**
     * Fetches the updated review list HTML from the server based on the current options set in the data attributes of
     * the .review--list element. It constructs the API URL with the appropriate query parameters, makes an AJAX GET
     * request, * and upon success, replaces the existing review list items container with the new HTML. It also handles
     * updating the previous filtered review count and resetting the cursor if necessary when filters change.
     *
     * The method will automatically limit cursor to bounds
     */

    reloadReviewList: function (options = {resetCursor: false, reloadStats: false}) {
        //Review.updateReviewListOptionsFromUI();

        const rvl = Review.getReviewListing(true);

        if (options?.resetCursor/* || (rvl.getCursor().isOutOfBounds(rvl.getUnpaginatedFilteredReviewCount()))*/) {
            rvl.setCursor(rvl.getCursor().withReset());
        }

        console.log("clamping cursor to max offset", rvl.getCursor(), rvl.getAccumulatedReviewCountByScoreFilter());
        const cursor = rvl.getCursor().withClamp(rvl.getAccumulatedReviewCountByScoreFilter());
        rvl.setCursor(cursor);
        console.log("new cursor", rvl.getCursor());



        const reloadStats = options?.reloadStats ?? false;
        if(reloadStats) rvl.setIncludeStatsOnce(reloadStats);

        // create url with search params
        const searchParams = rvl.buildQuerySearchParams();
        const url = "/api/reviews/list/json?" + searchParams.toString();

        // fetch
        Spinner.with(async () => {
            console.log("Reloading list, url", url, JSON.stringify(searchParams, null, 2));

            const json = await $.ajax({
                url: url,
                method: "GET",
                dataType: "json"
            });

            rvl.loadItemsFromJson(json);
            console.log("Replace old content with content from json");

            if(reloadStats && json?.statistics) {
                rvl.setStatistics(json.statistics);

                // initialize stats renderer, uses review listing state
                const statsRenderer = new StatisticsRenderer();
                statsRenderer.updateFromJson(rvl.getStatistics());

                console.log("refreshed statistics")
            }
        });
    },


    /**
     * reload a single review element by its ID. This is used after actions that affect a specific review
     * (like liking, disliking, or changing its status) to fetch the updated HTML for that review and replace the
     * existing review element in the DOM.
     */

    reloadReview(reviewId) {
        console.log("Review.reloadReview called with reviewId:", reviewId);

        Spinner.with(async () => {
            return $.ajax({
                url: `/api/review/${reviewId}/json`, dataType: "json", method: "GET", success: function (json) {
                    if (json === null) return;   // should catch errors here

                    const $newReview = Review.getReviewListing(true).renderItem(json);
                    const $oldReview = $(`.review--list .review-item-${reviewId}`);
                    if ($oldReview.length) $oldReview.replaceWith($newReview);
                }
            });
        });
    },


    /**
     * Calculates the next page cursor based on the current cursor and review count, then updates the data-cursor
     * attribute on the .review--list element and calls reloadReviewList to fetch the next set of reviews. It ensures
     * that the cursor does not exceed the maximum offset, which would indicate an invalid page.
     *
     * @param pageDelta
     */

    nextReviewListPage: function (pageDelta = 1) {
        pageDelta = pageDelta || 1;

        // get review listing instance
        const rvl = Review.getReviewListing(true);

        // advance the cursor by the given offset, ensuring it does not exceed the maximum offset
        // note that it will get clamped by reloading review listing, so we dont do it here
        const prevCursor = rvl.getCursor();
        const newCursor = prevCursor.withAdvance(prevCursor.limit * pageDelta);
        rvl.setCursor(newCursor);

        Review.reloadReviewList();
    },


    /**
     * Calculates the previous page cursor based on the current cursor and review count, then updates the data-cursor
     * attribute on the .review--list element and calls reloadReviewList to fetch the previous set of reviews. It ensures
     * that the cursor does not go below 0, which would indicate an invalid page.
     */

    prevReviewListPage: function () {
        Review.nextReviewListPage(-1);
    },

    initRoutes(){
        const router = new Router();

        initClientRoutes(router);
        initAdminRoutes(router);

        router.start();
    },
/*
    updateAdminStatusFilterButtons(statusFilterArry){
        $statusFilterButtons = $(".admin-filter-by-status-buttons a");
        if(statusFilterArry?.length > 0){
            const currentStatusNames = statusFilterArry?.map(Constants.getReviewStatusFriendlyCssName);
            console.log("status filters", currentStatusNames);

            $statusFilterButtons.each((i, el) => {
                const $el = $(el);
                let hit = false;
                for(const statusName of currentStatusNames) {
                    if ($el.hasClass("filter-" + statusName)) {
                        $el.addClass("disabled");
                        $el.attr("old-href", $el.attr("href"));
                        $el.attr("href", "javascript:void(0);");
                        hit = true;
                    }
                }
                if(!hit){
                    if($el.attr("old-href") !== ""){
                        $el.attr("href", $el.attr("old-href"));
                    }
                }
            });
        }
    },
 */

    initClient(state) {
        try {
            console.log("initClient() called");

            if ((state?.isAdministrator !== true)) {
                //$orderBySelect.
                console.log("TODO: hide status filter");

                const $statusFilterSelect = $(".review--list select[name='statusFilterEnum']").closest(".filter-component");
                $statusFilterSelect.hide();
            }

            /*
            check if listing is enabled for this page, if not, we will not initialize the review listing
             */

            if (!(state?.reviewConfig?.enableListing ?? true)) {
                // listing disabled for this page
                console.log("Warning: review listing has been disabled for this page");
                console.log("client-review.js loaded");
                return;
            }

            /*
            create review listing object and load settings from json + render + render stats
             */

            const $orderBySelect = $(".review--list select[name='orderByEnum']");
            for (const key in Constants.ORDER_BY_OPTIONS) {
                const value = Constants.ORDER_BY_OPTIONS[key];
                $orderBySelect.append(`<option value="${value}">${key}</option>`);
            }

            // initialize review listing
            const rl = new ReviewListing();
            rl.loadAllFromJson(state);

            // save snapshot of filters we loaded from json
            rl.saveFilterSnapshot();
            console.log("filter snapshop", rl.getFilterSnapshot());

            Review.setReviewListing(rl);

            // initialize stats renderer, uses review listing state
            const statsRenderer = new StatisticsRenderer();
            statsRenderer.updateFromJson(rl.getStatistics());

            $(".container--reviews").removeClass("d-none");
        }finally{
            console.log("initClient() OK");
        }
    },

    initAdmin(state){
        console.log("initAdmin() called");

        //this.updateAdminStatusFilterButtons(state?.filters?.statusFilter);
        // code here

        console.log("initAdmin() OK");
    },

    _isInitialised: 0,

    initReviewJs() {
        if (++this._isInitialised > 1) return;

        /*
        parse JSON from review--listing / html element (has json set in an attribute)
         */

        const getJsonFromDocument = () => {
            const $reviewList = $(".review--list");
            if ($reviewList.length === 0) throw new Error("Unable to find root review list element");

            const raw = $reviewList.attr("data-json");
            if (raw === null || raw === "") throw new Error("data-json attribute was null or empty");

            const state = JSON.parse(raw ?? "");
            if (state === null || state === "") throw new Error("parsed json was null or empty");

            return state;
        }

        // get json from document
        const state = getJsonFromDocument();

        console.log(state?.isAdministrator, "isAdministrator");

        //console.log("parsed json", JSON.stringify(state, null, 2));
        //state.reviewConfig.enableListing = false;

        Review.initClient(state);
        Review.initAdmin(state);

        // initialize routes
        Review.initRoutes();

        /***********************************************************/

        document.addEventListener("submit", async function (e) {
            const form = e.target.closest("form.form--submit-review-form");
            if(!form.length) throw new Error("Could not find new review form element from event target");

            Spinner.with(async () => {
                e.preventDefault();

                await fetch(form.action, {
                    method: form.method,
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    body: $(form).serialize()
                }).then(() => {
                    form.remove();

                    alert("Din omtale har blitt lagt til");

                    Review.reloadReviewList({resetCursor: true, reloadStats: true});
                });
            });
        });

        console.log("client-review.js OK");
    }
};

document.addEventListener("DOMContentLoaded", function () {
    Review.initReviewJs();
});

if(document.readyState === "complete"){
    Review.initReviewJs();
}


// TODO: refactor this function, used with "Legg til ny omtale" button, to be more generic and reusable for toggling
//  any form or element, not just the review form. It should also be renamed to reflect its more general purpose.

Review.client.showNewReviewForm = function() {
    const rvl = Review.getReviewListing(true);
    if (!rvl.getReviewListingConfig().enableSubmit) {
        alert("Lagring av omtale er midlertidig slått av for denne siden");
        return;
    }



    // open
    function openDialog() {
        $("#dlg").addClass("show").text("Hello world");
        $("body").css("overflow", "hidden"); // prevent background scroll
        console.log("dialog opened");
    }

    // close
    function closeDialog() {
        $("#dlg").removeClass("show");
        $("body").css("overflow", "");
    }

    //openDialog();

    // events
    $(document).on("click", ".dlg-close", closeDialog);

    // optional: close on background click
    $(document).on("click", "#dlg", function(e){
        if (e.target === this) closeDialog();
    });


    const searchParams = new URLSearchParams();
    searchParams.set("externalId", Review.getReviewListing().getExternalId());
    searchParams.set("prefilled", 1);

    console.log("loading", "/api/new-review-form/create?" + searchParams.toString());

    Spinner.with(() => {
        return $.ajax({
            url: "/api/new-review-form/create?" + searchParams.toString(), method: "GET", success: function (html) {
                const $form = $(html);

                const $html = $(`
    <div id="dlg" className="dlg">
        <div className="dlg-content">
            <button className="dlg-close">×</button>
            <div class="content"></div>
        </div>
    </div>`);
                $html.find(".content").append($form);
                openDialog();

                $('body').append(html);

                const $formContainer = $('.submit-review-form-container').empty();
                $formContainer.append($form);
                $form.removeClass("d-none");
            }
        });
    });
}