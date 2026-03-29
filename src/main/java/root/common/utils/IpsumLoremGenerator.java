package root.common.utils;


/**
 * Utility class for generating random Lorem Ipsum text.
 * The generated text will have proper capitalization and punctuation.
 *
 * Original by ChatGPT, licensed under CC-BY-SA 4.0. Source: https://chat.openai.com/
 * Modified by Bjarte Johansen, 2026-03-01.
 */

public class IpsumLoremGenerator {

    /**
     * Generates a random Lorem Ipsum text with the specified number of words.
     * The generated text will have proper capitalization and punctuation.
     *
     * Original by ChatGPT, licensed under CC-BY-SA 4.0. Source: https://chat.openai.com/
     * Modified by Bjarte Johansen, 2026-03-01.
     *
     * @param words the number of words to generate
     * @return a randomly generated Lorem Ipsum text
     */

    public static String generate(int words) {
        final String[] dict = {
            "lorem","ipsum","dolor","sit","amet","consectetur","adipiscing","elit",
            "sed","do","eiusmod","tempor","incididunt","ut","labore","et","dolore",
            "magna","aliqua","ut","enim","ad","minim","veniam","quis","nostrud",
            "exercitation","ullamco","laboris","nisi","ut","aliquip","ex","ea",
            "commodo","consequat"
        };

        var r = java.util.concurrent.ThreadLocalRandom.current();
        var sb = new StringBuilder(words * 6);

        for (int i = 0; i < words; i++) {
            String w = dict[r.nextInt(dict.length)];

            if (i == 0 || sb.charAt(sb.length() - 1) == '.') {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w, 1, w.length());
            } else {
                sb.append(w);
            }

            if (i < words - 1) {
                sb.append(r.nextDouble() < 0.15 ? ". " : " ");
            }
        }

        if (sb.charAt(sb.length() - 1) != '.') sb.append('.');
        return sb.toString();
    }
}
