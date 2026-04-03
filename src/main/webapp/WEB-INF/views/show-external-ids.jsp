<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box review--list">
    <h3>Artikler som kan kommenteres</h3>
    <div class="box clearfix testpage--external-id-list">
        <c:if test="${not empty externalId}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}">${externalId} (current)</a>
        </c:if>

        <c:forEach var="externalId" items="${uniqueExternalIds}">
            <a href="${pageContext.request.contextPath}/show-reviews?externalId=${externalId}">${externalId}</a>
        </c:forEach>
    </div>
</div>