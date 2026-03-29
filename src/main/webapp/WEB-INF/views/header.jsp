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
    <script src="${pageContext.request.contextPath}/js/utils.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <title>${defaultTitle}</title>
</head>
<body>
<h1>Main</h1>

<p class="alert alert-info">
    Les instruksjoner for skjemaer for å unngå problemer med innsendelse.
</p>

<c:if test="${not empty errorMessage}"><p class="alert alert-error">${errorMessage}</p></c:if>
<c:if test="${not empty successMessage}"><p class="alert alert-success">${successMessage}</p></c:if>