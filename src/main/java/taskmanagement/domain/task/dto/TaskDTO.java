package taskmanagement.domain.task.dto;

import lombok.Value;
import taskmanagement.domain.task.config.TaskConfig;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Value
public class TaskDTO {
    UUID id;
    Instant createdAt;
    Instant updatedAt;
    String title;
    String description;
    TaskConfig.Priority priority;
    TaskConfig.Status status;
    LocalDate dueDate;
    CategoryDTO category;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
