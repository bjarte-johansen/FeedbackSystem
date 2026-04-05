<div class="box">
    <h3>Dump</h3>
    <table class="w-100">
        <thead>
        <tr>
            <td>Id</td>
            <td>Dato</td>
            <td>ExternalId</td>
            <td>Status</td>
            <td>Forfatter</td>
            <td>Score</td>
            <td>Tittel</td>
            <td>Tekst</td>

            <!--<td>toString() equals ${review}</td>-->
            <td>
        </tr>
        </thead>
        <tbody>
            <c:forEach var="review" items="${reviewDump}">

            <tr>
                <td>${review.id}</td>
                <td>${review.createdAt.toString().split("T")[0]}</td>
                <td>${review.externalId}</td>
                <td>${review.status == 0 ? "pending" : (review.status == 1 ? "approved" : "rejected")}</td>
                <td>${review.authorName}</td>
                <td>${review.score}</td>
                <td>${review.title}</td>
                <td>${review.comment}</td>
                <!--               <td>toString() equals ${review}</td> -->

                <td>
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
                </td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
</div>