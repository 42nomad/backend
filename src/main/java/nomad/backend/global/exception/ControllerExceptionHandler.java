package nomad.backend.global.exception;

import nomad.backend.global.exception.custom.BadRequestException;
import nomad.backend.global.exception.custom.JsonDeserializeException;
import nomad.backend.global.exception.custom.NotFoundException;
import nomad.backend.global.exception.custom.TooManyRequestException;
import nomad.backend.global.reponse.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity handleBadRequestException(BadRequestException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity handleBadRequestException(NotFoundException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TooManyRequestException.class)
    protected ResponseEntity handleTooManyReqeustException(TooManyRequestException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(JsonDeserializeException.class)
    protected ResponseEntity handleJsonDeserializeException(JsonDeserializeException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }
}