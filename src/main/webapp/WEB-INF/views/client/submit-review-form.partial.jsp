<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box-virtual submit-review-form">
    <form class="ajax reload-on-success form--submit-review-form d-none" action="${pageContext.request.contextPath}/api/submit-review" method="post">
        <p class="alert alert-info">
            Benytt "test@test.com" og "Abacus556!" som epost og passord for å unngå å måtte verifisere konto.
        </p>

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
                <input type="text" id="displayName" name="displayName" class="form-control" placeholder="Ditt navn" value="${defaultNewReviewFormValues.displayNameSuggestion}">
            </div>

            <div class="form-group">
                <label for="score">Score:</label>
                <select id="score" name="score" class="form-control">
                    <option value="${defaultNewReviewFormValues.scoreSuggestion}"selected>${defaultNewReviewFormValues.scoreSuggestion}</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                </select>
            </div>

            <div class="form-group">
                <label for="title">Tittel:</label>
                <input type="text" id="title" name="title" class="form-control" placeholder="Tittel på din review" value="${defaultNewReviewFormValues.titleSuggestion}">
            </div>

            <div class="form-group">
                <label for="comment">Kommentar:</label>
                <textarea id="comment" name="comment" class="form-control" placeholder="Skriv din kommentar her...">${defaultNewReviewFormValues.commentSuggestion}</textarea>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Lagre</button>
            </div>
        </fieldset>
    </form>
</div>