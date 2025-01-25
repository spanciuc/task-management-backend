package taskmanagement.domain.user.config;

import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Set;

public class UserConfig {

    private static final Map<Role, Set<GrantedAuthority>> roleToAuthoritiesMap = Map.of(
            Role.USER, Set.of(Authority.READ, Authority.WRITE, Authority.DELETE)
    );

    public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|;:'\",.<>/?])(?!.*\\s).{8,32}$";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9]{4,20}$";

    public enum Role {
        USER
    }

    public enum Authority implements GrantedAuthority {
        READ, WRITE, DELETE;

        @Override
        public String getAuthority() {
            return this.name();
        }
    }

    public static Role getDefaultUserRole() {
        return Role.USER;
    }

    public static Set<GrantedAuthority> getAuthoritiesByRole(Role role) {
        return roleToAuthoritiesMap.get(role);
    }
}
