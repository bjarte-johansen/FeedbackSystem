package root.advice;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import root.app.AppConfig;
import root.controllers.helpers.ControllerHelper;
import root.includes.logger.Logger;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handle(IllegalStateException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception (IllegalStateException): " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handle(RuntimeException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception (BadRequestException): " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handle(NoSuchElementException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception (NoSuchElementException): " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(404).body(e.getMessage() != null ? e.getMessage() : "null");
    }


    /*
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handle(AccessDeniedException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(403).build();
    }

     */

/*
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public String handle(org.springframework.security.access.AccessDeniedException e, Model model) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ControllerHelper.create()
            .with(model)
            .withStatus(false, e.getMessage() + ", exception handler")
            .resolve("error");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public String handle(AuthorizationDeniedException e, Model model) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ControllerHelper.create()
            .with(model)
            .withStatus(false, e.getMessage() + ", exception handler")
            .resolve("error");
    }
*/
    /*
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception (Exception.class): " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(500).body(e.getMessage());
    }

     */
}