/*
 * serialization.test:CookieListTest
 *
 * Date Created: Jan/13/2018
 * Author:
 *   -Justin Ritter
 *   -Clint Masters
 */
package G8R.serialization.test;

import static org.junit.jupiter.api.Assertions.*;

import G8R.serialization.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        Collections.sort(names);

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

    @DisplayName("invalid")
    @ParameterizedTest
    @ValueSource(strings = {"a=1\r\nb=\r\n\r\n", "a==1\r\n\r\n", "a1\r\n"})
    void testEncodeInvalid(String str) {
        assertThrows(ValidationException.class, ()->new CookieList(
                new MessageInput(new ByteArrayInputStream(str.getBytes()))));
    }

    @ParameterizedTest
    @CsvSource({"a, 1", "sdasffhjklkjhgf, 123456432"})
    void testAdd(String name, String value) throws ValidationException {
        int oldSize = testCookie.size();
        testCookie.add(name, value);
        assertAll("properties", ()-> {
            assertTrue(oldSize != testCookie.size());
            assertEquals(value, testCookie.getValue(name));
        });
    }

    @ParameterizedTest
    @CsvSource({"a, 1", "b, 1234254365"})
    void testRemove(String name, String value) throws ValidationException {
        testCookie.add(name, value);
        assertTrue(testCookie.remove(name));
    }

    @ParameterizedTest
    @CsvSource({"a, 1", "b, 2", "dsafsfg, 123"})
    void testAddVal(String name, String value) throws ValidationException {
        String newVal = "newVal";
        testCookie.add(name, value);
        testCookie.add(name, newVal);
        assertEquals(newVal, testCookie.getValue(name));
    }

    @ParameterizedTest
    @CsvSource({"n allwed, 1", "a, n allwed", "a, a bc", "a bc, 1",
            "a, a-c", "a-c, 1", "a, ' '"})
    void testAddInvalid(String name, String value) {
        assertThrows(ValidationException.class, ()->
                testCookie.add(name, value));
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
    public void TestHash() throws ValidationException {
        CookieList c1 = new CookieList();
        c1.add("x", "1");
        c1.add("y", "g46");

        CookieList c2 = new CookieList(c1);
        c2.add("y", "g45");
        c1.hashCode();
        c2.hashCode();
    }
    @Test
    void testToString() {
        assertEquals(expStr, testCookie.toString());
    }
}

