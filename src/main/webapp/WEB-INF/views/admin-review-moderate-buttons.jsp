<div class="box-virtual">
    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/delete" method="post">
        <input type="hidden" name="_method" value="delete">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Slett</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-approved" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Godkjenn</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-rejected" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Avvis</button>
    </form>
<!--
    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-pending" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Kontroller</button>
    </form>
-->
</div>