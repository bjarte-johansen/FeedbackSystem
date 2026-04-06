let Utils = {
    requireNonNull: function(value, name = "value") {
        if (value == null) { // catches null AND undefined
            throw new Error(`${name} must not be null`);
        }
        return value;
    },
    validateInputElement(el) {
        el.setCustomValidity("");
        if(!el.checkValidity()) {
            el.reportValidity();
            return false;
        }
        return true;
    }
}

var Review = {
    utils: {
        incrementElementTextBy: function(el, delta) {
            const $el = $(el);
            const iOldValue = parseInt($el.text()) || 0;
            $el.text(iOldValue + Number(delta));
        }
    },

    triggerClientOrderByEnumChange(select) {
        const $reviewList = $(select).closest(".review--list");
        if(!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const val = parseInt($(select).val());
        $reviewList.attr("data-order-by-enum", val);

        Review.reloadReviewList();
    },

    triggerClientScoreFilterChange(select){
        const $reviewList = $(select).closest(".review--list");
        if(!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const val = parseInt($(select).val());
        $reviewList.attr("data-score-filter", val);

        Review.reloadReviewList();
    },

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

        /*
        const scoreFilterMin = $reviewList.attr("score-filter-min");
        const scoreFilterMax = $reviewList.attr("score-filter-max");
        if (scoreFilterMin !== undefined && scoreFilterMax !== undefined) {
            params.set("scoreFilterMin", scoreFilterMin);
            params.set("scoreFilterMax", scoreFilterMax);
        }
        */

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

    updateReviewListFromUI(){
        const $reviewList = $(".review--list");
        if(!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const $orderByEnumSelect = $(".review--list select[name='orderByEnum']");
        if($orderByEnumSelect.length !== 0) {
            const orderByEnumValue = parseInt($orderByEnumSelect.val());
            $reviewList.attr("data-order-by-enum", orderByEnumValue);
        }

        const $scoreFilter = $(".review--list select[name='scoreFilter']");
        if($scoreFilter.length !== 0) {
            const filterValue = $scoreFilter.val();
            //$reviewList.attr("data-score-filter-min", filterValue);
            //$reviewList.attr("data-score-filter-max", filterValue);
            $reviewList.attr("data-score-filter", filterValue);
        }
    },

    reloadReviewList: function(){
        Review.updateReviewListFromUI();

        const $reviewList = $(".review--list");
        const params = Review.buildSearchParamsFromReviewList($reviewList);

        const url = new URL(window.location.href);
        params.forEach((v, k) => url.searchParams.set(k, v));

        console.log("loading: " + url.toString());
        window.location.href = url.toString();
    },

    nextReviewListPage: function(offset = 1){
        offset = offset || 1;

        const $reviewList = $(".review--list");
        if(!$reviewList.length) return console.warn("No .review--list element found for reloadReviewList");

        const reviewCount = parseInt($reviewList.attr("data-review-count")) || 0;

        const currentCursorArr = ($reviewList.attr("data-cursor") || "0," + Number.MAX_SAFE_INTEGER).split(",");
        currentCursorArr[0] = parseInt(currentCursorArr[0]) || 0;
        currentCursorArr[1] = parseInt(currentCursorArr[1]) || Number.MAX_SAFE_INTEGER;
        currentCursorArr[0] = Math.max(0, Math.min(reviewCount - 1, currentCursorArr[0] + (currentCursorArr[1] * offset)));
        $reviewList.attr("data-cursor", currentCursorArr.join(","));
        console.log("new cursor", currentCursorArr[0], currentCursorArr[1]);

        Review.reloadReviewList();
    },
    prevReviewListPage: function(){
        Review.nextReviewListPage(-1);
    },

    reloadReview: function(reviewId) {
        console.log("Review.reloadReview called with reviewId:", reviewId);

        $.ajax({
            url: `/api/make-review-html/${reviewId}`,
            method: "GET",
            success: function(html) {
                const $newReview = $(html);
                const $oldReview = $(`.review--list .review[data-review-id="${reviewId}"]`);
                if ($oldReview.length) {
                    $oldReview.replaceWith($newReview);
                    console.log(`Review with ID ${reviewId} reloaded successfully.`);
                } else {
                    console.warn(`Old review element with ID ${reviewId} not found for replacement.`);
                }
                console.log('-----------------------------------------------------------');
            },
            error: function(xhr, status, error) {
                console.error(`Failed to reload review with ID ${reviewId}:`, status, error);
            }
        });
    },

    handlers: {

        // like/dislike events
        likeReviewDone: function(form, res){
            console.log("Review.formHandlers.likeReviewDone called");

            Review.reloadReview($(form).closest(".review").data("review-id"));
        },
        dislikeReviewDone: function(form, res) {
            console.log("Review.formHandlers.dislikeReviewDone called");

            Review.reloadReview($(form).closest(".review").data("review-id"));
        },

        // review management events
        deleteReviewDone: function(form, res) {
            console.log("Review.formHandlers.deleteReviewDone called");
        },
        submitReviewDone: function(form, res){
            console.log("Review.formHandlers.submitReviewDone called");
        },

        // status marking events
        markApprovedReviewDone: function(form, res){
            if(res.status !== 200) return;

            console.log("Review.formHandlers.markApprovedReviewDone called");
        },
        markRejectedReviewDone: function(form, res){
            if(res.status !== 200) return;

            console.log("Review.formHandlers.markRejectedReviewDone called");
        },
        markPendingReviewDone: function(form, res){
            if(res.status !== 200) return;

            console.log("Review.formHandlers.markPendingReviewDone called");
        }
    },

    invokeHandler: function(name, form, res){
        const handler = this.handlers[name];
        if (typeof handler === "function") {
            handler(form, res);
            return;
        }

        console.warn(`No form handler found for name: ${name}`);
    }
};

document.addEventListener("DOMContentLoaded", function() {
    document.addEventListener("submit", async e => {
        const form = e.target.closest("form");
        if(!form) return;

        if (!form.matches(".ajax")) return;
        e.preventDefault();

        const res = await fetch(form.action, {
            method: (form.method || "POST").toUpperCase(),
            body: new FormData(form)
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

        if(!res.ok) {
            console.warn(key, "Form submission failed", "HTTP error", res.status);
            return;
        }

        console.log(key, "Form submission succeeded");

        if(form.matches(".reload-on-success")) {
            location.reload();
            return;
        }

        if (form.dataset.handler){
            const fn = Review.handlers[form.dataset.handler];
            if(fn) fn(form, res);
        }
    });

    console.log("main.js loaded");
});