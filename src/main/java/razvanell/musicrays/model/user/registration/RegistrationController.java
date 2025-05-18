package razvanell.musicrays.model.user.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import razvanell.musicrays.security.util.ServerResponse;

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
