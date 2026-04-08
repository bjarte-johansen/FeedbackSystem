<div class="box-virtual">
    <h5>Buttons should not be here in client interface</h5>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/delete" method="post">
        <input type="hidden" name="_method" value="delete">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Delete</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-approved" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Approve</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-rejected" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Reject</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/review/mark-pending" method="post">
        <input type="hidden" name="tenantId" value="${tenantId}">
        <input type="hidden" name="reviewId" value="${review.id}">
        <button type="submit">Check</button>
    </form>
</div>