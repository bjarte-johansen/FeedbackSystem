<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="box review--list"
    data-external-id="${externalId}"
    data-order-by-enum="${currentOrderByEnum}"
    data-original-cursor="${pageCursor}"
    data-cursor="${pageCursor}"
    data-score-filter="${scoreFilter}"
    data-review-count="${reviewStats.totalCount}">

    <div class="box-virtual mb-4">
        <%@ include file="review-list-stats.partial.jsp" %>

        <!-- form to submit new review -->
        <%@ include file="submit-review-form.partial.jsp" %>
    </div>

    <div class="box">
        <!-- score filter dropdown -->
        <select name="scoreFilter" onchange="Review.reloadReviewList()">
            <option value="${scoreFilter}" selected}></option>

            <c:forEach var="i" begin="1" end="5">
                <option value="${i}" ${scoreFilter == i ? 'selected' : ''}>${i}</option>
            </c:forEach>
            <option value="-1">Alle</option>
        </select>

        <!-- order by dropdown -->
        <select name="orderByEnum" onchange="Review.reloadReviewList()">
            <c:forEach var="orderByOption" items="${reviewListOrderOptions}" varStatus="loop">
                <option value="${orderByOption.value}" ${orderByOption.value == currentOrderByEnum ? 'selected' : ''}>${orderByOption.key}</option>
            </c:forEach>
        </select>
    </div>

    <%@ include file="paginator.jsp" %>

    <c:forEach var="review" items="${reviews}">
        <%@ include file="pretty-review.partial.jsp" %>
    </c:forEach>

    <%@ include file="paginator.jsp" %>
</div>