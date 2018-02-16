/*
 * serialization:G8RResponse
 * Created on 1/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package G8R.serialization;

import java.io.IOException;
import java.util.Objects;

public class G8RResponse extends G8RMessage {

    private static final String errNullMessage = "null message";
    private static final String errMessage = "not an G8RResponse message";
    private static final String errStatus = "not an G8RResponse status";

    private static final String val_Type = "R";
    private static final String type_OK = "OK";
    private static final String type_ERROR = "ERROR";

    private static final String alphaNumSpLess = "[\\w\\d\\s()<>:]*";

    private String status;
    private String message;

    /**
     * creates an empty G8RResponse message
     */
    public G8RResponse() {
    }

    public G8RResponse(MessageInput in)
            throws IOException, ValidationException {
        if(in.isNull()) {
            throw new NullPointerException(errNullMessageIn);
        }

        String[] res = in.readUntil(delim_LineEnd).split(delim_Space);

        String message = "";
        for(int i = 0; i < res.length; i++) {
            switch(i) {
                case 0:
                    setStatus(res[i]);
                    break;
                case 1:
                    setFunction(res[i]);
                    break;
                default:
                    message += res[i] + " ";
                    break;
            }
        }
        if(message.isEmpty()) {
            setMessage(message);
        } else {
            setMessage(message.substring(0, message.length() - 1));
        }

        //reads in cookielist
        setCookieList(new CookieList(in));
    }

    /**
     * Constructs G8R response using given values
     * @param stat response status
     * @param funct response function
     * @param mess response message
     * @param cl response cookie list
     * @throws ValidationException if error with given values
     * @throws NullPointerException if null parameters
     */
    public G8RResponse(String stat, String funct, String mess, CookieList cl)
            throws ValidationException {
        setStatus(stat);
        setFunction(funct);
        setMessage(mess);
        setCookieList(cl);
    }

    /**
     * encodes a G8RResponse message
     * @param out serialization output sink
     * @throws IOException if write problem
     */
    public void encode(MessageOutput out) throws IOException {
        if(out.isNull()) {
            throw new NullPointerException(errNullMessageOut);
        }

        //writes message header
        super.encode(out);

        //writes out message type
        out.write(val_Type + delim_Space);

        //writes out message status
        out.write(getStatus() + delim_Space);

        //writes out message function
        if(!getMessage().isEmpty()) {
            out.write(getFunction() + delim_Space);

            //writes out messages' message
            out.write(getMessage() + delim_LineEnd);
        } else {
            out.write(getFunction() + delim_LineEnd);
        }

        //writes out message's cookieList
        getCookieList().encode(out);
    }


    /**
     * return message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * return status
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * set message
     * @param mess new message
     * @throws ValidationException if invalid message
     * @throws NullPointerException if null message
     */
    public void setMessage(String mess) throws ValidationException {
        if(!mess.matches(alphaNumSpLess)) {
            throw new ValidationException(errMessage, mess);
        }
        message = Objects.requireNonNull(mess, errNullMessage);
    }

    /**
     * set status
     * @param stat new status
     * @throws ValidationException if invalid status
     * @throws NullPointerException if null status
     */
    public void setStatus(String stat) throws ValidationException {
        if(!type_OK.equals(stat) && !type_ERROR.equals(stat)) {
            throw new ValidationException(errStatus, stat);
        }
        status = Objects.requireNonNull(stat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        G8RResponse that = (G8RResponse) o;
        return Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getFunction(), that.getFunction()) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getCookieList(), that.getCookieList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getFunction(),
                getMessage(), getCookieList());
    }

    /**
     * retruns human-readable string representation of G8R response message
     * @return string representation
     */
    public String toString() {
        return super.toString() + val_Type + "Status=" + getStatus() +
                "Function=" + getFunction() + "Message=" + getMessage() +
                getCookieList().toString();
    }
}
