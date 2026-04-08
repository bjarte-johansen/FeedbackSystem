<!-- header -->
<%@ include file="header.jsp" %>

    <h1>Client interface</h1>

    <div class="box">
        <a href="${pageContext.request.contextPath}/clear-session" class="btn btn-primary">Clear session</a>
    </div>

    <h1>Administrator interface</h1>
    <div class="box review--admin-interface">
        <%@ include file="show-external-ids.jsp" %>

        <!-- show review dump for debugging -->
        <%@ include file="raw-review-list.partial.jsp" %>
    </div>

<!-- footer -->
<%@ include file="footer.jsp" %>