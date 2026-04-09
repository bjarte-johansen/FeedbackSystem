package root.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import root.includes.logger.Logger;

import java.nio.file.AccessDeniedException;

@Deprecated
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handle() {
        return ResponseEntity.status(403).build();
    }

    @ExceptionHandler(Exception.class)
    public String handle(Exception ex, Model model) {
        ex.printStackTrace();
        Logger.log(ex.getMessage());

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("type", ex.getClass().getSimpleName());
        return "error";
    }
}