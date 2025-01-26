package taskmanagement.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanagement.domain.task.dto.TaskDTO;
import taskmanagement.domain.task.dto.TaskFilter;
import taskmanagement.domain.task.dto.TaskRequest;
import taskmanagement.domain.task.service.TaskService;
import taskmanagement.shared.dto.IdResponse;
import taskmanagement.shared.dto.PagedResponse;
import taskmanagement.shared.dto.SingleResponse;
import taskmanagement.shared.exception.ResourceNotFoundException;

import java.util.UUID;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/tasks")
@Tag(name = "Task controller")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get tasks list endpoint")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).READ)")
    public PagedResponse<TaskDTO> getList(@ParameterObject
                                          @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC)
                                          Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getPage(pageable);
        return PagedResponse.of(tasks);
    }

    @Operation(summary = "Get filtered tasks list endpoint")
    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).READ)")
    public PagedResponse<TaskDTO> getFilteredList(@ParameterObject
                                                  @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC)
                                                  Pageable pageable,
                                                  @ParameterObject
                                                  TaskFilter filter
    ) {
        Page<TaskDTO> tasks = taskService.getFilteredPage(filter, pageable);
        return PagedResponse.of(tasks);
    }

    @Operation(summary = "Get task by id endpoint")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).READ)")
    public SingleResponse<TaskDTO> getById(@PathVariable UUID id) {
        TaskDTO task = taskService.getTask(id)
                .orElseThrow(ResourceNotFoundException::new);
        return SingleResponse.of(task);
    }

    @Operation(summary = "Create task endpoint")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).WRITE)")
    public SingleResponse<IdResponse> create(@RequestBody @Valid TaskRequest createRequest) {
        TaskDTO task = taskService.create(createRequest);
        return SingleResponse.of(IdResponse.of(task.getId()));
    }

    @Operation(summary = "Update task endpoint")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).WRITE)")
    public void update(@PathVariable UUID id, @RequestBody @Valid TaskRequest updateRequest) {
        taskService.update(id, updateRequest);
    }

    @Operation(summary = "Delete task endpoint")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(T(taskmanagement.domain.user.config.UserConfig.Authority).DELETE)")
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }

}
