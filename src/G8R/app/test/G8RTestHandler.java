/*
 * G8R.app.test:G8RTestHandler
 *
 * Date Created: Mar/01/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.test;

import G8R.serialization.*;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class G8RTestHandler implements Runnable {

    private Logger logger = Logger.getLogger(G8RTestHandler.class.getName());

    private Socket client;

    public G8RTestHandler(Socket soc) {
        client = soc;
    }

    @Override
    public void run() {
        try {
            MessageInput in = new MessageInput(client.getInputStream());
            MessageOutput out = new MessageOutput(client.getOutputStream());

            logger.info("connection on: " + Thread.currentThread());

            while(true) {
                G8RMessage message = G8RMessage.decode(in);
                G8RResponse res = new G8RResponse(G8RResponse.type_OK,
                        message.getFunction(), "hi", message.getCookieList());
                res.encode(out);
            }
        } catch (IOException | ValidationException e) {
            e.printStackTrace();
        }
    }
}
