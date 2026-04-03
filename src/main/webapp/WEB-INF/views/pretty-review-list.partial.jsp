<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="box review--list">
    <h3>Fint formaterte reviews (${totalReviewCount})</h3>

    <div class="box">
        <h3>Sammendrag</h3>

        <div class="mb-2">
            Snittkarakter: ${dblFormatter2.apply(scoreStats.averageScore)} / 5<br>
            Antall omtalelser: ${scoreStats.totalScoreCount}
        </div>

        <c:forEach var="scoreCount" items="${scoreStats.scoreCounts}">
            <div class="grid mb-1">
                <div class="grid-auto">
                    ${scoreCount.key} Stjerner
                </div>
                <div class="grid-auto grid">
                    <div class="score-pct-bar">
                        <div style="width: ${scoreStats.scoreDistribution[scoreCount.key]}%;"></div>
                    </div>

                    ${scoreStats.scoreCounts[scoreCount.key]}
                </div>
            </div>
        </c:forEach>
    </div>

    <c:forEach var="review" items="${reviews}">
        <div class="box review">
            <div class="score-outer mb-0">
                <strong class="score-text">${review.score}/5</strong> <span class="score score-${review.score}"></span>
            </div>
            <span class="title mb-2">${not empty review.title ? review.title : ""}</span>
            <span class="name mb-0">${empty review.authorName ? 'Anonym' : review.authorName}</span>
            <em class="time mb-2">${review.getShortDateString()}</em>
            <span class="comment mb-4">${review.comment}</span>

            <!--<a href="" class="btn btn-secondary">Fjern</a>-->

            <form class="ajax" action="${pageContext.request.contextPath}/api/delete-review/${tenantId}/${review.id}" method="post">
                <input type="hidden" name="_method" value="delete">
                <button type="submit">Delete</button>
            </form>
        </div>
    </c:forEach>

    <div class="box paginator--cursors">
        <c:if test="${not empty pagePrevCursor}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}&cursor=${pagePrevCursor}" class="btn btn-primary">Forrige</a>
        </c:if>

        <c:if test="${not empty pageNextCursor}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}&cursor=${pageNextCursor}" class="btn btn-primary">Neste</a>
        </c:if>
    </div>
</div>