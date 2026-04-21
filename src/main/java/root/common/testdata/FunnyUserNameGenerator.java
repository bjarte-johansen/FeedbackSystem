package root.common.testdata;


/**
 * A utility class for generating random funny usernames by combining random adjectives, nouns, and numbers.
 * This can be used for creating unique and amusing usernames for various applications.
 *
 * Original by ChatGPT, licensed under CC-BY-SA 4.0. Source: https://chat.openai.com/
 * Modified by Bjarte Johansen, 2026-03-01.
 */
public class FunnyUserNameGenerator {
    private final static String[] adj = {"Wobbly","Sneaky","Chunky","Fluffy","Greasy","Bouncy","Salty","Spicy","Silly"
        ,"Grumpy","Goofy","Zany","Quirky","Jolly","Nerdy","Sassy","Clumsy","Wacky","Dizzy","Giggly"};
    private final static String[] noun = {"bun","Potato","Penguin","Noodle","Goblin","Banana","Toaster","Hamster"
        ,"Unicorn","Sloth","Octopus","Dragon","Llama","Pineapple","Cactus","Giraffe","Taco","Robot","Zombie","Mermaid"
        ,"Waffle"};


    /**
     * Generates a random funny username by combining a random adjective, a random noun, and a random number.
     * Example output: "WobblyPenguin123"
     *
     * Original by ChatGPT, licensed under CC-BY-SA 4.0. Source: https://chat.openai.com/
     * Modified by Bjarte Johansen, 2026-03-01.
     *
     * @return a randomly generated funny username
     */

    public static String generate() {
        return adj[(int)(Math.random()*adj.length)]
            + noun[(int)(Math.random()*noun.length)]
            + (int)(Math.random()*1000);
    }
}
