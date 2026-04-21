<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty uniqueExternalIds}">
    <div class="box clearfix testpage--external-id-list">
        <h3>Velg "side" (trykk en)</h3>
        <p>Stiene representerer ruter hos ekstern kunde, feks <em>"/hvitevarer/klesvask/frontbetjent-vaskemaskin/samsung-ww90dg6u25lhu3-vaskemaskin/p-3199487/"</em></p>

        <div class="box-virtual clearfix testpage--external-id-list">
            <c:forEach var="externalId" items="${uniqueExternalIds}">
                <a class="${externalId == param.externalId ? 'btn btn-primary' : ''}" href="${pageContext.request.contextPath}/api/reviews/list/html?showDemoPills=true&externalId=${externalId}">${externalId}</a>
            </c:forEach>
        </div>
    </div>
</c:if>