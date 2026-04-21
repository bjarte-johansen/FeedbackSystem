<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!-- header -->
<%@ include file="header.jsp" %>

    <!-- external ids to represent different pages/products -->
    <%@ include file="external-id-pills.jsp" %>

    <!-- review section part -->
    <div class="container--reviews d-none">
        <div class="box review--list" data-json="${fn:escapeXml(json)}" data-enable-listing="${reviewConfig.enableListing}">

            <div class="box-virtual mb-4">
                <%@ include file="review-listing-header.jsp" %>

                <!-- form to submit new review -->
                <div class="box-virtual submit-review-form-container">
                </div>
            </div>

            <%@ include file="filters.jsp" %>

            <%@ include file="paginator.jsp" %>

            <%@ include file="pretty-review-list.partial.jsp" %>

            <%--<%@ include file="paginator.jsp" %>--%>
        </div>
    </div>


<!-- footer -->
<%@ include file="footer.jsp" %>