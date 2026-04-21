class Assert{
    // utility methods, checkType from chatgpt or other project, we assume tested
    static checkArgument(cond, msg){
        if(!cond) throw new Error(msg);
    }

    static isType(v, type){
        return (type === "null" && v === null) ||
            (type === "array" && Array.isArray(v)) ||
            (type === "integer" && Number.isInteger(v)) ||
            (type === "number" && typeof v === "number" && Number.isFinite(v)) ||
            (type === "string" && typeof v === "string") ||
            (type === "object" && v !== null && typeof v === "object" && !Array.isArray(v)) ||
            (type === "boolean" && typeof v === "boolean") ||
            (type === "date" && v instanceof Date && !isNaN(v.getTime()));
    }

    static checkType(v, type, msg){
        this.checkArgument(
            this.isType(v, type),
            msg || `Expected ${type}`
        );
    }

    static checkTypeArray(v, types, msg){
        const names = Array.isArray(types) ? types : types.split(/[ ,|]+/);
        this.checkArgument(
            names.some(name => this.isType(v, name)),
            msg || `Expected one of types ${names.join(",")}`
        );
    }

    static checkIntArray(arr, msg){
        this.checkType(arr, "array", "Argument must be an array of integers");
        this.checkArgument(
            arr.every(el => this.isType(el, "integer")),
            msg || `Expected argument to be array of integers`
        );
    }
}