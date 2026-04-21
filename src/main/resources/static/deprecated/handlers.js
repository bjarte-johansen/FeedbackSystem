/**
 * handler are called with form,res IF form is tagged with class "ajax" and have a data-handler attribute matching
 * the handler name. This allows for custom handling of form submissions without needing to write separate event
 * listeners for each form. The handlers can perform actions like reloading a specific review or updating the
 * review list based on the response from the server after a form submission.
 */
const handlers = {

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
};