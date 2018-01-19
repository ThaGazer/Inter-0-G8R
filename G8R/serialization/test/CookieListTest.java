/*
 * serialization.test:CookieListConstructorTest
 *
 * Date Created: Jan/13/2018
 * Author:
 *   -Justin Ritter
 */
package serialization.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import serialization.CookieList;
import serialization.MessageInput;
import serialization.MessageOutput;
import serialization.ValidationException;

import java.io.*;
import java.util.*;


/*
 * serialization:CookieListTest
 * 
 * Date Created: Jan/13/2018
 * Author:
 *   -Justin Ritter
 */

class CookieListTest {

    private List<String> expNames =
            new ArrayList<>(Arrays.asList("Bob", "Ted"));
    private List<String> expValues =
            new ArrayList<>(Arrays.asList("1", "2"));

    private CookieList testCookie;
    private String expStr;

    CookieListTest() throws IOException, ValidationException {
        for(int i = 0; i < expNames.size(); i++) {
            expStr += (expNames.get(i) + "=" + expValues.get(i) + "\r\n");
        }
        expStr += "\r\n";

        ByteArrayInputStream bIn = new ByteArrayInputStream(expStr.getBytes());
        MessageInput mIn = new MessageInput(bIn);
        testCookie = new CookieList(mIn);
    }

    @Test
    void testGetNames() {
        assertTrue(testCookie.getNames().equals(expNames));
    }

    @Test
    void testGetValue() {
        List<String> names = new ArrayList<>(testCookie.getNames());

        int i = 0;
        for(String n : names) {
            assertEquals(expValues.get(i), testCookie.getValue(n));
            i++;
        }
    }

    @Test
    void testEncode() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);
        testCookie.encode(out);
        assertEquals(expStr.getBytes(), bOut.toByteArray());
    }

    @Test
    void testAdd() throws ValidationException {
        int oldSize = testCookie.size();
        testCookie.add("newName", "1");
        assertEquals(oldSize+1, testCookie.size());
        assertTrue(testCookie.contains("newName"));
    }

    @Test
    void testHashCode() {
    CookieList eqlCookie = testCookie;
        assertAll(("properties"),
            ()-> {
                assertEquals(testCookie.hashCode(), testCookie.hashCode());
                assertEquals(testCookie.hashCode(), eqlCookie.hashCode());
        });
    }

    @Test
    void testEquals() {
        CookieList eqlCookie = testCookie;
        CookieList eql2Cookie = eqlCookie;

        assertAll("porperties",
            () -> {
                if(testCookie != null) {
                    assertTrue(testCookie.equals(testCookie));

                    assertEquals(testCookie.equals
                            (eqlCookie), eqlCookie.equals(testCookie));

                    assertAll("transitive",
                        () -> {
                        assertTrue(testCookie.equals(eqlCookie));
                        assertTrue(eqlCookie.equals(eql2Cookie));
                        assertTrue(testCookie.equals(eql2Cookie));
                    });

                    for(int i = 0; i < 10000; i++) {
                            assertTrue(testCookie.equals(eqlCookie));
                    }

                    assertFalse(testCookie.equals(null));
                }
            });
    }

    @Test
    void testToString() {
        String shldBMessage = "?";
        assertEquals(shldBMessage, testCookie.toString());
    }

}