<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <!-- TODO: figure out if we can remove tenantId from rendering to data attributes safely -->

        <div class="box review review--review-${review.id}" data-review-id="${review.id}" data-tenant-id="${tenantId}">
            <h3>Status: ${review.statusToString(review.status)}</h3>


            <div class="score-outer mb-0">
                <strong class="score-text">${review.score}/5</strong> <span class="score score-${review.score}"></span>
            </div>

            <span class="title mb-2">${not empty review.title ? review.title : ""} (id = ${review.id}, @${daysAgoFormatter.apply(review.createdAt)} dager siden)</span>

            <span class="name mb-0">${empty review.authorName ? 'Anonym' : review.authorName}</span>

            <em class="time mb-2">${review.getShortDateString()}</em>

            <span class="comment mb-4">${review.comment}</span>

            <%@ include file="admin-review-moderate-buttons.jsp" %>
        </div>