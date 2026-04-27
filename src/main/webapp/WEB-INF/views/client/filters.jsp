<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box review--list-filters">
    <div class="active-filters mobile-full d-none" style="margin-right:8px; display:inline-block">
        Aktive filtre:
        <span class="items mobile-full"></span>

        <a class="btn text-deco-none btn-primary btn-remove-filters mobile-full" href="javascript:void(0);" onclick="Review.activeFiltersHelper.clear();">Fjern filtre</a>

        <div class="templates d-none">
            <a class="btn text-deco-none btn-primary mobile-full button-template disabled" href="javascript:void(0)">Default text</a>
        </div>
    </div>

    <!-- score filter dropdown -->
    <span class="filter-component">
        <label for="scoreFilter" class="d-none">Filtrer: </label>
        <select id="scoreFilter" name="scoreFilter" onchange="Review.clientTriggers.triggerClientScoreFilterChange(this)">
            <c:forEach var="i" begin="1" end="5">
                <option value="${i}" ${filters.scoreFilter.contains(i) ? 'selected' : ''}>Score: ${i}</option>
            </c:forEach>
            <option value="" selected>Score: alle</option>
        </select>
    </span>

    <!-- order by dropdown -->
    <span class="filter-component">
        <label for="orderByEnum" class="d-none">Sorter: </label>
        <select id="orderByEnum" name="orderByEnum" onchange="Review.clientTriggers.triggerClientOrderByEnumChange(this)">
        </select>
    </span>

    <!-- <hr> -->

    <!-- order by dropdown -->
    <span class="filter-component admin-only">
        <select name="statusFilterEnum" onchange="Review.clientTriggers.triggerClientStatusFilterChange(this)">
            <c:forEach var="mapEntry" items="${constants.reviewStatus.constNorwegianName}" varStatus="loop">
                <option value="${mapEntry.key}">${mapEntry.value} (${countByReviewStatus[mapEntry.key]})</option>
            </c:forEach>
            <option value="1,2,3">Alle</option>
            <option value="" selected>Status: valgt</option>
        </select>
    </span>

    <!-- <hr> -->

<!--
    <span class="filter-component">
        Dato fra
        <input type="date" name="startDateFilter" value="${filters.startDateFilter}" onchange="Review.clientTriggers.triggerStartDateFilterChange(this)">
        til
        <input type="date" name="endDateFilter" value="${filters.endDateFilter}" onchange="Review.clientTriggers.triggerEndDateFilterChange(this)">
    </span>
    ,
-->
    <span class="filter-component">
        Antall dager:
        <select name="numberOfDaysFilter" onchange="Review.clientTriggers.triggerNumberOfDaysFilterChange(this)">
            <option value="7" ${filters.numberOfDaysFilter == 7 ? 'selected' : ''}>Siste 7 dager</option>
            <option value="30" ${filters.numberOfDaysFilter == 30 ? 'selected' : ''}>Siste 30 dager</option>
            <option value="90" ${filters.numberOfDaysFilter == 90 ? 'selected' : ''}>Siste 90 dager</option>
            <option value="180" ${filters.numberOfDaysFilter == 180 ? 'selected' : ''}>Siste 180 dager</option>
            <option value="365" ${filters.numberOfDaysFilter == 365 ? 'selected' : ''}>Siste 1 år</option>
            <option value="730" ${filters.numberOfDaysFilter == 365 ? 'selected' : ''}>Siste 2 år</option>
            <option value="" selected>Alle</option>
        </select>
    </span>
</div>