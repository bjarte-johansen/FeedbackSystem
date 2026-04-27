// util by chatGPT, modified by us, only written by ChatGPT because its a simple utility
// and we wrote it for the original java rendering, CBA also doing it in javascript

class TimeAgoFormatter {
    static format(ts) {
        const now = Date.now();
        const diff = Math.max(0, now - new Date(ts).getTime());

        const units = [
            ["år", "år", 365 * 24 * 60 * 60 * 1000],
            ["måned", "måneder", 30 * 24 * 60 * 60 * 1000],
            ["uke", "uker", 7 * 24 * 60 * 60 * 1000],
            ["dag", "dager", 24 * 60 * 60 * 1000],
            ["time", "timer", 60 * 60 * 1000],
            ["minutt", "minutter", 60 * 1000],
            ["sekund", "sekunder", 1000],
        ];

        let remaining = diff;
        const parts = [];

        for (const [singular, plural, ms] of units) {
            if (remaining >= ms) {
                const val = Math.floor(remaining / ms);
                remaining -= val * ms;
                parts.push(`${val} ${val === 1 ? singular : plural}`);
                if (parts.length === 2) break; // limit to 2 parts
            }
        }

        return parts.length ? parts.join(" og ") + " siden" : "akkurat nå";
    }
}