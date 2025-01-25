package taskmanagement.domain.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taskmanagement.domain.user.dto.RegisterUserRequest;
import taskmanagement.domain.user.exception.EmailAlreadyExistsException;
import taskmanagement.domain.user.exception.UsernameAlreadyExistsException;
import taskmanagement.domain.user.mapper.UserAccountMapper;
import taskmanagement.domain.user.persistence.UserAccount;
import taskmanagement.domain.user.persistence.UserAccountRepository;
import taskmanagement.domain.user.service.UserRegistrationService;

@RequiredArgsConstructor
@Service
class UserRegistrationServiceImpl implements UserRegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;

    @Override
    public void register(RegisterUserRequest request) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        String username = request.getUsername().toLowerCase();
        String email = request.getEmail().toLowerCase();
        String password = passwordEncoder.encode(request.getPassword());

        validateUserRegistration(username, email);

        UserAccount user = userAccountMapper.map(request.getUsername(), password, email);
        userAccountRepository.save(user);
    }

    private void validateUserRegistration(String username, String email) {
        if (userAccountRepository.existsByUsernameIgnoreCase(username)) {
            throw new UsernameAlreadyExistsException();
        }

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException();
        }
    }
}
