/*
 * N4M.serialization.test:testN4MException
 *
 * Date Created: Mar/15/2018
 * Author:
 *   -Justin Ritter
 */
package N4M.serialization.test;

import N4M.serialization.ErrorCodeType;
import N4M.serialization.N4MException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class testN4MException {

    @Test
    public void testConstructor() {
        String msg = "a rather simple message";
        N4MException e = new N4MException(msg, ErrorCodeType.NOERROR);
        assertEquals(msg, e.getMessage());
        assertEquals(ErrorCodeType.NOERROR, e.getErrorCodeType());
    }
}
