/*
 * N4M.serialization.test:testN4MMessage
 *
 * Date Created: Mar/14/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization.test;

import N4M.serialization.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class testN4MQuery {

    private N4MQuery q = new N4MQuery();

    @Test
    public void testSetBusinessName() throws N4MException {
        String testName = "test";
        q.setBusinessName(testName);
        assertEquals(testName, q.getBusinessName());
    }

    @Test
    public void testErrorCodeNum() throws N4MException {
        int testCode = 0;
        q.setErrorCodeNum(testCode);
        assertEquals(testCode, q.getErrorCodeNum());
    }
}
