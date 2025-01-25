package taskmanagement.domain.user.exception;

import taskmanagement.shared.exception.ValidationException;
import taskmanagement.shared.message.MessageCode;

import java.util.Map;

public class EmailAlreadyExistsException extends ValidationException {

    public EmailAlreadyExistsException() {
        this(MessageCode.VALIDATION_USER_EMAIL_EXISTS);
    }

    public EmailAlreadyExistsException(MessageCode messageCode) {
        this(messageCode, null);
    }

    public EmailAlreadyExistsException(MessageCode messageCode, Throwable cause) {
        super(Map.of("email", messageCode), cause);
    }

}
