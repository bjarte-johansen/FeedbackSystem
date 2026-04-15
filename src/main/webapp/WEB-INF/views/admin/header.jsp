<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.time.*, java.time.format.*" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=<%= System.currentTimeMillis() %>">
    <script src="${pageContext.request.contextPath}/js/timeAgo.js"></script>
    <script src="${pageContext.request.contextPath}/js/router.js"></script>
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"
            integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo="
            crossorigin="anonymous"></script>
        <script src="${pageContext.request.contextPath}/js/PageCursor.js"></script>
        <script src="${pageContext.request.contextPath}/js/client-review.js"></script>
    <title>${defaultTitle}</title>
</head>
<body>

    <div class="d-none">
        <h1>Review system</h1>

        <p class="alert alert-info d-none">
            Les instruksjoner for skjemaer for å unngå problemer med innsendelse.
        </p>
    </div>

    <c:if test="${not empty errorMessage}"><p class="alert alert-error">${errorMessage}</p></c:if>
    <c:if test="${not empty successMessage}"><p class="alert alert-success">${successMessage}</p></c:if>

    <h1>Administrator interface</h1>