package razvanell.musicrays.model.user.registration.email;

import org.springframework.stereotype.Service;

@Service
public class EmailBuilder {

    public String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;padding:0;color:#0b0c0c;background-color:#f9f9f9\">\n" +
                "  <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color:#0b0c0c;padding:20px 0\">\n" +
                "    <tr>\n" +
                "      <td align=\"center\">\n" +
                "        <h1 style=\"color:#ffffff;margin:0;font-size:28px\">Confirm your email</h1>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "\n" +
                "  <table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:580px;background-color:#ffffff;margin:20px auto;padding:30px;border-radius:8px\">\n" +
                "    <tr>\n" +
                "      <td style=\"font-size:18px;line-height:1.6;color:#333\">\n" +
                "        <p>Hi " + name + ",</p>\n" +
                "        <p>Thank you for registering. Please click the button below to activate your account:</p>\n" +
                "        <p style=\"text-align:center;margin:30px 0\">\n" +
                "          <a href=\"" + link + "\" style=\"display:inline-block;padding:12px 24px;background-color:#1db954;color:#ffffff;text-decoration:none;font-weight:bold;border-radius:5px\">Activate Now</a>\n" +
                "        </p>\n" +
                "        <p>The link will expire in 15 minutes.</p>\n" +
                "        <p>See you soon,<br><strong>MusicRays Team</strong></p>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "</div>";
    }

}
