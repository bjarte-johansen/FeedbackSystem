package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import root.includes.logger.logger.Logger;

public class ControllerUtils {
    public static void setDefaults(Model model){
        model.addAttribute("defaultTitle", "Review System");
    }
}
