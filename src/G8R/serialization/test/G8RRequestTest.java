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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Request_method test")
public class G8RRequestTest {

    private G8RRequest req;

    @DisplayName("valid")
    @ParameterizedTest
    @MethodSource("getEncodeParam")
    public void RequestEncodeTest_valid(String b)
            throws IOException, ValidationException {
        req = (G8RRequest)G8RMessage.decode(new MessageInput(
                new ByteArrayInputStream(b.getBytes())));

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bout);
        req.encode(out);
        assertArrayEquals(b.getBytes(), bout.toByteArray());
    }

    @DisplayName("invalid")
    @ParameterizedTest
    @MethodSource("getEncodeInvalid")
    public void RequestDecodeTest_invalid(String b) {
        assertThrows(ValidationException.class, ()->G8RMessage.decode(
                new MessageInput(new ByteArrayInputStream(
                        b.getBytes(StandardCharsets.US_ASCII)))));
    }

    @DisplayName("set function")
    @Test
    public void setFunctionTest_valid() throws ValidationException {
        req = new G8RRequest();
        String funct = "aFunct";
        req.setFunction(funct);
        assertEquals(funct, req.getFunction());
    }

    @DisplayName("set parameters")
    @Test
    public void setParams_valid() throws ValidationException {
        req = new G8RRequest();
        String[] params = {"p2, p1"};
        req.setParams(params);
        assertEquals(params, req.getParams());
    }

    private static Stream<String> getEncodeParam() {
        return Stream.of("G8R/1.0 Q RUN Poll\r\n\r\n");
    }

    private static Stream<String> getEncodeInvalid() {
        return Stream.of("G8R/1.0 Q run fcn p1 p2\n\r\n",
                "G8R/2.0 Q RUN fnv p1\r\nx=1\r\n\r\n",
                "G8R/1.0 q RUN fun p \r\n\r\n",
                "G8r/1.0 Q RUN fcnp1\r\n\r\n");
    }
}
