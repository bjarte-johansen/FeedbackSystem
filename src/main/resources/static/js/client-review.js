let Review = {
    reviewListing: null,
    utils: Utils,
    activeFiltersHelper: new UiReviewListingFilter(),   // filters, score/number of days/date range/etc
    client: {},
    clientTriggers: UserInterfaceTriggers,  // UI triggers, methods that can be called when select/input/etc changes
    admin: {
        // replace counts for admin interface status filter (approved (1), rejected (3) etc)
        replaceAdministratorFilterStatusCounts(statusCounts ){
            const $statusFilterEnum = $(".review--list select[name='statusFilterEnum']");
            $statusFilterEnum.find("option").each((i, el) => {
                const $el = $(el);
                if ($el.text().includes("(")) {
                    const status = Utils.parseIntOr($el.attr("value"));
                    const count = statusCounts[status];
                    $el.text(`${Constants.getReviewStatusFriendlyDisplayName(status)} (${count})`);
                }
            });
        }
    },



    /*

     */

    getReviewListingDomElement(required = false) {
        const $reviewList = $('.review--list');
        if (required && $reviewList.length === 0) throw new Error("No .review--list element found");

        return $reviewList;
    },






    /**
     * Fetches the updated review list HTML from the server based on the current options set in the data attributes of
     * the .review--list element. It constructs the API URL with the appropriate query parameters, makes an AJAX GET
     * request, * and upon success, replaces the existing review list items container with the new HTML. It also handles
     * updating the previous filtered review count and resetting the cursor if necessary when filters change.
     *
     * The method will automatically limit cursor to bounds
     *
     * TODO: should get if we are administrator from elsewhere of course
     */

    reloadReviewList: function (options = {resetCursor: false, reloadStats: false, isAdministrator: false, append: false}) {
        const defaultOptions = {
            resetCursor: false,
            reloadStats: false,
            isAdministrator: false,
            append: false
        };
        options = Object.assign({}, defaultOptions, options ?? {});

        const rvl = Review.reviewListing;
        const qo = rvl.getQueryOptions();

        if (options.resetCursor) {
            // reset cursor?
            qo.setCursor(qo.getCursor().withReset());
        }else {
            // limit cursor
            const cursor = qo.getCursor().withClamp(rvl.getAccumulatedReviewCountByScoreFilter());
            qo.setCursor(cursor);
        }

        // check reload stats
        if(options.reloadStats) rvl.setIncludeStatsOnce(true);

        // create url with search params
        const searchParams = rvl.buildQuerySearchParams();
        const url = "/api/reviews/list/json?" + searchParams.toString();

        Spinner.with(async () => {
            console.log("Reloading list, url", url, JSON.stringify(searchParams, null, 2));

            const res = await $.ajax({ url: url, method: "GET", dataType: "json" });
            if(!res) throw new Error("Error fetching reviews");
            if(!res.reviews) throw new Error("Response reviews not found");

            // check if a review allready exists in list ..
            const bItemAlreadyExists = options.append && ($(".review--list .items .review-item-" + res.reviews?.[0]?.id).length > 0);
            if(!bItemAlreadyExists) ReviewListingRenderer.renderItems(res.reviews, options.append);

            // update counts
            const bUpdateStatusFilterCounts = options.isAdministrator && res.countByReviewStatus;
            if(bUpdateStatusFilterCounts) Review.admin.replaceAdministratorFilterStatusCounts(res.countByReviewStatus);

            // render stats
            if(res.statistics) StatisticsRenderer.updateFromJson(res.statistics);
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

                    const $newReview = ReviewListingRenderer.renderItem(json);
                    const $oldReview = $(`.review--list .review-item-${reviewId}`);
                    if ($oldReview.length) $oldReview.replaceWith($newReview);
                }
            });
        });
    },


    /* pagination */

    advancePage: function(pageDelta, mode){
        mode = mode || "append";

        // advance the cursor by the given offset, ensuring it does not exceed the maximum offset
        // note that it will get clamped by reloading review listing, so we dont do it here
        const qo = Review.reviewListing.getQueryOptions();
        const newCursor = qo.getCursor().withAdvancePage(pageDelta || 1);
        qo.setCursor(newCursor);

        Review.reloadReviewList({append: mode === "append"});
    },


    /*
    initialization
     */

    initRoutes(){
        const router = new Router();

        initClientRoutes(router);
        initAdminRoutes(router);

        router.start();
    },


    initClient(state) {
        /*
        init order by options
         */

        const $orderBySelect = $(".review--list select[name='orderByEnum']");
        for (const [key, value] of Object.entries(Constants.ORDER_BY_OPTIONS)) {
            $orderBySelect.append(`<option value="${value}">${key}</option>`);
        }


        /*
        init from data embedded in html
         */


        /*
        check if submission is on for this page, otherwise inject style to hide it
         */
        if(!(state?.reviewConfig?.enableSubmit ?? true)) {
            document.head.insertAdjacentHTML(
                "beforeend",
                `<style>.add-new-review-anchor{display:none;}</style>`
            );
        }

        /*
        check if listing is enabled for this page, if not, we will not initialize the review listing
         */

        const pageEnableListing = state?.reviewConfig?.enableListing ?? true;
        //const tenantEnableListing = state?.tenantConfig?.enableListing ?? true


        if (!pageEnableListing) {
            // listing disabled for this page
            console.log("Warning: review listing has been disabled for this page");
            console.log("client-review.js loaded");

            Review.reviewListing = new ReviewListing();
            return false;
        }


        /*
        create review listing object and load settings from json + render + render stats
         */

        // initialize review listing
        Review.reviewListing = new ReviewListing();
        Review.reviewListing.loadAllFromJson(state);

        // save snapshot of filters we loaded from json
        Review.reviewListing.saveFilterSnapshot();

        // initialize stats renderer, uses review listing state
        const stats = Review.reviewListing.getStatistics();
        StatisticsRenderer.updateFromJson(stats);

        $(".container--reviews").removeClass("d-none");

        return true;
    },

    initAdmin(state){
        console.log("initAdmin() called");

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

        // init client
        if(!Review.initClient(state))
            return;

        if(state?.isAdministrator){
            Review.initAdmin(state);
        }

        // initialize routes
        Review.initRoutes();

        // TODO: add submit for these, should probably load from routes, and have them save to routes
        //const $formEditPagePermits = Review.reviewListing.find(".form--edit-page-permits");
        //const $formEditTenantPermits = Review.reviewListing.find(".form--edit-tenant-permits");


        /*
        register handler for submitting new review form ("ny omtale")
         */

        document.addEventListener("submit", async function (e) {
            const form = e.target.closest("form.form--submit-review-form");
            if(!form.length) throw new Error("Could not find new review form element from event target");

            if(!NewReviewFormValidator.validate(form)){
                e.preventDefault();
                return;
            }

            Spinner.with(async () => {
                e.preventDefault();

                if(!(await Async.fetchOk(form.action, Async.options("POST", "form", form)))) {
                    alert("Det skjedde en feil ved innsending av omtalen, prøv igjen senere");
                    return;
                }

                form.remove();
                Review.reloadReviewList({resetCursor: true, reloadStats: true});

                alert("Din omtale har blitt lagt til, takk!");
            });
        });
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

Review.client.checkReviewSubmitEnabled = function(){
    const canSubmit = Review.reviewListing.getReviewListingConfig().enableSubmit;
    if (!canSubmit) alert("Lagring av omtale er midlertidig slått av for denne siden");

    return canSubmit;
}

Review.client.showNewReviewForm = function() {
    // early exit if submission is disabled
    if (!Review.client.checkReviewSubmitEnabled()) {
        alert("Lagring av omtale er midlertidig slått av for denne siden");
        return ;
    }

    const searchParams = new URLSearchParams();
    searchParams.set("externalId", Review.reviewListing.getExternalId());
    searchParams.set("prefilled", 1);

    console.log("loading", "/api/new-review-form/create?" + searchParams.toString());

    Spinner.with(() => {
        return $.ajax({
            url: "/api/new-review-form/create?" + searchParams.toString(), method: "GET", success: function (html) {
                const $form = $(html);

                const $formContainer = $('.submit-review-form-container').empty();
                $formContainer.append($form);
                $form.removeClass("d-none");

                // clearing validity on input, rewritten by chatgpt
                // we want to clear them on keyboard input
                const clearValidity = (e) => {
                    const el = e.target;
                    if (!(el instanceof HTMLInputElement ||
                        el instanceof HTMLTextAreaElement ||
                        el instanceof HTMLSelectElement)) return;

                    el.setCustomValidity("");
                };

                // clear errors when typing
                $form[0].addEventListener("input", clearValidity);
                $form[0].addEventListener("change", clearValidity);
            }
        });
    });
}