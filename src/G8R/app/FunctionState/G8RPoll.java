/*
 * G8R.app:G8RPollState
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

public enum G8RPoll {
    POLL("Poll"), NAMESTEP("NameStep"), FOODMOOD("FoodMood");

    private String name;

    G8RPoll(String str) {
        name = str;
    }

    public String getName() {
        return name;
    }

    public static G8RPoll getByName(String function) {
        if(G8RPoll.POLL.getName().equals(function)) {
            return POLL;
        } else if(G8RPoll.NAMESTEP.getName().equals(function)) {
            return NAMESTEP;
        } else if(G8RPoll.FOODMOOD.getName().equals(function)) {
            return FOODMOOD;
        } else {
            return null;
        }
    }
}
