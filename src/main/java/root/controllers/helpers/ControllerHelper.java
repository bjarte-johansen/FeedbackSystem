package root.controllers.helpers;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * small utility to help write unified status messages to error/success pages or similar.
 *
 * the class is over-generic in that it accepts lots of patterns that are supposed to be cleaned
 * up at a later time.
 *
 * TODO: clean it
 */

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

    private void setStatusMessage(boolean redirect){
        boolean hasStatus = this.status != null;
        boolean hasStatusMessage = statusMessage != null;
        boolean hasProperties = hasStatus || hasStatusMessage;

        if(hasProperties){
            if(redirect) {
                if (redirectAttributes == null) {
                    throw new RuntimeException("RedirectAttributes not set");
                }

                redirectAttributes.addFlashAttribute("status", status);
                redirectAttributes.addFlashAttribute("statusMessage", statusMessage);
            } else {
                if(model == null){
                    throw new RuntimeException("Model not set");
                }

                model.addAttribute("status", status);
                model.addAttribute("statusMessage", statusMessage);
            }
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
        setStatusMessage(true);

        return "redirect:" + path;
    }

    public String forward(String path) {
        setStatusMessage(false);

        return "forward:" + path;
    }

    public String resolve(String path) {
        setStatusMessage(false);

        return path;
    }
}
