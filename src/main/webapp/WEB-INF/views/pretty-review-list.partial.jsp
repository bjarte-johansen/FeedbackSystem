<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="en_US" scope="session"/>
<fmt:setBundle basename="dummy"/> <!-- forces fmt to apply locale -->

<div class="box review--list">
    <h3>Fint formaterte reviews (${fn:length(reviews)})</h3>

    <div class="box">
        <h3>Sammendrag</h3>
        AvaregeScore: ${dblFormatter2.apply(scoreStats.averageScore)}
        <br>
        TotalScoreCount: ${scoreStats.totalScoreCount} <br>
        <c:forEach var="scoreCount" items="${scoreStats.scoreCounts}" >
            <div class="grid mb-1">
                <div class="grid-auto">
                    ${scoreCount.key} Stjerner
                </div>
                <div class="grid-auto grid">
                    <div style="display:inline-block;width:60%;display:block;height:16px;background-color: #AAA; margin-bottom: 4px;">
                        <div style="display:inline-block;height:100%;background-color: #e8681e;width: ${scoreStats.scoreDistribution[scoreCount.key]}%;"></div>
                    </div>

                    ${scoreCount.value}
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
</div>