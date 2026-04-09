package root.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import root.includes.logger.Logger;

@ControllerAdvice
public class ViewExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handle(Exception ex, Model model) {
        Logger.log(ex.getMessage());

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("type", ex.getClass().getSimpleName());
        return "error";
    }
}
