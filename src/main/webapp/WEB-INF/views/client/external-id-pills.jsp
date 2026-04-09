<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box clearfix testpage--external-id-list">
    <h3>Velg "side" (trykk en)</h3>

    <div class="box-virtual clearfix testpage--external-id-list">
        <c:forEach var="externalId" items="${uniqueExternalIds}">
            <a class="${externalId == param.externalId ? 'btn btn-primary' : ''}" href="${pageContext.request.contextPath}/reviews/build-html?externalId=${externalId}">${externalId}</a>
        </c:forEach>
    </div>
</div>