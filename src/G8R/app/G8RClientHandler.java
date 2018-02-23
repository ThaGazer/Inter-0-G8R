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

    private final String errFunction = "unknown function";
    Socket client = null;
    Logger logger = Logger.getLogger(G8RServer.class.getName());

    public G8RClientHandler(Socket socket) {
        client = Objects.requireNonNull(socket);
    }

    @Override
    public void run() {
        try {
            MessageInput in = new MessageInput(client.getInputStream());
            MessageOutput out = new MessageOutput(client.getOutputStream());
            G8RMessage clientMess = G8RMessage.decode(in);

            G8RPoll state = G8RPoll.getByName(clientMess.getFunction());

            if(state == POLL) {
                functPoll(clientMess);
            } else if(state == NAMESTEP) {
                functNameStep(clientMess);
            } else if(state == FOODMOOD) {
                functFoodMood(clientMess);
            } else {
                G8RResponse res = new G8RResponse(G8RResponse.type_ERROR,
                        clientMess.getFunction(), errFunction,
                        clientMess.getCookieList());
                res.encode(out);
            }
        } catch (ValidationException | IOException e) {
            logger.log(Level.WARNING, "", e);
        }
    }

    private void functPoll(G8RMessage message) {

    }

    private void functNameStep(G8RMessage message) {

    }

    private void functFoodMood(G8RMessage message) {

    }
}
