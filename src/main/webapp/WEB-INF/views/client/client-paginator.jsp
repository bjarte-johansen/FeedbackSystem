    <!-- paginator previous/next -->
<c:if test="${not isAdministrator}">
    <div class="paginator--cursors client-only">
        <a href="javascript:void(0)" onclick="Review.advancePage(1, 'append');" class="btn-next append"></a>
    </div>
</c:if>