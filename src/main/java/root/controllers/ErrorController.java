package root.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import root.includes.logger.Logger;

import java.io.IOException;


/**
 * Controller for handling error pages. This controller provides a simple route to display an error page, which can be
 * used to show error messages to the user when something goes wrong. In a real application, you would typically have
 * more sophisticated error handling, possibly with different error pages for different types of errors (e.g., 404 Not
 * Found, 500 Internal Server Error, etc.).
 */

@Controller
public class ErrorController {
    /**
     * Simple route to display an error page. This is just for demonstration purposes and should be replaced with proper
     * error handling in production code.
     */

    @GetMapping("/403")
    public String accessDeniedError(
        @RequestParam(required = false) String statusMessage,
        @RequestParam(required = false) String error,
        Model model,
        HttpServletResponse res
    ) throws IOException {
        Logger.log("YOU ARE IN /403 HANDLER" + statusMessage + " " + error);

        if(error != null && !error.isBlank() && statusMessage == null || statusMessage.isBlank()){
            statusMessage = error;
        }

        model.addAttribute("statusMessage", statusMessage != null ? statusMessage : "Du har ikke tilgang til denne siden.");

        //return "admin/login";
        res.sendRedirect("/admin/login");
        return null;
    }


    /**
     * Simple route to display an error page. This is just for demonstration purposes and should be replaced with proper
     * error handling in production code.
     */

    @GetMapping("/error")
    public String error(
        @RequestParam(required = false) String statusMessage,
        @RequestParam(required = false) String error,
        Model model
    ) {
        Logger.log("YOU ARE IN /error HANDLER" + statusMessage + " " + error);

        if(error != null && !error.isBlank() && statusMessage == null || statusMessage.isBlank()){
            statusMessage = error;
        }

        if (statusMessage != null && !model.containsAttribute("statusMessage")) {
            model.addAttribute("statusMessage", statusMessage);
        }

        return "error";
    }
}
