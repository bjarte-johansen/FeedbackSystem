let Utils = {
    requireNonNull: function(value, name = "value") {
        if (value == null) { // catches null AND undefined
            throw new Error(`${name} must not be null`);
        }
        return value;
    },
    validateInputElement(el) {
        el.setCustomValidity("");
        if(!el.checkValidity()) {
            el.reportValidity();
            return false;
        }
        return true;
    }
}


/**
 * Collects form input values based on a selector, returning an array of [name, value] pairs.
 * In part written by chatGPT
 *
 * @param selector
 * @returns Object with input names as keys and their corresponding values
 */

const collectFormInputs = function(selector) {
    let vals = $(selector)
        .filter(el => el.name && !el.disabled)
        .map(el => {
            if (el.type === 'checkbox')
                return [el.name, el.checked];

            if (el.type === 'radio')
                return el.checked ? [el.name, el.value] : null;

            if (el.tagName === 'SELECT' && el.multiple)
                return [el.name, [...el.selectedOptions].map(o => o.value)];

            return [el.name, el.value];
        })
        .filter(Boolean)
        .toArray();

    return Object.fromEntries(vals);
}

document.addEventListener("DOMContentLoaded", function() {
    document.addEventListener("submit", async e => {
        if (!e.target.matches("form.ajax")) {
            console.log("Form submission ignored, not matching selector 'form.ajax'");
            return;
        }
        e.preventDefault();

        const f = e.target;

        const res = await fetch(f.action, {
            method: f.method,
            body: new FormData(f)
        });

        // status
        console.log(res.status, res.statusText);
        console.log(res.ok); // true/false

        // headers
        //console.log([...res.headers.entries()]);

        // body (choose ONE)
        const text = await res.text();
        if (text && text.length > 0) {
            console.log("response.text(): " + text);
        }

        location.reload();
    });

    console.log("main.js loaded");
});