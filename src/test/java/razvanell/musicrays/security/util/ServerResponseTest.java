package razvanell.musicrays.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ServerResponseTest {
    @Test
    public void testConstructor() {
        ServerResponse actualServerResponse = new ServerResponse(1, "An error occurred");
        actualServerResponse.setError("An error occurred");
        actualServerResponse.setMessage("Not all who wander are lost");
        actualServerResponse.setResult("Result");
        actualServerResponse.setStatus(1);
        assertEquals("An error occurred", actualServerResponse.getError());
        assertEquals("Not all who wander are lost", actualServerResponse.getMessage());
        assertEquals(1, actualServerResponse.getStatus());
    }

    @Test
    public void testConstructor2() {
        ServerResponse actualServerResponse = new ServerResponse(1, "Not all who wander are lost", "Result");
        actualServerResponse.setError("An error occurred");
        actualServerResponse.setMessage("Not all who wander are lost");
        actualServerResponse.setResult("Result");
        actualServerResponse.setStatus(1);
        assertEquals("An error occurred", actualServerResponse.getError());
        assertEquals("Not all who wander are lost", actualServerResponse.getMessage());
        assertEquals(1, actualServerResponse.getStatus());
    }

    @Test
    public void testConstructor3() {
        ServerResponse actualServerResponse = new ServerResponse(1, "Not all who wander are lost", "An error occurred",
                "Result");
        actualServerResponse.setError("An error occurred");
        actualServerResponse.setMessage("Not all who wander are lost");
        actualServerResponse.setResult("Result");
        actualServerResponse.setStatus(1);
        assertEquals("An error occurred", actualServerResponse.getError());
        assertEquals("Not all who wander are lost", actualServerResponse.getMessage());
        assertEquals(1, actualServerResponse.getStatus());
    }
}

