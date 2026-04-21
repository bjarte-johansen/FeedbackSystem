<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box-virtual submit-review-form">
    <form class="form--submit-review-form submit-url" action="${submitUrl}" method="post">
        <p class="alert alert-info">
            Benytt "test@test.com" og "Abacus556!" som epost og passord for å unngå å måtte verifisere konto.
        </p>

        <input type="hidden" name="externalId" class="externalId external-id" value="${externalId}">

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
                <input type="text" id="displayName" name="displayName" class="form-control display-name" placeholder="Ditt navn" value="${form.displayNameSuggestion}">
            </div>

            <div class="form-group">
                <label for="score">Score:</label>
                <select id="score" name="score" class="form-control score-filter">
                    <option value="1" ${form.scoreSuggestion == 1 ? 'selected' : ''}>1</option>
                    <option value="2" ${form.scoreSuggestion == 2 ? 'selected' : ''}>2</option>
                    <option value="3" ${form.scoreSuggestion == 3 ? 'selected' : ''}>3</option>
                    <option value="4" ${form.scoreSuggestion == 4 ? 'selected' : ''}>4</option>
                    <option value="5" ${form.scoreSuggestion == 4 ? 'selected' : ''}>5</option>
                </select>
            </div>

            <div class="form-group">
                <label for="title">Tittel:</label>
                <input type="text" id="title" name="title" class="form-control title" placeholder="Tittel på din review" value="${form.titleSuggestion}">
            </div>

            <div class="form-group">
                <label for="comment">Kommentar:</label>
                <textarea id="comment" name="comment" class="form-control comment" placeholder="Skriv din kommentar her...">${form.commentSuggestion}</textarea>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Lagre</button>
            </div>
        </fieldset>
    </form>
</div>