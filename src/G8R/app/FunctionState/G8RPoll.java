/*
 * G8R.app:G8RPollState
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.FunctionState;

import G8R.serialization.*;
import java.io.IOException;
import java.util.Set;

public enum G8RPoll implements G8RFunction {
    POLL("Poll") {
        @Override
        public G8RPoll next(G8RMessage request, MessageOutput out)
                throws ValidationException, IOException {
            return state_Poll(request, out);
        }

        @Override
        public G8RPoll nextFunct() {
            return NAMESTEP;
        }
    }, NAMESTEP("NameStep") {
        @Override
        public G8RPoll next(G8RMessage request, MessageOutput out)
                throws ValidationException, IOException {
            return state_NameStep(request, out);
        }

        @Override
        public G8RPoll nextFunct() {
            return FOODMOOD;
        }
    }, FOODMOOD("FoodMood") {
        @Override
        public G8RPoll next(G8RMessage request, MessageOutput out)
                throws ValidationException, IOException {
            return state_FoodMood(request, out);
        }

        @Override
        public G8RPoll nextFunct() {
            return NULL;
        }
    }, NULL("NULL") {
        @Override
        public G8RPoll next(G8RMessage request, MessageOutput out) {
            return NULL;
        }

        @Override
        public G8RPoll nextFunct() {
            return null;
        }
    };

    private static final String errName = "Poorly formed name. ";
    private static final String errMood = "Poorly formed food mood. ";

    private static final String msgNameStep = "Name (First Last)> ";
    private static final String msgFoodMood = "'s food mood> ";
    private static final String msgBaseDiscount = "10% + ";
    private static final String msgStoreDiscount = "% off at McDonalds";

    private static final String cookie_fName = "FName";
    private static final String cookie_lName = "LName";
    private static final String cookie_repeat = "Repeat";

    private String name;

    /**
     * constructs a new G8R poll
     * @param str name of enum
     */
    G8RPoll(String str) {
        name = str;
    }

    public static  G8RPoll first() {
        return POLL;
    }

    public G8RPoll last() {
        return NULL;
    }

    public String getName() {
        return name;
    }

    /**
     * handles the poll state
     * @param request a request message
     * @param out output sink
     * @return a response message to client
     * @throws ValidationException if response validation error
     * @throws IOException if I/O problem
     */
    protected G8RPoll state_Poll(G8RMessage request, MessageOutput out)
            throws ValidationException, IOException {
        G8RPoll state;
        String messageToClient;

        Set<String> cookieNames = request.getCookieList().getNames();
        if(cookieNames.contains(cookie_fName) &&
                cookieNames.contains(cookie_fName)) {
            state = FOODMOOD;
            messageToClient = buildFoodMood(request.getCookieList());
        } else {
            state = NAMESTEP;
            messageToClient = msgNameStep;
        }

        request = buildOkResponse(state.getName(), messageToClient,
                request.getCookieList());
        request.encode(out);
        return state;
    }

    /**
     * handles the name step state
     * @param message a request message
     * @param out output sink
     * @return a response message to client
     * @throws ValidationException if response validation error
     */
    protected G8RPoll state_NameStep(G8RMessage message, MessageOutput out)
            throws ValidationException, IOException {
        G8RRequest request = (G8RRequest)message;

        if(request.getParams().length != 2) {
            message = buildErrResponse(NAMESTEP.getName(),
                    errName + msgNameStep,
                    request.getCookieList());
            message.encode(out);
            return NAMESTEP;
        }
        request.getCookieList().add(cookie_fName, request.getParams()[0]);
        request.getCookieList().add(cookie_lName, request.getParams()[1]);

        message = buildOkResponse(FOODMOOD.getName(),
                buildFoodMood(request.getCookieList()),
                request.getCookieList());
        message.encode(out);
        return FOODMOOD;
    }

    /**
     * handles the food mood state
     * @param message a request message
     * @param out output sink
     * @return a response message to client
     * @throws ValidationException if response validation error
     * @throws IOException if I/O problem
     */
    protected G8RPoll state_FoodMood(G8RMessage message, MessageOutput out)
            throws ValidationException, IOException {
        G8RRequest request = (G8RRequest)message;

        if(request.getParams().length != 1) {
            message = buildErrResponse(FOODMOOD.getName(),
                    errMood + buildFoodMood(request.getCookieList()),
                    request.getCookieList());
            message.encode(out);
            return FOODMOOD;
        }

        if(request.getCookieList().getValue(cookie_repeat) == null) {
            request.getCookieList().add(cookie_repeat, "0");
        }

        request.getCookieList().add(cookie_repeat, addToCookie(cookie_repeat,
                request.getCookieList()));

        message = buildOkResponse(NULL.getName(),
                buildDiscount(request.getCookieList()),
                request.getCookieList());
        message.encode(out);
        return NULL;
    }

    /**
     * a string builder function
     * @param cookies the cookie list used to form the string
     * @return food mood string
     */
    private String buildFoodMood(CookieList cookies) {
        return cookies.getValue(cookie_fName) + msgFoodMood;
    }

    /**
     * a string building function
     * @param cookies the cookie list used to form the string
     * @return discount string
     */
    private String buildDiscount(CookieList cookies) {
        return msgBaseDiscount +
                cookies.getValue(cookie_repeat) + msgStoreDiscount;
    }

    /**
     * builds a ok response message using the status and message passed in
     * @param state state of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildOkResponse
    (String state, String message, CookieList cookies)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_OK, state, message,
                cookies);
    }

    /**
     * builds an error response message using the status and message passed in
     * @param state state of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildErrResponse
    (String state, String message, CookieList cookies)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_ERROR, state,
                message, cookies);
    }

    /**
     * adds one to the repeat cookie
     * @return string representation of an int
     */
    private String addToCookie(String name, CookieList cookies) {
        return String.valueOf(
                Integer.parseInt(cookies.getValue(name)) + 1);
    }

}
