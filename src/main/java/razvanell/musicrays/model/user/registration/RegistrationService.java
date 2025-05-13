package razvanell.musicrays.model.user.registration;

import razvanell.musicrays.model.user.User;
import razvanell.musicrays.model.user.UserRepository;
import razvanell.musicrays.model.user.UserRole;
import razvanell.musicrays.model.user.UserService;
import razvanell.musicrays.model.user.registration.email.EmailValidator;
import razvanell.musicrays.model.user.registration.token.ConfirmationToken;
import razvanell.musicrays.model.user.registration.token.ConfirmationTokenService;
import razvanell.musicrays.security.util.ServerResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    public ServerResponse register(RegistrationRequest request) {
        if (!emailValidator.test(request.getEmail())) {
            System.out.println("invalid email");
            return new ServerResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Email");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            System.out.println("passwords mismatch");
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
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).get();

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
