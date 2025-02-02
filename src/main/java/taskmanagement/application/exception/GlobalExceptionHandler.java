package taskmanagement.application.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import taskmanagement.shared.exception.ApplicationException;
import taskmanagement.shared.exception.ResourceNotFoundException;
import taskmanagement.shared.exception.ValidationException;
import taskmanagement.shared.message.MessageCode;
import taskmanagement.shared.message.MessageResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERRORS_OBJECT_KEY = "validationError";

    private final MessageResolver messageResolver;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        String message = messageResolver.resolve(MessageCode.ERROR_VALIDATION_FAILED, request.getLocale());
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        if (!errors.isEmpty()) {
            problemDetail.setProperties(Map.of(VALIDATION_ERRORS_OBJECT_KEY, errors));
        }
        log.debug(e.getMessage(), e);
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        String message = messageResolver.resolve(MessageCode.ERROR_BAD_CREDENTIALS, request.getLocale());
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException e, WebRequest request) {
        String message = messageResolver.resolve(MessageCode.ERROR_ACCESS_DENIED, request.getLocale());
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, message);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidationException(ValidationException e, WebRequest request) {
        String message = messageResolver.resolve(MessageCode.ERROR_VALIDATION_FAILED, request.getLocale());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        if (!e.getValidationErrors().isEmpty()) {
            Map<String, String> validationErrors = e.getValidationErrors().entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> messageResolver.resolve(entry.getValue(), request.getLocale())
                    ));
            problemDetail.setProperties(Map.of(VALIDATION_ERRORS_OBJECT_KEY, validationErrors));
        }
        log.debug(e.getMessage(), e);
        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        String message = messageResolver.resolve(MessageCode.ERROR_RESOURCE_NOT_FOUND, request.getLocale());
        log.debug(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleApplicationException(ApplicationException e, WebRequest request) {
        String message = messageResolver.resolve(MessageCode.ERROR_INTERNAL_SERVER_ERROR, request.getLocale());
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception e, WebRequest request) {
        String message = messageResolver.resolve(MessageCode.ERROR_INTERNAL_SERVER_ERROR, request.getLocale());
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
