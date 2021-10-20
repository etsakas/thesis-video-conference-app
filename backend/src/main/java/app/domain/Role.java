package app.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN, ROLE_CLIENT;

    @Override
    public String getAuthority() {
        // Returns the name of this enum constant, exactly as declared in its enum
        // declaration. Most programmers should use the toString() method in preference
        // to this one, as the toString method may return a more user-friendly name.
        return name();
    }
}