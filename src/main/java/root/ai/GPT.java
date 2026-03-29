package root.ai;

import root.logger.Logger;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;


/**
 * A simple wrapper for OpenAI's API. In a real application, you'd want to handle errors, rate limits, and parse the
 * JSON response properly.
 */

public class GPT {
    static final String API_KEY = "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";

    static String extractText(String json) throws Exception {
        var root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);

        for (var node : root.get("output")) {
            if ("message".equals(node.get("type").asText())) {
                return node.at("/content/0/text").asText();
            }
        }
        return null;
    }

    public static String ask(String prompt) throws Exception {
        String body = """
{
  "model": "gpt-5-mini",
  "input": [
    {"role": "system", "content": "Answer very briefly."},
    {"role": "user", "content": "%s"}
  ],
  "text": {
    "format": {
      "type": "text"
    }
  }
}
""".formatted(prompt.replace("\"", "\\\""));

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/responses"))
            .header("Authorization", "Bearer " + API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        //Logger.log("RESPONSE: " + extractText(res.body()));

        return extractText(res.body()); // parse JSON for actual text
    }
}