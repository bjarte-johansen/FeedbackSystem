/**
 * DISPATCHER (NOT router) is in part written by chatGPT to save time. Most routers wanted to rewrite window.location
 * but we just want it to intercept our clicks. Its odd that more routers didnt do this, or was too complicated to
 * make work effortlessly.
 */

class Router{
    routes = [];
    started = false;

    route(pattern, fn) {
        const keys = [];

        const regex = new RegExp("^" + pattern
            .replace(/:([^/]+)/g, (_, k) => {
                keys.push(k);
                return "([^/]+)";
            }) + "$"
        );

        this.routes.push({regex, keys, fn});
    }

    getRoutes(){
        return this.routes;
    }

    dispatch(path, e) {
        const routes = this.getRoutes();

        for (const r of routes) {
            const m = path.match(r.regex);
            if (!m) continue;

            if(e) {
                e.preventDefault();
                e.stopImmediatePropagation();
            }

            const params = {};
            r.keys.forEach((k, i) => params[k] = m[i + 1]);

            r.fn({ path, params, e });
            return true;
        }

        return false;
    }

    start(){
        if(this.started) return;
        this.started = true;

        document.addEventListener("click", (e) => {
            const a = e.target.closest("a[href]");
            if (!a) return;

            // only normal left-click
            if (e.button !== 0 || e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;

            const url = new URL(a.href, location.href);

            // only same-origin
            if (url.origin !== location.origin) return;

            const path = url.pathname;
            this.dispatch(path, e);


        }, true); // capture = important
    }
}