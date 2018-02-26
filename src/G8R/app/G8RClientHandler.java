/*
 * G8R.app:G8RClientHandler
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.app.FunctionState.G8RPoll;
import G8R.serialization.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static G8R.app.FunctionState.G8RPoll.*;

public class G8RClientHandler implements Runnable {

    //error message to client
    private static final String errFunction = "Unexpected function";
    private static final String errName = "Poorly formed name. ";
    private static final String errMood = "Poorly formed food mood. ";

    //messages to client
    private static final String msgConnection = "connected to: ";
    private static final String msgCloseConnect = "closed connection to: ";
    private static final String msgSendMessage = "sending to client";
    private static final String msgRecivMessage = "receive from client";
    private static final String msgNameStep = "Name (First Last)> ";
    private static final String msgFoodMood = "'s food mood> ";
    private static final String msgBaseDiscount = "10% + ";
    private static final String msgStoreDiscount = "% off at McDonalds";

    //cookie names
    private static final String cookie_fname = "FName";
    private static final String cookie_lname = "LName";
    private static final String cookie_repeat = "Repeat";

    //class variables
    private Socket client;
    private CookieList clientCookies;
    private G8RRequest clientRequest;
    private Logger logger = Logger.getLogger(G8RServer.class.getName());

    /**
     * creates a new clientHandler runnable
     * @param socket the client connection
     */
    public G8RClientHandler(Socket socket) {
        client = Objects.requireNonNull(socket);
    }

    @Override
    public void run() {
        try {
            logger.info(msgConnection + client.getLocalSocketAddress());

            MessageInput in = new MessageInput(client.getInputStream());
            MessageOutput out = new MessageOutput(client.getOutputStream());

            G8RResponse res;
            do {
                res = handleRequest(G8RMessage.decode(in));

                res.encode(out);
                logger.log(Level.INFO, msgSendMessage, res);
            } while(G8RPoll.getByName(res.getFunction()) != NULL);

            client.close();
            logger.info(msgCloseConnect + client.getLocalSocketAddress());
        } catch(ValidationException ve) {
            logger.warning(ve.getReason());
        } catch(IOException e) {
            logger.warning(e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * handler of the state received from client
     * @param clientMess the message from the client
     * @return the response to the client
     * @throws ValidationException if message validation error
     */
    private G8RResponse handleRequest(G8RMessage clientMess)
            throws ValidationException {
        logger.log(Level.INFO, msgRecivMessage, clientMess);
        clientCookies = clientMess.getCookieList();
        clientRequest = (G8RRequest)clientMess;
        G8RPoll state = G8RPoll.getByName(clientMess.getFunction());

        if(state == POLL) {
            return state_Poll(state);
        } else if(state == NAMESTEP) {
            return state_NameStep(state);
        } else if(state == FOODMOOD) {
            return state_FoodMood(state);
        } else {
            return buildErrResponse(NULL, errFunction);
        }
    }

    /**
     * handles the poll state
     * @param state used to access the next state
     * @return a response message to client
     * @throws ValidationException if response validation error
     */
    private G8RResponse state_Poll(G8RPoll state) throws ValidationException {
        state = state.next(clientCookies);

        String message;

        if(state == NAMESTEP) {
            message = msgNameStep;
        } else {
            message = buildFoodMood();
        }

        return buildOkResponse(state, message);
    }

    /**
     * handles the name step state
     * @param state used to access the next state
     * @return a response message to client
     * @throws ValidationException if response validation error
     */
    private G8RResponse state_NameStep(G8RPoll state)
            throws ValidationException {
        if(clientRequest.getParams().length != 2) {
            return buildErrResponse(state, errName + msgNameStep);
        }
        clientCookies.add(cookie_fname, clientRequest.getParams()[0]);
        clientCookies.add(cookie_lname, clientRequest.getParams()[1]);

        state = state.next(clientCookies);
        return buildOkResponse(state, buildFoodMood());
    }

    /**
     * handles the food mood state
     * @param state used to access the next state
     * @return a response message to client
     * @throws ValidationException if response validation error
     */
    private G8RResponse state_FoodMood(G8RPoll state)
            throws ValidationException {
        if(clientRequest.getParams().length != 1) {
            return buildErrResponse(state, errMood + buildFoodMood());
        }

        if(clientCookies.getValue(cookie_repeat) == null) {
            clientCookies.add(cookie_repeat, "0");
        }

        clientCookies.add(cookie_repeat, addtoCookie(cookie_repeat));

        state = state.next(clientCookies);
        return buildOkResponse(state, buildDiscount());
    }

    private String buildFoodMood() {
        return clientCookies.getValue(cookie_fname) + msgFoodMood;
    }

    private String buildDiscount() {
        return msgBaseDiscount +
                clientCookies.getValue(cookie_repeat) + msgStoreDiscount;
    }

    /**
     * builds a ok response message using the status and message passed in
     * @param status status of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildOkResponse(G8RPoll status, String message)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_OK, status.getName(), message,
                clientCookies);
    }

    /**
     * builds an error response message using the status and message passed in
     * @param status status of the response
     * @param message message to be sent to client
     * @return the built response
     * @throws ValidationException if response validation error
     */
    private G8RResponse buildErrResponse(G8RPoll status, String message)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_ERROR, status.getName(),
                message, clientCookies);
    }

    /**
     * adds one to the repeat cookie
     * @return string representation of an int
     */
    private String addtoCookie(String name) {
        return String.valueOf(
                Integer.parseInt(clientCookies.getValue(name)) + 1);
    }
}
