package taskmanagement.shared.exception;

import taskmanagement.shared.message.MessageCode;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException() {
        this(MessageCode.ERROR_RESOURCE_NOT_FOUND, null);
    }

    public ResourceNotFoundException(MessageCode messageCode, Throwable cause) {
        super(messageCode, cause);
    }
}
