package taskmanagement.domain.auth.exception;

import taskmanagement.shared.exception.ApplicationException;
import taskmanagement.shared.message.MessageCode;

public class JwtGenerationFailedException extends ApplicationException {

    public JwtGenerationFailedException(Throwable cause) {
        super(MessageCode.ERROR_INTERNAL_SERVER_ERROR, cause);
    }

}
