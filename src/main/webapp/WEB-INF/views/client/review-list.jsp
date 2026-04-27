<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="review--list-items review--admin">
    <!--<p class="alert alert-info">Remember to remove review--admin before deploy</p>-->

    <!-- element to show if review were found -->
    <div class="items">
    </div>

    <!-- element to show if no review were found -->
    <div class="no-items">
        <p>Ingen elementer funnet</p>
    </div>

    <!-- START OF TEMPLATE -
        note: this is our template used for rendering items, it can be adapted but classes must be accurate so replacing values still work :)
    -->
    <div class="review-item-template d-none box">
        <div class="score-outer mb-0">
            <strong class="score-text"></strong> <span class="score score-value"></span>
        </div>

        <span class="title mb-2">{title}</span>

        <span class="name mb-0">{authorName}</span>

        <em class="time mb-2">{short date string}</em>

        <span class="comment mb-2">{comment}</span>

        <div class="votes mb-0 client-only">
            <span class="vote up-votes">
                <a href="/R/review/{reviewId}/like">
                    <span class="text"><span class="like-count">-</span></span>
                </a>
            </span>

            <span class="vote down-votes">
                <a href="/R/review/{reviewId}/dislike">
                    <span class="text"><span class="dislike-count">-</span></span>
                </a>
            </span>
        </div>

        <!-- TODO add admin test instead of just relying on css -->
        <c:if test="${isAdministrator}">
            <%@ include file="../admin/admin-review-moderate-buttons.jsp" %>
        </c:if>
    </div>
    <!-- END OF TEMPLATE -->

</div>



