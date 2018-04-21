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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class testN4MQuery {

    private N4MQuery q = new N4MQuery();

    @ParameterizedTest
    @CsvSource({"test", "''", "' '", "test 1", "test1",
            "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    })
    public void testSetBusinessName(String testName) throws N4MException {
        q.setBusinessName(testName);
        assertEquals(testName, q.getBusinessName());
    }

    @Test
    public void testErrorCodeNum() throws N4MException {
        ErrorCodeType testCode = ErrorCodeType.NOERROR;
        q.setErrorCode(testCode);
        assertEquals(testCode.getErrorCodeNum(),
                q.getErrorCode().getErrorCodeNum());
    }
}
