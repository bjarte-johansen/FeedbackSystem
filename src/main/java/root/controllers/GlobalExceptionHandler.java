package root.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import root.logger.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handle(Exception ex, Model model) {
        ex.printStackTrace();
        Logger.log(ex.getMessage());

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("type", ex.getClass().getSimpleName());
        return "error";
    }
}