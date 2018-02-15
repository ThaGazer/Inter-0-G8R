/*
 * serialization.test:G8RRequestTest
 * Created on 1/28/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package G8R.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import G8R.serialization.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Request_method test")
@Nested
public class G8RRequestTest {

    private G8RRequest req;

    @DisplayName("constructor test")
    @Nested
    public class constructorTest {

        @DisplayName("attribute invalid")
        @Test
        public void testAttributeConstrInvalid() throws ValidationException {
            assertThrows(ValidationException.class, ()-> {
                new G8RRequest("invalid function", new String[]{"para1"},
                        new CookieList());
            });
        }

        @DisplayName("attribute valid")
        @Test
        public void testAttributeConstrValid() throws ValidationException {
            req = new G8RRequest("F1", new String[]{"P1"},
                    new CookieList());
        }
    }

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
    @MethodSource("getDecodeInvalid")
    public void RequestDecodeTest_invalid(String b) {
        assertThrows(ValidationException.class, ()->G8RMessage.decode(
                new MessageInput(new ByteArrayInputStream(
                        b.getBytes(StandardCharsets.US_ASCII)))));
    }

    @DisplayName("valid")
    @ParameterizedTest
    @MethodSource("getDecodeValid")
    public void RequestDecodeTest_valid(String b)
            throws IOException, ValidationException {
        req = (G8RRequest)G8RMessage.decode(new MessageInput(
                new ByteArrayInputStream(
                        b.getBytes(StandardCharsets.US_ASCII))));
        ByteArrayOutputStream encoder = new ByteArrayOutputStream();
        req.encode(new MessageOutput(encoder));
        assertEquals(b, new String(encoder.toByteArray()));
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
        String[] params = {"p2", "p1"};
        req.setParams(params);
        assertEquals(params, req.getParams());
    }

    private static Stream<String> getEncodeParam() {
        return Stream.of("G8R/1.0 Q RUN Poll\r\n\r\n");
    }

    private static Stream<String> getDecodeInvalid() {
        return Stream.of("G8R/1.0 Q run fcn p1 p2\n\r\n",
                "G8R/2.0 Q RUN fnv p1\r\nx=1\r\n\r\n",
                "G8R/1.0 q RUN fun p \r\n\r\n",
                "G8r/1.0 Q RUN fcnp1\r\n\r\n",
                "G8R/1.0 Q RuN fcnp1\r\n\r\n");
    }

    private static Stream<String> getDecodeValid() {
        return Stream.of("G8R/1.0 Q RUN f1\r\n\r\n",
                "G8R/1.0 Q RUN f1 p1\r\n\r\n",
                "G8R/1.0 Q RUN f1 p1 p2\r\nx=1\r\ny=2\r\nz=3\r\n\r\n");
    }
}
