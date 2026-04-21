<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box-virtual admin-review-moderate-buttons">
    <a class="btn disabled current-status">
        <span class="d-none show-if-approved">Status: Godkjent</span>
        <span class="d-none show-if-rejected">Status: Avvist</span>
        <span class="d-none show-if-pending">Status: Til vurdering</span>
    </a>

    <span class="actions">
        <a href="/R/api/review/${review.id}/mark-approved" class="btn mark-approved hide-if-approved">Godkjenn</a>
        <a href="/R/api/review/${review.id}/mark-rejected" class="btn mark-rejected hide-if-rejected">Avvis</a>
        <a href="/R/api/review/${review.id}/mark-pending" class="btn mark-pending hide-if-pending">Vurder på ny</a>
    </span>
</div>