/*
 * serialization:MessageInput
 * 
 * Date Created: Jan/11/2018
 * Author:
 *   -Justin Ritter
 */
package serialization;

import java.io.IOException;
import java.io.InputStream;

/**
 * Deserialization input source for messages
 */
public class MessageInput {

    private InputStream buffer;
    /**
     * Constructs a new input source from an InputStream
     * @param in byte input source
     * @throws NullPointerException if in is null
     */
    public MessageInput(InputStream in) {
        buffer = in;
    }

    public int read() throws IOException {
        return buffer.read();
    }
}
