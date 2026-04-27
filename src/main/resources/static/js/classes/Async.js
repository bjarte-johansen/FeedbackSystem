class Async{
    static options(method, type, data){
        switch(type){
            case 'form':
                const $form = $(data);
                if(!$form.is("form")) throw new Error("Expected form, found ", $form[0]?.tagName);

                return {
                    method: method,
                    body: new URLSearchParams(new FormData($form[0]))
                };
            case 'form-json':
                // form is in data
                const obj = Object.fromEntries(new FormData($(data)[0]));
                return this.json(method, data);
            case 'json':
                return this.json(method, data);
             default:
                 throw new Error(`Unknown fetch options type: ${type}`);
        }
    }

    static json(method, data){
        return {
            method,
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        };
    }

    static async fetchOk(url, opts = {}){
        console.log("fetch using", opts)
        const res = await fetch(url, opts);

        if (!res.ok) {
            const text = await res.text();
            console.log("HTTP error:", res.status, text);
            return null;
        }

        return res;
    }
}