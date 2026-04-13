<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>FeedbackSystem</title>
</head>
<body>

<h1>Feilmelding</h1>

<div class="box">
    ${not empty statusMessage ? statusMessage : 'Ukjent feil.'}
</div>

</body>
</html>