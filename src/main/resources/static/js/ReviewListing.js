/*
class ListingFilter{
    orderId = null;             // order by enum

    score = [];                 // scores to include
    status = [];                // status to include
    cursor = null;              // page cursor

    fromDate = null;            // date from
    toDate = null;              // date to

    days = null;                // alternative to from/to date, e.g. last 30 days
}
*/

/**
 * QueryOptions class needs all filters to be correctly set, either null or array or integer
 */


class ReviewListing {
    //#newFilters = new ListingFilter();

    externalId = null;
    cursor = new PageCursor(0, Number.MAX_SAFE_INTEGER);
    orderByEnum = -1;

    filters = {
        scoreFilter: [],
        statusFilter: [],
        startDateFilter: null,
        endDateFilter: null,
        numberOfDaysFilter: null
    };
    statistics = {};
    constants = {};

    #reviewListingConfig = {};

    #includeStatsOnce = false;

    #templateHtml = null;

    #filterSnapshot = structuredClone(this.filters);
    #orderByEnumSnapshot = structuredClone(this.orderByEnum);

    constructor() {
    }

    setReviewListingConfig(config){
        Assert.checkArgument(config !== null, "Missing config object");

        this.#reviewListingConfig = config;
    }
    getReviewListingConfig(){

        return this.#reviewListingConfig;
    }

