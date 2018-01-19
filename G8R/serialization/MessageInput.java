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

    private static final String errClosedEarly = "connection closed early";

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
        int a;
        if((a = buffer.read()) != -1) {
            if(a == '\n') {
                a = buffer.read();
            }
            return a;
        } else {
            throw new IOException(errClosedEarly);
        }
    }

    public String readUntil() throws IOException {
        int a;
        String line = "";
        while((a = read()) != '\n') {
            line += String.valueOf(a);
        }
        if(line.isEmpty()) {
            line = "\n";
        }
        return line;
    }
}
