<!-- header -->
<%@ include file="header.jsp" %>

    <div class="box-virtual review--admin-interface">
        <div class="box-virtual review--list"
            data-external-id="${externalId}"
            data-cursor="${pageCursor}"
            data-status-filter="${currentReviewStatusFilter}"
            data-score-filter="${scoreFilter}"
            data-review-count="${totalStatusFilterCount}">

            <div class="box filter-buttons">
                Filtrer status:
                <c:forEach var="option" items="${reviewStatusFilterOptions}">
                    <!-- fake form with .ajax and .custom-handler will trigger javascript and never be posted -->
                    <form class="ajax d-inline-block custom-handler" data-cmd="addStatusFilter:${option.value}" method="post">
                        <button type="submit" class="${option.value == currentReviewStatusFilter ? 'active' : ''}">${option.key}</button>
                    </form>
                </c:forEach>
            </div>

            <div class="box">
                Dato fra
                <input type="date" name="dateFilter" value="${currentDateFilterStart}" onchange="Review.reloadReviewList()">
                til
                <input type="date" name="dateFilter" value="${currentDateFilterEnd}" onchange="Review.reloadReviewList()">
                , Antall dager:
                <select name="dateFilterPreset" onchange="Review.admin.triggerDateFilterPresetChange(this)">
                    <option value="7" ${currentDateFilterPreset == 7 ? 'selected' : ''}>Siste 7 dager</option>
                    <option value="30" ${currentDateFilterPreset == 30 ? 'selected' : ''}>Siste 30 dager</option>
                    <option value="90" ${currentDateFilterPreset == 90 ? 'selected' : ''}>Siste 90 dager</option>
                    <option value="180" ${currentDateFilterPreset == 180 ? 'selected' : ''}>Siste 180 dager</option>
                    <option value="365" ${currentDateFilterPreset == 365 ? 'selected' : ''}>Siste 365 dager</option>
                </select>
            </div>

<%--
        <div class="box">
            <!-- order by dropdown -->
            <select name="reviewStatusFilter" onchange="Review.reloadReviewList()">
                <c:forEach var="option" items="${reviewStatusFilterOptions}">
                    <option value="${option.value}" <c:if test="${option.value == currentReviewStatusFilter}">selected</c:if>>${option.key}</option>
                </c:forEach>
            </select>
        </div>
--%>



            <!-- show review dump for debugging -->
            <c:forEach var="review" items="${reviews}">
                <%@ include file="admin-review.partial.jsp" %>
            </c:forEach>
        </div>
    </div>

<!-- footer -->
<%@ include file="footer.jsp" %>