<!-- header -->
<%@ include file="admin-header.jsp" %>

    <h1>Admin interface</h1>

    <div class="box">
        <a href="${pageContext.request.contextPath}/clear-session" class="btn btn-primary">Clear session</a>
    </div>

    <h1>Administrator interface</h1>
    <div class="box review--admin-interface">
        <%@ include file="external-id-pills.jsp" %>

        <div class="box">
            ${reviewStatusFilterOptions}

            <!-- order by dropdown -->
            <select name="reviewStatusFilter" onchange="Review.reloadReviewList()">
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
            data-status-filter="${currentReviewStatusFilter}"
            data-score-filter="${scoreFilter}"
            data-review-count="${reviewStats.totalCount}"
            data-detailed-review-count="${scoreCountsJson}"
            >

                <!-- show review dump for debugging -->
            <c:forEach var="review" items="${reviews}">
                <%@ include file="admin-review.partial.jsp" %>

            </c:forEach>
        </div>
    </div>

<!-- footer -->
<%@ include file="footer.jsp" %>