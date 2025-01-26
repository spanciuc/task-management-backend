package taskmanagement.domain.task.exception;

import taskmanagement.shared.exception.ValidationException;
import taskmanagement.shared.message.MessageCode;

import java.util.Map;

public class CategoryNotFoundException extends ValidationException {
    public CategoryNotFoundException() {
        super(Map.of("category", MessageCode.ERROR_CATEGORY_NOT_FOUND));
    }
}
