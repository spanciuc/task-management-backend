package taskmanagement.domain.task.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskmanagement.domain.task.dto.TaskDTO;
import taskmanagement.domain.task.dto.TaskFilter;
import taskmanagement.domain.task.dto.TaskRequest;
import taskmanagement.domain.task.exception.CategoryNotFoundException;
import taskmanagement.domain.task.exception.TaskNotFoundException;
import taskmanagement.domain.task.mapper.TaskMapper;
import taskmanagement.domain.task.persistence.Category;
import taskmanagement.domain.task.persistence.CategoryRepository;
import taskmanagement.domain.task.persistence.Task;
import taskmanagement.domain.task.persistence.TaskRepository;
import taskmanagement.domain.task.service.TaskService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecificationService taskSpecificationService;

    @Override
    public Page<TaskDTO> getPage(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::map);
    }

    @Override
    public Page<TaskDTO> getFilteredPage(TaskFilter filter, Pageable pageable) {
        Specification<Task> specification = taskSpecificationService.toSpecification(filter);
        return taskRepository.findAll(specification, pageable).map(taskMapper::map);
    }

    @Override
    public Optional<TaskDTO> getTask(UUID id) {
        return taskRepository.findById(id).map(taskMapper::map);
    }

    @Override
    public TaskDTO create(TaskRequest request) throws CategoryNotFoundException {
        Category category = this.categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);
        Task task = taskMapper.map(request, category);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Override
    public TaskDTO update(UUID id, TaskRequest request) throws CategoryNotFoundException, TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

        Category category;
        if (request.getCategoryId().equals(task.getCategory().getId())) {
            category = task.getCategory();
        } else {
            category = this.categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(CategoryNotFoundException::new);
        }

        taskMapper.map(task, request, category);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    @Override
    public void delete(UUID id) {
        taskRepository.deleteById(id);
    }
}
