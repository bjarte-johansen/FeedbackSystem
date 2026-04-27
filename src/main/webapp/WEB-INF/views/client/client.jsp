<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
<c:forEach var="e" items="${requestScope}">
    <c:if test="${not fn:startsWith(e.key, 'org.springframework') and not fn:startsWith(e.key, 'jakarta')}">
        <c:if test="${not fn:startsWith(e.key, 'reviews')}">
            <c:if test="${
                not fn:startsWith(e.key, 'hiddenHttpMethodFilter') and
                not fn:startsWith(e.key, 'characterEncodingFilter') and
                not fn:startsWith(e.key, 'formContentFilter') and
                not fn:startsWith(e.key, 'requestContextFilter') and
                not fn:startsWith(e.key, 'myRequestContextFilter') and
                not fn:startsWith(e.key, 'json')
            }">
                ${e.key} = ${e.value}<br>
            </c:if>
        </c:if>
    </c:if>
</c:forEach>
--%>


<c:if test="${not enableListing}">
    <div class="container--reviews d-none">
        <div class="box-virtual review--list ${isClient ? 'is-client' : ''} ${isAdministrator ? 'is-admin' : ''}">
            <p>Omtaler er slått av for denne siden</p>
        </div>
    </div>
</c:if>

<c:if test="${enableListing}">
    <link rel="stylesheet/less" href="/css/main.less">
    <script src="https://cdn.jsdelivr.net/npm/less@4"></script>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <script src="/js/classes/Constants.js"></script>
    <script src="/js/classes/Scroll.js"></script>
    <script src="/js/classes/TimeAgoFormatter.js"></script>
    <script src="/js/classes/Assert.js"></script>
    <script src="/js/classes/Spinner.js"></script>
    <script src="/js/classes/Async.js"></script>
    <script src="/js/classes/Router.js"></script>
    <script src="/js/classes/PageCursor.js"></script>
    <script src="/js/classes/ReviewQueryOptions.js"></script>
    <script src="/js/classes/ReviewListing.js"></script>
    <script src="/js/classes/ReviewListingRenderer.js"></script>
    <script src="/js/classes/StatisticsRenderer.js"></script>
    <script src="/js/classes/UiReviewListingFilter.js"></script>
    <script src="/js/classes/NewReviewFormValidator.js"></script>
    <script src="/js/classes/Utils.js"></script>
    <script src="/js/classes/UserInterfaceTriggers.js"></script>
    <script src="/js/classes/ModalDialog.js"></script>

    <script src="/js/routes.js"></script>
    <script src="/js/client-review.js"></script>


    <!-- review section part -->
    <div class="container--reviews d-none">
        <div class="box-virtual review--list ${isClient ? 'is-client' : ''} ${isAdministrator ? 'is-admin' : ''}"
            data-json="${fn:escapeXml(json)}"
            data-enable-listing="${reviewConfig.enableListing}"
            data-is-client="${isClient}"
            data-is-administrator="${isAdministrator}">

            <div class="admin-only">
                <!--<h2>Administrator settings</h2>-->
                <div class="d-flex">
                    <button type="button" class="btn btn-primary" onclick="window.location.href='/api/review/settings/list';">⚙ Artikkel innst.</button>
                    <button type="button" class="btn btn-primary " onclick="window.location.href='/api/tenant/edit/form/html';">⚙ Hoved innst</button>
                    <button type="button" class="btn btn-primary ms-auto" onclick="window.location.href='/admin/logout';">Logout</button>
                </div>
            </div>

            <div class="box-virtual mb-4">
                <%@ include file="reviews-statistics.jsp" %>

                <!-- form to submit new review -->
                <div class="box-virtual submit-review-form-container"></div>
            </div>

            <%@ include file="filters.jsp" %>

            <%@ include file="admin-paginator.jsp" %>

            <%@ include file="review-list.jsp" %>

            <%@ include file="admin-paginator.jsp" %>
            <%@ include file="client-paginator.jsp" %>
        </div>
    </div>
</c:if>