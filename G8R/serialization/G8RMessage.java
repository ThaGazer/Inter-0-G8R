/*
 * serialization:G8RMessage
 * Created on 1/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package serialization;

import java.io.IOException;

/**
 * Represents generic portion of a G8R message and provides
 * serialization/deserialization.
 */
public class G8RMessage {

    private static final String errNullMessageOut = "Null MessageOutput object";

    public G8RMessage() {
    }

    /**
     * Creates a new G8R message by deserializing from the given
     * input according to the specified serialization.
     * @param in user input source
     * @return new G8R message
     * @throws ValidationException if validation fails
     * @throws IOException if I/O problems
     * @throws NullPointerException if in is null
     */
    public static G8RMessage decode(MessageInput in)
            throws ValidationException, IOException {
        return new G8RMessage();
    }

    /**
     * Encode the entire G8R message
     * @param out serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    public void encode(MessageOutput out) throws IOException {
        if(out == null) {
            throw new NullPointerException(errNullMessageOut);
        }
    }
}
