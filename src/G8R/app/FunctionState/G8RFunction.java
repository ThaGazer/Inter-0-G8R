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
    default Enum<?> last() {
        return null;
    }

    default String getName() {
        return "";
    }

    Enum<?> next(G8RMessage request, MessageOutput out)
            throws ValidationException, IOException;

    Enum<? extends G8RFunction> nextFunct();
}
