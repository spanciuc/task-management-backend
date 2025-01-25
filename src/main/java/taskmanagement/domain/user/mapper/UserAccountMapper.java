package taskmanagement.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.userdetails.UserDetails;
import taskmanagement.domain.user.persistence.UserAccount;
import taskmanagement.domain.user.config.UserConfig;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserAccount map(String username, String password, String email);

    default UserDetails map(UserAccount user) {
        UserConfig.Role defaultRole = UserConfig.getDefaultUserRole();
        org.springframework.security.core.userdetails.User.UserBuilder builder = org.springframework.security.core.userdetails.User.builder();
        return builder
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(UserConfig.getAuthoritiesByRole(defaultRole))
                .build();
    }

}
