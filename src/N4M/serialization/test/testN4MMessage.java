/*
 * N4M.serialization.test:testN4MMessage
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization.test;

import N4M.serialization.N4MException;
import N4M.serialization.N4MMessage;
import N4M.serialization.N4MQuery;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class testN4MMessage {

    private N4MQuery q = new N4MQuery();

    @ParameterizedTest
    @MethodSource("getValid")
    public void testValidEncode(byte[] bArr) throws N4MException {
        assertArrayEquals(N4MMessage.decode(bArr).encode(), bArr);
    }

    @ParameterizedTest
    @MethodSource("getInvalid")
    public void testInvalidEncode(byte[] bArr) {
        assertThrows(N4MException.class, ()-> N4MMessage.decode(bArr));
    }

    public static Stream<byte[]> getValid() {
        return Stream.of("".getBytes());
    }

    public static Stream<byte[]> getInvalid() {
        return Stream.of("".getBytes());
    }
}
