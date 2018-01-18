/*
 * serialization.test:CookieListTestConstruct
 * 
 * Date Created: Jan/18/2018
 * Author:
 *   -Justin Ritter
 */
package serialization.test;

import org.junit.jupiter.api.Test;
import serialization.CookieList;
import serialization.MessageInput;
import serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CookieListTestConstruct {

    private static byte[] epxByte = {0x00,0x01};

    @Test
    public void testConstMessageInput() throws IOException, ValidationException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(epxByte);

        MessageInput mIn = new MessageInput(bIn);

        CookieList cookie = new CookieList(mIn);
    }
}
