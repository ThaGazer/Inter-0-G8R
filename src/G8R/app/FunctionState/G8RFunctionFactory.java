/*
 * G8R.app.FunctionState:G8RFunctionFactory
 *
 * Date Created: Feb/26/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

public class G8RFunctionFactory {
    public static Enum<? extends G8RFunction> getByName(String name) {
        if(G8RPoll.POLL.getName().equals(name)) {
            return G8RPoll.POLL;
        }
        else if(G8RMathTeacher.MATH.getName().equals(name)) {
            return G8RMathTeacher.MATH;
        } else {
            return null;
        }
    }
}
