package razvanell.musicrays.model.user.registration.email;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String email) {
        return isEMailValid(email);
    }

    public boolean isEMailValid(String email) {
        if (email == null) return false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}

/*
 * ^ means it has to start on a new line
 * [a-zA-Z0-9_+&*-] any character in that range
 * + means it must contain at least one character
 * * means it can contain 0 or more characters
 * \\. allows any character to pass once. Like having an underline in an email
 * ?: is a non-capturing group (it doesn't matter what it contains, but it has to respect boundaries).
 * $ means it has to be the end of the line
 */

//Example: ^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$
//Explanation:
//  ^[a-zA-Z0-9_+&*-]+              - On a new line, at least 1 character in [a-zA-Z0-9_+&*-]
//  (?:\.[a-zA-Z0-9_+&*-]+)*@       - Followed by any character in that range until it meets @
//  (?:[a-zA-Z0-9-]+\.)+            - At least 1 character, followed by a dot "\."
//  [a-zA-Z]{2,7}$                  - A suffix of 2-7 chars at the end of the line