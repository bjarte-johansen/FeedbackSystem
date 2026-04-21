package root.unittests.api;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import root.DatabaseManager;
import root.TestA0Base;
import root.includes.logger.Logger;
import root.includes.logger.LoggerScope;
import root.models.Review;
import root.repositories.ReviewRepository;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class Test_ClientController_Api_Review_Id extends TestA0Base {
    @Autowired
    MockMvc mockMvc;


    /**
     *
     * Tests the API endpoint for retrieving an existing review as JSON. It sends a GET request to the endpoint with a
     * valid review ID, expects a 200 OK response, and asserts that the response body matches the expected JSON
     * structure and content for that review.
     *
     * Route begin tested:
     *  GET /api/review/17/json
     *
     * @throws Exception
     */

    @Test
    public void test_existing_review_as_json() throws Exception {
        String uri = "/api/review/17/json";

        var result = mockMvc.perform(get(uri))
            .andExpect(status().isOk())
            .andReturn();

        String body = result.getResponse().getContentAsString();
        Logger.log("response body: '" + body + "'");

        // assert output taken from actual testing, a real route
        assertEquals(
            "{\"id\":17,\"externalId\":\"/product/1\",\"authorId\":1,\"authorName\":\"GoofyLlama178\",\"score\":3,\"title\":\"Dolor labore.\",\"comment\":\"Sit lorem elit dolor consectetur ex. ad amet. do. consectetur eiusmod nisi ut ad sit. exercitation et sed.\",\"createdAt\":\"2024-10-02T22:28:28.685206Z\",\"status\":3,\"likeCount\":0,\"dislikeCount\":1,\"shortDateString\":\"for 1 Ã¥r siden\"}",
            body
        );
    }

    /**
     * Tests the API endpoint for retrieving an existing review as JSON. It sends a GET request to the endpoint with a
     * valid review ID, expects a 404 response
     *
     * Route:
     *  GET /api/review/-1/json
     *
     * @throws Exception
     */

    @Test
    public void test_non_existing_review_as_json() throws Exception {
        // expect isNotFound for non-existing reviewId
        var result = mockMvc.perform(get("/api/review/-1/json"))
            .andExpect(status().isNotFound())
            .andReturn();
    }


    /*
    test like functionality
     */

    @Test
    public void test_non_existing_review_like() throws Exception {
        // expect isNotFound for non-existing reviewId
        var result = mockMvc.perform(post("/api/review/-1/like"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    }

    @Test
    public void test_existing_review_like() throws Exception {
        // expect is2xxSuccessful for non-existing reviewId
        var result = mockMvc.perform(post("/api/review/17/like"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    }


    /*
    test dislike functionality
     */

    @Test
    public void test_non_existing_review_dislike() throws Exception {
        // expect isNotFound for non-existing reviewId
        var result = mockMvc.perform(post("/api/review/-1/dislike"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    }

    @Test
    public void test_existing_review_dislike() throws Exception {
        // expect is2xxSuccessful for non-existing reviewId
        var result = mockMvc.perform(post("/api/review/17/dislike"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    }
}
