package taskmanagement.domain.task.dto;

import lombok.Value;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Value
public class CategoryDTO {
    UUID id;
    Instant createdAt;
    Instant updatedAt;
    String title;
    String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
