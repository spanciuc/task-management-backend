package taskmanagement.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import taskmanagement.domain.auth.dto.LoginRequest;
import taskmanagement.domain.auth.service.AuthenticationTokenProvider;
import taskmanagement.domain.task.config.TaskConfig;
import taskmanagement.domain.task.dto.TaskDTO;
import taskmanagement.domain.task.dto.TaskRequest;
import taskmanagement.domain.task.persistence.Category;
import taskmanagement.domain.task.persistence.CategoryRepository;
import taskmanagement.domain.task.persistence.Task;
import taskmanagement.domain.task.persistence.TaskRepository;
import taskmanagement.domain.user.persistence.UserAccount;
import taskmanagement.domain.user.persistence.UserAccountRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class TaskControllerTest {

    private static final String USERNAME = "username1";
    private static final String PASSWORD = "!Aa111111111";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AuthenticationTokenProvider authenticationTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static Stream<MockHttpServletRequestBuilder> provideRequestsWithNoAuth() {
        String jsonString = """
                {
                "title": "title",
                "description": "description",
                "priority": "NORMAL",
                "status": "READY",
                "dueDate": "2025-01-26T18:10:47.853450Z",
                "categoryId": "021b752c-6ba0-43d4-98a7-80afe2e915e4"
                }""";
        UUID id = UUID.randomUUID();
        return Stream.of(
                get("/tasks"),
                get("/tasks/filter"),
                get("/tasks/" + id),
                post("/tasks").contentType(MediaType.APPLICATION_JSON).content(jsonString),
                put("/tasks/" + id).contentType(MediaType.APPLICATION_JSON).content(jsonString),
                delete("/tasks/" + id)
        );
    }

    @BeforeEach
    void setUp() {
        UserAccount testUserAccount = new UserAccount();
        testUserAccount.setEmail("email@email.com");
        testUserAccount.setUsername(USERNAME);
        testUserAccount.setPassword(passwordEncoder.encode(PASSWORD));
        userAccountRepository.save(testUserAccount);

        Category cat1 = new Category();
        cat1.setTitle("category1");
        cat1.setDescription("category1-description");
        Category cat2 = new Category();
        cat2.setTitle("category2");
        cat2.setDescription("category2-description");
        Category cat3 = new Category();
        cat3.setTitle("category3");
        cat3.setDescription("category3-description");
        List<Category> categories = List.of(cat1, cat2, cat3);
        categoryRepository.saveAll(categories);

        Task task1 = new Task();
        task1.setTitle("task1");
        task1.setDescription("task1-description");
        task1.setCategory(cat1);
        task1.setDueDate(LocalDate.now());
        task1.setPriority(TaskConfig.Priority.LOW);
        task1.setStatus(TaskConfig.Status.READY);
        Task task2 = new Task();
        task2.setTitle("task2");
        task2.setDescription("task2-description");
        task2.setCategory(cat1);
        task2.setDueDate(LocalDate.now());
        task2.setPriority(TaskConfig.Priority.NORMAL);
        task2.setStatus(TaskConfig.Status.IN_PROGRESS);
        Task task3 = new Task();
        task3.setTitle("task3");
        task3.setDescription("task3-description");
        task3.setCategory(cat2);
        task3.setDueDate(LocalDate.now());
        task3.setPriority(TaskConfig.Priority.HIGH);
        task3.setStatus(TaskConfig.Status.DONE);
        List<Task> tasks = List.of(task1, task2, task3);
        taskRepository.saveAll(tasks);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        categoryRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideRequestsWithNoAuth")
    void endpoints_whenNoAuthentication_shouldReturnUnauthorized(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("provideRequestsWithNoAuth")
    void endpoints_whenNoPermissions_shouldReturnAccessDenied(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(USERNAME, null, Collections.emptyList());
        String tokenWithNoPermissions = authenticationTokenProvider.generateToken(authentication);
        requestBuilder.header("Authorization", "Bearer " + tokenWithNoPermissions);
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden());
    }

    @Test
    void getList_shouldReturnTasks() throws Exception {
        var requestBuilder = get("/tasks");
        addAuthenticationHeader(requestBuilder);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        JsonNode dataJson = responseJson.get("data");
        Set<TaskDTO> actualTasks = objectMapper.treeToValue(dataJson,
                objectMapper.getTypeFactory().constructCollectionType(Set.class, TaskDTO.class));

        assertEquals(3, actualTasks.size());
    }

    @Test
    void getFiltered_shouldReturnFilteredTasks() throws Exception {
        List<Category> categories = categoryRepository.findAll();
        UUID category1Id = categories.stream().filter(c -> c.getTitle().equals("category1")).findFirst().orElseThrow().getId();
        UUID category3Id = categories.stream().filter(c -> c.getTitle().equals("category3")).findFirst().orElseThrow().getId();

        var requestBuilder = get("/tasks/filter")
                .param("categories", category1Id.toString())
                .param("categories", category3Id.toString())
                .param("statuses", TaskConfig.Status.READY.toString())
                .param("statuses", TaskConfig.Status.IN_PROGRESS.toString())
                .param("priorities", TaskConfig.Priority.LOW.toString())
                .param("priorities", TaskConfig.Priority.NORMAL.toString());

        addAuthenticationHeader(requestBuilder);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        JsonNode dataJson = responseJson.get("data");
        Set<TaskDTO> actualTasks = objectMapper.treeToValue(dataJson,
                objectMapper.getTypeFactory().constructCollectionType(Set.class, TaskDTO.class));

        assertEquals(2, actualTasks.size());
    }

    @Test
    void getById_whenDoesNotExist_shouldReturnNotFound() throws Exception {
        var requestBuilder = get("/tasks/" + UUID.randomUUID());

        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    void getById_whenExists_shouldReturnOk() throws Exception {
        List<Task> tasks = taskRepository.findAll();
        UUID task1Id = tasks.stream().filter(c -> c.getTitle().equals("task1")).findFirst().orElseThrow().getId();

        var requestBuilder = get("/tasks/" + task1Id);

        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(task1Id.toString()));
    }

    @Test
    void create_whenInvalidRequest_shouldNotCreateAndReturnBadRequest() throws Exception {
        var createTaskRequest = new TaskRequest("", null, null, null, null, null);

        var requestBuilder = post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationError.title").exists())
                .andExpect(jsonPath("$.validationError.priority").exists())
                .andExpect(jsonPath("$.validationError.status").exists())
                .andExpect(jsonPath("$.validationError.dueDate").exists());

        List<Task> existentTasks = taskRepository.findAll();
        assertEquals(3, existentTasks.size());
    }

    @Test
    void create_whenCategoryNotFound_shouldReturnBadRequest() throws Exception {
        var createTaskRequest = new TaskRequest(
                "new task",
                "description",
                TaskConfig.Priority.HIGH, TaskConfig.Status.READY,
                LocalDate.now(),
                UUID.randomUUID());

        var requestBuilder = post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationError.category").exists());

        List<Task> existentTasks = taskRepository.findAll();
        assertEquals(3, existentTasks.size());
    }

    @Test
    void create_whenValidRequest_shouldCreateAndReturnCreated() throws Exception {
        List<Category> categories = categoryRepository.findAll();
        UUID category1Id = categories.stream().filter(c -> c.getTitle().equals("category1")).findFirst().orElseThrow().getId();
        var createTaskRequest = new TaskRequest(
                "new task",
                "description",
                TaskConfig.Priority.HIGH, TaskConfig.Status.READY,
                LocalDate.now(),
                category1Id);

        var requestBuilder = post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists());

        List<Task> existentCategories = taskRepository.findAll();
        assertEquals(4, existentCategories.size());
    }

    @Test
    void update_whenInvalidRequest_shouldNotUpdateAndReturnBadRequest() throws Exception {
        List<Task> tasks = taskRepository.findAll();
        UUID task1Id = tasks.stream().filter(c -> c.getTitle().equals("task1")).findFirst().orElseThrow().getId();
        var updateTaskRequest = new TaskRequest("", null, null, null, null, null);

        var requestBuilder = put("/tasks/" + task1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationError.title").exists())
                .andExpect(jsonPath("$.validationError.priority").exists())
                .andExpect(jsonPath("$.validationError.status").exists())
                .andExpect(jsonPath("$.validationError.dueDate").exists());

        List<Task> existentTasks = taskRepository.findAll();
        assertEquals(3, existentTasks.size());
    }

    @Test
    void update_whenCategoryNotFound_shouldReturnBadRequest() throws Exception {
        List<Task> tasks = taskRepository.findAll();
        UUID task1Id = tasks.stream().filter(c -> c.getTitle().equals("task1")).findFirst().orElseThrow().getId();
        var updateTaskRequest = new TaskRequest(
                "updated task",
                "updated description",
                TaskConfig.Priority.HIGH,
                TaskConfig.Status.READY,
                LocalDate.now(),
                UUID.randomUUID());

        var requestBuilder = put("/tasks/" + task1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationError.category").exists());

        List<Task> existentTasks = taskRepository.findAll();
        assertEquals(3, existentTasks.size());
    }

    @Test
    void update_whenValidRequest_shouldUpdateAndReturnOk() throws Exception {
        List<Task> tasks = taskRepository.findAll();
        UUID task1Id = tasks.stream().filter(c -> c.getTitle().equals("task1")).findFirst().orElseThrow().getId();
        List<Category> categories = categoryRepository.findAll();
        UUID category3Id = categories.stream().filter(c -> c.getTitle().equals("category3")).findFirst().orElseThrow().getId();
        LocalDate updatedDueDate = LocalDate.now();
        var updateTaskRequest = new TaskRequest(
                "updated task",
                "updated description",
                TaskConfig.Priority.HIGH,
                TaskConfig.Status.READY,
                updatedDueDate,
                category3Id);

        var requestBuilder = put("/tasks/" + task1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        List<Task> existentTasks = taskRepository.findAll();
        assertEquals(3, existentTasks.size());

        Task task = existentTasks.stream().filter(c -> c.getId().equals(task1Id)).findFirst().orElseThrow();
        assertEquals(task.getTitle(), "updated task");
        assertEquals(task.getDescription(), "updated description");
        assertEquals(task.getPriority(), TaskConfig.Priority.HIGH);
        assertEquals(task.getStatus(), TaskConfig.Status.READY);
        assertEquals(task.getCategory().getId(), category3Id);
    }

    @Test
    void delete_whenTaskExists_shouldDeleteAndReturnNoContent() throws Exception {
        Task taskToDelete = taskRepository.findAll().stream().findFirst().orElseThrow();

        var requestBuilder = delete("/tasks/" + taskToDelete.getId())
                .contentType(MediaType.APPLICATION_JSON);
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isNoContent());

        List<Task> existentTasks = taskRepository.findAll();
        assertEquals(2, existentTasks.size());
        assertNull(existentTasks.stream().filter(c -> c.getId().equals(taskToDelete.getId())).findFirst().orElse(null));
    }

    private void addAuthenticationHeader(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        LoginRequest request = new LoginRequest(USERNAME, PASSWORD);
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        String token = responseJson.get("token").asText();
        requestBuilder.header("Authorization", "Bearer " + token);
    }
}