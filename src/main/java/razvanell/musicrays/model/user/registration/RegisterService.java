package razvanell.musicrays.model.user.registration;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import razvanell.musicrays.model.user.User;
import razvanell.musicrays.model.user.UserRepository;
import razvanell.musicrays.model.user.UserRole;
import razvanell.musicrays.model.user.UserService;
import razvanell.musicrays.model.user.registration.email.EmailValidator;
import razvanell.musicrays.model.user.registration.token.ConfirmationToken;
import razvanell.musicrays.model.user.registration.token.ConfirmationTokenService;
import razvanell.musicrays.util.ServerResponse;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    public ServerResponse register(RegisterRequest request) {
        if (!emailValidator.test(request.getEmail())) {
            System.out.println("invalid email");
            return new ServerResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Email");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            System.out.println("passwords mismatch: " + request.getPassword() + " " + request.getConfirmPassword());
            return new ServerResponse(HttpStatus.BAD_REQUEST.value(), "Passwords do not match");
        }

        userService.postUser(User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .imageUrl(request.getImageUrl())
                .userRole(UserRole.USER)
                .build());

        return new ServerResponse(HttpStatus.OK.value(), "User created", request);
    }

    @Transactional
    public ServerResponse confirmToken(String token) {
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenService.getToken(token);

        if (confirmationTokenOptional.isEmpty()) {
            return new ServerResponse(HttpStatus.BAD_REQUEST.value(), "Token not found");
        }

        ConfirmationToken confirmationToken = confirmationTokenOptional.get();

        if (confirmationToken.getConfirmedAt() != null) {
            return new ServerResponse(HttpStatus.BAD_REQUEST.value(), "Email already confirmed");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ServerResponse(HttpStatus.BAD_REQUEST.value(), "Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userRepository.enableUser(confirmationToken.getUser().getEmail());
        return new ServerResponse(HttpStatus.OK.value(), "Token confirmed", null);
    }


}
