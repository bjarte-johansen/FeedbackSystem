// util by chatGPT, modified by us, only written by ChatGPT because its a simple utility
// and we wrote it for the original java rendering, CBA also doing it in javascript
function timeAgo(ts) {
    const now = Date.now();
    const diff = Math.max(0, now - ts);

    const units = [
        ["sekund", "sekunder", 1000],
        ["minutt", "minutter", 60 * 1000],
        ["time", "timer", 60 * 60 * 1000],
        ["dag", "dager", 24 * 60 * 60 * 1000],
        ["uke", "uker", 7 * 24 * 60 * 60 * 1000],
        ["måned", "måneder", 30 * 24 * 60 * 60 * 1000],
    ];

    for (let i = units.length - 1; i >= 0; i--) {
        const [singular, plural, ms] = units[i];
        if (diff >= ms) {
            const val = Math.floor(diff / ms);
            return `${val} ${val === 1 ? singular : plural} siden`;
        }
    }

    return "akkurat nå";
}