<div class="box review--list">
    <h3>Fint formaterte reviews</h3>
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