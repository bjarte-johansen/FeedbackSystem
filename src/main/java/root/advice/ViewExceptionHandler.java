package root.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import root.app.AppConfig;
import root.controllers.helpers.ControllerHelper;
import root.includes.logger.Logger;

@ControllerAdvice
public class ViewExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public String handle(IllegalStateException e, Model model) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ControllerHelper.create()
            .with(model)
            .withStatus(false, e.getMessage())
            .resolve("error");
    }

    @ExceptionHandler(Exception.class)
    public String handle(Exception e, Model model) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log("Exception: " + ((e.getMessage() != null) ?  e.getMessage() : "null"));

        return ControllerHelper.create()
            .with(model)
            .withStatus(false, e.getMessage())
            .resolve("error");
    }
}
