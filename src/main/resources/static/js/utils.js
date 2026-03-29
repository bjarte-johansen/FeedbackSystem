class JQueryLikeObject{
    items = [];
    length = 0;

    constructor(items){
        this.items = [...items];
        this.length = items.length;
    }

    forEach(fn){
        this.items.forEach(fn);
        return this;
    }
    map(fn){
        return new JQueryLikeObject(this.items.map(fn));
    }
    filter(fn){
        return new JQueryLikeObject(this.items.filter(fn));
    }

    any(fn){
        return this.items.some(fn);
    }
    every(fn){
        return this.items.every(fn);
    }
    none(fn){
        return !this.items.some(fn);
    }

    first(){
        return this.items[0];
    }
    last(){
        return this.items[this.items.length - 1];
    }

    addClass(c){
        this.items.forEach(el => el.classList.add(c));
        return this;
    }
    removeClass(c){
        this.items.forEach(el => el.classList.remove(c));
        return this;
    }
    on(ev, fn){
        this.items.forEach(el => el.addEventListener(ev, fn));
        return this;
    }

    val(v) {
        if (v === undefined) {
            return this.first()?.value;
        } else {
            this.items.forEach(el => el.value = v);
            return this;
        }
    }

    toArray(){
        return this.items;
    }
}

let $ = function(selector, root = document) {
    const el = document.querySelectorAll(selector);
    return new JQueryLikeObject(el);
};