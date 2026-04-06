<div class="box-virtual">
    <h5>Buttons should not be here in client interface</h5>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/delete-review/${tenantId}/${review.id}" method="post">
        <input type="hidden" name="_method" value="delete">
        <button type="submit">Delete</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/mark-review-approved/${tenantId}/${review.id}" method="post">
        <button type="submit">Approve</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/mark-review-rejected/${tenantId}/${review.id}" method="post">
        <button type="submit">Reject</button>
    </form>

    <form class="ajax d-inline-block reload-on-success" action="${pageContext.request.contextPath}/api/mark-review-pending/${tenantId}/${review.id}" method="post">
        <button type="submit">Check</button>
    </form>
</div>