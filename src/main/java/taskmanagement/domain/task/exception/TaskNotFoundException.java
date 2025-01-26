package taskmanagement.domain.task.exception;

import taskmanagement.shared.exception.ValidationException;
import taskmanagement.shared.message.MessageCode;

import java.util.Map;

public class TaskNotFoundException extends ValidationException {
    public TaskNotFoundException() {
        super(Map.of("task", MessageCode.ERROR_CATEGORY_NOT_FOUND));
    }
}
