<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 12.03.2026
  Time: 01:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

    <form method="post" action="/api-1/submit-review">
        <label for="score">Score:</label><br>
        <select name="score" id="score">
            <% for(int i = 1; i <= 10; i++) { %>
                <option value="<%= i %>"><%= i %></option>
            <% } %>
        </select>

        <br><br>

        <label for="comment">Review:</label><br>
        <textarea name="comment" id="comment" rows="5" cols="40"></textarea>

        <br><br>

        <button type="submit">Submit</button>
    </form>

</body>
</html>
