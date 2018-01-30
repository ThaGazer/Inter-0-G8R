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

    protected static final String errNullMessageOut =
            "Null MessageOutput object";
    protected static final String errNullMessageIn = "Null MessageInput object";
    protected static final String errHeader = "Not a G8RMessage header";
    protected static final String errType = "Not a G8RMessage type";

    protected static final String val_G8Rheader = "G8R/1.0";
    protected static final String lineEnd = "\r\n";
    protected static final String delim_Space = " ";
    protected static final String delim_LineEnd = "\n";

    /**
     * Creates a new G8R message by deserialization from the given
     * input according to the specified serialization.
     * @param in user input source
     * @return new G8R message
     * @throws ValidationException if validation fails
     * @throws IOException if I/O problems
     * @throws NullPointerException if in is null
     */
    public static G8RMessage decode(MessageInput in)
            throws ValidationException, IOException {
        if(in.isNull()) {
            throw new NullPointerException(errNullMessageIn);
        }

        String header;
        if(!val_G8Rheader.equals(header = in.readUntil(delim_Space))) {
            throw new ValidationException(errHeader, header);
        }

        String type = in.readUntil(delim_Space).toLowerCase();
        switch(type) {
            case "q":
                return new G8RRequest(in);
            case "r":
                return new G8RResponse(in);
            default:
                throw new ValidationException(errType, type);
        }
    }

    /**
     * encodes the entire G8R message
     * @param out serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    public void encode(MessageOutput out) throws IOException {
        if(out == null) {
            throw new NullPointerException(errNullMessageOut);
        }
        out.write(val_G8Rheader + delim_Space);
    }

    public String toString() {
        return val_G8Rheader + "=";
    }
}
