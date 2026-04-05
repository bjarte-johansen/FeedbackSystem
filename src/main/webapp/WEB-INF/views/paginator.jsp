    <!-- paginator previous/next -->
    <div class="box paginator--cursors">
        <!--
        cursor{offset:${cursor.offset}, limit: ${cursor.limit}}<br>
        -->

        <c:if test="${not empty pagePrevCursor}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}&cursor=${pagePrevCursor}" class="">Forrige</a>
        </c:if>

        <c:if test="${not empty pageNextCursor}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}&cursor=${pageNextCursor}" class="">Neste</a>
        </c:if>
    </div>