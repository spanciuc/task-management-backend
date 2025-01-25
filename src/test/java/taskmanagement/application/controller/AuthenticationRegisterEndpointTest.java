package taskmanagement.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import taskmanagement.domain.auth.filter.JwtAuthenticationFilter;
import taskmanagement.domain.user.dto.RegisterUserRequest;
import taskmanagement.domain.user.persistence.UserAccount;
import taskmanagement.domain.user.persistence.UserAccountRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthenticationRegisterEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        UserAccount existentUserAccount = new UserAccount();
        existentUserAccount.setEmail("email@email.com");
        existentUserAccount.setUsername("username1");
        existentUserAccount.setPassword("test_password");
        userAccountRepository.save(existentUserAccount);
    }

    @AfterEach
    void tearDown() {
        userAccountRepository.deleteAll();
    }

    @Test
    void register_shouldNotBeFilteredByJwtFilter() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest("", "", "");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        verify(jwtAuthenticationFilter, times(0)).doFilter(any(), any(), any());
    }

    @Test
    void register_whenInvalidRequest_shouldReturnBadRequestResponse() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest("not valid", "weak", "not email");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.validationError.username").exists())
                .andExpect(jsonPath("$.validationError.password").exists())
                .andExpect(jsonPath("$.validationError.email").exists());
        ;
    }

    @Test
    void register_whenExistentEmail_shouldReturnBadRequestResponse() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest("username2", "Aa!11111111", "email@email.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.validationError.email").exists());
    }

    @Test
    void register_whenExistentUsername_shouldReturnBadRequestResponse() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest("username1", "Aa!11111111", "email2@email.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.validationError.username").exists());
    }

    @Test
    void register_whenValidRequest_shouldReturnSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest("username2", "Aa!11111111", "email3@email.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}