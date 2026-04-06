    <!-- paginator previous/next -->
    <div class="box paginator--cursors">
        <a href="#" onclick="Review.prevReviewListPage();">Forrige</a>
        <a href="#" onclick="Review.nextReviewListPage();">Neste</a>

<!--
        <c:if test="${not empty pagePrevCursor}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}&cursor=${pagePrevCursor}">Forrige</a>
        </c:if>

        <c:if test="${not empty pageNextCursor}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}&cursor=${pageNextCursor}">Neste</a>
        </c:if>
-->
    </div>