function initClientRoutes(router) {
    const applyReviewVoteAction = function (reviewId, voteType) {
        Spinner.with(async () => {
            if(!(await Async.fetchOk(`/api/review/${reviewId}/${voteType}`, {method: "POST"}))) {
                console.error("Failed vote-action on review");
                return;
            }

            Review.reloadReview(reviewId);
        });
    };

    router.route("/R/review/:id/like", ({params}) => {
        applyReviewVoteAction(params.id, "like");
    });

    router.route("/R/review/:id/dislike", ({params}) => {
        applyReviewVoteAction(params.id, "dislike");
    });

    router.route("/R/reviews/filter-by-score/:score", ({params}) => {
        Review.clientTriggers.triggerClientScoreFilterPresetChange(params.score);
    });

    router.start();
}


function initAdminRoutes (router){
    async function patchReviewStatus(reviewId, action){
        await Spinner.with(async () => {
            const url = `/api/review/mark-${action}?reviewId=${reviewId}`;
            if(!(await Async.fetchOk(url,{method: "PATCH"}))) {
                console.error("Failed to change review status");
                return;
            }

            Review.reloadReviewList({ isAdministrator: true});
        });
    }

    router.route("/R/api/review/:reviewId/mark-:action", ({params}) => {
        patchReviewStatus(params.reviewId, params.action);
    });

    router.start();

    console.log("admin routes initialized");
}