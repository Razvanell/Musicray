package razvanell.musicrays.model.user.registration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import razvanell.musicrays.model.user.User;
import razvanell.musicrays.model.user.UserRole;
import razvanell.musicrays.model.user.UserService;
import razvanell.musicrays.model.user.registration.email.EmailValidator;
import razvanell.musicrays.model.user.registration.token.ConfirmationToken;
import razvanell.musicrays.model.user.registration.token.ConfirmationTokenService;
import razvanell.musicrays.security.util.ServerResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    public void testRegister() {
        doNothing().when(userService).postUser(any());
        when(emailValidator.test(anyString())).thenReturn(true);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("foo");

        RegistrationRequest registrationRequest = new RegistrationRequest("Jane", "Doe", "jane.doe@example.org", "iloveyou",
                "iloveyou", "https://example.org/example");

        ServerResponse actualRegisterResult = registrationService.register(registrationRequest);

        assertEquals(" ", actualRegisterResult.getError());
        assertEquals(200, actualRegisterResult.getStatus());
        assertSame(registrationRequest, actualRegisterResult.getResult());
        assertEquals("User created", actualRegisterResult.getMessage());

        verify(bCryptPasswordEncoder).encode(any());
        verify(emailValidator).test(anyString());
        verify(userService).postUser(any());
    }

    @Test
    public void testRegisterInvalidEmail() {
        when(emailValidator.test(anyString())).thenReturn(false);

        ServerResponse actualRegisterResult = registrationService.register(new RegistrationRequest("Jane", "Doe",
                "jane.doe@example.org", "iloveyou", "iloveyou", "https://example.org/example"));

        assertEquals("Invalid Email", actualRegisterResult.getError());
        assertEquals(400, actualRegisterResult.getStatus());
        assertNull(actualRegisterResult.getResult());
        assertEquals("", actualRegisterResult.getMessage());

        verify(emailValidator).test(anyString());
        verifyNoMoreInteractions(userService, bCryptPasswordEncoder);
    }

    @Test
    public void testRegisterPasswordsDoNotMatch() {
        when(emailValidator.test(anyString())).thenReturn(true);

        ServerResponse actualRegisterResult = registrationService.register(new RegistrationRequest("Jane", "Doe",
                "jane.doe@example.org", "User created", "iloveyou", "https://example.org/example"));

        assertEquals("Passwords do not match", actualRegisterResult.getError());
        assertEquals(400, actualRegisterResult.getStatus());
        assertNull(actualRegisterResult.getResult());
        assertEquals("", actualRegisterResult.getMessage());

        verify(emailValidator).test(anyString());
        verifyNoMoreInteractions(userService, bCryptPasswordEncoder);
    }

    @Test
    public void testRegisterWithMockedRequest() {
        doNothing().when(userService).postUser(any());
        when(emailValidator.test(anyString())).thenReturn(true);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("foo");

        RegistrationRequest registrationRequest = mock(RegistrationRequest.class);
        when(registrationRequest.getImageUrl()).thenReturn("https://example.org/example");
        when(registrationRequest.getLastName()).thenReturn("foo");
        when(registrationRequest.getFirstName()).thenReturn("foo");
        when(registrationRequest.getConfirmPassword()).thenReturn("foo");
        when(registrationRequest.getPassword()).thenReturn("foo");
        when(registrationRequest.getEmail()).thenReturn("foo");

        ServerResponse actualRegisterResult = registrationService.register(registrationRequest);

        assertEquals(" ", actualRegisterResult.getError());
        assertEquals(200, actualRegisterResult.getStatus());
        assertEquals("User created", actualRegisterResult.getMessage());

        verify(bCryptPasswordEncoder).encode(any());
        verify(emailValidator).test(anyString());
        verify(registrationRequest).getFirstName();
        verify(registrationRequest).getLastName();
        verify(registrationRequest, times(2)).getEmail();
        verify(registrationRequest).getImageUrl();
        verify(registrationRequest, times(2)).getPassword();
        verify(registrationRequest).getConfirmPassword();
        verify(userService).postUser(any());
    }

    @Test
    public void testConfirmTokenAlreadyConfirmed() {
        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        confirmationToken.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        confirmationToken.setId(123L);
        confirmationToken.setExpiresAt(LocalDateTime.of(1, 1, 1, 1, 1));
        confirmationToken.setToken("ABC123");
        confirmationToken.setUser(user);

        Optional<ConfirmationToken> tokenOptional = Optional.of(confirmationToken);
        when(confirmationTokenService.getToken(anyString())).thenReturn(tokenOptional);

        ServerResponse actualConfirmTokenResult = registrationService.confirmToken("ABC123");

        assertEquals("Email already confirmed", actualConfirmTokenResult.getError());
        assertEquals(400, actualConfirmTokenResult.getStatus());
        assertNull(actualConfirmTokenResult.getResult());
        assertEquals("", actualConfirmTokenResult.getMessage());

        verify(confirmationTokenService).getToken(anyString());
    }

    @Test
    public void testConfirmTokenExpired() {
        User user = new User();
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.org");
        user.setPassword("iloveyou");
        user.setTokens(new ArrayList<>());
        user.setPlaylists(new ArrayList<>());
        user.setLocked(true);
        user.setImageUrl("https://example.org/example");
        user.setId(123L);
        user.setUserRole(UserRole.USER);
        user.setEnabled(true);
        user.setFirstName("Jane");

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmedAt(null);
        confirmationToken.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        confirmationToken.setId(123L);
        confirmationToken.setExpiresAt(LocalDateTime.of(1, 1, 1, 1, 1));
        confirmationToken.setToken("ABC123");
        confirmationToken.setUser(user);

        Optional<ConfirmationToken> tokenOptional = Optional.of(confirmationToken);
        when(confirmationTokenService.getToken(anyString())).thenReturn(tokenOptional);

        ServerResponse actualConfirmTokenResult = registrationService.confirmToken("ABC123");

        assertEquals("Token expired", actualConfirmTokenResult.getError());
        assertEquals(400, actualConfirmTokenResult.getStatus());
        assertNull(actualConfirmTokenResult.getResult());
        assertEquals("", actualConfirmTokenResult.getMessage());

        verify(confirmationTokenService).getToken(anyString());
    }

    @Test
    public void testConfirmTokenNotFound() {
        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.empty());

        ServerResponse actualConfirmTokenResult = registrationService.confirmToken("ABC123");

        // Adjust these asserts depending on what your confirmToken method returns when token not found
        assertNotNull(actualConfirmTokenResult);
        assertEquals("Token not found", actualConfirmTokenResult.getError());
        assertEquals(400, actualConfirmTokenResult.getStatus());

        verify(confirmationTokenService).getToken(anyString());
    }
}