    loadFilterSnapshot(){
        this.filters = structuredClone(this.#filterSnapshot);
        this.orderByEnum = structuredClone(this.#orderByEnumSnapshot);
    }
    saveFilterSnapshot(){
        this.#filterSnapshot = structuredClone(this.filters);
        this.#orderByEnumSnapshot = structuredClone(this.orderByEnum);
    }

    getNumberOfDaysFilter(){
        return this.filters.numberOfDaysFilter;
    }
    setNumberOfDaysFilter(val){
        if(val !== null){
            Assert.checkType(val, "integer", "Number of days must be an integer");
        }
        this.filters.numberOfDaysFilter = val;
        console.log("updated numberOfDaysFilter", val);
    }

    getStartDateFilter(){
        return this.filters.startDateFilter;
    }
    setStartDateFilter(val){
        Assert.checkTypeArray(val, ["date", "null"], "Start date must be a valid date or null");
        this.filters.startDateFilter = val;
        console.log("updated startDateFilter", val);
    }

    getEndDateFilter(){
        return this.filters.endDateFilter;
    }
    setEndDateFilter(val){
        Assert.checkTypeArray(val, ["date", "null"], "End date must be a valid date or null");
        this.filters.endDateFilter = val;
        console.log("updated endDateFilter", val);
    }

    getScoreFilter(){ return this.filters.scoreFilter; }
    setScoreFilter(val){
        if(val !== null){
            Assert.checkIntArray(val, "scoreFilter must be an array of integers");
        }

        this.filters.scoreFilter = val;
        console.log("updated scoreFilter", val);
    }

    getStatusFilter(){ return this.filters.statusFilter; }
    setStatusFilter(val){
        console.log("updating scoreFilter", val);

        if(Assert.isType(val, "integer")){
            this.filters.statusFilter = [val];
            return;
        }

        if(Assert.isType(val, "array")){
            Assert.checkIntArray(val, "statusFilter must be an array of integers");
            this.filters.statusFilter = val;
            return;
        }

        if(val === null || val === "" || val === undefined) {
            this.filters.statusFilter = [];
            return;
        }

        throw new Error("invalid argument type for statusFilter, found (" + (typeof val) + ")");
    }

    getOrderByEnum(){ return this.filters.orderByEnum; }
    setOrderByEnum(val){
        console.log("updating orderByEnum", val);

        if(val === null || val === ""){
            this.filters.orderByEnum = null;
            return;
        }

        Assert.checkType(val, "integer", ["orderByEnum must be integer", "actual", val, typeof val].join(","));

        this.filters.orderByEnum = val;
    }

    getCursor(){
        return this.cursor;
    }
    setCursor(cursor){
        this.cursor = cursor;
    }

    getIncludeStatsOnce(){
        return this.#includeStatsOnce;
    }
    setIncludeStatsOnce(bool_val){
        this.#includeStatsOnce = bool_val;
        console.log("updated includeStatsOnce", bool_val);
    }

    /*
    getFilters(){
        if(!this.#newFilters) throw new Error("getFilters failed");

        return this.#newFilters;
    }
    setFilters(filters){
        this.#newFilters = filters;
    }
    */

    getExternalId(){
        return this.externalId;
    }
    setExternalId(id){
        this.externalId = id;
    }

    getConstants(){
        return this.constants;
    }
    setConstants(values){
        this.constants = values;
    }

    getReviewStatusAsFriendlyName(status){
        return this?.constants?.reviewStatus?.constToFriendlyName[status] ?? "unknown-status";
    }



    setItems(items){
        this.reviews = items;
    }
    getItems(){
        return this.reviews;
    }

    setStatistics(stats){
        this.statistics = stats;
    }
    getStatistics(){
        return this.statistics;
    }

    getTotalReviewCount(){
        return this.statistics?.totalCount ?? 0;
    }

    buildQuerySearchParams() {
        const map = new Map();

        map.set("cursor", this.getCursor().toCsv());
        map.set("orderByEnum", this.getOrderByEnum() ?? "");
        map.set("externalId", this.getExternalId() ?? "");

        map.set("scoreFilter", this.getScoreFilter().join(","));
        map.set("statusFilter", this.getStatusFilter().join(","));

        map.set("startDateFilter", this.getStartDateFilter()?.toISOString()?.slice(0, 10) ?? "");
        map.set("endDateFilter", this.getEndDateFilter()?.toISOString()?.slice(0, 10) ?? "");

        map.set("numberOfDaysFilter", this.getNumberOfDaysFilter() ?? "");

        if(this.getIncludeStatsOnce()){
            this.setIncludeStatsOnce(false);
            map.set("includeStats", "true");
        }

        console.log(map);

        return new URLSearchParams(Object.fromEntries(map));
    }


    #getReviewCountByScore(score) {
        return this.statistics?.scoreCount[score] || this.getTotalReviewCount();
    }

    getAccumulatedReviewCountByScoreFilter() {
        const values = this.getScoreFilter();
        const filteredCount = values?.reduce(
            (acc, score) => acc + this.#getReviewCountByScore(score),
            0);
        return filteredCount || this.getTotalReviewCount();
    }

    loadItemsFromJson(o){
        this.setItems(o.reviews);

        this.renderItems();
    }

    loadOptionsFromJson(o){
        const cursor = o?.pageCursor ? Review.utils.createPageCursorFromString(o.pageCursor) : new PageCursor(0, Number.MAX_SAFE_INTEGER);
        this.setCursor(cursor);

        this.setExternalId(o.externalId ?? "");
        this.setOrderByEnum(o?.orderByEnum ?? "");

        if(!o?.filters) {
            console.log("Error loading options from json, no 'filters' key present");
            return;
        }

        this.setScoreFilter(o?.filters?.scoreFilter);
        this.setStatusFilter(o?.filter?.statusFilter);
        this.setStartDateFilter(o.filters.startDateFilter);
        this.setEndDateFilter(o.filters.endDateFilter);
        this.setNumberOfDaysFilter(o?.filters.numberOfDaysFilter);
    }

    loadAllFromJson(o) {
        console.log("json", JSON.stringify(o, null, 2))

        this.setReviewListingConfig(o.reviewConfig);
        this.loadOptionsFromJson(o);
        this.setConstants(o.constants);
        this.setStatistics(o.statistics);
        this.setItems(o.reviews);

        this.renderItems();

        return this;
    }

    getTemplateHtml() {
        if (this.#templateHtml === null) {
            // load template html from DOM
            const $tpl = $('.review--list .review-item-template');
            if (!$tpl) throw new Error("Unable to find first element child");
            this.#templateHtml = $tpl[0].outerHTML;
        }

        return this.#templateHtml;
    }

    renderItems(){
        const _this = this;

        const $itemsContainer = $(".review--list-items .items");
        $itemsContainer.empty().hide();

        if(!this.reviews) {
            console.log("cannot find reviews by json, we probably in admininterface");
            return;
        }

        this.reviews.forEach((r) => {
            if(r === null) return;
            const $item = this.renderItem(r);
            $itemsContainer.append($item);
        });

        $itemsContainer.show();
    }

    renderItem(review){
        //console.log("called renderItem", review);

        let cloneHtml = this.getTemplateHtml();
        cloneHtml = cloneHtml.replaceAll("{reviewId}", review.id);
        cloneHtml = cloneHtml.replaceAll("${review.id}", review.id);
        //cloneHtml = cloneHtml.replaceAll("{reviewStatus}", review.status);

        const roundToHalf = (x) => Math.round(x * 2.0) / 2.0;
        const formatNumber = (num, maxPrec = 2, minPrec = 0, dot_to_dash) =>
            num.toLocaleString("en-US", {
                minimumFractionDigits: minPrec,
                maximumFractionDigits: maxPrec
            }).replace(".", dot_to_dash ? "-" : ".");

        const avgScore = Math.round((this.statistics.averageScore * 2.0) / 2.0);
        const avgDashedScore = (x) => Math.round((x * 2.0) / 2.0).replace(".", "-");

        const friendlyStatusName = this.getReviewStatusAsFriendlyName(review.status);

        const $clone = $(cloneHtml);
        $clone.removeClass("review-item-template").addClass("box review mb-2");
        //$clone.addClass("review-" + review.id);

        $clone.find('.score-text').text(`${review.score}/5`);
        $clone.find('.score-value').addClass(`score-${formatNumber(roundToHalf(review.score),1,1, true)}`);
        $clone.find(".title").text(`${review.title ?? "(tom)"}`);
        $clone.find(".name").text(`${review.authorName ?? "(anonym)"}`);
        $clone.find(".time").text(timeAgo(review.createdAt))
        $clone.find(".comment").text(review.comment ?? "(empty)");
        $clone.find(".like-count").text(review.likeCount);
        $clone.find(".dislike-count").text(review.dislikeCount);
        //$clone.attr("data-review-status-const", review.status);
        //$clone.attr("data-review-status-name", friendlyStatusName);
        $clone.addClass("review-item-" + review.id);
        $clone.addClass("review-item-" + friendlyStatusName);
        $clone.find('.status').text(friendlyStatusName);

        return $clone;
    }
}