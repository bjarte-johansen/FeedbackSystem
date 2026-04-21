<div class="box review--stats">
    <h3>Omtaler (<span class="total-review-count">-</span>)</h3>

    <div class="grid mb-4">
        <div class="grid-col-6 mobile-full">
            <div class="mb-2">
               <div class="score-outer mb-0">
                    <span class="big-average-score">-</span> <span class="score"></span>
                </div>
                Snittkarakter: <span class="average-score">-</span>
                <br>
                Antall omtaler: <span class="total-review-count">-</span>
            </div>
        </div>

        <div class="grid-col-6 score-bars-with-count mobile-full">
            <div class="items">
                <!-- container for modded duplicates of template -->
            </div>

            <div class="template d-none">
                <div class="row mb-1">
                    <a class="text-deco-none" href="/R/reviews/filter-by-score/:score">
                        <div class="grid">
                            <div class="grid-col-3 score-description">Stjerner</div>
                            <div class="grid-col-6 score-pct-bar"><div style="width: 50%"></div></div>
                            <div class="grid-col-3 score-count">-</div>
                        </div>
                    </a>
                </div>
            </div>
        </div>
    </div>

    <a href="javascript:void(0)" onclick="Review.client.showNewReviewForm();">Legg til ny omtale</a>
</div>

<%--
<div class="box review--stats">
    <h3>Omtaler (<span class="total-review-count">${reviewStats.totalCount}</span>)</h3>

    <div class="grid mb-4">
        <div class="grid-col-6 mobile-full">
            <div class="mb-2">
               <div class="score-outer mb-0">
                    <span class="big-average-score">${reviewStats.averageScore}, ${dblFormatter1.apply(reviewStats.averageScore)} / 5</span> <span class="score score-${dblFormatterCssPointFive.apply(reviewStats.averageScore)}"></span>
                </div>
                Snittkarakter: <span class="average-score">${dblFormatter1.apply(reviewStats.averageScore)} av 5</span><br>
                Antall omtaler: <span class="total-review-count">${reviewStats.totalCount}
            </div>
        </div>

        <div class="grid-col-6 score-bars-with-count mobile-full">
            <c:forEach var="scoreCount" items="${reviewStats.scoreCount}">
                <div class="mb-1">
                    <a class="text-deco-none" href="javascript:void(0)" data-integral-score-attr="${scoreCount.key}" onclick="Review.triggerClientScoreFilterPresetChange(this)">
                        <div class="grid">
                            <div class="grid-col-3 score-description">
                                ${scoreCount.key} Stjerner
                            </div>

                            <div class="grid-col-6 score-pct-bar">
                                <div style="width: ${reviewStats.scoreDistribution[scoreCount.key]}%;"></div>
                            </div>

                            <div class="grid-col-3 score-count">
                                ${reviewStats.scoreCount[scoreCount.key]}
                            </div>
                        </div>
                    </a>
                </div>
            </c:forEach>
        </div>
    </div>

    <a href="javascript:void(0)" onclick="toggleReviewForm();">Legg til ny omtale</a>
</div>
--%>