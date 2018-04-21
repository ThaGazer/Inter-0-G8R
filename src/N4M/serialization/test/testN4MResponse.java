/*
 * N4M.serialization.test:testN4MResponse
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization.test;

import N4M.serialization.ApplicationEntry;
import N4M.serialization.ErrorCodeType;
import N4M.serialization.N4MException;
import N4M.serialization.N4MResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class testN4MResponse {

    N4MResponse r = new N4MResponse();

    @Test
    public void testSetApplications() throws N4MException {
        List<ApplicationEntry> test = new ArrayList<>();
        test.add(new ApplicationEntry("test"));
        r.setApplications(test);
        assertEquals(test, r.getApplications());
    }

    @ParameterizedTest
    @MethodSource("getTime")
    public void testSetTimeStep(long time) throws N4MException {
        r.setTimestamp(time);
        assertEquals(time, r.getTimestamp());
    }

    @Test
    @DisplayName("Equal hashes")
    public void testequalHash() throws N4MException {
        N4MResponse res = new N4MResponse
                (ErrorCodeType.NOERROR, 0, 1, new ArrayList<>());
        N4MResponse res1 = res;
        N4MResponse res2 = new N4MResponse
                (ErrorCodeType.NOERROR, 0, 1, new ArrayList<>());

        assertAll("Equal hashCodes", ()-> {
            assertEquals(res.hashCode(), res1.hashCode());
            assertEquals(res1.hashCode(), res2.hashCode());
            assertEquals(res.hashCode(), res2.hashCode());
        });
    }

    @Test
    @DisplayName("Unequal hashes")
    public void testUnequalHash() throws N4MException {
        ArrayList<N4MResponse> arr = new ArrayList<>();
        ArrayList<ApplicationEntry> apps = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            arr.add(new N4MResponse(ErrorCodeType.valueOf(i), 0, 0 ,
                    new ArrayList<>()));
            arr.add(new N4MResponse(ErrorCodeType.NOERROR, i+1, 0,
                    new ArrayList<>()));
            arr.add(new N4MResponse(ErrorCodeType.NOERROR, 0, i+1,
                    new ArrayList<>()));

            apps.add(new ApplicationEntry(Integer.toString(i), i));
            arr.add(new N4MResponse(ErrorCodeType.NOERROR, 0, 0, apps));
        }

        for(N4MResponse r : arr) {
            for(N4MResponse res : arr) {
                if(r != res) {
                    assertNotEquals(r.hashCode(), res.hashCode());
                }
            }
        }
    }

    @Test
    @DisplayName("equals properties")
    public void testEqualObjects() throws N4MException {
        N4MResponse testRes = new N4MResponse(ErrorCodeType.NOERROR, 0, 0,
                new ArrayList<>());
        N4MResponse res = testRes;
        N4MResponse res1 = res;

        assertAll("porperties",
                () -> {
                    if(testRes != null) {
                        assertTrue(testRes.equals(testRes));

                        assertEquals(testRes.equals
                                (res), res.equals(testRes));

                        assertAll("transitive",
                                () -> {
                                    assertTrue(testRes.equals(res));
                                    assertTrue(res.equals(res1));
                                    assertTrue(testRes.equals(res1));
                                });

                        for(int i = 0; i < 10000; i++) {
                            assertTrue(testRes.equals(res));
                        }

                        assertFalse(testRes.equals(null));
                    }
                });
    }

    public static Stream<Long> getTime() {
        return Stream.of(0L, (long)Integer.MAX_VALUE, (long)Integer.MAX_VALUE,
                4294967295L);
    }
}
