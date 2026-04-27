class Assert{
    // utility methods
    static isBlank(v){
        return v == null || (typeof v === "string" && v.trim().length === 0);
    }

    static isNullOrEmptyArray(v){
        return v == null || Assert.isEmptyArray(v);
    }
    static isEmptyArray(arr){
        return Array.isArray(arr) && arr.length === 0;
    }

    static hasText(v){
        return (typeof v === "string") && (v.length > 0) && (v.trim().length > 0);
    }

    static isEmpty(v){
        return v == null || v === "";
    }
    static notEmpty(v, msg = "Argument must not be empty"){
        if(this.isEmpty(v))
            throw new Error(msg);
    }

    static isType(v, type){
        switch(type){
            case "null":      return v === null;
            case "undefined": return v === undefined;
            case "function":  return typeof v === "function";
            case "array":     return Array.isArray(v);
            case "integer":   return Number.isInteger(v);
            case "number":    return typeof v === "number" && Number.isFinite(v);
            case "string":    return typeof v === "string";
            case "object":    return v !== null && typeof v === "object" && !Array.isArray(v);
            case "boolean":   return typeof v === "boolean";
            case "date":      return v instanceof Date && !isNaN(v.getTime());
            default:          return false;
        }
    }

    // utility methods, checkType from chatgpt or other project, we assume tested
    static checkArgument(cond, msg){
        if(!cond) throw new Error(msg);
    }

    static checkType(v, type, msg){
        this.checkArgument(
            this.isType(v, type),
            msg || `Expected ${type}`
        );
    }

    static checkTypeArray(v, types, msg){
        types = Array.isArray(types) ? types : types.split(/[ ,|]+/);
        this.checkArgument(
            types.some(type => this.isType(v, type)),
            msg || `Expected one of types ${types.join(",")}`
        );
    }

    // checks that input is array and tht every typeof element == type
    // TODO: optimize, ut should use internal methods
    static checkIntArray(arr, msg){
        this.checkArrayOf(arr, "integer", msg);
    }

    // checks that input is array and that every typeof element == type
    static checkTypeArrayOf(arr, type, msg){
        this.checkArrayOf(arr, type, msg);
    }

    // checks that input is array and that every typeof element == type
    static checkArrayOf(arr, type, msg){
        this.checkType(arr, "array", `Argument must be an array of ${type}`);
        this.checkArgument(
            arr.every(el => this.isType(el, type)),
            msg || `Expected argument to be array of ${type}`
        );
    }
}