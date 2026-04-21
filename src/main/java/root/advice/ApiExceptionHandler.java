package root.advice;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import root.app.AppConfig;
import root.includes.logger.Logger;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handle(IllegalStateException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handle(RuntimeException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handle(AccessDeniedException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(403).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ResponseEntity.status(500).body(e.getMessage());
    }
}