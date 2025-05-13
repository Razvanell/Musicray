package razvanell.musicrays.model.user.registration;

import razvanell.musicrays.security.util.ServerResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ServerResponse register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public ServerResponse confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
