/*
 * serialization.test:G8RResponseTest
 * Created on 1/28/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package serialization.test;

import org.junit.jupiter.api.*;
import serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("response_method test")
@Nested
public class G8RResponseTest {

    private G8RResponse res;
    private String message = "G8R/1.0 R status funct " +
            "this is a message\r\nx=1\r\ny=2\r\n\r\n";

    public G8RResponseTest() throws IOException, ValidationException {
        res = (G8RResponse)G8RMessage.decode(new MessageInput(
                new ByteArrayInputStream(
                        message.getBytes(StandardCharsets.US_ASCII))));
    }

    @DisplayName("encode")
    @Test
    public void ResponseEncodeTest() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bout);
        res.encode(out);
        assertArrayEquals(message.getBytes(StandardCharsets.US_ASCII),
                bout.toByteArray());
    }

    @DisplayName("set function")
    @Test
    public void setFunctionTest_Valid() throws ValidationException {
        String funct = "aFunct";
        res.setFunction(funct);
        assertEquals(funct, res.getFunction());
    }

    @DisplayName("set message")
    @Test
    public void setMessageTest() throws ValidationException {
        String message = "a rather gentlemanly message";
        res.setMessage(message);
        assertEquals(message, res.getMessage());
    }

    @DisplayName("setStatus")
    @Test
    public void setStatusTest() throws ValidationException {
        String status = "valid";
        res.setStatus(status);
        assertEquals(status, res.getStatus());
    }
}
