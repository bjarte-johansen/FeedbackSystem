<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- header -->
<%@ include file="header.jsp" %>

    <p class="alert alert-info">Husk brukerhistorier for å disable review-visning, tilfelle feil på side, eller hacking
    andre katastrofale tilfeller.</p>

    <div class="box-virtual REVIEW review--admin review--admin-interface">
        <div class="box-virtual review--list"
            data-external-id="${externalId}"
            data-cursor="${pageCursor}"
            data-status-filter="${currentReviewStatusFilter}"
            data-score-filter="${scoreFilter}"
            data-json="${fn:escapeXml(json)}">
<%--            data-review-count="${totalStatusFilterCount}"--%>

            <div class="box filter-buttons">
                Filtrer status:
                <c:forEach var="option" items="${reviewStatusFilterOptions}">
                    <a href="/R/admin/filter/status/${option.value}" class="btn text-deco-none ${option.value == currentReviewStatusFilter ? 'active' : ''}">${option.key}</a>
                </c:forEach>
            </div>

            <div class="box">
                Dato fra
                <input type="date" name="startDateFilter" value="${currentDateFilterStart}" onchange="Review.reloadReviewList()">
                til
                <input type="date" name="endDateFilter" value="${currentDateFilterEnd}" onchange="Review.reloadReviewList()">
                , Antall dager:
                <select name="numberOfDaysFilter" onchange="Review.admin.triggerDateFilterPresetChange(this)">
                    <option value="7" ${currentDateFilterPreset == 7 ? 'selected' : ''}>Siste 7 dager</option>
                    <option value="30" ${currentDateFilterPreset == 30 ? 'selected' : ''}>Siste 30 dager</option>
                    <option value="90" ${currentDateFilterPreset == 90 ? 'selected' : ''}>Siste 90 dager</option>
                    <option value="180" ${currentDateFilterPreset == 180 ? 'selected' : ''}>Siste 180 dager</option>
                    <option value="365" ${currentDateFilterPreset == 365 ? 'selected' : ''}>Siste 365 dager</option>
                </select>
            </div>

            <%@ include file="filters.jsp" %>

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