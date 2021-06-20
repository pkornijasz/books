package pl.kornijasz.books.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public boolean isOwnerOrAdmin(String objectOwner, UserDetails user) {
        return isAdmin(user) || isUser(objectOwner, user);
    }

    private boolean isUser(String objectOwner, UserDetails user) {
        return user.getUsername().equalsIgnoreCase(objectOwner);
    }

    private boolean isAdmin(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }

}
