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
    private Boolean status = true;
    private String statusMessage = null;

    private RedirectAttributes redirectAttributes = null;
    private Model model = null;

    public static ControllerHelper create(){
        return new ControllerHelper();
    }

    public ControllerHelper withStatus(boolean status, String message) {
        this.status = status;
        this.statusMessage = message;
        return this;
    }

    private void setStatusMessage(){
        boolean hasStatus = this.status != null;
        boolean hasStatusMessage = statusMessage != null;

        if(hasStatus && hasStatusMessage && (redirectAttributes == null && model == null)){
            throw new RuntimeException("Both model and redirectattributes should not be null");
        }

        if(redirectAttributes != null){
            redirectAttributes.addFlashAttribute("status", status);
            redirectAttributes.addFlashAttribute("statusMessage", statusMessage);
        }

        if(model != null){
            model.addAttribute("status", status);
            model.addAttribute("statusMessage", statusMessage);
        }
    }

    public ControllerHelper with(RedirectAttributes ra) {
        this.redirectAttributes = ra;
        return this;
    }
    public ControllerHelper with(Model model) {
        this.model = model;
        return this;
    }

    public String redirect(String path) {
        setStatusMessage();

        return "redirect:" + path;
    }

    public String forward(String path) {
        setStatusMessage();

        return "forward:" + path;
    }

    public String resolve(String path) {
        setStatusMessage();

        return path;
    }

/*
    public String redirect(String path) {
        return "redirect:" + path;
    }
*/
}
