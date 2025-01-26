package taskmanagement.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.util.Optional;

@Value
public class CategoryRequest {
    @NotBlank
    String title;
    String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
