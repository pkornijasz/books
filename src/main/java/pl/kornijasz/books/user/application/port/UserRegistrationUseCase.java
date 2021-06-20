package pl.kornijasz.books.user.application.port;

import pl.kornijasz.books.commons.Either;
import pl.kornijasz.books.user.domain.UserEntity;

public interface UserRegistrationUseCase {
    RegisterResponse register(String username, String password);

    class RegisterResponse extends Either<String, UserEntity> {

        public RegisterResponse(boolean success, String left, UserEntity right) {
            super(success, left, right);
        }

        public static RegisterResponse success(UserEntity right) {
            return new RegisterResponse(true, null, right);
        }

        public static RegisterResponse failure(String left) {
            return new RegisterResponse(false, left, null);
        }
    }
}
