/*
 * G8R.app.FunctionState:Function
 *
 * Date Created: Feb/26/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

import G8R.serialization.G8RRequest;
import G8R.serialization.G8RResponse;
import G8R.serialization.ValidationException;

public interface G8RFunction {
    G8RResponse next(G8RRequest request) throws ValidationException;
}
