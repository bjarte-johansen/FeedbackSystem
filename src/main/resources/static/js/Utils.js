class Utils{
    static clamp(x, min, max) {
        return Math.min(max, Math.max(min, x));
    }


    static parseIntOrIntArrayOr(s, delim, def = null){
        Assert.checkType(delim, "string", "Delimiter must be a string");

        const intVal = this.parseIntOr(s, null);
        if(intVal !== null) return [intVal];

        const arr = s?.split(delim)?.map(x => parseInt(x.trim(), 10)).filter(Number.isInteger);
        return arr.length ? arr : def;
    }


    // modified by chatgpt
    static parseIntOr (v, def = null) {
        if (v == null) return def;

        if (typeof v === "number")
            return Number.isInteger(v) ? v : def;

        if (typeof v === "string") {
            const s = v.trim();
            if (!/^-?\d+$/.test(s)) return def; // only base-10 ints
            return Number(s);
        }

        return def;
    }


    static parseDateOr(s, def = null){
        if (!s) return def;
        const d = new Date(s);
        return isNaN(d.getTime()) ? def : d;
    }


    static isEmptySelectValue(v){
        return v === null || v === "";
    }


    /**
     * parse value has been written by chatGPT, it tries to parse a string value into a boolean, number, or
     * leaves it as a string if it cannot be parsed. It handles trimming whitespace, case-insensitive boolean
     * parsing, and strict number parsing that only accepts valid finite numbers. This is useful for converting
     * string values from data attributes or user input into their appropriate types for easier handling in the
     * code.
     */

    static parsePrimitive(v) {
        if (typeof v !== "string") return v;

        const s = v.trim();
        if (s === "") return v;

        // boolean
        if (s === "true" || s === "TRUE") return true;
        if (s === "false" || s === "FALSE") return false;

        // number (strict)
        const n = Number(s);
        if (!Number.isFinite(n)) return v;

        // int vs float
        return Number.isInteger(n) ? n : n;
    }


    /**
     * creates an immutable page cursor object with the given offset and limit. The returned object has methods to
     * advance the cursor by a given delta (number of pages) while ensuring it does not exceed a maximum offset, and
     * to serialize the cursor to a CSV string format. This is used for managing pagination state when fetching review
     * lists from the API.
     */

    static createPageCursorFromString(cursorStr) {
        const parts = (cursorStr || "0,9007199254740991").split(",");

        const offset = Review.utils.parseIntOr(parts?.[0], 0);
        const limit = Review.utils.parseIntOr(parts?.[1], Number.MAX_SAFE_INTEGER);

        console.log("Creating page cursor from string:", cursorStr, "parsed values:", parts);

        return new PageCursor(offset, limit);
    }

    /*
   // modified by chatgpt
   static snakeToCamel (s) {
       let out = '', up = false;
       for (let i = 0; i < s.length; i++) {
           const ch = s[i];
           if (ch === '_') {
               up = true;
           } else {
               out += up ? ch.toUpperCase() : ch;
               up = false;
           }
       }
       return out;
   }

   // modified by chatgpt
   static dashedToCamel (s) {
       let out = '', up = false;
       for (let i = 0; i < s.length; i++) {
           const ch = s[i];
           if (ch === '-') {
               up = true;
           } else {
               out += up ? ch.toUpperCase() : ch;
               up = false;
           }
       }
       return out;
   }
   */
}