<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- header -->
<%@ include file="client-header.jsp" %>

    <h1>Client interface</h1>

    <div class="box">
        <a href="${pageContext.request.contextPath}/clear-session" class="btn btn-primary">Clear session</a>
    </div>

    <!-- external ids to represent different pages/products -->
    <%@ include file="external-id-pills.jsp" %>

    <!-- review section part -->
    <div class="container--reviews">

        <div class="box review--list"
            data-external-id="${externalId}"
            data-order-by-enum="${currentOrderByEnum}"
            data-original-cursor="${pageCursor}"
            data-cursor="${pageCursor}"
            data-score-filter="${scoreFilter}"
            data-review-count="${reviewStats.totalCount}"
            data-detailed-review-count="<c:out value="${scoreCountsJson}"/>">

            <div class="box-virtual mb-4">
                <%@ include file="review-list-stats.partial.jsp" %>

                <!-- form to submit new review -->
                <%@ include file="submit-review-form.partial.jsp" %>
            </div>

            <div class="box review--list-filters">
                <span class="active-filters mobile-full d-none" style="margin-right:8px;">
                    Aktive filtre:
                    <span class="items mobile-full"></span>

                    <a class="btn text-deco-none btn-primary btn-remove-filters mobile-full" href="javascript:void(0);" onclick="Review.client.resetReviewListFilters();">Fjern filtre</a>

                    <div class="templates d-none">
                        <a class="btn text-deco-none btn-primary mobile-full button-template disabled" href="/reviews/build-html?externalId=/product/1">Score: 5</a>
                    </div>
                </span>

                <!-- score filter dropdown -->
                <select name="scoreFilter" onchange="Review.triggerClientScoreFilterChange()">
                    <option value="${scoreFilter}" selected}>Filtrer score</option>

                    <c:forEach var="i" begin="1" end="5">
                        <option value="${i}" ${scoreFilter == i ? 'selected' : ''}>Score: ${i}</option>
                    </c:forEach>
                    <option value="-1">Alle</option>
                </select>

                <!-- order by dropdown -->
                <select name="orderByEnum" onchange="Review.triggerClientOrderByEnumChange(this)">
                    <c:forEach var="orderByOption" items="${reviewListOrderOptions}" varStatus="loop">
                        <option value="${orderByOption.value}" ${orderByOption.value == currentOrderByEnum ? 'selected' : ''}>${orderByOption.key}</option>
                    </c:forEach>
                </select>
            </div>

            <%@ include file="paginator.jsp" %>

            <%@ include file="pretty-review-list.partial.jsp" %>

            <%@ include file="paginator.jsp" %>
        </div>
    </div>


<!-- footer -->
<%@ include file="footer.jsp" %>