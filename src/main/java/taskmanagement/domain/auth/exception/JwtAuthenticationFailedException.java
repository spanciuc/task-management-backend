package taskmanagement.domain.auth.exception;

import taskmanagement.shared.exception.ApplicationException;
import taskmanagement.shared.message.MessageCode;

public class JwtAuthenticationFailedException extends ApplicationException {

    public JwtAuthenticationFailedException(Throwable cause) {
        super(MessageCode.ERROR_INVALID_AUTHENTICATION_TOKEN, cause);
    }

}
