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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Nested
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
        int testId = 1;
        N4MMessage message = N4MMessage.decode(bArr);
        message.setMsgId(testId);
        assertEquals(testId, message.getMsgId());
    }

    @ParameterizedTest
    @MethodSource("getValid")
    public void testSetErrorCodeType(byte[] bArr) throws N4MException {
        ErrorCodeType testEct = ErrorCodeType.NOERROR;
        N4MMessage message = N4MMessage.decode(bArr);
        message.setErrorCode(testEct);
        assertEquals(testEct.getErrorCodeNum(),
                message.getErrorCode().getErrorCodeNum());
    }

    public static Stream<byte[]> getValid() {
        return Stream.of(new byte[]{0x20,0x01,0x01,0x31},
                new byte[]{(byte) 0x28,0x02,0x00,0x00,0x00,0x00,0x01,0x00,0x01,
                        0x01,0x31},
                new byte[]{0x28,0x01,0x00,(byte)0xff,(byte)0xff,(byte)0xff,
                        0x01,0x00,0x01,0x01,0x31},
                new byte[]{0x20,(byte)0xcc,0x00},
                new byte[]{0x2b,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
                        (byte)0xff,0x00},
                new byte[]{0x28,(byte)0xff,(byte)0xff,(byte)0xff,
                        (byte)0xff,(byte)0xff,0x00},
                new byte[]{0x2b, (byte)0xfa, 0x00, 0x00, 0x00, 0x00, 0x01,
                        (byte)0xff, (byte)0xff, (byte)0x80, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78,
                        0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78, 0x78});
    }

    public static Stream<byte[]> getInvalid() {
        return Stream.of("".getBytes(), new byte[]{0x00,0x01,0x01,0x31},
                new byte[]{0x20,0x01,0x01},
                new byte[]{0x20,0x01,0x02,0x31,0x31,0x31},
                new byte[]{0x20,0x01}, new byte[]{0x20,0x01,0x01,0x01},
                new byte[]{0x20,0x01,(byte)0xff,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31,(byte)0x7a,
                        0x78,0x31,(byte)0x7a,0x78,0x31},
                new byte[]{0x21,0x01,0x01,0x01},
                new byte[]{0x28, 0x00, 0x00, 0x00, 0x00, 0x06, 0x02, 0x00,
                        0x01, 0x01, 0x61, 0x00, 0x00, 0x02, 0x62},
                new byte[]{0x27,0x01,0x01,0x01},
                new byte[]{0x2e,0x01,0x01,0x00,0x00,0x00,0x00,0x01,0x00,0x00,
                        0x01,0x31,0x31},
                new byte[]{0x28,0x01,0x00,0x00,0x00,0x01,0x02,0x00,0x00,
                        0x01,0x31},
                new byte[]{0x28,0x01,0x01,0x31});
    }
}
