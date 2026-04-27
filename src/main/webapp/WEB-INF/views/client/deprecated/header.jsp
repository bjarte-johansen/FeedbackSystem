<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.time.*, java.time.format.*" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <title>Client</title>
    <meta charset="UTF-8">
<%--
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dev-only.css"></link>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/new-review-form.css"></link>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/review--list.css"></link>

    <!-- REQUIRED PART OF SCRIPTS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <script src="${pageContext.request.contextPath}/js/TimeAgoFormatter.js"></script>
    <script src="${pageContext.request.contextPath}/js/Assert.js"></script>
    <script src="${pageContext.request.contextPath}/js/Spinner.js"></script>
    <script src="${pageContext.request.contextPath}/js/Router.js"></script>
    <script src="${pageContext.request.contextPath}/js/ReviewListing.js"></script>
    <script src="${pageContext.request.contextPath}/js/StatisticsRenderer.js"></script>
    <script src="${pageContext.request.contextPath}/js/PageCursor.js"></script>
    <script src="${pageContext.request.contextPath}/js/Utils.js"></script>
    <script src="${pageContext.request.contextPath}/js/routes.js"></script>
    <script src="${pageContext.request.contextPath}/js/client-review.js"></script>
    <!-- END OF REQUIRED PART OF SCRIPTS -->
--%>
</head>
<body>

    <c:if test="${not empty errorMessage}"><p class="alert alert-error">${errorMessage}</p></c:if>
    <c:if test="${not empty successMessage}"><p class="alert alert-success">${successMessage}</p></c:if>

    <h1>Client interface</h1>