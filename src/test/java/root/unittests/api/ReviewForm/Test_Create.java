package root.unittests.api.ReviewForm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import root.TestA0Base;
import root.config.RequestContextFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class Test_Create extends TestA0Base {
    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    public static void setup() {
        RequestContextFilter.VERBOSE = false;
    }

    @Test
    public void testPrefilledDevCreateForm() throws Exception {
        String devUrl = "/api/new-review-form/create?externalId=/product/1&prefilled=1";
        mockMvc.perform(get(devUrl))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testProductionCreateForm() throws Exception {
        String productionUrl = "/api/new-review-form/create?externalId=/product/1";
        mockMvc.perform(get(productionUrl))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    void submitReviewForm() throws Exception {
        mockMvc.perform(post("/api/review/submit")
            .param("externalId", "/product/1")
            .param("email", "test@test.com")
            .param("password", "Abacus556!")
            .param("displayName", "ClumsyOctopus405")
            .param("score", "5")
            .param("title", "Consectetur aliquip")
            .param("comment", "Do lorem...")
        )
        .andExpect(status().isOk());
    }
}
