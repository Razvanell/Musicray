package razvanell.musicrays.security.login;

import lombok.Getter;
import lombok.Setter;
import razvanell.musicrays.model.user.User;

@Setter
@Getter
public class LoginResponse {
    private String token;
    private User user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

}