<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box-virtual">
    <!--
    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/delete" method="post">
        <input type="hidden" name="_method" value="delete">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Slett</button>
    </form>
    -->

    <span class="status-buttons">
        <button class="review-status-${toCssIdentifier.apply(review.statusToString(review.status))}" disabled>Status: ${review.statusToNorwegianString(review.status)}</button>
    </span>

    <c:if test="${review.status != review.getApprovedStatusConst()}">
    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-approved" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Godkjenn</button>
    </form>
    </c:if>

    <c:if test="${review.status != review.getRejectedStatusConst()}">
    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-rejected" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Avvis</button>
    </form>
    </c:if>

    <c:if test="${review.status != review.getPendingStatusConst()}">
    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-pending" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Vurder på ny</button>
    </form>
    </c:if>

</div>