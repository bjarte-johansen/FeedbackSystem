<!-- header -->
<%@ include file="header.jsp" %>

    <!-- external ids to represent different pages/products -->
    <%@ include file="show-external-ids.jsp" %>

    <!-- form to submit new review -->
    <%@ include file="submit-review-form.partial.jsp" %>

    <!-- review section part -->
    <div class="container--reviews">
        <%@ include file="pretty-review-list.partial.jsp" %>
    </div>

    <!-- show review dump for debugging -->
    <%@ include file="raw-review-list.partial.jsp" %>

<!-- footer -->
<%@ include file="footer.jsp" %>