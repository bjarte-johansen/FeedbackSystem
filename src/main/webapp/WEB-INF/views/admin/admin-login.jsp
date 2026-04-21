<!-- header -->
<%@ include file="header.jsp" %>

    <c:if test="${not empty statusMessage}">
        <p class="alert alert-info">${statusMessage}</p>
    </c:if>

    <style>
        .review--admin-login{
            label{width:80px;}
            input{width:100%;display:block;padding:4px;}
            .form-group{margin-bottom:8px;padding:8px;}
            button{width:100%;display:block;}
        }
    </style>

    <div class="box review--admin-interface review--admin-login">
        <h1>Innlogging</h1>

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
                <button type="submit" class="btn btn-primary">Login</button>
            </div>
        </form>
    </div>


<!-- footer -->
<%@ include file="footer.jsp" %>