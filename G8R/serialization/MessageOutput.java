/*
 * serialization:MessageOutput
 * 
 * Date Created: Jan/11/2018
 * Author:
 *   -Justin Ritter
 */
package serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Serialization output source for message
 */
public class MessageOutput {

    private static final String errNullStream = "null output stream";

    private OutputStream outBuff;

    /**
     * Constructs a new output source from an OutputStream
     * @param out byte output sink
     * @throws NullPointerException if out is null
     */
    public MessageOutput(OutputStream out) {
        if(out == null) {
            throw new NullPointerException(errNullStream);
        }
        outBuff = out;
    }

    /**
     * returns true or false whether or not the stream has been initialized
     * @return true if the stream is null
     */
    public boolean isNull() {
        return outBuff == null;
    }

    /**
     * Writes a single byte out through the outputStream
     * @param b byte to write
     * @throws IOException if I/O problems
     */
    public void write(byte b) throws IOException {
        outBuff.write(b);
    }

    /**
     * Writes an array of bytes out through the outputStream
     * @param bytes byte array to write
     * @throws IOException if I/O problems
     */
    public void write(byte[] bytes) throws IOException {
        outBuff.write(bytes);
    }

    /**
     * Writes a String out through the outputStream
     * using the StandardCharset US_ASCII
     * @param str string to write
     * @throws IOException if I/O problem
     */
    public void write(String str) throws IOException {
        outBuff.write(str.getBytes(StandardCharsets.US_ASCII));
    }
}
