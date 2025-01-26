package taskmanagement.domain.task.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taskmanagement.domain.task.config.TaskConfig;
import taskmanagement.domain.task.dto.TaskRequest;
import taskmanagement.domain.task.exception.CategoryNotFoundException;
import taskmanagement.domain.task.exception.TaskNotFoundException;
import taskmanagement.domain.task.persistence.Category;
import taskmanagement.domain.task.persistence.CategoryRepository;
import taskmanagement.domain.task.persistence.Task;
import taskmanagement.domain.task.persistence.TaskRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void create_whenCategoryDoesNotExist_shouldThrowException() {
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        TaskRequest createRequest = new TaskRequest(
                "title",
                null,
                TaskConfig.Priority.NORMAL,
                TaskConfig.Status.READY,
                LocalDate.now(),
                UUID.randomUUID()
        );

        assertThatThrownBy(() -> taskService.create(createRequest))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void update_whenTaskDoesNotExist_shouldThrowException() {
        when(taskRepository.findById(any())).thenReturn(Optional.empty());

        TaskRequest createRequest = new TaskRequest(
                "title",
                null,
                TaskConfig.Priority.NORMAL,
                TaskConfig.Status.READY,
                LocalDate.now(),
                UUID.randomUUID()
        );

        assertThatThrownBy(() -> taskService.update(UUID.randomUUID(), createRequest))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void update_whenNewCategoryDoesNotExist_shouldThrowException() {

        UUID oldCategoryId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        task.setId(taskId);
        task.setId(oldCategoryId);
        Category category = new Category();
        category.setId(oldCategoryId);
        task.setCategory(category);

        when(taskRepository.findById(eq(taskId))).thenReturn(Optional.of(task));
        when(categoryRepository.findById(eq(newCategoryId))).thenReturn(Optional.empty());

        TaskRequest updateRequest = new TaskRequest(
                "title",
                null,
                TaskConfig.Priority.NORMAL,
                TaskConfig.Status.READY,
                LocalDate.now(),
                newCategoryId
        );

        assertThatThrownBy(() -> taskService.update(taskId, updateRequest))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}