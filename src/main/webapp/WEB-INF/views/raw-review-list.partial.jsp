<div class="box">
    <h3>Dump</h3>
    <table class="w-100">
        <thead>
        <tr>
            <th>Id</th>
            <th>Data</th>
            <th>Action (visual only)</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach var="review" items="${reviews}">
            <tr>
                <td>${review.id}</td>
                <td>${review}</td>
                <td>
                    <button>Delete</button>
                    <button>Approve</button>
                </td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
</div>