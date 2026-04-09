<div class="box review--stats">
    <h3>Omtaler (${reviewStats.totalCount})</h3>

    <div class="grid mb-4">
        <div class="grid-col-6 mobile-full">
            <div class="mb-2">
               <div class="score-outer mb-0">
                    <strong class="score-text">${dblFormatter1.apply(reviewStats.averageScore)} / 5</strong> <span class="score score-${dblFormatterCssPointFive.apply(reviewStats.averageScore)}"></span>
                </div>
                Snittkarakter: ${dblFormatter1.apply(reviewStats.averageScore)} av 5<br>
                Antall omtaler: ${reviewStats.totalCount}
            </div>
        </div>

        <div class="grid-col-6 score-bars-with-count mobile-full">
            <c:forEach var="scoreCount" items="${reviewStats.scoreCounts}">
                <div class="mb-1">
                    <a class="text-deco-none" href="javascript:void(0)" onclick="
                            const $reviewList = $('.review--list');
                            let $select = $('.review--list select[name=scoreFilter]');
                            $select.val(${scoreCount.key});

                            const $activeFilters = $reviewList.find('.active-filters');
                            $activeFilters.find('.score-filter-enum').remove();

                            const $clone = $activeFilters.find('.templates .btn').clone();
                            $clone.removeClass('d-none')
                                .text('Score: ${scoreCount.key}')
                                .addClass('score-filter-enum');

                            //$reviewList.find('.active-filters').find('.items .btn').remove();
                            $activeFilters.find('.items').append($clone);
                            $activeFilters.removeClass('d-none').show();

                            Review.reloadReviewList();
                            ">
                        <div class="grid">
                            <div class="grid-col-3 score-description">
                                ${scoreCount.key} Stjerner
                            </div>

                            <div class="grid-col-6 score-pct-bar">
                                <div style="width: ${reviewStats.scoreDistribution[scoreCount.key]}%;"></div>
                            </div>

                            <div class="grid-col-3 score-count">
                                ${reviewStats.scoreCounts[scoreCount.key]}
                            </div>
                        </div>
                    </a>
                </div>
            </c:forEach>
        </div>
    </div>

    <a href="javascript:void(0)" onclick="toggleReviewForm();">Legg til ny omtale</a>
</div>