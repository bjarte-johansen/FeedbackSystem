<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.time.*, java.time.format.*" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html lang="no">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet/less" href="/css/main.less">
    <script src="https://cdn.jsdelivr.net/npm/less@4"></script>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>


    <script src="/js/classes/Constants.js"></script>
    <script src="/js/classes/Scroll.js"></script>
    <script src="/js/classes/TimeAgoFormatter.js"></script>
    <script src="/js/classes/Assert.js"></script>
    <script src="/js/classes/Spinner.js"></script>
    <script src="/js/classes/Async.js"></script>
    <script src="/js/classes/Router.js"></script>
    <script src="/js/classes/PageCursor.js"></script>
    <script src="/js/classes/ReviewQueryOptions.js"></script>
    <script src="/js/classes/ReviewListing.js"></script>
    <script src="/js/classes/ReviewListingRenderer.js"></script>
    <script src="/js/classes/StatisticsRenderer.js"></script>
    <script src="/js/classes/UiReviewListingFilter.js"></script>
    <script src="/js/classes/NewReviewFormValidator.js"></script>
    <script src="/js/classes/Utils.js"></script>
    <script src="/js/classes/UserInterfaceTriggers.js"></script>
    <script src="/js/classes/ModalDialog.js"></script>

    <style>
        .d-none{display:none;}
        html { visibility: hidden; }
    </style>

    <script>
        window.onload = () => document.documentElement.style.visibility = "visible";
    </script>

    <!-- END OF REQUIRED PART OF SCRIPTS -->

    <title>Administrator - Feedback system </title>
</head>

<body class="feedback-system">
    <h1>Administrator (feedbacksystem)</h1>