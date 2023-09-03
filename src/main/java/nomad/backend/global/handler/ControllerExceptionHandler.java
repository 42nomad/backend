package nomad.backend.global.handler;

import nomad.backend.global.exception.*;
import nomad.backend.global.reponse.Response;
import nomad.backend.global.reponse.ResponseWithData;
import nomad.backend.global.reponse.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpRequestMethodNotSupportedException.class, MissingServletRequestParameterException.class})
    protected ResponseEntity handleBadArgumentRequestException(Exception e) {
        return new ResponseEntity(Response.res(StatusCode.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity handleNotFoundException(NotFoundException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SlackNotFoundException.class)
    protected ResponseEntity handleSlackNotFoundException(SlackNotFoundException e) {
        return new ResponseEntity(ResponseWithData.res(e.getErrorCode(), e.getMessage(), e.getNotificationId()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity handleConflictException(ConflictException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TooManyRequestException.class)
    protected ResponseEntity handleTooManyReqeustException(TooManyRequestException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(JsonDeserializeException.class)
    protected ResponseEntity handleJsonDeserializeException(JsonDeserializeException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnexpectedException.class)
    protected ResponseEntity handleUnexpectedException(UnexpectedException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity(Response.res(StatusCode.INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}