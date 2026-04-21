package root.unittests.api;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import root.EndpointTestUtils;
import root.TestA0Base;
import root.includes.logger.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class Test_ClientController_Api_Reviews_List_Html extends TestA0Base {
    @Autowired
    MockMvc mockMvc;

    /**
     * GET /api/reviews/list/html
     * <p>
     * check that is not 4xx, and log the response body, which should be an HTML string containing the list of reviews.
     * This test verifies that the endpoint is functioning correctly and returns the expected HTML content.
     */

    @Test
    public void testEndpointNot404() throws Exception {

        String uri = "/api/reviews/list/html";

        var result = mockMvc.perform(get(uri))
            .andExpect(status().isOk())
            .andReturn();

        String body = result.getResponse().getContentAsString();
        Logger.log("response body: '" + body + "'");
    }


    /**
     * GET /api/reviews/list/html
     * <p>
     * Tests variations of parameters
     * <p>
     * check that is not 4xx, and log the response body, which should be an HTML string containing the list of reviews.
     * This test verifies that the endpoint is functioning correctly and returns the expected HTML content.
     * <p>
     * This will increase the coverage of our routes, and although it doesnt check if the controller gives the correct
     * result, it will show if all parameters for endpoints are valid and accepted by the controller, and that the
     * controller does not throw an error when receiving unexpected or edge case values for the parameters.
     */

    @Test
    public void testEndpointVariations() throws Exception {
        List<String> variations = List.of(
            "?externalId=/product/1&scoreFilter=-1",
            "?externalId=/product/1&scoreFilter=0",
            "?externalId=/product/1&scoreFilter=",

            "?externalId=/product/1&scoreFilter=5",
            "?externalId=/product/1&scoreFilter=1,2,3,4,5"
        );

        List<String> statusVariations = List.of(
            "&statusFilter=",
            "&statusFilter=1",
            "&statusFilter=1,2",
            "&statusFilter=1,2,3",
            "&statusFilter=0"
        );

        List<String> orderByVariations = List.of(
            "&orderByEnum=1",
            "&orderByEnum=2",
            "&orderByEnum=24",
            "&orderByEnum=9",
            "&orderByEnum=8"
        );

        String uri = "/api/reviews/list/html";

        for (String variation : variations) {
            for (String statusVariation : statusVariations) {
                for (String orderByVariation : orderByVariations) {
                    String current = uri + variation + statusVariation + orderByVariation;
                    var result = mockMvc.perform(get(current))
                        .andExpect(status().isOk())
                        .andReturn();
                }
            }
        }
    }


    /**
     * GET /does/not/exist
     * <p>
     * check that is 4xx or 5xx, and log the response body, which should be an HTML string containing the list of
     * reviews. This test verifies that the endpoint is functioning correctly and returns the expected HTML content.
     */
    @Test
    public void testFakeEndpointIs404() throws Exception {

        String uri = "/does/not/exist";

        var result = mockMvc.perform(get(uri))
            .andExpect(EndpointTestUtils::assertIs4xxOr5xx)
            .andReturn();

        String body = result.getResponse().getContentAsString();
        Logger.log("response body: '" + body + "'");
    }
}
