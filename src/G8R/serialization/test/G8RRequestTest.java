/*
 * serialization.test:G8RRequestTest
 * Created on 1/28/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package G8R.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import G8R.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Request_method test")
public class G8RRequestTest {

    private G8RRequest res;
    private String message = "G8R/1.0 Q run fcn p1 p2\r\nx=1\r\ny=2\r\n\r\n";

    public G8RRequestTest() throws IOException, ValidationException {
        res = (G8RRequest) G8RMessage.decode(new MessageInput(
                new ByteArrayInputStream(
                        message.getBytes(StandardCharsets.US_ASCII))));
    }

    @DisplayName("encode")
    @Test
    public void RequestEncodeTest() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bout);
        res.encode(out);
        assertArrayEquals(message.getBytes(StandardCharsets.US_ASCII),
                bout.toByteArray());
    }

    @DisplayName("set function")
    @Test
    public void setFunctionTest_valid() throws ValidationException {
        String funct = "aFunct";
        res.setFunction(funct);
        assertEquals(funct, res.getFunction());
    }

    @DisplayName("set parameters")
    @Test
    public void setParams_valid() throws ValidationException {
        String[] params = {"p2, p1"};
        res.setParams(params);
        assertEquals(params, res.getParams());
    }
}
