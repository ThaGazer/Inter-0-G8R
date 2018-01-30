/*
 * serialization.test:CookieListTest
 *
 * Date Created: Jan/13/2018
 * Author:
 *   -Justin Ritter
 *   -Clint Masters
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

/**
 * test the non-constructor functions in CookieList
 */
@DisplayName("CookieList_method test")
@Nested
public class CookieListTest {

    private List<String> expNames =
            new ArrayList<>(Arrays.asList("Bob", "Ted"));
    private List<String> expValues =
            new ArrayList<>(Arrays.asList("1", "2"));

    private CookieList testCookie;
    private String expByte = "";
    private String expStr = "Cookies=[";

    CookieListTest() throws IOException, ValidationException {
        boolean firstAdd = true;
        for(int i = 0; i < expNames.size(); i++) {
            expByte += (expNames.get(i) + "=" + expValues.get(i) + "\r\n");
            if(!firstAdd) {
                expStr += ",";
            }
            expStr += (expNames.get(i) + "=" + expValues.get(i));
            firstAdd = false;
        }
        expStr += "]";
        expByte += "\r\n";

        ByteArrayInputStream bIn = new ByteArrayInputStream(expByte.getBytes());
        MessageInput mIn = new MessageInput(bIn);
        testCookie = new CookieList(mIn);
    }

    @Test
    void testGetNames() {
        Set<String> expSetNames = new HashSet<>(expNames);
        assertEquals(expSetNames, testCookie.getNames());
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
        assertArrayEquals(expByte.getBytes(), bOut.toByteArray());
    }

    @Test
    void testAdd() throws ValidationException {
        int oldSize = testCookie.size();
        testCookie.add("newName", "1");
        assertEquals(oldSize+1, testCookie.size());
        assertTrue(testCookie.getValue("newName") != null);
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
        assertEquals(expStr, testCookie.toString());
    }
}

