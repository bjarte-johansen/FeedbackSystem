<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- header -->
<%@ include file="header.jsp" %>

<script>
    $(document).ready(() => {
        $(".review--admin-review-settings-editor .settings-list .btn-save").click(async function(){
            // collect "form" parameters as an object
            const collectParameters = function($tr){
                const params = new URLSearchParams();

                $tr.find("select, input, textarea").each((i,el) => {
                    const $el = $(el);
                    const val = ($el.attr("data-boolean") === "true") ? JSON.parse($el.val()) : $el.val();
                    params.set($el.attr("name"), val);
                });

                return Object.fromEntries(params.entries());
            }

            const data = collectParameters($(this).closest("tr"));

            const res = await fetch("/api/review/settings/list", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if(!res.ok){ alert("En feil oppstod under lagring. Prøv igjen eller kontakt leverandør av programvare"); }

            window.location.reload();
        });
    });
</script>

<div class="box clearfix review--admin-review-settings-editor">
    <h3>Rediger omtaleinnstillinger</h3>
    <table class="w-100 settings-list mb-4">
        <thead>
            <tr>
                <th>Id</th>
                <th>Navn</th>
                <th>Sti</th>
                <th>Tillat vis</th>
                <th>Tillat nye</th>
                <th>Handling</th>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="rec" items="${reviewSettingsList}">
                <tr data-id="${rec.id}">
                    <td><input type="hidden" name="id" value="${rec.id}">${rec.id}</td>
                    <td><input type="text" name="name" value="${rec.name}"></td>
                    <td><input type="hidden" name="externalId" value="${rec.externalId}">${rec.externalId}</td>
                    <td>
                        <select name="enableListing" data-boolean="true">
                            <option value="true">Ja</option>
                            <option value="false">Nei</option>
                            <option value="${rec.enableListing ? "true" : "false"}" selected>${rec.enableListing ? "Ja" : "Nei"}</option>
                        </select>
                    </td>
                    <td>
                        <select name="enableSubmit" data-boolean="true">
                            <option value="true">Ja</option>
                            <option value="false">Nei</option>
                            <option value="${rec.enableSubmit ? "true" : "false"}" selected>${rec.enableSubmit ? "Ja" : "Nei"}</option>
                        </select>
                    </td>
                    <td>
                        <button class="btn btn-primary btn-save">Lagre</button>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="form-group">
        <button class="" onclick="window.location.href = '/admin/dashboard';">Tilbake</button>
    </div>
</div>

<!-- footer -->
<%@ include file="footer.jsp" %>