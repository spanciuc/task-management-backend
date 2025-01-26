package taskmanagement.domain.task.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taskmanagement.domain.task.dto.TaskDTO;
import taskmanagement.domain.task.dto.TaskFilter;
import taskmanagement.domain.task.dto.TaskRequest;
import taskmanagement.domain.task.exception.CategoryNotFoundException;
import taskmanagement.domain.task.exception.TaskNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface TaskService {
    /**
     * Gets all tasks.
     *
     * @param pageable pageable request.
     * @return page of tasks.
     */
    Page<TaskDTO> getPage(Pageable pageable);

    /**
     * Gets tasks by filter.
     *
     * @param filter   filter.
     * @param pageable pageable.
     * @return filtered page of tasks.
     */
    Page<TaskDTO> getFilteredPage(TaskFilter filter, Pageable pageable);

    /**
     * Gets a task by id.
     *
     * @param id task id.
     * @return optional of task.
     */
    Optional<TaskDTO> getTask(UUID id);

    /**
     * Creates a new task.
     *
     * @param request request.
     * @return the created task.
     * @throws CategoryNotFoundException if category does not exist.
     */
    TaskDTO create(TaskRequest request) throws CategoryNotFoundException;

    /**
     * Updates an existing task
     *
     * @param id      task id.
     * @param request request.
     * @return the updated task.
     * @throws TaskNotFoundException     if task does not exist.
     * @throws CategoryNotFoundException if category does not exist.
     */
    TaskDTO update(UUID id, TaskRequest request) throws CategoryNotFoundException, TaskNotFoundException;

    /**
     * Deletes a task by id.
     *
     * @param id the task id.
     */
    void delete(UUID id);
}
