class UiReviewListingFilter{
    constructor(){
        this.filters = new Map();
    }

    getRenderContainer(){
        return Review.getReviewListingDomElement(true)
            .find(".active-filters");
    }
    getOuterContainer(){
        return Review.getReviewListingDomElement(true)
            .find(".review--list-filters");
    }

    toggleFilter (type, text, replace, forced = true) {
        const exists = this.filters.has(type);
        if(!forced && exists){
            this.filters.delete(type);
        } else {
            if(!replace && exists) return;
            this.filters.set(type, text);
        }

        this.render();
    }

    render(){
        const $container = this.getRenderContainer();
        const $items = $container.find(".items").hide().empty();

        if(this.filters.size === 0){
            $container.addClass("d-none");
            return;
        }

        const $tpl = $container.find('.templates .btn');

        for(const [type, text] of this.filters){
            const $item = $tpl
                .clone()
                .addClass(type)
                .text(text);

            $items.append($item);
        }

        $items.show();
        $container.removeClass("d-none").show();
    }

    resetInputs(){
        this.getOuterContainer()
            .find("select, input")
            .val("");
    }

    clear() {
        Spinner.with(() => {
            this.filters.clear();
            this.resetInputs();

            this.render();

            Review.reviewListing.loadFilterSnapshot();

            Review.reloadReviewList({resetCursor: true, reloadStats: true});
        });
    }
}