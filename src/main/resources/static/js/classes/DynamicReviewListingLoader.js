/**
 * snippet from chatGPT to run javascript from loaded html content
 */
class DynamicReviewListingLoader {

    static async injectHtmlLikePage(container, html) {
        const tmp = document.createElement("div");
        tmp.style.display = "none";
        tmp.innerHTML = html;

        const elReviewList = tmp.querySelector(".review--list");
        if (!elReviewList) throw new Error("review list element on find");

        // uncomment to simulate data-enabled-listing="false"
        //elReviewList.setAttribute("data-enable-listing", "false");

        // check if listing is enabled
        const isListingEnabled = (elReviewList.getAttribute("data-enable-listing") !== "false");

        if (!isListingEnabled) {
            console.log("early rejection or review listing, cause: enable-listing = false")
            return;
        }

        // 1. stylesheets
        tmp.querySelectorAll('link[rel="stylesheet"]').forEach(link => {
            const href = link.href;
            if (!document.querySelector(`link[href="${href}"]`)) {
                const l = document.createElement("link");
                l.rel = "stylesheet";
                l.href = href;
                document.head.appendChild(l);
            }
        });

        // inject HTML
        container.innerHTML = tmp.innerHTML;
        container.style.display = "none";

        // 2. inline styles
        tmp.querySelectorAll("style").forEach(style => {
            const s = document.createElement("style");
            s.textContent = style.textContent;
            document.head.appendChild(s);
        });

        // 3. extract scripts
        const scripts = Array.from(tmp.querySelectorAll("script"));
        scripts.forEach(s => s.remove());

        // 4. run scripts in order
        for (const s of scripts) {
            if (s.src) {
                await new Promise((resolve, reject) => {
                    const script = document.createElement("script");
                    script.src = s.src;
                    script.onload = resolve;
                    script.onerror = reject;
                    document.head.appendChild(script);
                });
            } else {
                // execute inline script in global scope
                //(0, eval)(s.textContent);
                const script = document.createElement("script");
                script.textContent = s.textContent;
                document.head.appendChild(script).remove();
            }
        }

        container.style.display = "";
        tmp.style.display = "";
    }

    /** load review listing, target must be a dom lement, search_params must be a URLSearchParams object */
    static async loadReviewListing(target, search_params) {
        const url = "/api/reviews/list/html?" + search_params.toString();

        const res = await fetch(url);
        const html = await res.text();

        await DynamicReviewListingLoader.injectHtmlLikePage(target, html);
    }
}
