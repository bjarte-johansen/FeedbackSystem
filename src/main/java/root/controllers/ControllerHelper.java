package root.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class ControllerHelper {
    public String errorMessage = null;
    public String successMessage = null;

    public static ControllerHelper create(){
        return new ControllerHelper();
    }

    public ControllerHelper withError(String message) {
        this.errorMessage = message;
        return this;
    }

    public ControllerHelper withSuccess(String message) {
        this.successMessage = message;
        return this;
    }

    public String redirect(RedirectAttributes ra, String path) throws Exception{
        ra.addFlashAttribute("successMessage", successMessage);
        ra.addFlashAttribute("errorMessage", errorMessage);

        return "redirect:" + path;
    }
    public String redirect(String path) throws Exception{
        return "redirect:" + path;
    }

    //
    public static void setErrorMessage(RedirectAttributes ra, String message) {
        ra.addFlashAttribute("errorMessage", message);
    }

    public static void setErrorMessage(Model model, String message) {
        model.addAttribute("errorMessage", message);
    }


    //
    public static void setSuccessMessage(RedirectAttributes ra, String message) {
        ra.addFlashAttribute("successMessage", message);
    }

    public static void setSuccessMessage(Model model, String message) {
        model.addAttribute("successMessage", message);
    }


    //
    public static void setupModel(Model model) throws Exception {
        model.addAttribute("defaultTitle", "Review System");
    }
}
