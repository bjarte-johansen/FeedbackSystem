<div class="box">
    <h3>Sammendrag</h3>

    <div class="grid mb-4">
        <div class="grid-col-6">
            <div class="mb-2">
               <div class="score-outer mb-0">
                    <strong class="score-text">${dblFormatter1.apply(reviewStats.averageScore)} / 5</strong> <span class="score score-${dblFormatterCssPointFive.apply(reviewStats.averageScore)}"></span>
                </div>
                Snittkarakter: ${dblFormatter1.apply(reviewStats.averageScore)} av 5<br>
                Antall omtaler: ${reviewStats.totalCount}
            </div>
        </div>

        <div class="grid-col-6">
            <c:forEach var="scoreCount" items="${reviewStats.scoreCounts}">
                <div class="mb-1">
                    <div class="grid">
                        <div class="grid-col-3">
                            ${scoreCount.key} Stjerner
                        </div>

                        <div class="score-pct-bar grid-col-6">
                            <div style="width: ${reviewStats.scoreDistribution[scoreCount.key]}%;"></div>
                        </div>

                        <div class="grid-col-3">
                            ${reviewStats.scoreCounts[scoreCount.key]}
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <a href="#" onclick="toggleReviewForm();">Legg til ny omtale</a>
</div>