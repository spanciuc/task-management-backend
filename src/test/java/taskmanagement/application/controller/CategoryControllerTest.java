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
import taskmanagement.domain.task.dto.CategoryDTO;
import taskmanagement.domain.task.dto.CategoryRequest;
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
class CategoryControllerTest {

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
        String categoryRequestJson = "{\"title\":\"test\"}";
        UUID id = UUID.randomUUID();
        return Stream.of(
                get("/categories"),
                post("/categories").contentType(MediaType.APPLICATION_JSON).content(categoryRequestJson),
                delete("/categories/" + id)
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
    }

    @AfterEach
    void tearDown() {
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
    void getList_shouldReturnCategories() throws Exception {
        var requestBuilder = get("/categories");
        addAuthenticationHeader(requestBuilder);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        JsonNode dataJson = responseJson.get("data");
        Set<CategoryDTO> actualCategories = objectMapper.treeToValue(dataJson,
                objectMapper.getTypeFactory().constructCollectionType(Set.class, CategoryDTO.class));

        assertEquals(3, actualCategories.size());
    }

    @Test
    void create_whenInvalidRequest_shouldNotCreateAndReturnBadRequest() throws Exception {
        var createCategoryRequest = new CategoryRequest("", null);

        var requestBuilder = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCategoryRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationError.title").exists());

        List<Category> existentCategories = categoryRepository.findAll();
        assertEquals(3, existentCategories.size());
    }

    @Test
    void create_whenValidRequest_shouldCreateAndReturnCreated() throws Exception {
        var createCategoryRequest = new CategoryRequest("new-category", "with description");

        var requestBuilder = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCategoryRequest));
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists());

        List<Category> existentCategories = categoryRepository.findAll();
        assertEquals(4, existentCategories.size());
    }


    @Test
    void delete_whenCategoryExists_shouldDeleteAndReturnNoContent() throws Exception {
        Category categoryToDelete = categoryRepository.findAll().stream().findFirst().orElseThrow();

        var requestBuilder = delete("/categories/" + categoryToDelete.getId())
                .contentType(MediaType.APPLICATION_JSON);
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isNoContent());

        List<Category> existentCategories = categoryRepository.findAll();
        assertEquals(2, existentCategories.size());
        assertNull(existentCategories.stream().filter(c -> c.getId().equals(categoryToDelete.getId())).findFirst().orElse(null));
    }

    @Test
    void delete_whenCategoryHasTasks_shouldDeleteCategoryAndTasksAndReturnNoContent() throws Exception {
        Category categoryToDelete = categoryRepository.findAll().stream().findFirst().orElseThrow();

        addTaskInCategory(categoryToDelete);
        assertEquals(3, taskRepository.findAll().size());

        var requestBuilder = delete("/categories/" + categoryToDelete.getId())
                .contentType(MediaType.APPLICATION_JSON);
        addAuthenticationHeader(requestBuilder);

        mockMvc.perform(requestBuilder).andExpect(status().isNoContent());

        List<Category> existentCategories = categoryRepository.findAll();
        assertEquals(2, existentCategories.size());
        assertNull(existentCategories.stream().filter(c -> c.getId().equals(categoryToDelete.getId())).findFirst().orElse(null));

        assertEquals(0, taskRepository.findAll().size());
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

    private void addTaskInCategory(Category category) {


        Task task1 = new Task();
        task1.setTitle("task1");
        task1.setDescription("task1-description");
        task1.setCategory(category);
        task1.setDueDate(LocalDate.now());
        task1.setPriority(TaskConfig.Priority.LOW);
        task1.setStatus(TaskConfig.Status.READY);
        Task task2 = new Task();
        task2.setTitle("task2");
        task2.setDescription("task2-description");
        task2.setCategory(category);
        task2.setDueDate(LocalDate.now());
        task2.setPriority(TaskConfig.Priority.NORMAL);
        task2.setStatus(TaskConfig.Status.IN_PROGRESS);
        Task task3 = new Task();
        task3.setTitle("task3");
        task3.setDescription("task3-description");
        task3.setCategory(category);
        task3.setDueDate(LocalDate.now());
        task3.setPriority(TaskConfig.Priority.HIGH);
        task3.setStatus(TaskConfig.Status.DONE);
        List<Task> tasks = List.of(task1, task2, task3);
        taskRepository.saveAll(tasks);
    }
}