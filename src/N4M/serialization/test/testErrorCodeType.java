/*
 * N4M.serialization.test:testErrorCodeType
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

public class testErrorCodeType {

    @Test
    public void testValueOf() throws N4MException {
        for(ErrorCodeType ect : ErrorCodeType.values()) {
            assertEquals(ect, ErrorCodeType.valueOf(ect.getErrorCodeNum()));
        }
    }
}
