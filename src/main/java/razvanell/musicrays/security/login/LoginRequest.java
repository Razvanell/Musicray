package razvanell.musicrays.security.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
