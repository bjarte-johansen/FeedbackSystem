<!-- header -->
<%@ include file="header.jsp" %>

    <style>
        .feedback-system h1{display:none;}
    </style>

    <div class="box-virtual review--list  review--admin-login form-defaults d-none">
        <h4>Feedbacksystem - Innlogging</h4>

        <c:if test="${not empty statusMessage}">
            <p class="alert alert-info">${statusMessage}</p>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/admin/login">
            <div class="form-group">
                <label for="username">Brukernavn</label>
                <input type="text" class="form-control" id="username" name="username" required value="tenant1@test.com">
            </div>
            <div class="form-group">
                <label for="password">Passord</label>
                <input type="text" class="form-control" id="password" name="password" required value="password1">
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary w-100">Login</button>
            </div>
        </form>
    </div>
<script>


    $(document).ready(() => {
        // open center
        ModalDialog.open($('.review--admin-login'), true);
    });
</script>

<!-- footer -->
<%@ include file="footer.jsp" %>