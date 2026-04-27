class ModalDialog {
    static #baseZIndex = 1000;
    static #uniqueId = 0;

    static #getNextUniqueDepthIndex(){
        const cur = ModalDialog.#baseZIndex + ModalDialog.#uniqueId;
        ModalDialog.#uniqueId += 2;
        return cur;
    }

    static open(what, clone = true){
        const Z = ModalDialog.#getNextUniqueDepthIndex();
        const dialogClass = "showModalDialog-" + Z;

        const $container = $('<div class="overlay"><div class="modal"></div></div>');
        $container.attr("id", dialogClass);

        const $overlay = $container.find(".overlay");
        $overlay.css('zIndex', Z);
        $overlay.addClass(dialogClass);

        const $what = clone ? $(what).clone() : $(what);
        $what.removeClass("d-none");
        $what.show();

        const $modal = $container.find('.modal');
        $modal.css("zIndex", Z + 1);
        $modal.append($what);
        $modal.show();

        $('body').append($container);

        return $container;
    }

    static close(el){
        if(el) $(el).remove();
    }
}