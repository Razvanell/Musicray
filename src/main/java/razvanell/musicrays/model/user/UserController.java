package razvanell.musicrays.model.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
        userService.deleteUser(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/put")
    public ResponseEntity<?> putArtist(@RequestBody User user) {
        userService.putUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
