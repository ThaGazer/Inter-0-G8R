/*
 * G8R.app.FunctionState:G8RFunctionFactory
 *
 * Date Created: Feb/26/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

import N4M.serialization.ApplicationEntry;
import N4M.serialization.N4MException;

import java.util.ArrayList;
import java.util.List;

public class G8RFunctionFactory {
    public static Enum<? extends G8RFunction> getByName(String name) {
        if(G8RPoll.POLL.getName().equals(name)) {
            return G8RPoll.POLL;
        }
        else if(G8RCalculator.MATH.getName().equals(name)) {
            return G8RCalculator.MATH;
        } else {
            return null;
        }
    }

    public static List<ApplicationEntry> values() throws N4MException {
        ArrayList<ApplicationEntry> list = new ArrayList<>();
        list.add(new ApplicationEntry(G8RPoll.first().getName()));
        list.add(new ApplicationEntry(G8RCalculator.first().getName()));
        return list;
    }
}
