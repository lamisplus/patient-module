//package org.lamisplus.modules.patient.controller.exception;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import javax.persistence.EntityNotFoundException;
//import java.sql.SQLException;
//
//import static org.springframework.http.HttpStatus.*;
//
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@ControllerAdvice
//public class RestExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @Override
//    protected ResponseEntity<Object> handleHttpMessageNotReadable(
//            HttpMessageNotReadableException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//        String error = "Malformed JSON request";
//        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
//    }
//
//    @ExceptionHandler(JsonProcessingException.class)
//    protected ResponseEntity<Object> handleJsonProcessingException(EntityNotFoundException ex) {
//        ApiError apiError = new ApiError(FORBIDDEN);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
//        ApiError apiError = new ApiError(NOT_FOUND);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }
//
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<Object> handleGeneralException(Exception ex) {
//        ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }
//
//    @ExceptionHandler(EmptyResultDataAccessException.class)
//    protected ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
//        ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }
//
//
//    @ExceptionHandler(SQLException.class)
//    protected ResponseEntity<Object> handleSQLException(SQLException ex) {
//        ApiError apiError = new ApiError(BAD_REQUEST);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }
//
//    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
//        return new ResponseEntity<>(apiError, apiError.getStatus());
//    }
//
//
//}
