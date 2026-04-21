function initClientRoutes(router) {
    const applyReviewVoteAction = function (reviewId, voteType) {
        Spinner.with(async () => {
            return fetch(`/api/review/${reviewId}/${voteType}`, {method: "POST"})
                .then(res => {
                    if (!res.ok) {
                        return;
                    }
                    Review.reloadReview(reviewId);
                })
                .catch(err => {
                    console.error("Error voting ${voteType} for review", reviewId, err);
                });
        });
    };

    router.route("/R/review/:id/like", ({params}) => {
        applyReviewVoteAction(params.id, "like");
    });

    router.route("/R/review/:id/dislike", ({params}) => {
        applyReviewVoteAction(params.id, "dislike");
    });

    router.route("/R/reviews/filter-by-score/:score", ({params}) => {
        Review.triggerClientScoreFilterPresetChange(params.score);
    });

    router.start();
}

function initAdminRoutes (router){
    // admin routes
    router.route("/R/admin/filter/status/:statusEnum", ({params, e}) => {
        $(e.target).toggleClass("active");
        console.log("admin filter review status clicked", params.statusEnum);

        const url = new URL(window.location.href);
        url.searchParams.set("statusFilter", params.statusEnum); // 👈 overwrite
        window.location.href = url.toString();
    });

    async function patchReviewStatus(reviewId, action){
        await Spinner.with(async () => {
            const url = `/api/review/mark-${action}?reviewId=${reviewId}`;
            const res = await fetch(url,{ method: "PATCH" });

            if(!res.ok){
                const text = await res.text();
                console.log("HTTP error:", res.status, text);
                return;
            }

            console.log("success", res.ok, res.status);
        });
    }

    router.route("/R/api/review/:reviewId/mark-:action", ({params, e}) => {
        patchReviewStatus(params.reviewId, params.action);
        window.location.reload();
    });

    router.start();
}