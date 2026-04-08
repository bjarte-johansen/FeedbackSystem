<!-- header -->
<%@ include file="header.jsp" %>

    <h1>Client interface</h1>

    <div class="box">
        <a href="${pageContext.request.contextPath}/clear-session" class="btn btn-primary">Clear session</a>
    </div>

    <h1>Administrator interface</h1>
    <div class="box review--admin-interface">
        <%@ include file="show-external-ids.jsp" %>

        <div class="box">
            ${reviewStatusFilterOptions}
            <!-- order by dropdown -->
            <select name="currentReviewStatusFilterOptions" onchange="alert('not implemented yet')">
                <c:forEach var="option" items="${reviewStatusFilterOptions}">
                    <option value="${option.value}" <c:if test="${option.value == currentReviewStatusFilter}">selected</c:if>>${option.key}</option>
                </c:forEach>
            </select>
        </div>

        <div class="box review--list"
            data-external-id="${externalId}"
            data-order-by-enum="${currentOrderByEnum}"
            data-original-cursor="${pageCursor}"
            data-cursor="${pageCursor}"
            data-score-filter="${scoreFilter}"
            data-review-count="${reviewStats.totalCount}"
            data-filtered-review-count="${totalFilteredCount}"
            >

                <!-- show review dump for debugging -->
            <c:forEach var="review" items="${reviews}">
                <%@ include file="admin-review.partial.jsp" %>

            </c:forEach>
        </div>
    </div>

<!-- footer -->
<%@ include file="footer.jsp" %>