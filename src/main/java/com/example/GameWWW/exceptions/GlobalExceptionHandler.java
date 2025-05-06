package com.example.GameWWW.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Order (Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @Bean
    public ErrorAttributes errorAttributes(){
        return new DefaultErrorAttributes(){
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults()
                        .including(ErrorAttributeOptions.Include.MESSAGE));
            }
        };
    }

    @ExceptionHandler(CommonBackendException.class)
    public void handlerCommonBackendException (HttpServletResponse response, CommonBackendException e) throws IOException {
        response.sendError(e.getStatus().value(), e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> handlerMissingParam(MissingServletRequestParameterException e){
        String parameter = e.getParameterName();
        log.error("{} parameter is missing", parameter);
        return ResponseEntity.badRequest().body(new ErrorMessage(String.format("Parameter %s is missing", parameter)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handlerMismatchType(MethodArgumentTypeMismatchException e){
        String parameter = e.getParameter().getParameterName();
        log.error("Wrong type of parameter {}",parameter);
        return ResponseEntity.badRequest().body(new ErrorMessage(String.format("Wrong type of parameter %s", parameter)));
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handlerNotValid(MethodArgumentNotValidException e){
        FieldError fieldError =e.getFieldError();
        String message = fieldError!=null ? fieldError.getField() + " "+ fieldError.getDefaultMessage() : e.getMessage();
        log.error(message);
        return ResponseEntity.badRequest().body(new ErrorMessage(message));

    }
}
