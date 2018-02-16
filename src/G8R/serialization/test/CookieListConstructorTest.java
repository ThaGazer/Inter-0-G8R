/*
 * serialization.test:CookieListTestConstruct
 *
 * Date Created: Jan/18/2018
 * Author:
 *   -Justin Ritter
 *   -Clint Masters
 */
package G8R.serialization.test;

import com.sun.tracing.dtrace.ProviderAttributes;
import org.junit.jupiter.api.DisplayName;
import G8R.serialization.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


@DisplayName("CookieList_Constructor test")
class CookieListConstructorTest {
    private List<String> expNames =
            new ArrayList<>(Arrays.asList("Test", "Cookie"));
    private List<String> expValues =
            new ArrayList<>(Arrays.asList("1", "2"));


    /**********************************************************
     * Tests for the constructor passed another CookieList
     *********************************************************/
    public CookieListConstructorTest() {

    }

    /**
     * Test the easy success route for creating CookieList from another
     * CookieList
     * @throws IOException on an error with input or output
     * @throws ValidationException if the message in messageInput is bad
     */
    @Test
    void testCookieListCookieListSuccess()
            throws IOException, ValidationException {
        String expStr="";
        for(int i = 0; i < expNames.size(); i++) {
            expStr += (expNames.get(i) + "=" + expValues.get(i) + "\r\n");
        }
        expStr += "\r\n";

        ByteArrayInputStream bIn = new ByteArrayInputStream(expStr.getBytes());
        MessageInput mIn = new MessageInput(bIn);
        CookieList testCookie = new CookieList(mIn);
        CookieList cookieCopy = new CookieList(testCookie);
        assertNotNull(cookieCopy);
    }

    /**
     * Tests that a NullPointerException is thrown when
     * the parameter passed in is NULL.
     */
    @Test
    void testCookieListCookieListNullException() {

        assertThrows(NullPointerException.class, () -> {
            CookieList cookie = null;
            CookieList cookieTest = new CookieList(cookie);
        });
    }


    /***************************************************
     * Tests of constructor passed a MessageInput
     **************************************************/


    /**
     * Tests the success case for passing a MessageInput as a parameter
     * @throws IOException on an error with input or output
     * @throws ValidationException if the message in messageInput is bad
     */
    @Test
    void testCookieListMessageInput() throws IOException, ValidationException {
        String expStr="";
        for(int i = 0; i < expNames.size(); i++) {
            expStr += (expNames.get(i) + "=" + expValues.get(i) + "\r\n");
        }
        expStr += "\r\n";

        ByteArrayInputStream bIn = new ByteArrayInputStream(expStr.getBytes());
        MessageInput mIn = new MessageInput(bIn);
        CookieList testCookie = new CookieList(mIn);
    }

    @ParameterizedTest
    @MethodSource("getInvalid")
    void testCookieListMessageInputInvalid(String str) {
        assertThrows(ValidationException.class, ()->
           new CookieList(new MessageInput(new ByteArrayInputStream(
                   str.getBytes(StandardCharsets.US_ASCII)))));
    }

    /**
     * Tests throwing an input validation by passing bad input
     */
    @Test
    void testCookieListMessageInputValidationException() {
        assertThrows(ValidationException.class, () -> {
            String encode = "a\r\n=1\r\nb\r\n=2\r\n";
            MessageInput in = new MessageInput(
                    new ByteArrayInputStream(encode.getBytes()));
            CookieList cookie = new CookieList(in);
        });
    }

    /**
     * Tests throwing a NullPointerException by passing null input
     */
    @Test
    void testCookieListMessageInputNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            MessageInput in = null;
            CookieList cookie = new CookieList(in);
        });
    }

    @DisplayName("empty cookie")
    @ParameterizedTest
    @MethodSource("getValid")
    public void testEmptyCookie(String list)
            throws IOException, ValidationException {
        CookieList cl = new CookieList(new MessageInput(
                new ByteArrayInputStream(list.getBytes(
                        StandardCharsets.US_ASCII))));

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        cl.encode(new MessageOutput(bout));
        assertArrayEquals(list.getBytes(), bout.toByteArray());
    }

    @DisplayName("multiEncode")
    @ParameterizedTest
    @MethodSource("getinvalid")
    public void testCokie(String str) {
        assertThrows(ValidationException.class, () -> {
            MessageInput in = new MessageInput(new ByteArrayInputStream(str.getBytes(StandardCharsets.US_ASCII)));
            new CookieList(in);
            new CookieList(in);
            new CookieList(in);
        });
    }

    private static Stream getValid() {
        return Stream.of("\r\nx=1\r\n\r\n", "x=1\r\n\r\n",
                "x=1\r\ny=1\r\nx=2\r\n\r\n", "x=1\r\n\r\n\r\n", "\r\n");
    }

    private static Stream getinvalid() {
        return Stream.of("x=1\r\n\r\n\r\n");
    }

    private static Stream getInvalid() {
        return Stream.of("", "x=1\r\n", "x==1\r\n\r\n", "x\r\n\r\n",
                "x=1y=2\r\n");
    }
}