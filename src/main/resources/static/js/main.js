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

    reloadReview: function(reviewId) {
        console.log("Review.reloadReview called with reviewId:", reviewId);

        const tenantId = $(`.review--list .review[data-review-id="${reviewId}"]`).data("tenant-id");
        console.log("Using tenant-id for AJAX request:", tenantId);
        console.log("Using url", `/api/make-review-html/${tenantId}/${reviewId}`);

        $.ajax({
            url: `/api/make-review-html/${tenantId}/${reviewId}`,
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