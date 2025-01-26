package taskmanagement.application.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import taskmanagement.domain.auth.filter.JwtAuthenticationFilter;
import taskmanagement.domain.user.persistence.UserAccountRepository;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @AfterEach
    void tearDown() {
        userAccountRepository.deleteAll();
    }

    public static Stream<MockHttpServletRequestBuilder> providePublicEndpointsRequests() {
        String loginJson = "{\"username\":\"test\", \"password\":\"test\"}";
        String registerJson = "{\"username\":\"username1\", \"password\":\"!Aa11111111\", \"email\":\"email@email.com\"}";
        return Stream.of(
                post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerJson),
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson)
        );
    }

    @ParameterizedTest
    @MethodSource("providePublicEndpointsRequests")
    void publicEndpoints_shouldNotBeFilteredByJwtFilter(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder);
        verify(jwtAuthenticationFilter, times(0)).doFilter(any(), any(), any());
    }
}