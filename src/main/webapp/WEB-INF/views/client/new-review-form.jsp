<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box-virtual submit-review-form inline-form">
    <form class="form--submit-review-form submit-url" action="${submitUrl}" method="post">
        <!--
        <p class="alert alert-info">
            Benytt "test@test.com" og "Abacus556!" som epost og passord for å unngå å måtte verifisere konto.
        </p>
        TODO: legg til verifiseringskode
        -->

        <input type="hidden" name="externalId" class="externalId external-id" value="${externalId}">

        <fieldset>
            <h4>Omtale</h4>

            <div class="form-group">
                <label for="email">Epost:</label>
                <input type="text" id="email" name="email" class="form-control" placeholder="Bruk test@test.com" value="test@test.com">
            </div>

            <div class="form-group d-none">
                <label for="password">Passord:</label>
                <input type="password" id="password" name="password" class="form-control" value="Abacus556!">
            </div>

            <div class="form-group">
                <label for="displayName">Navn:</label>
                <input type="text" id="displayName" name="displayName" class="form-control display-name" placeholder="Ditt navn" value="${form != null ? form.displayNameSuggestion : ''}">
            </div>

            <div class="form-group">
                <label for="score">Score:</label>
                <select id="score" name="score" class="form-control score-filter">
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5" selected>5</option>
                    <c:if test="${form != null && form.scoreSuggestion != null}">
                        <option value="${form.scoreSuggestion}" selected>${form.scoreSuggestion}</option>
                    </c:if>
                </select>
            </div>

            <div class="form-group">
                <label for="title">Tittel:</label>
                <input type="text" id="title" name="title" class="form-control title" placeholder="Tittel på din review" value="${form != null ? form.titleSuggestion : ''}">
            </div>

            <div class="form-group mb-4">
                <label for="comment">Kommentar:</label>
                <textarea id="comment" name="comment" class="form-control comment" placeholder="Skriv din kommentar her...">${form != null ? form.commentSuggestion : ''}</textarea>
            </div>

            <div class="form-group w-100">
                <button type="button" class="btn btn-cancel" onclick="$(this).closest('.submit-review-form').remove();">Avbryt</button>
                <button type="submit" class="btn btn-primary">Lagre</button>
            </div>
        </fieldset>
    </form>
</div>