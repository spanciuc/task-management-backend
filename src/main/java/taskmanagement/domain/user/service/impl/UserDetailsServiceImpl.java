package taskmanagement.domain.user.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import taskmanagement.domain.user.mapper.UserAccountMapper;
import taskmanagement.domain.user.persistence.UserAccountRepository;
import taskmanagement.shared.message.MessageCode;

@RequiredArgsConstructor
@Service
class UserDetailsServiceImpl implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null) {
            throw new UsernameNotFoundException(MessageCode.ERROR_RESOURCE_NOT_FOUND.name());
        }

        return userAccountRepository.findByUsername(username)
                .map(userAccountMapper::map)
                .orElseThrow(() -> new UsernameNotFoundException(MessageCode.ERROR_RESOURCE_NOT_FOUND.name()));
    }
}
