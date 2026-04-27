class Scroll {
    static withStableScroll(fn) {
        const y = window.scrollY;

        const res = fn();
        const restore = () => {
            requestAnimationFrame(() => {
                window.scrollTo(0, y);
            });
        };

        // supports Promise + thenables (fetch, jQuery, etc.)
        if (res && typeof res.then === "function") {
            return res.finally ? res.finally(restore)
                : res.then(restore, restore);
        }

        restore();
        return res;
    }
}