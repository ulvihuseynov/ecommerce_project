package com.e_commerce.project.exception;

import com.e_commerce.project.payload.APIResponse;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentValidException(MethodArgumentNotValidException exception){

        Map<String,String> errors=new HashMap<>();

        exception.getBindingResult()
                .getAllErrors()
                .forEach(error->errors.put(
                        ((FieldError) error).getField(),
                        error.getDefaultMessage()
                ));
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException exception){

        APIResponse response=new APIResponse();
        response.setMessage(exception.getMessage());
        response.setStatus(false);
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException exception){

        APIResponse apiResponse=new APIResponse();
        String message = exception.getMessage();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
}
