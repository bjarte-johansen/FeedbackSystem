<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet/less" href="/css/main.less?a">
<script src="https://cdn.jsdelivr.net/npm/less@4"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
<script src="/js/classes/Spinner.js"></script>

<script>
    if(!window.Review) window.Review = {};
    if(!window.Review.admin) window.Review.admin = {};

    class EditTenantSettingsFormValidator{
        static validate(form){
            return true;
        }
    }

    Review.admin.submitEditTenantSettingsForm = function(e){
        const $form = $(e.target).closest('form');
        const form = $form[0];
        console.log(e);

        if(!EditTenantSettingsFormValidator.validate(form)){
            e.preventDefault();
            return;
        }

        const $method = $form.find("input[name='_method']");
        const methodName = $method.length && $method.val().toUpperCase() || form.method.toUpperCase();

        Spinner.with(async () => {
            e.preventDefault();

            fetch(form.action, {
                method: methodName,
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: $(form).serialize()
            }).then(() => {
                //form.remove();

                console.log("Endringene har blitt lagret");
            }).catch(err => {
                console.log("Error submitting form", err, err.message);
            });
        });

        return false;
    }
</script>

<div class="box-virtual feedback-system ">
    <form class="form--edit-tenant-settings form-defaults" action="${submitUrl}" method="post">
        <input type="hidden" name="_method" value="PUT">

        <p class="alert alert-info">
            Flere opsjoner er readonly, eller disabled da de trenger mer opplæring for å ta i bruk. Spesielt
            host-operasjoner er viktige å få korrekt.
        </p>

        <fieldset>
            <h4>Innstillinger - main</h4>

            <div class="form-group">
                <label for="name">Epost:</label>
                <input type="text" id="name" name="name" class="form-control" placeholder="" value="${tenant.name}" readonly disabled style="pointer-events:none;">
            </div>

            <div class="form-group">
                <label for="email">Epost:</label>
                <input type="text" id="email" name="email" class="form-control" placeholder="Bruk test@test.com" value="test@test.com" readonly disabled style="pointer-events:none;">
            </div>

            <div class="form-group d-none">
                <label for="password">Passord:</label>
                <input type="text" id="password" value="Ta kontakt for å endre" readonly disabled style="pointer-events:none;">
            </div>

            <div class="form-group">
                <label for="enableListing">Tillat visning av omtaler (global):</label>
                <select id="enableListing" name="enableListing" class="form-control">
                    <option value="true">Ja</option>
                    <option value="false">Nei</option>
                    <option value="${tenant.enableListing ? 'true' : 'false'}" selected>${tenant.enableListing ? 'Ja' : 'Nei'}</option>
                </select>
            </div>

            <div class="form-group">
                <label for="enableSubmit">Tillat oppretting av omtaler (global):</label>
                <select id="enableSubmit" name="enableSubmit" class="form-control">
                    <option value="true">Ja</option>
                    <option value="false">Nei</option>
                    <option value="${tenant.enableSubmit ? 'true' : 'false'}" selected>${tenant.enableSubmit ? 'Ja' : 'Nei'}</option>
                </select>
            </div>

            <div class="form-group mb-4 box">
                <div class="form-group">
                    <label>Hostnavn</label>
                    <p>
                        <em>Redigering av hostlist er disabled, men kan lett legges til for avanserte kunder med spesielle
                        behov. Funksjonalitet er disabled pga fare for å gjøre feil.
                        </em>
                        <br><br/>
                        Hostnavn kan redigeres slik at du vårt system finner riktig databaseentries. Domene må være på formen
                        domene.etternavn, feks *.viking.no, sub.viking.no, azure.ider.eurupe.com etc, www.*.viking.no). * matcher hvilken
                        som helst tekst, men ditt domene må være representert.
                    </p>
                </div>

                <div class="domain-entries">

                <c:forEach var="entry" items="${tenantDomainList}">
                    <div class="form-group">
                        <input type="text" name="domain" class="form-control domain" placeholder="Hostnavn" value="${entry.domain}" readonly disabled style="pointer-events:none;">
                    </div>
                </c:forEach>
                </div>

                <div class="form-group">
                    <button type="button" class="disabled" onclick="$('.domain-entries').append('<div class=\'form-group\'><input type=\'text\' name=\'domain\' class=\'form-control domain\' placeholder=\'Hostnavn\' value=\'\'></div>');" disabled>Legg til</button>
                </div>
            </div>

            <div class="form-group w-100">
                <button type="button" class="btn btn-cancel" onclick="window.location.href='/admin/dashboard';">Tilbake</button>
                <button type="button" class="btn btn-primary" onclick="Review.admin.submitEditTenantSettingsForm(event)">Lagre</button>
            </div>
        </fieldset>
    </form>
</div>

