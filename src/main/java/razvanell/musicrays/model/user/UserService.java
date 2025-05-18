package razvanell.musicrays.model.user;

import razvanell.musicrays.model.user.registration.email.EmailBuilder;
import razvanell.musicrays.model.user.registration.email.EmailSender;
import razvanell.musicrays.model.user.registration.token.ConfirmationToken;
import razvanell.musicrays.model.user.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final EmailBuilder emailBuilder;

    @Override //Spring Security method using email as username
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found."));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void postUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()) {
            if(existingUser.get().isEnabled()) {
                throw new IllegalStateException("Email already registered & confirmed");
            } else {
                sendConfirmationEmail(existingUser.get());
                throw new IllegalStateException("Resent confirmation mail");
            }
        }
        userRepository.save(user);
        sendConfirmationEmail(user);
    }

    private void sendConfirmationEmail(User user) {
        String token = generateToken(user);
        String link = "http://localhost:8080/api/registration/confirm?token=" + token;
        emailSender.send(user.getEmail(), emailBuilder.buildEmail(user.getFirstName(), link));
    }

    private String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        confirmationTokenService.saveConfirmationToken(ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build());
        return token;
    }

    public void deleteUser(String email) {
        Long id = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("No user with email: " + email + " exists in the database")).getId();
        userRepository.deleteById(id);
    }

    @Transactional
    public void putUser(User user) {

        User oldUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new IllegalStateException("User with this email does not exist"));

        if(user.getFirstName() != null) oldUser.setFirstName(user.getFirstName());
        if(user.getLastName() != null) oldUser.setLastName(user.getLastName());
        if(user.getImageUrl() != null) oldUser.setImageUrl(user.getImageUrl());
    }

}
