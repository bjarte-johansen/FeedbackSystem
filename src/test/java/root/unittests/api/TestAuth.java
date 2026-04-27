package root.unittests.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import root.services.utils.IsAdminService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * This test is mostly written by ChatGPT as we were unsure about how to do it, but it appears to
 * be written pretty well using the MockMvc. We glad we using SpringAUTH at this point :)
 *
 */

@SpringBootTest
@AutoConfigureMockMvc
class TestAuth {
    @Autowired
    private MockMvc mvc;

    @Test
    void login_ok() throws Exception {
        mvc.perform(post("/admin/login")
            .param("username", "tenant1@test.com")
            .param("password", "password1"))
            .andExpect(status().is3xxRedirection())   // success → redirect
            .andExpect(redirectedUrl("/admin/dashboard"))
            .andExpect(authenticated());
    }

    @Test
    void login_fail() throws Exception {
        mvc.perform(post("/admin/login")
            .param("username", "admin")
            .param("password", "wrong"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/admin/login?*"))
            .andExpect(unauthenticated());

        assertFalse(IsAdminService.isAdmin(), "Expected to be not logged in");
    }

    @Test
    void logout() throws Exception {
        mvc.perform(post("/admin/logout"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/admin/login?*"))
            .andExpect(unauthenticated());

        assertFalse(IsAdminService.isAdmin(), "Expected to be logged out");
    }
}