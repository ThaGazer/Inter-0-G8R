/*
 * N4M.serialization.test:testN4MMessage
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization.test;

import N4M.serialization.ErrorCodeType;
import N4M.serialization.N4MException;
import N4M.serialization.N4MMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import sun.nio.cs.StandardCharsets;
import sun.nio.cs.US_ASCII;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class testN4MMessage {

    @ParameterizedTest
    @MethodSource("getValid")
    public void testValidEncode(byte[] bArr) throws N4MException {
        assertArrayEquals(bArr, N4MMessage.decode(bArr).encode());
    }

    @ParameterizedTest
    @MethodSource("getInvalid")
    public void testInvalidEncode(byte[] bArr) {
        assertThrows(N4MException.class, ()-> N4MMessage.decode(bArr));
    }

    @ParameterizedTest
    @MethodSource("getValid")
    public void testSetMessageId(byte[] bArr) throws N4MException {
        int testId = Integer.MAX_VALUE;
        N4MMessage message = N4MMessage.decode(bArr);
        message.setMsgId(testId);
        assertEquals(testId, message.getMsgId());
    }

    @ParameterizedTest
    @MethodSource("getValid")
    public void testSetErrorCodeType(byte[] bArr) throws N4MException {
        ErrorCodeType testEct = ErrorCodeType.NOERROR;
        N4MMessage message = N4MMessage.decode(bArr);
        message.setErrorCodeNum(testEct.getErrorCodeNum());
        assertEquals(testEct.getErrorCodeNum(), message.getErrorCodeNum());
    }

    public static Stream<byte[]> getValid() {
        return Stream.of(new byte[]{0x20,0x01,0x01,0x31},
                new byte[]{(byte) 0x28,0x02,0x00,0x00,0x00,0x00,0x01,0x00,0x01,
                        0x01,0x31});
    }

    public static Stream<byte[]> getInvalid() {
        return Stream.of("".getBytes(), new byte[]{0x00,0x01,0x01,0x31},
                new byte[]{0x20,0x01,0x01},
                new byte[]{0x20,0x01,0x01,0x31,0x31},
                new byte[]{0x20,0x01}, new byte[]{0x20,0x01,0x01,0x01},
                new byte[]{0x2e,0x01,0x01,0x00,0x00,0x00,0x00,0x01,0x00,0x00,
                        0x01,0x31,0x31},
                new byte[]{0x28,0x01,0x01,0x00,0x00,0x00,0x01,0x00,0x00,
                        0x01,0x31},
                new byte[]{0x28,0x01,0x01,0x31});
    }
}
