package taskmanagement.domain.task.dto;

import lombok.Value;
import taskmanagement.domain.task.config.TaskConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Value
public class TaskFilter {
    List<UUID> categories;
    List<TaskConfig.Status> statuses;
    List<TaskConfig.Priority> priorities;

    public Optional<List<UUID>> getCategories() {
        return Optional.ofNullable(categories);
    }

    public Optional<List<TaskConfig.Status>> getStatuses() {
        return Optional.ofNullable(statuses);
    }

    public Optional<List<TaskConfig.Priority>> getPriorities() {
        return Optional.ofNullable(priorities);
    }
}
