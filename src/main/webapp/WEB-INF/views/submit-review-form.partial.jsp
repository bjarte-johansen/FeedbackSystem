<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
function toggleReviewForm() {
    jQuery(".form--submit-review-form").toggle().removeClass("d-none");
    return false;
}
</script>

<div class="box-virtual submit-review-form">
    <br>
    <a href="#" onclick="toggleReviewForm();">Legg til ny omtale</a>

    <form class="ajax reload-on-success form--submit-review-form d-none" action="${pageContext.request.contextPath}/submit-review" method="post">
        <p class="alert alert-info">
            Benytt "test@test.com" og "Abacus556!" som epost og passord for å unngå å måtte verifisere konto.
        </p>

        <input type="hidden" name="tenantId" value="1">
        <input type="hidden" name="externalId" value="${externalId}">

        <fieldset class="mb-3">
            <h4>Konto</h4>

            <div class="form-group">
                <label for="email">Epost:</label>
                <input type="text" id="email" name="email" class="form-control" placeholder="Bruk test@test.com" value="test@test.com">
            </div>

            <div class="form-group">
                <label for="password">Passord:</label>
                <input type="password" id="password" name="password" class="form-control" value="Abacus556!">
            </div>
        </fieldset>

        <fieldset>
            <h4>Omtale</h4>

            <div class="form-group">
                <label for="displayName">Navn:</label>
                <input type="text" id="displayName" name="displayName" class="form-control" placeholder="Ditt navn" value="${displayNameSuggestion}">
            </div>

            <div class="form-group">
                <label for="score">Score:</label>
                <select id="score" name="score" class="form-control">
                    <option value="${scoreSuggestion}"selected>${scoreSuggestion}</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                </select>
            </div>

            <div class="form-group">
                <label for="title">Tittel:</label>
                <input type="text" id="title" name="title" class="form-control" placeholder="Tittel på din review" value="${titleSuggestion}">
            </div>

            <div class="form-group">
                <label for="comment">Kommentar:</label>
                <textarea id="comment" name="comment" class="form-control" placeholder="Skriv din kommentar her...">${commentSuggestion}</textarea>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Lagre</button>
            </div>
        </fieldset>
    </form>
</div>