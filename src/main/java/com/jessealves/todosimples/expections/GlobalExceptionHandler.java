package com.jessealves.todosimples.expections;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jessealves.todosimples.services.exceptions.DataBindingViolationException;
import com.jessealves.todosimples.services.exceptions.ObjectNotFoundException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler {
    
    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
                MethodArgumentNotValidException methodArgumentNotValidException,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de validação. Verifique o campo 'errors' para mais detalhes.");
        for(FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationErro(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception exception,
            WebRequest request
    ) {
        final String errorMessage = "Ocorreu um erro desconhecido";
        log.error(errorMessage, exception);

        return buildErrorResponse(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request
    ) {
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Falha ao salvar uma entidade com problemas de integridade: " + errorMessage, dataIntegrityViolationException);

        return buildErrorResponse(dataIntegrityViolationException, errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request
    ) {
        String errorMessage = constraintViolationException.getMessage();
        log.error("Falha ao salvar uma entidade com problemas de integridade: " + errorMessage, constraintViolationException);

        return buildErrorResponse(constraintViolationException, errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException objectNotFoundException,
            WebRequest request
    ) {
        String errorMessage = objectNotFoundException.getMessage();
        log.error("Falha ao encontrar um elemento: " + errorMessage, objectNotFoundException);

        return buildErrorResponse(objectNotFoundException, errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleObjectNotFoundException(
            DataBindingViolationException dataBindingViolationException,
            WebRequest request
    ) {
        String errorMessage = dataBindingViolationException.getMessage();
        log.error("Falha ao salvar elemento que possui dados associados: " + errorMessage, dataBindingViolationException);

        return buildErrorResponse(dataBindingViolationException, errorMessage, HttpStatus.CONFLICT);
    }
    
    private ResponseEntity<Object> buildErrorResponse(
                Exception exception,
                String message,
                HttpStatus httpStatus
    ) {    
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        Integer status = HttpStatus.FORBIDDEN.value();
        ErrorResponse errorResponse = new ErrorResponse(status, "Usuário ou senha inválidos.");

        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().append(errorResponse.toJson());
    }

}
