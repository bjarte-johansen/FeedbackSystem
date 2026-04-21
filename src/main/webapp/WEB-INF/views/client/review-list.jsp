<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="review--list-items review--admin">
    <p class="alert alert-info">Remember to remove review--admin before deploy</p>

    <div class="items">
    </div>

    <!-- START OF TEMPLATE -
        note: this is our template used for rendering items, it can be adapted but classes must be accurate so replacing values still work :)
    -->
    <div class="review-item-template d-none box mb-2">
        <p class="review-status-outer">Status: <span class="status">${constants.reviewStatus.constToFriendlyName[review.status]}</span></p>

        <div class="score-outer mb-0">
            <strong class="score-text"></strong> <span class="score score-value"></span>
        </div>

        <span class="title mb-2">{title}</span>

        <span class="name mb-0">{authorName}</span>

        <em class="time mb-2">{short date string}</em>

        <span class="comment mb-2">{comment}</span>

        <div class="votes mb-0">
            <span class="vote up-votes">
                <a href="/R/review/{reviewId}/like" class="vote up-votes text-deco-none">
                    <svg aria-hidden="true" viewBox="0 0 16 16" fill="inherit" class="" xmlns="http://www.w3.org/2000/svg" width="14px" height="14px" role="img"><path fill-rule="evenodd" clip-rule="evenodd" d="M7.94.94A1.5 1.5 0 0 1 10.5 2a20.774 20.774 0 0 1-.384 4H14.5A1.5 1.5 0 0 1 16 7.5v.066l-1.845 6.9-.094.095A1.5 1.5 0 0 1 13 15H9c-.32 0-.685-.078-1.038-.174-.357-.097-.743-.226-1.112-.349l-.008-.003c-.378-.126-.74-.246-1.067-.335C5.44 14.047 5.18 14 5 14v.941l-5 .625V6h5v.788c.913-.4 1.524-1.357 1.926-2.418A10.169 10.169 0 0 0 7.5 1.973 1.5 1.5 0 0 1 7.94.939ZM8 2l.498.045v.006l-.002.013a4.507 4.507 0 0 1-.026.217 11.166 11.166 0 0 1-.609 2.443C7.396 5.951 6.541 7.404 5 7.851V13c.32 0 .685.078 1.038.174.357.097.743.226 1.112.349l.008.003c.378.126.74.246 1.067.335.335.092.594.139.775.139h4a.5.5 0 0 0 .265-.076l1.732-6.479A.5.5 0 0 0 14.5 7H8.874l.138-.61c.326-1.44.49-2.913.488-4.39a.5.5 0 0 0-1 0v.023l-.002.022L8 2ZM4 7H1v7.434l3-.375V7Zm-1.5 5.75a.25.25 0 1 0 0-.5.25.25 0 0 0 0 .5Zm-.75-.25a.75.75 0 1 1 1.5 0 .75.75 0 0 1-1.5 0Z"></path></svg>
                    <span class="text"><span class="like-count">-</span></span>
                </a>
            </span>

            <span class="vote down-votes">
                <a href="/R/review/{reviewId}/dislike" class="vote down-votes text-deco-none">
                    <svg aria-hidden="true" viewBox="0 0 16 16" fill="inherit" class="flip-vertical" xmlns="http://www.w3.org/2000/svg" width="14px" height="14px" role="img"><path fill-rule="evenodd" clip-rule="evenodd" d="M7.94.94A1.5 1.5 0 0 1 10.5 2a20.774 20.774 0 0 1-.384 4H14.5A1.5 1.5 0 0 1 16 7.5v.066l-1.845 6.9-.094.095A1.5 1.5 0 0 1 13 15H9c-.32 0-.685-.078-1.038-.174-.357-.097-.743-.226-1.112-.349l-.008-.003c-.378-.126-.74-.246-1.067-.335C5.44 14.047 5.18 14 5 14v.941l-5 .625V6h5v.788c.913-.4 1.524-1.357 1.926-2.418A10.169 10.169 0 0 0 7.5 1.973 1.5 1.5 0 0 1 7.94.939ZM8 2l.498.045v.006l-.002.013a4.507 4.507 0 0 1-.026.217 11.166 11.166 0 0 1-.609 2.443C7.396 5.951 6.541 7.404 5 7.851V13c.32 0 .685.078 1.038.174.357.097.743.226 1.112.349l.008.003c.378.126.74.246 1.067.335.335.092.594.139.775.139h4a.5.5 0 0 0 .265-.076l1.732-6.479A.5.5 0 0 0 14.5 7H8.874l.138-.61c.326-1.44.49-2.913.488-4.39a.5.5 0 0 0-1 0v.023l-.002.022L8 2ZM4 7H1v7.434l3-.375V7Zm-1.5 5.75a.25.25 0 1 0 0-.5.25.25 0 0 0 0 .5Zm-.75-.25a.75.75 0 1 1 1.5 0 .75.75 0 0 1-1.5 0Z"></path></svg>
                    <span class="text"><span class="dislike-count">-</span></span>
                </a>
            </span>
        </div>

        <br>
        <%@ include file="../admin/admin-review-moderate-buttons.jsp" %>
    </div>
    <!-- END OF TEMPLATE -->

</div>



