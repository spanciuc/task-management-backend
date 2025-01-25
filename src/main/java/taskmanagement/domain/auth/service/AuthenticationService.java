package taskmanagement.domain.auth.service;

import org.springframework.security.core.AuthenticationException;
import taskmanagement.domain.auth.dto.LoginRequest;
import taskmanagement.domain.auth.dto.LoginResponse;
import taskmanagement.domain.user.dto.RegisterUserRequest;
import taskmanagement.domain.user.exception.EmailAlreadyExistsException;
import taskmanagement.domain.user.exception.UsernameAlreadyExistsException;

/**
 * Authentication service.
 */
public interface AuthenticationService {

    /**
     * Registers a new user.
     *
     * @param request register request.
     * @throws UsernameAlreadyExistsException if username already exists.
     * @throws EmailAlreadyExistsException    if email already exists.
     */
    void register(RegisterUserRequest request) throws UsernameAlreadyExistsException, EmailAlreadyExistsException;

    /**
     * Logs in a user and generate the authentication token.
     *
     * @param request login request.
     * @return login response, containing the token.
     * @throws AuthenticationException if login attempt fails.
     */
    LoginResponse login(LoginRequest request);

}
