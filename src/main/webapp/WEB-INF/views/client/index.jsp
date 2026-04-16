<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!-- header -->
<%@ include file="client-header.jsp" %>

    <h1>Client interface</h1>

    <!-- external ids to represent different pages/products -->
    <%@ include file="external-id-pills.jsp" %>

    <!-- review section part -->
    <div class="container--reviews">
        <div class="box review--list" data-json="${fn:escapeXml(json)}">

            <div class="box-virtual mb-4">
                <%@ include file="review-list-stats.partial.jsp" %>

                <!-- form to submit new review -->
                <%@ include file="submit-review-form.partial.jsp" %>
            </div>

            <%@ include file="filters.jsp" %>

            <%@ include file="paginator.jsp" %>

            <%@ include file="pretty-review-list.partial.jsp" %>

            <%--<%@ include file="paginator.jsp" %>--%>
        </div>
    </div>


<!-- footer -->
<%@ include file="footer.jsp" %>