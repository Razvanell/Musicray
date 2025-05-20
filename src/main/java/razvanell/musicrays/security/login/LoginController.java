package razvanell.musicrays.security.login;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import razvanell.musicrays.model.user.User;
import razvanell.musicrays.security.JwtTokenUtil;
import razvanell.musicrays.util.ServerResponse;

@RestController
@RequestMapping(path = "api/login")
@AllArgsConstructor
public class LoginController {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ServerResponse loginWithToken(@RequestBody LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = (User) authenticate.getPrincipal();

            // Preparing object for client
            LoginResponse loginResponse = new LoginResponse(jwtTokenUtil.generateAccessToken(user), user);
            return new ServerResponse(HttpStatus.OK.value(), "Login successful " + user.getFirstName(), loginResponse);

        } catch (BadCredentialsException ex) {
            System.out.println("Invalid credentials");
            return new ServerResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        }
    }

}

