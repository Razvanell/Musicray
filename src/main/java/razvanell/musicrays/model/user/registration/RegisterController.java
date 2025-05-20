package razvanell.musicrays.model.user.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import razvanell.musicrays.util.ServerResponse;

@RestController
@RequestMapping(path = "api/register")
@AllArgsConstructor
public class RegisterController {

    private final RegisterService registrationService;

    @PostMapping
    public ServerResponse register(@RequestBody RegisterRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public ServerResponse confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
