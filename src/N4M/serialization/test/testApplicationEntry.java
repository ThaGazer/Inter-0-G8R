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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class testApplicationEntry {

    ApplicationEntry appEntry = new ApplicationEntry();

    @Test
    public void testSetAccessCount() throws N4MException {
        int testCount = 12341234;
        appEntry.setAccessCount(testCount);
        assertEquals(testCount, appEntry.getAccessCount());
    }

    @Test
    public void testSetApplicationName() throws N4MException {
        String testName = "testApp";
        appEntry.setApplicationName(testName);
        assertEquals(testName, appEntry.getApplicationName());
    }
}
