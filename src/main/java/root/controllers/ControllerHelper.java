package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.ldap.Control;
import java.util.ArrayList;
import java.util.List;

@Component
public class ControllerHelper {
    public boolean status = true;
    public String statusMessage = null;

    public String errorMessage = null;
    public String successMessage = null;

    public static ControllerHelper create(){
        return new ControllerHelper();
    }

    public ControllerHelper withStatus(boolean status, String message) {
        this.status = status;
        this.statusMessage = message;
        return this;
    }

    public String redirect(RedirectAttributes ra, String path) {
        ra.addFlashAttribute("status", status);
        ra.addFlashAttribute("statusMessage", statusMessage);

        // old code so keep it in
        if(status) {
            ra.addFlashAttribute("successMessage", successMessage);
        }else {
            ra.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:" + path;
    }

/*
    public String redirect(String path) {
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
 */
}
