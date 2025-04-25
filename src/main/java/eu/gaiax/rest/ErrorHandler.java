package eu.gaiax.rest;

import eu.gaiax.model.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.sql.SQLException;

@Slf4j
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ConnectException.class})
    public ResponseEntity<ErrorDto> connectionRefused(HttpServletRequest httpServletRequest, Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(httpServletRequest.getRequestURI(), "Connection error. Please, try later"));
    }

    @ExceptionHandler({WebClientResponseException.class})
    public ResponseEntity<ErrorDto> pageNotFound(HttpServletRequest httpServletRequest, Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(httpServletRequest.getRequestURI(), "Page not found. Please, try later"));
    }

    @ExceptionHandler({ConstraintViolationException.class, JDBCException.class, SQLException.class})
    public ResponseEntity<ErrorDto> databaseError(HttpServletRequest httpServletRequest, Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto(httpServletRequest.getRequestURI(), "Error occurred in database connection."));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto(request.getContextPath(), ex.getMessage()));
    }
}
