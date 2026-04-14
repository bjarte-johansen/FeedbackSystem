/*
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
*/


var Review = {
    utils: {
        clamp: function (x, min, max) {
            return Math.min(max, Math.max(min, x));
        },

        parseIntOr: function (v, def = 0) {
            if (v == null) return def;

            if (typeof v === "number")
                return Number.isInteger(v) ? v : def;

            if (typeof v === "string") {
                const s = v.trim();
                if (!/^-?\d+$/.test(s)) return def; // only base-10 ints
                return Number(s);
            }

            return def;
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
        },

        dashedToCamel: function (s) {
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
        },

        /**
         * parse value has been written by chatGPT, it tries to parse a string value into a boolean, number, or
         * leaves it as a string if it cannot be parsed. It handles trimming whitespace, case-insensitive boolean
         * parsing, and strict number parsing that only accepts valid finite numbers. This is useful for converting
         * string values from data attributes or user input into their appropriate types for easier handling in the
         * code.
         */

        parsePrimitive: function (v) {
            if (typeof v !== "string") return v;

            const s = v.trim();
            if (s === "") return v;

            // boolean
            if (s === "true" || s === "TRUE") return true;
            if (s === "false" || s === "FALSE") return false;

            // number (strict)
            const n = Number(s);
            if (!Number.isFinite(n)) return v;

            // int vs float
            return Number.isInteger(n) ? n : n;
        },


        /**
         * creates an immutable page cursor object with the given offset and limit. The returned object has methods to
         * advance the cursor by a given delta (number of pages) while ensuring it does not exceed a maximum offset, and
         * to serialize the cursor to a CSV string format. This is used for managing pagination state when fetching review
         * lists from the API.
         */

        createPageCursorFromString: function (cursorStr) {
            const parts = (cursorStr || "0,9007199254740991").split(",");

            const offset = Review.utils.parseIntOr(parts?.[0], 0);
            const limit = Review.utils.parseIntOr(parts?.[1], Number.MAX_SAFE_INTEGER);

            console.log("Creating page cursor from string:", cursorStr, "parsed values:", parts);

            return new PageCursor(offset, limit);
        }
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

            Review.reloadReviewList({resetCursor: true});
        }
    },

    reviewListing: null,

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

    activeFilterDisplay: {
        addFilterButton: function (type, text, replace) {
            const $reviewList = Review.getReviewListingDomElement(true);
            const $container = $reviewList.find('.active-filters');

            if (replace && $container.find(`.btn.${type}`).length) {
                $container.find(`.btn.${type}`).text(text);
            } else {
                const $btn = $container.find('.templates .btn').clone();
                $btn.addClass(type).text(text);
                $container.find('.items').append($btn);
            }
            $container.removeClass('d-none').show();
        },
        clear: function () {
            const $reviewList = Review.getReviewListingDomElement(true);
            const $container = $reviewList.find('.active-filters');

            $container.find('.items').empty();
            $container.addClass('d-none').hide();
        }
    },

    getReviewListingDomElement(required = false) {
        const $reviewList = $('.review--list');
        if (required && $reviewList.length === 0) throw new Error("No .review--list element found");
        return $reviewList;
    },

    triggerClientOrderByEnumChange(select) {
        const text = $(select).find(':selected').text();
        Review.activeFilterDisplay.addFilterButton('order-by-enum', "Sorter: " + text, true);

        Review.reloadReviewList({resetCursor: true});
    },

    triggerClientScoreFilterChange(select) {
        const text = $(select).find(':selected').text();
        Review.activeFilterDisplay.addFilterButton('score-filter-enum', text, true);

        Review.reloadReviewList({resetCursor: true});
    },


    /**
     * This function is triggered when a user selects a preset score filter (e.g., "5 stars", "4 stars and above")
     * from the UI. It reads the integral score value from the data attribute of the clicked element, updates the
     * score filter select element in the review list with this value, triggers a change event to update the UI,
     * and then calls reloadReviewList to fetch and display the reviews that match the selected score filter preset.
     *
     * @param sender
     */
    triggerClientScoreFilterPresetChange(sender) {
        const newValue = Number($(sender).attr('data-integral-score-attr'));

        const $reviewList = Review.getReviewListingDomElement(true);
        const $select = $reviewList.find('select[name=scoreFilter]');
        $select.val(newValue);

        Review.reloadReviewList({resetCursor: true})
    },


    /**
     * Constructs a URLSearchParams object based on the data attributes of the given $reviewList element. This is
     * used to build the query parameters for the API call when reloading the review list. It checks for attributes
     * like data-external-id, data-order-by-enum, data-score-filter, and data-cursor, and includes them in the search
     * parameters if they are present.
     *
     * @param $reviewList
     * @param options
     * @returns {URLSearchParams}
     */

    __getReviewListOptionAsMap($reviewList, options = {exclude: null}) {
        if (!$reviewList || $reviewList.length === 0) {
            console.log("No $reviewList element provided or found");
            return new Map();
        }

        const params = new Map();

        const el = $reviewList.get(0);
        if (!el) throw new Error("$reviewList[0] element not found");

        // gets data-attr from element and dash-to-camel case convert the key, then sets it in the params map. If the
        // value looks like a JSON array or object, it tries to parse it before setting it.

        const getDataAttr = (el, camelKey, dashedKey) => el.dataset[camelKey] ?? el.getAttribute(dashedKey);

        const setDataAttr = function (dashedKey) {
            const camelKey = Review.utils.dashedToCamel(dashedKey)
            const value = getDataAttr(el, camelKey, dashedKey); //$reviewList.attr("data-" + dashed_key); // jQuery attr returns string or undefined ALWAYS
            if (value === null || value === undefined) return;

            const ch = value?.trim()?.[0];
            if (ch === "[" || ch === "{") {
                try {
                    return params.set(camelKey, JSON.parse(value));
                } catch (e) {
                }
            }

            params.set(camelKey, value);
        };

        const keys = ["external-id", "order-by-enum", "score-filter", "cursor", "review-count", "detailed-review-count", "json"];
        keys.forEach(setDataAttr);

        if (options?.exclude !== null) {
            const excludeSet = new Set(Array.isArray(options.exclude) ? options.exclude : [options.exclude]);
            for (let key of excludeSet) {
                params.delete(Review.utils.dashedToCamel(key));
            }
        }

        console.log("Extracted review list options from data attributes:", Object.fromEntries(params));

        return params;
    },


    /**
     * Reads the current state of the review list UI (like sorting and filtering options) and updates the corresponding
     * data attributes on the .review--list element. This ensures that when reloadReviewList is called, it uses the
     * latest user-selected options to construct the search parameters for the API call.
     */

    updateReviewListOptionsFromUI: function () {
        const rvl = Review.getReviewListing(true);

        const $reviewList = $(".review--list");
        if (!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const $orderByEnumSelect = $reviewList.find("select[name='orderByEnum']");
        if ($orderByEnumSelect.length !== 0) rvl.orderByEnum = Number($orderByEnumSelect.val());

        const $scoreFilter = $reviewList.find("select[name='scoreFilter']");
        if ($scoreFilter.length !== 0) rvl.scoreFilter = [Number($scoreFilter.val())];
    },


    /**
     * Fetches the updated review list HTML from the server based on the current options set in the data attributes of
     * the .review--list element. It constructs the API URL with the appropriate query parameters, makes an AJAX GET
     * request, * and upon success, replaces the existing review list items container with the new HTML. It also handles
     * updating the previous filtered review count and resetting the cursor if necessary when filters change.
     *
     * The method will automatically limit cursor to bounds
     */

    reloadReviewList: function (options = {resetCursor: false}) {
        Review.updateReviewListOptionsFromUI();

        const rvl = Review.getReviewListing(true);

        if (options?.resetCursor || (rvl.cursor.isOutOfBounds(rvl.getAccumulatedReviewCountByScoreFilter()))) {
            rvl.cursor = rvl.cursor.withReset();
        }

        // create url with search params
        const searchParams = rvl.buildQuerySearchParams();
        const url = "/api/reviews/build-html?" + searchParams.toString();

        console.log("Reloading review list with URL:", url);

        // fetch
        $.ajax({
            url: url, method: "GET", success: function (html) {
                const $newReviewListItemsContainer = $(html);
                const $oldReviewListItemsContainer = $(`.review--list .review--list-items`);
                if (!$oldReviewListItemsContainer.length) return console.warn(`Old review list container not found.`);

                $oldReviewListItemsContainer.replaceWith($newReviewListItemsContainer);
                console.log('-----------------------------------------------------------');
            }, error: function (xhr, status, error) {
                console.error(`Failed to reload review list:`, status, error);
            }
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
        rvl.cursor = rvl.cursor
            .withAdvance(rvl.cursor.limit * pageDelta)
            .withClamp(rvl.getAccumulatedReviewCountByScoreFilter());

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
                const $oldReview = $(`.review--list .review[data-review-id="${reviewId}"]`);
                if ($oldReview.length) {
                    $oldReview.replaceWith($newReview);
                }
                console.log('-----------------------------------------------------------');
            }, error: function (xhr, status, error) {
                console.error(`Failed to reload review with ID ${reviewId}:`, status, error);
            }
        });
    },

    /**
     * handler are called with form,res IF form is tagged with class "ajax" and have a data-handler attribute matching
     * the handler name. This allows for custom handling of form submissions without needing to write separate event
     * listeners for each form. The handlers can perform actions like reloading a specific review or updating the
     * review list based on the response from the server after a form submission.
     */
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


