/*
 * serialization:G8RResponse
 * Created on 1/24/2018
 *
 * Author(s):
 * -Justin Ritter
 */
package serialization;

public class G8RResponse extends G8RMessage {

    private String status;
    private String function;
    private String message;
    private CookieList cookieList;

    public G8RResponse() {

    }

    public G8RResponse(String stat, String funct, String mess, CookieList cl) {

    }

    public CookieList getCookieList() {
        return cookieList;
    }

    public String getFunction() {
        return function;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public void setCookieList(CookieList cl) {
        cookieList = cl;
    }

    public void setFunction(String funct) {
        function = funct;
    }

    public void setMessage(String mess) {
        message = mess;
    }

    public void setStatus(String stat) {
        status = stat;
    }

    public String toString() {
        return "";
    }
}
