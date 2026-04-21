<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <div class="box review review-item-${review.id} review-item-${constants.reviewStatus.constToFriendlyName[review.status]}">

            <div class="score-outer mb-0">
                <strong class="score-text">${review.score}/5</strong> <span class="score score-${review.score}"></span>
            </div>

            <span class="external-id mb-2">Sti: ${not empty review.externalId ? review.externalId : ""}</span>

            <span class="title mb-2">${not empty review.title ? review.title : ""}</span>

            <span class="name mb-0">${empty review.authorName ? 'Anonym' : review.authorName}</span>

            <em class="time mb-2">${review.getShortDateString()}</em>

            <span class="comment mb-4">${review.comment}</span>

            <%@ include file="admin-review-moderate-buttons.jsp" %>
        </div>