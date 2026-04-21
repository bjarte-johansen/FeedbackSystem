class StatisticsRenderer {
    updateFromJson(stats) {
        if(!stats) return console.warn("invalid stats object");
        console.log("StatisticsRenderer.updateFromJson", stats);

        const $reviewStats = $('.review--stats');
        if ($reviewStats.length === 0) return;// throw new Error("Unable to find element .review--stats");

        const bigAverageScore = stats.averageScore > 0.99
            ? String((Math.round(stats.averageScore * 2.0) / 2.0).toFixed(1))
            : '-';

        // update big average score
        $reviewStats.find(".big-average-score").text(bigAverageScore + " / 5");

        // update score (stars)
        $reviewStats.find(".score").addClass("score-" + bigAverageScore.replace(".", "-"));

        // update average score
        $reviewStats.find(".average-score").text(bigAverageScore);

        // update total review count
        $reviewStats.find(".total-review-count").text(stats.totalCount);


        const $scoreBarsContainer = $reviewStats.find(".score-bars-with-count");
        const $scoreBarsItems = $scoreBarsContainer.find(".items");
        const $rowTemplate = $scoreBarsContainer.find('.template .row');
        if($rowTemplate.length === 0) throw new Error("Row template for statistics bars found");

        const sortedKeys = Object.keys(stats.scoreCount)
            .sort((a,b) => a - b);

        const numKeys = sortedKeys.length;

        const $fragment = $(document.createDocumentFragment());

        let i = 0;
        for(const key of sortedKeys){
            const $clone = $rowTemplate.clone();

            const $anchor = $clone.find('a');
            const oldHref = $anchor.attr("href");
            const newHref = oldHref.replace(":score", "" + (numKeys - i));
            $anchor.attr("href", newHref);

            $clone.find('.score-description').text("" + (numKeys - i) + " Stjerner");
            $clone.find(".score-pct-bar>div").css("width", stats.scoreDistribution[numKeys - i] + "%");
            $clone.find(".score-count").text(stats.scoreCount[numKeys - i]);

            $fragment.append($clone);
            ++i;
        }

        $scoreBarsItems.hide().empty().append($fragment).show();
    }
}