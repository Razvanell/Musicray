package razvanell.musicrays.security.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServerResponse {
    private int status; // 200, 400, 500
    private String message;
    private String error;
    private Object result;

    public ServerResponse(int status, String error){
        this(status, "", error, null);
    }

    public ServerResponse(int status, String message, Object result){
        this.status = status;
        this.message = message;
        this.error = " ";
        this.result = result;
    }

    public ServerResponse(int status, String message, String error, Object result) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.result = result;
    }

}