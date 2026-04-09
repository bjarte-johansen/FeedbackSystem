<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <div class="review--list-items">
        <c:forEach var="review" items="${reviews}">
            <%@ include file="pretty-review.partial.jsp" %>
        </c:forEach>
    </div>
