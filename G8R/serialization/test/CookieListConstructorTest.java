/*
 * serialization.test:CookieListTestConstruct
 *
 * Date Created: Jan/18/2018
 * Author:
 *   -Justin Ritter
 *   -Clint Masters
 */
package serialization.test;

import serialization.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
}