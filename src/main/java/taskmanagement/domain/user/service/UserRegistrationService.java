package taskmanagement.domain.user.service;

import taskmanagement.domain.user.dto.RegisterUserRequest;
import taskmanagement.domain.user.exception.EmailAlreadyExistsException;
import taskmanagement.domain.user.exception.UsernameAlreadyExistsException;

public interface UserRegistrationService {
    /**
     * Registers a new user.
     *
     * @param request register request.
     * @throws UsernameAlreadyExistsException if username already exists.
     * @throws EmailAlreadyExistsException    if email already exists.
     */
    void register(RegisterUserRequest request) throws UsernameAlreadyExistsException, EmailAlreadyExistsException;
}
