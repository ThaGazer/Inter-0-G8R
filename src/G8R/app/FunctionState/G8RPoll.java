/*
 * G8R.app:G8RPollState
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

import G8R.serialization.CookieList;

import java.util.Set;

public enum G8RPoll {
    POLL("Poll") {
        @Override
        public G8RPoll next(CookieList cl) {
            Set<String> cookieNames = cl.getNames();
            if(cookieNames.contains(fname) && cookieNames.contains(lname)) {
                return FOODMOOD;
            } else {
                return NAMESTEP;
            }
        }
    }, NAMESTEP("NameStep") {
        @Override
        public G8RPoll next(CookieList cl) {
            return FOODMOOD;
        }
    }, FOODMOOD("FoodMood") {
        @Override
        public G8RPoll next(CookieList cl) {
            return NULL;
        }
    }, NULL("NULL") {
        @Override
        public G8RPoll next(CookieList cl) {
            return NULL;
        }
    };

    private static final String fname = "FName";
    private static final String lname = "LName";

    private String name;

    G8RPoll(String str) {
        name = str;
    }

    public String getName() {
        return name;
    }

    public abstract G8RPoll next(CookieList cl);

    public static G8RPoll getByName(String function) {
        if(POLL.getName().equals(function)) {
            return POLL;
        } else if(NAMESTEP.getName().equals(function)) {
            return NAMESTEP;
        } else if(FOODMOOD.getName().equals(function)) {
            return FOODMOOD;
        } else {
            return NULL;
        }
    }
}
