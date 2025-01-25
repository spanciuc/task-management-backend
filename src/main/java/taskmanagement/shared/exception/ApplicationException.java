package taskmanagement.shared.exception;

import lombok.Getter;
import taskmanagement.shared.message.MessageCode;

@Getter
public class ApplicationException extends RuntimeException {
    private final MessageCode messageCode;

    public ApplicationException(MessageCode messageCode, Throwable cause) {
        super(messageCode.getCode(), cause);
        this.messageCode = messageCode;
    }
}
