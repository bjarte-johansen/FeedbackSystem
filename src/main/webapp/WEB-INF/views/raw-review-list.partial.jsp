<div class="box">
    <h3>Dump ${fn:length(reviewDump)}</h3>
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
                <td>${review.statusToString(review.status)}</td>
                <td>${review.authorName}</td>
                <td>${review.score}</td>
                <td>${review.title}</td>
                <td>${review.comment}</td>
                <!--               <td>toString() equals ${review}</td> -->

                <td>
                    <%@ include file="admin-review-moderate-buttons.jsp" %>
                </td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
</div>