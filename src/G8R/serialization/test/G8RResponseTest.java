/*
 * serialization.test:G8RResponseTest
 * Created on 1/28/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package G8R.serialization.test;

import org.junit.jupiter.api.*;
import G8R.serialization.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("response_method test")
@Nested
public class G8RResponseTest {

    private G8RResponse res;

    @DisplayName("encode valid")
    @ParameterizedTest
    @MethodSource("getEncodeParam")
    public void ResponseEncodeTest(String b)
            throws IOException, ValidationException {
        res = (G8RResponse)G8RMessage.decode(new MessageInput(new
                ByteArrayInputStream(b.getBytes(StandardCharsets.US_ASCII))));

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bout);
        res.encode(out);
        assertArrayEquals(b.getBytes(StandardCharsets.US_ASCII),
                bout.toByteArray());
    }

    @DisplayName("encode invalid")
    @ParameterizedTest
    @MethodSource("getEncodeInvalid")
    public void ResponseEncodeInvalid(String b) {
        assertThrows(ValidationException.class, ()->G8RMessage.decode(
                new MessageInput(new ByteArrayInputStream(
                        b.getBytes(StandardCharsets.US_ASCII)))));
    }

    @DisplayName("set function")
    @Test
    public void setFunctionTest_Valid() throws ValidationException {
        res = new G8RResponse();
        String funct = "aFunct";
        res.setFunction(funct);
        assertEquals(funct, res.getFunction());
    }

    @DisplayName("set message")
    @ParameterizedTest
    @ValueSource(strings = "a rather gentlemanly message")
    public void setMessageTest_Valid(String str) throws ValidationException {
        res = new G8RResponse();
        res.setMessage(str);
        assertEquals(str, res.getMessage());
    }

    @DisplayName("setStatus")
    @ParameterizedTest
    @ValueSource(strings = "ERROR")
    public void setStatusTest_Valid(String str) throws ValidationException {
        res = new G8RResponse();
        res.setStatus(str);
        assertEquals(str, res.getStatus());
    }

    private static Stream<String> getEncodeParam() {
        return Stream.of(
                "G8R/1.0 R OK func thie is a good message\r\nx=1\r\n\r\n");
    }

    private static Stream<String> getEncodeInvalid() {
        return Stream.of("G8r/1.0 R OK func thisisamessage\r\n",
                "G8R/1.0 R error func message\r\n", " R OK fun this\r\n");
    }
}
