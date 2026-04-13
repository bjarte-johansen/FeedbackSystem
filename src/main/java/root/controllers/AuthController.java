package root.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {
    /**
     * Show the admin login page. This is just a placeholder for the admin login endpoint. The actual implementation of
     * the login logic is handled by spring, we only need to show the view
     */

    @GetMapping("/admin/login")
    public String showLogin() {
        return "admin/admin-login";
    }
}
