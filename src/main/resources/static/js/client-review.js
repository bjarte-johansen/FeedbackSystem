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
        clamp: function (x, min, max) {
            return Math.min(max, Math.max(min, x));
        },

        parseIntOr: function (a, def = 0) {
            const n = parseInt(a, 10);
            return Number.isNaN(n) ? def : n;
        },

        snakeToCamel: function (s) {
            let out = '', up = false;
            for (let i = 0; i < s.length; i++) {
                const ch = s[i];
                if (ch === '_') {
                    up = true;
                } else {
                    out += up ? ch.toUpperCase() : ch;
                    up = false;
                }
            }
            return out;
        }, dashedToCamel: function (s) {
            let out = '', up = false;
            for (let i = 0; i < s.length; i++) {
                const ch = s[i];
                if (ch === '-') {
                    up = true;
                } else {
                    out += up ? ch.toUpperCase() : ch;
                    up = false;
                }
            }
            return out;
        }, /*
                isNil: function (value) {
                    return value === null || value === undefined;
                },
                notNil: function (value) {
                    return !Review.utils.isNil(value);
                }
        */
        /**
         * creates an immutable page cursor object with the given offset and limit. The returned object has methods to
         * advance the cursor by a given delta (number of pages) while ensuring it does not exceed a maximum offset, and
         * to serialize the cursor to a CSV string format. This is used for managing pagination state when fetching review
         * lists from the API.
         */

        createPageCursor: function (offset, limit) {
            if (offset < 0) throw new Error("Offset must be non-negative");

            return {
                "offset": offset, "limit": limit,

                reset: function () {
                    return Review.utils.createPageCursor(0, this.limit);
                }, advance: function (pageDelta, maxOffset = Number.MAX_SAFE_INTEGER) {
                    let newOffset = Review.utils.clamp(this.offset + pageDelta * this.limit, 0, maxOffset);
                    newOffset = Math.floor(newOffset / this.limit) * this.limit; // ensure offset is always a multiple of limit
                    return Review.utils.createPageCursor(newOffset, this.limit);
                },

                toCsv: function () {
                    return this.offset + "," + this.limit;
                },

                toString: function () {
                    return this.toCsv();
                }
            };
        },

        createPageCursorFromString: function (cursorStr, pageDelta, maxOffset) {
            const cursorArr = (cursorStr || ("0," + Number.MAX_SAFE_INTEGER)).split(",");
            cursorArr[0] = Review.utils.parseIntOr(cursorArr?.[0] ?? "0", 0);
            cursorArr[1] = Review.utils.parseIntOr(cursorArr?.[1] ?? "" + Number.MAX_SAFE_INTEGER, Number.MAX_SAFE_INTEGER);

            console.log("Creating page cursor from string:", cursorStr, "parsed values:", cursorArr);

            let cursor = Review.utils.createPageCursor(cursorArr[0], cursorArr[1]);

            if ((pageDelta !== null && pageDelta !== undefined) && (maxOffset !== null && maxOffset !== undefined)) {
                cursor = cursor.advance(pageDelta, maxOffset);
                console.log("advancing cursor with offsetDelta:", cursor.toString());
            }
            return cursor;
        },
    },

    client: {
        resetReviewListFilters: function () {
            const $reviewList = $(".review--list");
            if (!$reviewList.length) return console.warn("No .review--list element found");

            const $activeFilters = $reviewList.find(".active-filters");

            $activeFilters.find(".items").empty();

            $reviewList.find("select[name='scoreFilter']").val(-1);
            $reviewList.find("select[name='orderByEnum']").val($reviewList.attr("data-order-by-enum") || -1);
            $activeFilters.addClass('d-none');

            console.log("orderByEnum", $reviewList.attr("data-order-by-enum"));

            Review.reloadReviewList();
        }
    },


    triggerClientOrderByEnumChange(select) {
        const $reviewList = $('.review--list');
        if ($reviewList.length === 0) return console.warn("No .review--list element found");

        const $select = $(select);
        const text = $select.find(':selected').text();

        const $activeFilters = $reviewList.find('.active-filters');
        $activeFilters.find('.order-by-enum').remove();

        const $clone = $activeFilters.find('.templates .btn').clone();
        $clone.removeClass('d-none')
            .addClass('order-by-enum')
            .text("Sorter: " + text);

        //$reviewList.find('.active-filters').find('.items .btn').remove();
        $activeFilters.find('.items').append($clone);
        $activeFilters.removeClass('d-none').show();

        Review.reloadReviewList();
    },

    triggerClientScoreFilterChange() {
        // TODO: update page cursor to reset to first page if order by enum is changed, otherwise it may cause invalid page offsets

        Review.reloadReviewList();
    },


    /**
     * Constructs a URLSearchParams object based on the data attributes of the given $reviewList element. This is
     * used to build the query parameters for the API call when reloading the review list. It checks for attributes
     * like data-external-id, data-order-by-enum, data-score-filter, and data-cursor, and includes them in the search
     * parameters if they are present.
     *
     * @param $reviewList
     * @returns {URLSearchParams}
     */

    getReviewListOptionAsMap($reviewList) {
        const params = new Map();

        // gets data-attr from element and dash-to-camel case convert the key, then sets it in the params map. If the
        // value looks like a JSON array or object, it tries to parse it before setting it.
        const setDataAttr = function (attrName) {
            let value = $reviewList.attr("data-" + attrName); // jQuery attr returns string or undefined ALWAYS
            if (value === undefined) return;

            const camelName = Review.utils.dashedToCamel(attrName)
            const ch = value.trim()?.[0];
            if (ch === "[" || ch === "{") {
                try { return params.set(camelName, JSON.parse(value)); } catch (e) {}
            }

            params.set(camelName, value);
        };

        const keys = ["external-id", "order-by-enum", "score-filter", "cursor", "review-count", "detailed-review-count"];
        keys.forEach(setDataAttr);

        //console.log("Extracted review list options from data attributes:", Object.fromEntries(params));

        return params;
    },


    /**
     * Returns the count of reviews that have the specified score. It first checks if the review list element exists,
     * then it retrieves the review list options from the data attributes (or uses the provided map) to access the
     * detailed review count for the given score. If the score count is not available, it defaults to 0.
     *
     * @param score
     * @param reviewListOptionMap
     * @returns {number|number|number|void}
     */

    getReviewCountByScore(score, reviewListOptionMap) {
        const $reviewList = $(".review--list");
        if (!$reviewList.length) return 0;

        reviewListOptionMap = reviewListOptionMap || Review.getReviewListOptionAsMap($reviewList);
        if (reviewListOptionMap === undefined) return console.error("Failed to get review list options from review list");

        return Review.utils.parseIntOr(reviewListOptionMap.get("detailedReviewCount")?.["" + score], 0);
    },


    /**
     * Reads the current state of the review list UI (like sorting and filtering options) and updates the corresponding
     * data attributes on the .review--list element. This ensures that when reloadReviewList is called, it uses the
     * latest user-selected options to construct the search parameters for the API call.
     */

    updateReviewListOptionsFromUI: function () {
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


    /**
     * Fetches the updated review list HTML from the server based on the current options set in the data attributes of
     * the .review--list element. It constructs the API URL with the appropriate query parameters, makes an AJAX GET
     * request, * and upon success, replaces the existing review list items container with the new HTML. It also handles
     * updating the previous filtered review count and resetting the cursor if necessary when filters change.
     */

    reloadReviewList: function () {
        Review.updateReviewListOptionsFromUI();

        const $reviewList = $(".review--list");
        if (!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const reviewListOptions = Review.getReviewListOptionAsMap($reviewList);

        // limit cursor if necessary to ensure it is within the bounds of the available reviews
        const currentFilteredReviewCount = Review.getReviewCountByScore(reviewListOptions.get("scoreFilter"), reviewListOptions);
        const previousFilteredReviewCount = Review.utils.parseIntOr($reviewList.attr("data-previous-filtered-review-count"), -1);
        const totalReviewCount = Review.utils.parseIntOr(reviewListOptions.get("reviewCount"), 0);

        if (previousFilteredReviewCount !== currentFilteredReviewCount) {
            if ((previousFilteredReviewCount !== -1) && (currentFilteredReviewCount < totalReviewCount)) {
                let cursor = Review.utils.createPageCursorFromString(reviewListOptions.get("cursor"));
                cursor = cursor.reset();
                reviewListOptions.set("cursor", cursor.toString());
                $reviewList.attr("data-cursor", cursor.toString());
            }

            $reviewList.attr("data-previous-filtered-review-count", currentFilteredReviewCount);
        }

        // create url with search params
        const url = "/api/reviews/build-html?" + (new URLSearchParams(reviewListOptions)).toString();
        console.log("loading: " + url);

        // fetch
        $.ajax({
            url: url, method: "GET", success: function (html) {
                const $newReviewListItemsContainer = $(html);
                const $oldReviewListItemsContainer = $(`.review--list .review--list-items`);
                if ($oldReviewListItemsContainer.length) {
                    $oldReviewListItemsContainer.replaceWith($newReviewListItemsContainer);
                    console.log(`New review list container loaded successfully`);
                } else {
                    console.warn(`Old review list container not found.`);
                }
                console.log('-----------------------------------------------------------');
            }, error: function (xhr, status, error) {
                console.error(`Failed to reload review list:`, status, error);
            }
        });
    },


    /**
     * Calculates the next page cursor based on the current cursor and review count, then updates the data-cursor
     * attribute on the .review--list element and calls reloadReviewList to fetch the next set of reviews. It ensures
     * that the cursor does not exceed the maximum offset, which would indicate an invalid page. The pageDelta
     * parameter allows advancing by multiple pages at once (e.g., for "Next 5 pages" functionality), and defaults to 1
     * for normal "Next page" behavior.
     *
     * @param pageDelta
     */

    nextReviewListPage: function (pageDelta = 1) {
        const fnGetScoreFilterAsIntArray = function () {
            const scoreFilterStr = $reviewList.attr("data-score-filter");
            if (!scoreFilterStr) return [];

            return scoreFilterStr.split(",").map(s => parseInt(s)).filter(n => !isNaN(n));
        }


        pageDelta = pageDelta || 1;

        const $reviewList = $(".review--list");
        if (!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        // fetch options as map
        const reviewListOptions = Review.getReviewListOptionAsMap($reviewList);

        // get the total review count, but if score filters are applied, we need to calculate the accumulated count of reviews
        let maxPageOffset = Review.utils.parseIntOr($reviewList.attr("data-review-count"), 0);
        let accumulatedReviewCount = Number.MAX_SAFE_INTEGER;

        // if there are score filters applied, we need to calculate the accumulated count of reviews that match those filters
        if (reviewListOptions.get("scoreFilter") !== "-1") {
            const scoreFilterArr = fnGetScoreFilterAsIntArray();
            accumulatedReviewCount = scoreFilterArr.length > 0 ? scoreFilterArr.reduce((acc, score) => acc + Review.getReviewCountByScore(score, reviewListOptions), 0) : accumulatedReviewCount;
            maxPageOffset = accumulatedReviewCount > 0 ? accumulatedReviewCount : maxPageOffset;
        }

        //console.log("maxPageOffset", maxPageOffset, accumulatedReviewCount);

        // advance the cursor by the given offset, ensuring it does not exceed the maximum offset
        let cursor = Review.utils.createPageCursorFromString($reviewList.attr("data-cursor"));
        cursor = cursor.advance(pageDelta, maxPageOffset - 1);
        $reviewList.attr("data-cursor", cursor.toString());

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
            url: `/api/review/${reviewId}/build-html`, method: "GET", success: function (html) {
                const $newReview = $(html);
                //console.log("new html: ", html);
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

    console.log("client-review.js loaded");
});


// TODO: refactor this function, used with "Legg til ny omtale" button, to be more generic and reusable for toggling
//  any form or element, not just the review form. It should also be renamed to reflect its more general purpose.

function toggleReviewForm() {
    $(".form--submit-review-form")
        .toggle()
        .removeClass("d-none");
    return false;
}