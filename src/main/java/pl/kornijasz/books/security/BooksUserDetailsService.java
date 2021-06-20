package pl.kornijasz.books.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.kornijasz.books.user.db.UserEntityRepository;

@AllArgsConstructor
public class BooksUserDetailsService implements UserDetailsService {

    private final UserEntityRepository repository;
    private final AdminConfig config;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (config.getUsername().equalsIgnoreCase(username)) {
            return config.adminUser();
        }
        return repository.findByUsernameIgnoreCase(username)
                .map(x -> new UserEntityDetails(x))
                .orElseThrow(() -> new UsernameNotFoundException(username));

    }
}
