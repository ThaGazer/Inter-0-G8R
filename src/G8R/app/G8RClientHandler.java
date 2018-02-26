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
import java.util.logging.Logger;

import static G8R.app.FunctionState.G8RPoll.*;

public class G8RClientHandler implements Runnable {

    private static final String errFunction = "Unexpected function";
    private static final String errName = "Poorly formed name.";
    private static final String errMood = "Poorly formed food mood.";

    private static final String msgConnection = "connected to: ";
    private static final String msgNameStep = "Name (First Last)>";
    private static final String msgFoodMood = "'s food mood>";
    private static final String msgBaseDiscount = "10% + ";
    private static final String msgStoreDiscount = "% off at McDonalds";

    //cookie names
    private static final String cookie_fname = "FName";
    private static final String cookie_lname = "LName";
    private static final String cookie_repeat = "Repeat";

    private Socket client;
    private CookieList clientCookies;
    private Logger logger = Logger.getLogger(G8RServer.class.getName());

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
            res = handleRequest((G8RRequest)G8RMessage.decode(in));

            res.encode(out);
        } catch(ValidationException ve) {
            logger.warning(ve.getReason());
        } catch(IOException e) {
            logger.warning(e.getMessage());
        }
    }

    private G8RResponse handleRequest(G8RRequest clientReq)
            throws ValidationException {
        clientCookies = clientReq.getCookieList();
        G8RPoll state = G8RPoll.getByName(clientReq.getFunction());

        if(state == POLL) {
            String message;

            state = state.next(clientCookies);

            if(state == NAMESTEP) {
                message = msgNameStep;
            } else {
                message = msgFoodMood;
            }

            return buildOkResponse(state, message);
        } else if(state == NAMESTEP) {
            if(clientReq.getParams().length != 2) {
                return buildErrResponse(state, errName);
            }
            clientCookies.add(cookie_fname, clientReq.getParams()[0]);
            clientCookies.add(cookie_lname, clientReq.getParams()[1]);

            state.next(clientCookies);
            String message = clientCookies.getValue(cookie_fname) + msgFoodMood;
            return buildOkResponse(state, message);
        } else if(state == FOODMOOD) {
            if(clientReq.getParams().length != 1) {
                return buildErrResponse(state, errMood);
            }

            if(clientCookies.getValue(cookie_repeat) == null) {
                clientCookies.add(cookie_repeat, "1");
            } else {
                clientCookies.add(cookie_repeat,
                        clientCookies.getValue(cookie_repeat) + 1);
            }

            state = state.next(clientCookies);
            String message = msgBaseDiscount +
                    clientCookies.getValue(cookie_repeat) + msgStoreDiscount;
            return buildOkResponse(state, message);
        } else {
            return buildErrResponse(NULL, errFunction);
        }
    }

    private G8RResponse buildOkResponse(G8RPoll status, String message)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_OK, status.getName(), message,
                clientCookies);
    }

    private G8RResponse buildErrResponse(G8RPoll status, String message)
            throws ValidationException {
        return new G8RResponse(G8RResponse.type_ERROR, status.getName(),
                message, clientCookies);
    }
}
