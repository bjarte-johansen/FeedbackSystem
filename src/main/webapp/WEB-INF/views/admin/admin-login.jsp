<!-- header -->
<%@ include file="header.jsp" %>

    <h1>Administrator login - FeedbackSystem</h1>
<!--
    <div class="box">
        <a href="${pageContext.request.contextPath}/clear-session" class="btn btn-primary">Clear session</a>
    </div>
-->

    <div class="box review--admin-interface">
        <form method="post" action="${pageContext.request.contextPath}/admin/login">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" class="form-control" id="username" name="username" required value="tenant1@test.com">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="text" class="form-control" id="password" name="password" required value="password1">
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Login</button>
            </div>
        </form>
    </div>


<!-- footer -->
<%@ include file="footer.jsp" %>