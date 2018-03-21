/*
 * N4M.serialization.test:testN4MResponse
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization.test;

import N4M.serialization.ApplicationEntry;
import N4M.serialization.N4MException;
import N4M.serialization.N4MResponse;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Test
    public void testSetTimeStep() throws N4MException {
        Date test = new Date();
        r.setTimeStamp(test);
        assertEquals(test, r.getTimeStamp());
    }

}
