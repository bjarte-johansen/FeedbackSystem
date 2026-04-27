class PageCursor{
    offset = 0;
    limit = 0;

    constructor(offset, limit) {
        if (!Number.isFinite(offset) || !Number.isFinite(limit) || limit <= 0) {
            throw new Error("Offset must be number, limit > 0");
        }

        this.offset = Math.max(0, offset);
        this.limit = limit;

        Object.freeze(this);
    }

    clone() {
        return new PageCursor(this.offset, this.limit);
    }

    withReset() {
        if(this.offset === 0) return this;

        return new PageCursor(0, this.limit);
    }

    #withOffset(v){
        v = Math.max(0, v);
        const aligned = v - (v % this.limit);
        return (aligned === this.offset) ? this : new PageCursor(aligned, this.limit);
    }

    withOffset(newOffset) {
        return this.#withOffset(newOffset);
    }

    withAdvance(delta) {
        return this.#withOffset(this.offset + delta);
    }

    withAdvancePage(delta){
        return this.#withOffset(this.offset + (delta * this.limit));
    }

    isOutOfBounds(maxCount) {
        return this.offset >= maxCount;
    }

    withClamp(maxCount) {
        if(!this.isOutOfBounds(maxCount)) return this;

        const lastOffset = Math.max(0, Math.floor((maxCount - 1) / this.limit) * this.limit);
        return new PageCursor(lastOffset, this.limit);
    }

    toCsv() {
        return this.offset + "," + this.limit;
    }

    toString() {
        return this.toCsv();
    }
}