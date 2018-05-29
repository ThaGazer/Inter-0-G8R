/*
 * G8R.app.FunctionState:Function
 *
 * Date Created: Feb/26/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

import G8R.serialization.G8RMessage;
import G8R.serialization.MessageOutput;
import G8R.serialization.ValidationException;

import java.io.IOException;

public interface G8RFunction {
    /**
     * last enum of a G8RFunction
     */
    default Enum<?> last() {
        return null;
    }

    /**
     * gets the name of the enum
     * @return the name
     */
    default String getName() {
        return "";
    }

    /**
     * determin and operates based off of which state it is in and the cookies
     * @param request client request
     * @param out output sink
     * @return the next enum in the sequence
     * @throws ValidationException if G8R error
     * @throws IOException if I/O problem
     */
    Enum<?> next(G8RMessage request, MessageOutput out)
            throws ValidationException, IOException;

    /**
     * forms a linked list of all the functions apart of the enum
     * @return the next enum
     */
    Enum<? extends G8RFunction> nextFunct();
}
