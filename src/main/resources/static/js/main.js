let Utils = {
    requireNonNull: function (value, name = "value") {
        if (value == null) { // catches null AND undefined
            throw new Error(`${name} must not be null`);
        }
        return value;
    }, validateInputElement(el) {
        el.setCustomValidity("");
        if (!el.checkValidity()) {
            el.reportValidity();
            return false;
        }
        return true;
    }
}

var Review = {
    utils: {

        /*
        // deprecated in favor of loading the entire review element(s)
        incrementElementTextBy: function (el, delta) {
            const $el = $(el);
            const iOldValue = parseInt($el.text()) || 0;
            $el.text(iOldValue + Number(delta));
        }
         */
    },

    triggerClientOrderByEnumChange() {
        Review.reloadReviewList();
    },

    triggerClientScoreFilterChange() {
        Review.reloadReviewList();
    },


    /**
     * Constructs a URLSearchParams object based on the data attributes of the given $reviewList element. This is
     * used to build the query parameters for the API call when reloading the review list. It checks for attributes
     * like data-external-id, data-order-by-enum, data-score-filter, and data-cursor, and includes them in the search
     * parameters if they are present.
     * @param $reviewList
     * @returns {URLSearchParams}
     */

    buildSearchParamsFromReviewList($reviewList) {
        const params = new URLSearchParams();

        const externalId = $reviewList.attr("data-external-id");
        if (externalId !== undefined) {
            params.set("externalId", externalId);
        }

        const orderByEnum = $reviewList.attr("data-order-by-enum");
        if (orderByEnum !== undefined) {
            params.set("orderByEnum", orderByEnum);
        }

        const scoreFilter = $reviewList.attr("data-score-filter");
        if (scoreFilter !== undefined) {
            params.set("scoreFilter", scoreFilter);
        }

        const cursor = $reviewList.attr("data-cursor");
        if (cursor !== undefined) {
            params.set("cursor", cursor);
        }

        return params;
    },


    /**
     * Reads the current state of the review list UI (like sorting and filtering options) and updates the corresponding
     * data attributes on the .review--list element. This ensures that when reloadReviewList is called, it uses the
     * latest user-selected options to construct the search parameters for the API call.
     */

    updateReviewListFromUI() {
        const $reviewList = $(".review--list");
        if (!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const $orderByEnumSelect = $(".review--list select[name='orderByEnum']");
        if ($orderByEnumSelect.length !== 0) {
            const orderByEnumValue = parseInt($orderByEnumSelect.val());
            $reviewList.attr("data-order-by-enum", orderByEnumValue);
        }

        const $scoreFilter = $(".review--list select[name='scoreFilter']");
        if ($scoreFilter.length !== 0) {
            const filterValue = $scoreFilter.val();
            $reviewList.attr("data-score-filter", filterValue);
        }
    },

    reloadReviewList: function () {
        Review.updateReviewListFromUI();

        const $reviewList = $(".review--list");
        if (!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const params = Review.buildSearchParamsFromReviewList($reviewList);

        const url = new URL(window.location.href);
        params.forEach((v, k) => url.searchParams.set(k, v));

        console.log("loading: " + url.toString());
        window.location.href = url.toString();
    },


    /**
     * Calculates the next page cursor based on the current cursor and review count, then updates the data-cursor
     * attribute on the .review--list element and calls reloadReviewList to fetch the next set of reviews. It ensures
     * that the cursor does not exceed the total review count, which would indicate an invalid page.
     * The offset parameter allows for moving multiple pages at once (e.g., offset=2 to skip ahead two pages).
     * @param offset
     */

    nextReviewListPage: function (offset = 1) {
        offset = offset || 1;

        const $reviewList = $(".review--list");
        if (!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const reviewCount = parseInt($reviewList.attr("data-review-count")) || 0;

        const currentCursorArr = ($reviewList.attr("data-cursor") || "0," + Number.MAX_SAFE_INTEGER).split(",");
        currentCursorArr[0] = parseInt(currentCursorArr[0]) || 0;
        currentCursorArr[1] = parseInt(currentCursorArr[1]) || Number.MAX_SAFE_INTEGER;
        currentCursorArr[0] = Math.max(0, Math.min(reviewCount - 1, currentCursorArr[0] + (currentCursorArr[1] * offset)));
        $reviewList.attr("data-cursor", currentCursorArr.join(","));
        console.log("new cursor", currentCursorArr[0], currentCursorArr[1]);

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


    /**
     * reload a single review element by its ID. This is used after actions that affect a specific review
     * (like liking, disliking, or changing its status) to fetch the updated HTML for that review and replace the
     * existing review element in the DOM.
     */

    reloadReview: function (reviewId) {
        console.log("Review.reloadReview called with reviewId:", reviewId);

        $.ajax({
            url: `/api/review/build-html/${reviewId}`, method: "GET", success: function (html) {
                const $newReview = $(html);
                const $oldReview = $(`.review--list .review[data-review-id="${reviewId}"]`);
                if ($oldReview.length) {
                    $oldReview.replaceWith($newReview);
                    console.log(`Review with ID ${reviewId} reloaded successfully.`);
                } else {
                    console.warn(`Old review element with ID ${reviewId} not found for replacement.`);
                }
                console.log('-----------------------------------------------------------');
            }, error: function (xhr, status, error) {
                console.error(`Failed to reload review with ID ${reviewId}:`, status, error);
            }
        });
    },

    handlers: {

        // like/dislike events
        likeReviewDone: function (form, res) {
            console.log("Review.formHandlers.likeReviewDone called");
            if (res.status !== 200) return;

            const reviewId = $(form).closest(".review").data("review-id");
            Review.reloadReview(reviewId);
        }, dislikeReviewDone: function (form, res) {
            console.log("Review.formHandlers.dislikeReviewDone called");
            if (res.status !== 200) return;

            const reviewId = $(form).closest(".review").data("review-id");
            Review.reloadReview(reviewId);
        },

        // review management events
        deleteReviewDone: function (form, res) {
            console.log("Review.formHandlers.deleteReviewDone called");
        }, submitReviewDone: function (form, res) {
            console.log("Review.formHandlers.submitReviewDone called");
        },

        // status marking events
        markApprovedReviewDone: function (form, res) {
            if (res.status !== 200) return;

            console.log("Review.formHandlers.markApprovedReviewDone called");
        }, markRejectedReviewDone: function (form, res) {
            if (res.status !== 200) return;

            console.log("Review.formHandlers.markRejectedReviewDone called");
        }, markPendingReviewDone: function (form, res) {
            if (res.status !== 200) return;

            console.log("Review.formHandlers.markPendingReviewDone called");
        }
    },

    invokeHandler: function (name, form, res) {
        const handler = this.handlers[name];
        if (typeof handler === "function") {
            handler(form, res);
            return;
        }

        console.warn(`No form handler found for name: ${name}`);
    }
};

document.addEventListener("DOMContentLoaded", function () {
    document.addEventListener("submit", async e => {
        const form = e.target.closest("form");
        if (!form) return;

        if (!form.matches(".ajax")) return;
        e.preventDefault();

        const res = await fetch(form.action, {
            method: (form.method || "POST").toUpperCase(), body: new FormData(form)
        });

        const key = "[FormPoster]";

        // status
        console.log(key, res.status, res.ok, res.statusText);

        const ct = res.headers.get("content-type") || "";
        let text = null;
        if (ct.includes("text") || ct.includes("html")) {
            text = await res.clone().text();
            if (text && text.length > 0) {
                console.log(key, "text:", text);
            }
        }

        if (!res.ok) {
            console.warn(key, "Form submission failed", "HTTP error", res.status);
            return;
        }

        console.log(key, "Form submission succeeded");

        if (form.matches(".reload-on-success")) {
            location.reload();
            return;
        }

        if (form.dataset.handler) {
            const fn = Review.handlers[form.dataset.handler];
            if (fn) fn(form, res);
        }
    });

    console.log("main.js loaded");
});



// TODO: refactor this function, used with "Legg til ny omtale" button, to be more generic and reusable for toggling
//  any form or element, not just the review form. It should also be renamed to reflect its more general purpose.

function toggleReviewForm() {
    $(".form--submit-review-form")
        .toggle()
        .removeClass("d-none");
    return false;
}