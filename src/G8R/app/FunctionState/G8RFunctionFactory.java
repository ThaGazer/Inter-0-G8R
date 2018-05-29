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
        for(Enum<? extends G8RFunction> e : getFirst()) {
            for(Enum<? extends G8RFunction> funct = e; funct != null;
                    funct = ((G8RFunction)funct).nextFunct()) {
                if(((G8RFunction)funct).getName().equals(name)) {
                    return funct;
                }
            }
        }
        return null;
    }

    public static Enum<? extends G8RFunction> getByFirst(String name) {
        for(Enum<? extends G8RFunction> e : getFirst()) {
            if(((G8RFunction)e).getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static List<Enum<? extends G8RFunction>> getFirst() {
        List<Enum<? extends G8RFunction>> list = new ArrayList<>();
        list.add(G8RPoll.first());
        list.add(G8RCalculator.first());
        return list;
    }

    public static List<ApplicationEntry> values() {
        ArrayList<ApplicationEntry> list = new ArrayList<>();
        try {
            list.add(new ApplicationEntry(G8RPoll.first().getName()));
            list.add(new ApplicationEntry(G8RCalculator.first().getName()));
        } catch (N4MException e) {
            e.printStackTrace();
        }
        return list;
    }
}
