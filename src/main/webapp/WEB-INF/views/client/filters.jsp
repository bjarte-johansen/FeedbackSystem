<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="box review--list-filters">
    <div class="box filter-buttons">
        (Admin uferdig)
        Filtrer status:
        <a href="/R/admin/filter/status/1" class="btn text-deco-none filter-approved">Godkjent</a>
        <a href="/R/admin/filter/status/2" class="btn text-deco-none filter-pending">Kontroll</a>
        <a href="/R/admin/filter/status/3" class="btn text-deco-none filter-rejected">Avvist</a>
        <a href="/R/admin/filter/status/-1" class="btn text-deco-none filter-any">Alle</a>
        )
    </div>

    <span class="active-filters mobile-full d-none" style="margin-right:8px;">
        Aktive filtre:
        <span class="items mobile-full"></span>

        <a class="btn text-deco-none btn-primary btn-remove-filters mobile-full" href="javascript:void(0);" onclick="Review.activeFiltersHelper.clear();">Fjern filtre</a>

        <div class="templates d-none">
            <a class="btn text-deco-none btn-primary mobile-full button-template disabled" href="javascript:void(0)">Default text</a>
        </div>
    </span>

    <!-- score filter dropdown -->
    <select name="scoreFilter" onchange="Review.triggerClientScoreFilterChange(this)">
        <!--<option value="${scoreFilter}" selected>Filtrer score</option>-->

        <c:forEach var="i" begin="1" end="5">
            <option value="${i}" ${scoreFilter == i ? 'selected' : ''}>Score: ${i}</option>
        </c:forEach>
        <option value="" selected>Score: alle</option>
    </select>

    <!-- order by dropdown -->
    <select name="orderByEnum" onchange="Review.triggerClientOrderByEnumChange(this)">
        <c:forEach var="orderByOption" items="${reviewListOrderOptions}" varStatus="loop">
            <option value="${orderByOption.value}" ${orderByOption.value == currentOrderByEnum ? 'selected' : ''}>${orderByOption.key}</option>
        </c:forEach>
        <option value="" selected>Sorter: vanlig</option>
    </select>

    <hr>

    <!-- order by dropdown -->
    <select name="statusFilterEnum" onchange="Review.triggerClientStatusFilterChange(this)">
        <c:forEach var="mapEntry" items="${constants.reviewStatus.friendlyNameToConst}" varStatus="loop">
            <option value="${mapEntry.value}">${mapEntry.key}</option>
        </c:forEach>
        <option value="1,2,3">Alle</option>
        <option value="" selected>Status: valgt</option>
    </select>

    <hr>

    <div class="box">
        Dato fra
        <input type="date" name="startDateFilter" value="${filters.startDateFilter}" onchange="Review.triggerStartDateFilterChange(this)">
        til
        <input type="date" name="endDateFilter" value="${filters.endDateFilter}" onchange="Review.triggerEndDateFilterChange(this)">
        , Antall dager:
        <select name="numberOfDaysFilter" onchange="Review.triggerNumberOfDaysFilterChange(this)">
            <option value="7" ${currentDateFilterPreset == 7 ? 'selected' : ''}>Siste 7 dager</option>
            <option value="30" ${currentDateFilterPreset == 30 ? 'selected' : ''}>Siste 30 dager</option>
            <option value="90" ${currentDateFilterPreset == 90 ? 'selected' : ''}>Siste 90 dager</option>
            <option value="180" ${currentDateFilterPreset == 180 ? 'selected' : ''}>Siste 180 dager</option>
            <option value="365" ${currentDateFilterPreset == 365 ? 'selected' : ''}>Siste 1 år</option>
            <option value="730" ${currentDateFilterPreset == 365 ? 'selected' : ''}>Siste 2 år</option>
            <option value="" selected>Intervall: ingen</option>
        </select>
    </div>
</div>