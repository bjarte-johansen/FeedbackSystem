class ReviewListingRenderer{
    static #_templateHtml = null;

    static getTemplateHtml() {
        if (ReviewListingRenderer.#_templateHtml === null) {
            // load template html from DOM
            const $tpl = $('.review--list .review-item-template');
            if (!$tpl) throw new Error("Unable to find first element child");

            ReviewListingRenderer.#_templateHtml = $tpl[0].outerHTML;
        }

        return ReviewListingRenderer.#_templateHtml;
    }

    static renderItems(items, append = false){
        Scroll.withStableScroll(() => {
            const $itemsContainer = $(".review--list-items .items");
            const $noItemsContainer = $(".review--list .no-items");

            if(items?.length === 0) {
                $itemsContainer.empty().hide();
                $noItemsContainer.show();
                return;
            }

            $itemsContainer.hide();
            $noItemsContainer.hide();

            const $fragment = $(document.createDocumentFragment());
            items.forEach((r) => {
                if(!r) return;
                $fragment.append( ReviewListingRenderer.renderItem(r) );
            });


            if (!append) $itemsContainer.empty();
            $itemsContainer.append($fragment).show();
        });
    }

    static renderItem(review){
        // define useful functions
        const roundToHalf = (x) => Math.round(x * 2.0) / 2.0;
        const formatScore = (score, minFractionDigits = 0, maxFractionDigits = 2) =>
            score.toLocaleString("en-US", {
                minimumFractionDigits: minFractionDigits,
                maximumFractionDigits: maxFractionDigits
            });

        const formatScoreCss = (score) =>
            formatScore(score, 1, 1)
                .replace(".", "-");

        let cloneHtml = ReviewListingRenderer.getTemplateHtml();
        cloneHtml = cloneHtml.replaceAll("{reviewId}", review.id);
        cloneHtml = cloneHtml.replaceAll("${review.id}", review.id);

        const friendlyNorwegianStatusName = Constants.getReviewStatusFriendlyDisplayName(review.status);
        // const friendlyNorwegianStatusName = Review.reviewListing
        //     .getReviewStatusAsFriendlyNorwegianName(review.status);

        const cssStatusName = Constants.getReviewStatusFriendlyCssName(review.status, "unknown-status");
        // const cssStatusName = Review.reviewListing
        //     .getReviewStatusAsFriendlyName(review.status);

        const $clone = $(cloneHtml);
        $clone
            .removeClass("review-item-template")
            .removeClass("d-none")
            .addClass("box review");

        $clone.find('.score-text').text(`${review.score}/5`);
        $clone.find('.score-value').addClass(`score-${formatScoreCss(roundToHalf(review.score))}`);
        $clone.find(".title").text(`${review.title ?? "(tom)"}`);
        $clone.find(".name").text(`${review.authorName ?? "(anonym)"}`);
        $clone.find(".time").text(TimeAgoFormatter.format(review.createdAt))
        $clone.find(".comment").text(review.comment ?? "(empty)");
        $clone.find(".like-count").text(review.likeCount);
        $clone.find(".dislike-count").text(review.dislikeCount);
        //$clone.attr("data-review-status-const", review.status);
        //$clone.attr("data-review-status-name", friendlyStatusName);
        $clone.addClass("review-item");
        $clone.addClass("review-item-" + review.id);
        $clone.addClass("review-item-" + cssStatusName);
        $clone.find('.status').text(friendlyNorwegianStatusName);

        const $moderateButtons = $('.admin-review-moderate-buttons');
        $moderateButtons.find(".actions a").each((i,el) => {
            const $el = $(el);
            $el.attr("href", $el.attr("href").replace("{reviewId}", review.id));
        });

        return $clone;
    }
}