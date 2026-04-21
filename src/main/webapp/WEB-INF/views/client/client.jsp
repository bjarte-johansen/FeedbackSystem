<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dev-only.css"></link>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/new-review-form.css"></link>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/review--list.css"></link>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/review--stats.css"></link>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <script src="/js/timeAgo.js"></script>
    <script src="/js/Assert.js"></script>
    <script src="/js/Spinner.js"></script>
    <script src="/js/Router.js"></script>
    <script src="/js/ReviewListing.js"></script>
    <script src="/js/StatisticsRenderer.js"></script>
    <script src="/js/PageCursor.js"></script>
    <script src="/js/Utils.js"></script>
    <script src="/js/routes.js"></script>
    <script src="/js/client-review.js"></script>

    <!-- external ids to represent different pages/products -->
    <jsp:include page="external-id-pills.jsp" />

    <!-- review section part -->
    <div class="container--reviews d-none">
        <div class="box-virtual review--list" data-json="${fn:escapeXml(json)}" data-enable-listing="${reviewConfig.enableListing}" data-is-client="${isClient}" data-is-administrator="${isAdministrator}">

            <div class="box-virtual mb-4">
                <%@ include file="reviews-statistics.jsp" %>

                <!-- form to submit new review -->
                <div class="box-virtual submit-review-form-container"></div>
            </div>

            <%@ include file="filters.jsp" %>

            <%@ include file="paginator.jsp" %>

            <%@ include file="review-list.jsp" %>

            <%--<%@ include file="paginator.jsp" %>--%>
        </div>
    </div>




