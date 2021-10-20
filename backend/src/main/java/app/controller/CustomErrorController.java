package app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import app.exception.ErrorResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

// Without this CustomErrorController, spring catches the errors thrown
// inside JwtTokenFilter and sends an error response with spring's default format,
// so our custom GlobalExceptionHandler will be ignored.
// By using CustomErrorController we can throw the exceptions so that GlobalExceptionHandler
// will handle them. However, if we do this, our custom error reponse field path will
// be always /error. We use our custom attribute "initialPath" to retrieve the initial path.
// But now the user can access the /error endpoint. For this reason we throw the
// NoHandlerFoundException to the GlobalExceptionHandler.

@RestController
public class CustomErrorController implements ErrorController {
    
    private static final String PATH = "/error";
    
    @RequestMapping(PATH)
    public ResponseEntity<ErrorResponse> handleError(final HttpServletRequest request,
                                                     final HttpServletResponse response) throws Throwable {
        
        Throwable ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String initialPath = (String)request.getAttribute("initialPath");
        
        // User requested at /error endpoint. Let GlobalExceptionHandler handle it.
        // Any other case in which ex can be null?
        if (ex == null) {
            throw new NoHandlerFoundException("", PATH ,null);
        }
        
        // We may get an DataAccessResourceFailureException in case
        // the db is down and the client does a database request
        // We throw a RuntimeException so that GlobalExceptionHandler
        // will throw a generic "Server internal error!!!"" message 
        if (ex instanceof DataAccessResourceFailureException) {
            throw new RuntimeException();
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());  
        errorResponse.setPath(initialPath);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
    
    @Override
    public String getErrorPath() {
        return PATH;
    }
}