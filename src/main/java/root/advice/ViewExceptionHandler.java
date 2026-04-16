package root.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import root.app.AppConfig;
import root.controllers.helpers.ControllerHelper;
import root.includes.logger.Logger;

@ControllerAdvice
public class ViewExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public String handle(IllegalStateException e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log(e.getMessage());

        return ControllerHelper.create()
            .withStatus(false, e.getMessage())
            .resolve("error");
    }

    @ExceptionHandler(Exception.class)
    public String handle(Exception e) {
        if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        Logger.log(e.getMessage());

        return ControllerHelper.create()
            .withStatus(false, e.getMessage())
            .resolve("error");
    }
}
