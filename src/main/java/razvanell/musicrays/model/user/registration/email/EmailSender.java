package razvanell.musicrays.model.user.registration.email;

public interface EmailSender {
    void send(String to, String email);
    //using maildev to recieve emails
}