class ReviewListing {
    externalId = null;
    orderByEnum = -1;
    scoreFilter = -1;
    cursor = new PageCursor(0, Number.MAX_SAFE_INTEGER);
    totalReviewCount = 0;
    detailedReviewCount = {};

    constructor() {
    }

    /*
    loadNextPage() {
        Review.nextReviewListPage(1);
        return this;
    }

    loadPreviousPage() {
        Review.prevReviewListPage();
        return this;
    }
    */

    getExternalId(){
        return this.externalId;
    }
    getOrderByEnum(){
        return this.orderByEnum;
    }
    getScoreFilter(){
        return this.scoreFilter;
    }
    getCursor(){
        return this.cursor;
    }
    getTotalReviewCount(){
        return this.totalReviewCount;
    }

    #getReviewCountByScore(score) {
        return this.detailedReviewCount?.[score] || this.totalReviewCount;
    }

    getAccumulatedReviewCountByScoreFilter() {
        const filteredCount = this.scoreFilter?.reduce(
            (acc, score) => acc + this.#getReviewCountByScore(score),
            0);
        return filteredCount || this.totalReviewCount;
    }

    populateFromOptionsMap(opts) {
        this.externalId = opts.get("externalId");
        this.orderByEnum = Number(opts.get("orderByEnum"));
        this.scoreFilter = opts.get("scoreFilter")?.split(",").map(s => Number(s)).filter(n => !isNaN(n)) || [-1];
        this.cursor = Review.utils.createPageCursorFromString(opts.get("cursor"));
        this.totalReviewCount = Number(opts.get("reviewCount"));
        this.detailedReviewCount = opts.get("detailedReviewCount");
        this.import = opts.get("json");

        return this;
    }

    buildQuerySearchParams() {
        const map = new Map();
        map.set("externalId", this.getExternalId());
        map.set("orderByEnum", this.getOrderByEnum());
        map.set("scoreFilter", this.getScoreFilter().join(","));
        map.set("cursor", this.cursor.toCsv());

        return new URLSearchParams(Object.fromEntries(map));
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const rl = new ReviewListing();
    const $reviewList = Review.getReviewListingDomElement(false);

    rl.populateFromOptionsMap(Review.__getReviewListOptionAsMap($reviewList));
    Review.setReviewListing(rl);

    document.addEventListener("submit", async e => {
        const form = e.target.closest("form");
        if (!form) return;

        if (form.matches(".ajax") && form.matches(".custom-handler")) {
            e.preventDefault();

            const __parsed = form.dataset.cmd?.split(":");
            const cmd = __parsed?.[0];
            const args = (__parsed?.[1]?.split(',') || []).map(Review.utils.parsePrimitive);
            //for (var a in args) args[a] = Review.utils.parsePrimitive(args[a]);

            //if(debug) console.log("cmd", cmd, "args", args);

            switch (cmd) {
                case "addStatusFilter": {
                    const statusEnum = Review.utils.parseIntOr(args[0], -1);
                    const params = new URLSearchParams();
                    const rvl = Review.getReviewListing(true);
                    params.set("statusFilter", statusEnum);

                    window.location.href = window.location.pathname + "?" + params.toString();
                    return;
                }
            }

            console.log("custom handler called");
        }
    });

    document.addEventListener("submit", async e => {
        const form = e.target.closest("form");
        if (!form) return;

        if (!form.matches(".ajax")) return;
        e.preventDefault();

        if (form.matches(".custom-handler")) {
            return false;
        }

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