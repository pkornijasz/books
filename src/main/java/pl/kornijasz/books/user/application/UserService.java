package pl.kornijasz.books.user.application;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kornijasz.books.user.application.port.UserRegistrationUseCase;
import pl.kornijasz.books.user.db.UserEntityRepository;
import pl.kornijasz.books.user.domain.UserEntity;

@Service
@AllArgsConstructor
public class UserService implements UserRegistrationUseCase {

    private final UserEntityRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public RegisterResponse register(String username, String password) {
        if (repository.findByUsernameIgnoreCase(username).isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity entity = new UserEntity(username, encoder.encode(password));
        return RegisterResponse.success(repository.save(entity));
    }
}
