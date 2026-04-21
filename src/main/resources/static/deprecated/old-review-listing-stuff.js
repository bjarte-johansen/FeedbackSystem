
document.addEventListener("submit", async e => {
    const form = e.target.closest("form");
    if (!form) return;

    if (!form.matches(".ajax")) return;
    e.preventDefault();

    if (form.matches(".custom-handler")) {
        return false;
    }

    const res = await fetch(form.action, {
        method: (form.method || "POST").toUpperCase(), body: new FormData(form)
    });

    const key = "[FormPoster]";

    // status
    console.log(key, res.status, res.ok, res.statusText);

    const ct = res.headers.get("content-type") || "";
    let text = null;
    if (ct.includes("text") || ct.includes("html")) {
        text = await res.clone().text();
        if (text && text.length > 0) {
            console.log(key, "text:", text);
        }
    }

    if (!res.ok) {
        console.warn(key, "Form submission failed", "HTTP error", res.status);
        return;
    }

    console.log(key, "Form submission succeeded");

    if (form.matches(".reload-on-success")) {
        location.reload();
        return;
    }
    /*
            if (form.dataset.handler) {
                throw new Error("Something is still using deprecated handlers");
                const fn = Review.handlers[form.dataset.handler];
                if (fn) fn(form, res);
            }
     */
});