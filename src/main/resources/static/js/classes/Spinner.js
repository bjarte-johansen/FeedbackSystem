class Spinner{
    static #depth = 0;

    static #ref() {
        let div = document.getElementById("feedback-loader");
        if (!div) {
            div = document.createElement("div");
            div.id = "feedback-loader";
            div.innerHTML = `<div class="spinner"></div>`;

            document.body.appendChild(div);
        }

        return div;
    }

    static spinner(on) {
        const loader = this.#ref();
        if (!loader) throw new Error("feedback loader not found");

        if (on) {
            ++this.#depth;
        } else {
            this.#depth = Math.max(0, --this.#depth);
        }

        const active = this.#depth > 0;
        document.body.classList.toggle("feedback-loading", active);
        loader.style.display = active ? "flex" : "none";
    }

    static async with(fn) {
        this.spinner(true);
        try {
            return await fn();
        } catch(err){
            console.log("Exception triggered", err);
        } finally {
            this.spinner(false); // always runs (success + error)
        }
    }
}