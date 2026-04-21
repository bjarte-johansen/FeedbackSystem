<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box review--list-filters d-none">
    <span class="active-filters mobile-full d-none" style="margin-right:8px;">
        Aktive filtre:
        <span class="items mobile-full"></span>

        <a class="btn text-deco-none btn-primary btn-remove-filters mobile-full" href="javascript:void(0);" onclick="Review.client.clear();">Fjern filtre</a>

        <div class="templates d-none">
            <a class="btn text-deco-none btn-primary mobile-full button-template disabled" href="/reviews/build-html?externalId=/product/1">Score: 5</a>
        </div>
    </span>

    <%--
    <!-- score filter dropdown -->
    <select name="scoreFilter" onchange="Review.triggerClientScoreFilterChange(this)">
        <option value="${scoreFilter}" selected}>Filtrer score</option>

        <c:forEach var="i" begin="1" end="5">
            <option value="${i}" ${scoreFilter == i ? 'selected' : ''}>Score: ${i}</option>
        </c:forEach>
        <option value="-1">Alle</option>
    </select>

    <!-- order by dropdown -->
    <select name="orderByEnum" onchange="Review.triggerClientOrderByEnumChange(this)">
        <c:forEach var="orderByOption" items="${reviewListOrderOptions}" varStatus="loop">
            <option value="${orderByOption.value}" ${orderByOption.value == currentOrderByEnum ? 'selected' : ''}>${orderByOption.key}</option>
        </c:forEach>
    </select>
    --%>
</div>