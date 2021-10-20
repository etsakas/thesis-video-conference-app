package app.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import app.exception.EmailTakenException;
import app.exception.ErrorResponse;
import app.exception.InvalidSessionPasswordException;
import app.exception.NoUserFoundException;
import app.exception.SessionIsFullException;
import app.exception.SessionNameTakenException;
import app.exception.SessionNotFoundException;
import app.exception.TokenException;
import app.exception.UserNameTakenException;
import app.exception.UserNotConnectedException;
import app.exception.WrongCredentialsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(SessionIsFullException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleSessionIsFullException(SessionIsFullException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(InvalidSessionPasswordException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleInvalidSessionPasswordException(InvalidSessionPasswordException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(SessionNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleSessionNotFoundException(SessionNotFoundException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(UserNotConnectedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleUserNotConnectedException(UserNotConnectedException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(SessionNameTakenException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleSessionNameTakenException(SessionNameTakenException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(UserNameTakenException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleUserNameTakenException(UserNameTakenException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(EmailTakenException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleEmailTakenException(EmailTakenException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(NoUserFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleNoUserFoundException(NoUserFoundException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(WrongCredentialsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleWrongCredentialsException(WrongCredentialsException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(TokenException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleTokenException(TokenException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // Handle exception caused by annotation like @Size, @NotNull, etc (that is, @Valid)
  // Because global handler extends ResponseEntityExceptionHandler we have to
  // override the handleMethodArgumentNotValid. Creating a new method and then
  // using @ExceptionHandler(MethodArgumentNotValidException.class) won't work.
  @Override
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input");
    if (request.getDescription(false).length() >= 4)
      errorResponse.setPath(request.getDescription(false).substring(4));

    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    for (FieldError fieldError : fieldErrors) {
      String field = fieldError.getField().toString();
      String message = fieldError.getDefaultMessage().toString();
      String rejectedValue;
      // In case json post request doesnt have a field (for example username)
      if (fieldError.getRejectedValue() != null)
        rejectedValue = fieldError.getRejectedValue().toString();
      else
        rejectedValue = null;

      errorResponse.addValidationError(field, message, rejectedValue);
    }

    // I don't know in which cases we can get GlobalErrors.
    // Our custom error hasn't the proper fields for this situation anyway.
    // If the API user doesn't have to know these errors in detail then we
    // are fine.
    List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
    for (ObjectError objectError : globalErrors) {
        String objectName = objectError.getObjectName();
        String message = objectError.getDefaultMessage();
        System.out.println("objectName: " + objectName);
        System.out.println("message: " + message);
    }

    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // In case the post request doesn't have the same content type in its' body (or empty)
  // as what the method defines (using consumes="")
  @Override
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE) // Not needed, only for readability
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
    if (request.getDescription(false).length() >= 4)
      errorResponse.setPath(request.getDescription(false).substring(4));
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    
  }

  // In cases we get a request with a method (GET, POST, etc) that is not supported.
  @Override
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED) // Not needed, only for readability
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method is not allowed");
    if (request.getDescription(false).length() >= 4)
      errorResponse.setPath(request.getDescription(false).substring(4));
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // In cases we send a request to a path that is doesn't have a controller's handler.
  @Override
  @ResponseStatus(HttpStatus.NOT_FOUND) // Not needed, only for readability
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, "No handler found");
    if (request.getDescription(false).length() >= 4)
      errorResponse.setPath(request.getDescription(false).substring(4));
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // In cases that request body is not readable. Say for example that the controller
  // consumes a json body. If there is a comma after the last entry inside json
  // then we get a HttpMessageNotReadableException.
  @Override
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Request body is not readable");
    if (request.getDescription(false).length() >= 4)
      errorResponse.setPath(request.getDescription(false).substring(4));
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // It's possible to get MissingRequestHeaderException when a controller's method requests a
  // parameter using @RequestHeader annotation. Since jwt authentication filter is in higher level
  // than our custom global exception handler and checks that Authorization header exists, its
  // not possible to get an MissingRequestHeaderException because of a request with no Authorization
  // header: in this case we'll get an TokenException thrown from CustomErrorController.
  // In other words we can still get this exception if controller has @RequestHeader annotation
  // and the request is not filtered from jwt filter.
  @ExceptionHandler(MissingRequestHeaderException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // Not needed, only for readability
  public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // fallback handler — a catch-all type of logic that deals with all other
  // exceptions that don't have specific handlers
  // In case server (that is up and running) accepts a request that needs database
  // connection but database is down, this handler will be activated.
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Not needed, only for readability
  public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest httpRequest) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server internal error!!!");  
    errorResponse.setPath(httpRequest.getRequestURI());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

}