<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="box review--list">
    <h3>Omtaler (${reviewStats.totalCount})</h3>

        <%@ include file="review-list-stats.partial.jsp" %>

        <!-- form to submit new review -->
        <%@ include file="submit-review-form.partial.jsp" %>
    </div>

    <%@ include file="paginator.jsp" %>

    <c:forEach var="review" items="${reviews}">
        <%@ include file="pretty-review.partial.jsp" %>
    </c:forEach>

    <%@ include file="paginator.jsp" %>
</div>