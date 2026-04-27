/**
 * QueryOptions class needs all filters to be correctly set, either null or array or integer
 */

class ReviewQueryOptions{
    #cursor = new PageCursor(0, Number.MAX_SAFE_INTEGER);

    #orderByEnum = -1;

    #filters = {
        scoreFilter: [],
        statusFilter: [],
        startDateFilter: null,
        endDateFilter: null,
        numberOfDaysFilter: null
    };

    clone(){
        const c = new ReviewQueryOptions();
        c.#cursor = this.#cursor.clone();
        c.#orderByEnum = this.#orderByEnum;
        c.#filters = structuredClone(this.#filters);

        return c;
    }


    // return reference to actual filters
    getFilters(){
        return this.#filters;
    }


    //
    logMessage(...args){
        //console.log(...args);
    }


    //
    getNumberOfDaysFilter(){
        return this.#filters.numberOfDaysFilter;
    }
    setNumberOfDaysFilter(val){
        if(val !== null){
            Assert.checkType(val, "integer", "Number of days must be an integer");
        }
        this.#filters.numberOfDaysFilter = val;
        this.logMessage("updated numberOfDaysFilter", val);
    }

    //
    getStartDateFilter(){
        return this.#filters.startDateFilter;
    }
    setStartDateFilter(val){
        Assert.checkTypeArray(val, ["date", "null"], "Start date must be a valid date or null");
        this.#filters.startDateFilter = val;
        this.logMessage("updated startDateFilter", val);
    }

    //
    getEndDateFilter(){
        return this.#filters.endDateFilter;
    }
    setEndDateFilter(val){
        Assert.checkTypeArray(val, ["date", "null"], "End date must be a valid date or null");
        this.#filters.endDateFilter = val;
        this.logMessage("updated endDateFilter", val);
    }

    //
    getScoreFilter(){
        return this.#filters.scoreFilter;
    }
    setScoreFilter(val){
        if(val !== null){
            Assert.checkIntArray(val, "scoreFilter must be an array of integers");
        }

        this.#filters.scoreFilter = val;
        this.logMessage("updated scoreFilter", val);
    }

    //
    getStatusFilter(){
        return this.#filters.statusFilter;
    }
    setStatusFilter(val){
        this.logMessage("updating scoreFilter", val);

        if(Assert.isType(val, "integer")){
            this.#filters.statusFilter = [val];
            return;
        }

        if(Assert.isType(val, "array")){
            Assert.checkIntArray(val, "statusFilter must be an array of integers");
            this.#filters.statusFilter = val;
            return;
        }

        if(Utils.isEmpty(val)) {
            this.#filters.statusFilter = [];
            return;
        }

        throw new Error("invalid argument type for statusFilter, found (" + (typeof val) + ")");
    }

    //
    getOrderByEnum(){
        return this.#orderByEnum;
    }
    setOrderByEnum(val){
        this.logMessage("updating orderByEnum", val);

        if(val === null || val === ""){
            this.#orderByEnum = null;
            return;
        }

        Assert.checkType(val, "integer", ["orderByEnum must be integer", "actual", val, typeof val].join(","));

        this.#orderByEnum = val;
    }

    //
    getCursor(){
        return this.#cursor;
    }
    setCursor(cursor){
        this.#cursor = cursor;
        this.logMessage("updating cursor", cursor);
    }
}