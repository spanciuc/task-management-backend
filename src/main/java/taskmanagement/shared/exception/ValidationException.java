package taskmanagement.shared.exception;

import lombok.Getter;
import taskmanagement.shared.message.MessageCode;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ValidationException extends ApplicationException {

    private final Map<String, MessageCode> validationErrors = new HashMap<>();

    public ValidationException(Map<String, MessageCode> validationErrors, Throwable cause) {
        this(MessageCode.ERROR_VALIDATION_FAILED, validationErrors, cause);
    }

    public ValidationException(MessageCode messageCode, Map<String, MessageCode> validationErrors, Throwable cause) {
        super(messageCode, cause);
        if (validationErrors != null) {
            this.validationErrors.putAll(validationErrors);
        }
    }
}
