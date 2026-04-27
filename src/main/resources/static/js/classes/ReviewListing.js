

class ReviewListing {
    externalId = null;

    statistics = {};

    // constants = {};
    // #filteredReviewCount = 0;

    #queryOptions = new ReviewQueryOptions();
    #queryOptionsSnapshot = this.#queryOptions.clone();

    #reviewListingConfig = {};
    #includeStatsOnce = false;

    constructor() {
    }


    getQueryOptions(){
        return this.#queryOptions;
    }


    //

    setReviewListingConfig(config){
        Assert.checkArgument(config !== null, "Missing config object");

        this.#reviewListingConfig = config;
    }
    getReviewListingConfig(){
        return this.#reviewListingConfig;
    }

    //

    loadFilterSnapshot(){
        this.#queryOptions = this.#queryOptionsSnapshot.clone();
    }
    saveFilterSnapshot(){
        this.#queryOptionsSnapshot = this.#queryOptions.clone();
    }
    getFilterSnapshot(){
        return this.#queryOptionsSnapshot;
    }


    //

    getIncludeStatsOnce(){
        return this.#includeStatsOnce;
    }
    setIncludeStatsOnce(bool_val){
        this.#includeStatsOnce = bool_val;
    }


    //

    getExternalId(){
        return this.externalId;
    }
    setExternalId(id){
        this.externalId = id;
    }


    //

    // getConstants(){
    //     return this.constants;
    // }
    // setConstants(values){
    //     this.constants = values;
    // }


    //

    setStatistics(stats){
        this.statistics = stats;
    }
    getStatistics(){
        return this.statistics;
    }


    //

    getTotalReviewCount(){
        return this.statistics?.totalCount ?? 0;
    }


    //

    buildQuerySearchParams() {
        const map = new Map();
        const qo = this.#queryOptions;

        map.set("externalId", this.getExternalId() ?? "");

        map.set("cursor", qo.getCursor().toCsv());
        map.set("orderByEnum", qo.getOrderByEnum() ?? "");

        map.set("scoreFilter", qo.getScoreFilter().join(","));
        map.set("statusFilter", qo.getStatusFilter().join(","));

        map.set("startDateFilter", qo.getStartDateFilter()?.toISOString()?.slice(0, 10) ?? "");
        map.set("endDateFilter", qo.getEndDateFilter()?.toISOString()?.slice(0, 10) ?? "");

        map.set("numberOfDaysFilter", qo.getNumberOfDaysFilter() ?? "");

        if(this.getIncludeStatsOnce()){
            this.setIncludeStatsOnce(false);
            map.set("includeStats", "true");
        }

        console.log("buildQuerySearchParams", map);

        return new URLSearchParams(Object.fromEntries(map));
    }


    #getReviewCountByScore(score) {
        return this.statistics?.scoreCount[score] || this.getTotalReviewCount();
    }

    getAccumulatedReviewCountByScoreFilter() {
        const values = this.#queryOptions.getScoreFilter();
        const filteredCount = values?.reduce(
            (acc, score) => acc + this.#getReviewCountByScore(score),
            0);
        return filteredCount || this.getTotalReviewCount();
    }

    loadOptionsFromJson(o){
        Assert.checkType(o, "object", "Argument must be an object");

        this.setExternalId(o.externalId ?? "");
        const qo = this.#queryOptions;

        const cursor = o.pageCursor
            ? Review.utils.createPageCursorFromString(o.pageCursor)
            : new PageCursor(0, Number.MAX_SAFE_INTEGER);

        qo.setCursor(cursor);
        qo.setOrderByEnum(o.orderByEnum ?? "");

        // All of this is valid json, so we could just store it, but it wasnt like that in the earlier
        // development of the project. We dont want to risk anything right before delivery
        qo.setScoreFilter(o.filters?.scoreFilter);
        qo.setStatusFilter(o.filters?.statusFilter);
        qo.setStartDateFilter(o.filters?.startDateFilter);
        qo.setEndDateFilter(o.filters?.endDateFilter);
        qo.setNumberOfDaysFilter(o.filters?.numberOfDaysFilter);
    }

    loadAllFromJson(o) {
        console.log("json", JSON.stringify(o, null, 2));

        // more and more of this is deprecated
        //this.setUnpaginatedFilteredReviewCount(o?.unpaginatedFilteredReviewCount ?? 0);
        //this.setTenantListingConfig(o.tenantConfig);
        //this.setConstants(o.constants);

        this.setReviewListingConfig(o.reviewConfig);
        this.loadOptionsFromJson(o);
        this.setStatistics(o.statistics);

        ReviewListingRenderer.renderItems(o.reviews, true);

        return this;
    }


}

