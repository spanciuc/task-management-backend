package taskmanagement.domain.user.exception;

import taskmanagement.shared.exception.ValidationException;
import taskmanagement.shared.message.MessageCode;

import java.util.Map;

public class UsernameAlreadyExistsException extends ValidationException {
    public UsernameAlreadyExistsException() {
        this(MessageCode.VALIDATION_USER_USERNAME_EXISTS);
    }

    public UsernameAlreadyExistsException(MessageCode messageCode) {
        this(messageCode, null);
    }

    public UsernameAlreadyExistsException(MessageCode messageCode, Throwable cause) {
        super(Map.of("username", messageCode), cause);
    }
}
