/*
unused but a keeper
 */

const processTemplate = function(str, data) {
    return str.replace(/\$\{([\w.]+)\}/g, (_, path) => {
        const keys = path.split('.');
        let cur = data;

        for (const k of keys) {
            if (!Object.prototype.hasOwnProperty.call(cur, k)) return "";
            cur = cur[k];
        }
        return cur ?? "";
    });
}
