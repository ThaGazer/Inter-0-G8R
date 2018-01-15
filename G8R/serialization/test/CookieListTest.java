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

    private List<String> expNames = new ArrayList<>(Arrays.asList("Bob", "Ted"));
    private List<String> expValues = new ArrayList<>(Arrays.asList("1", "2"));
    private byte[] expBytes = {0x00};

    private CookieList testCookie;

    CookieListTest() throws IOException, ValidationException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(expBytes);
        MessageInput mIn = new MessageInput(bIn);
        testCookie = new CookieList(mIn);
    }

    @Test
    void testGetNames() {
        assertTrue(testCookie.getNames() == expNames);
    }

    @Test
    void testGetValue() {
        List<String> names = new ArrayList<>();
        names.addAll(testCookie.getNames());

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
        assertEquals(expBytes, bOut.toByteArray());
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
    }

    @Test
    void testEquals() {
    }

    @Test
    void testToString() {
    }

}