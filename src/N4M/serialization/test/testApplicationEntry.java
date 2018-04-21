/*
 * N4M.serialization.test:testApplicationEntry
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization.test;

import N4M.serialization.ApplicationEntry;
import N4M.serialization.N4MException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@Nested
public class testApplicationEntry {

    ApplicationEntry appEntry = new ApplicationEntry();

    @Test
    public void testSetAccessCount() throws N4MException {
        int testCount = 1;
        appEntry.setAccessCount(testCount);
        assertEquals(testCount, appEntry.getAccessCount());
    }

    @ParameterizedTest
    @CsvSource({"testApp", "xyz", "' '", "''", "xyz123"})
    public void testSetApplicationName(String name) throws N4MException {
        appEntry.setApplicationName(name);
        assertEquals(name, appEntry.getApplicationName());
    }
}
