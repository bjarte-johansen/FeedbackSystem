package root.controllers;

import org.springframework.ui.Model;

@Deprecated
public class ControllerUtils {
    public static void setDefaults(Model model) {
        model.addAttribute("defaultTitle", "Review System");
    }
}
