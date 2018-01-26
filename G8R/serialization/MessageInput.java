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
    private static final String errNullStream = "null input stream";


    private InputStream inBuff;

    /**
     * Constructs a new input source from an InputStream
     * @param in byte input source
     * @throws NullPointerException if in is null
     */
    public MessageInput(InputStream in) {
        if(in == null) {
            throw new NullPointerException(errNullStream);
        }
        inBuff = in;
    }

    /**
     * returns true or false whether or not the stream has been initialized
     * @return true if the stream is null
     */
    public boolean isNull() {
        return inBuff == null;
    }

    /**
     * reads a single byte from the stream. If a '\r' is read then it will read
     * the next character automatically
     * @return the int representation of that byte
     * @throws IOException if I/O problem
     */
    public int read() throws IOException {
        int a;
        if((a = inBuff.read()) != -1) {
            if(a == '\r') {
                a = inBuff.read();
            }
            return a;
        } else {
            throw new IOException(errClosedEarly);
        }
    }

    /**
     * reads all bytes in the stream until it finds a '\r\n'. Returns an empty
     * string if nothing was read or found an ending before other characters
     * @return the string representation of the bytes read
     * @throws IOException if I/O problems
     */
    public String readUntil() throws IOException {
        int a;
        String line = "";
        while((a = read()) != '\n') {
            line += (char)a;
        }
        return line;
    }
}
