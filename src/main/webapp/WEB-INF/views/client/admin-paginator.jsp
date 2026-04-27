    <!-- paginator previous/next -->
    <c:if test="${isAdministrator}">
        <div class="box paginator--cursors admin-only">
            <a href="javascript:void(0)" class="btn-previous replace" onclick="Review.advancePage(-1, 'replace');">Forrige</a>
            <a href="javascript:void(0)" class="btn-next replace" onclick="Review.advancePage(1, 'replace');">Neste</a>
        </div>
    </c:if>