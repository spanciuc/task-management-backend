package taskmanagement.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import taskmanagement.domain.task.config.TaskConfig;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Value
public class TaskRequest {
    @NotBlank
    String title;
    String description;
    @NotNull
    TaskConfig.Priority priority;
    @NotNull
    TaskConfig.Status status;
    @NotNull
    LocalDate dueDate;
    @NotNull
    UUID categoryId;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
