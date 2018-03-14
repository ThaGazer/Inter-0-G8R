/*
 * G8R.app:G8RClientHandler
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.app.FunctionState.G8RFunction;
import G8R.app.FunctionState.G8RFunctionFactory;
import G8R.serialization.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.*;

public class G8RClientHandler implements Runnable {

    //logging information
    private static final String LOGGERNAME = G8RServer.class.getName();
    private static final String LOGGERFILE = "./logs/connections.log";

    //error message to client
    private static final String errFunction = "Unexpected function";
    private static final String errNullSocket = "null socket";

    //messages to client
    private static final String msgConnection = "connected to: ";
    private static final String msgCloseConnect = "closed connection to: ";
    private static final String msgCreatedLogFile = "Created new logging file";

    //class variables
    private static final int server_Timeout = 20000;

    private Socket client;
    private Logger logger;

    /**
     * creates a new clientHandler runnable
     * @param socket the client connection
     */
    public G8RClientHandler(Socket socket) throws IOException {
        client = Objects.requireNonNull(socket, errNullSocket);
        client.setSoTimeout(server_Timeout);
        setup_logger();
    }

    @Override
    public void run() {
        try {
            logger.info(msgConnection + client.getLocalSocketAddress());

            MessageInput in = new MessageInput(client.getInputStream());
            MessageOutput out = new MessageOutput(client.getOutputStream());

            G8RMessage mess = G8RMessage.decode(in);
            Enum e = G8RFunctionFactory.getByName(mess.getFunction());

            if(e != null) {
                while (e != ((G8RFunction)e).last()) {
                    e = handleRequest(mess, e, out);
                    logger.log(Level.INFO, buildConnection(mess, false), mess);

                    if(e != ((G8RFunction)e).last()) {
                        mess = G8RMessage.decode(in);
                    }
                }
            } else {
                mess = new G8RResponse(G8RResponse.type_ERROR, "NULL",
                        errFunction, mess.getCookieList());
                mess.encode(out);
                logger.log(Level.WARNING, buildConnection(mess, true), mess);
            }

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
     * @param function function state from the client
     * @param out output sink
     */
    private Enum<?> handleRequest
    (G8RMessage clientMess, Enum function, MessageOutput out)
            throws ValidationException, IOException {
        logger.log(Level.INFO, buildConnection(clientMess, true), clientMess);
        G8RRequest clientRequest = (G8RRequest) clientMess;

        if(!clientRequest.getFunction().equals(
                ((G8RFunction) function).getName())) {
            new G8RResponse(G8RResponse.type_ERROR, "NULL", errFunction,
                    clientMess.getCookieList()).encode(out);
            return ((G8RFunction) function).last();
        } else {
            return ((G8RFunction) function).next(clientRequest, out);
        }
    }

    /**
     * builds the logging message for a connection
     * @param message the message sent or received
     * @param sentOrReceiv which message to build (sent or received)
     * @return string representation of connection message
     */
    private String buildConnection(G8RMessage message, boolean sentOrReceiv) {
        if(sentOrReceiv) {
            return client.getLocalSocketAddress() + "-" + Thread.currentThread()
                    + " [Received:" + message + "]";
        } else {
            return client.getLocalSocketAddress() + "-" + Thread.currentThread()
                    + " [Sent:" + message + "]";
        }
    }

    /**
     * sets up the logger for the server
     * @throws IOException if I/O problem
     */
    private void setup_logger() throws IOException {
        //initializes the logger
        logger = Logger.getLogger(LOGGERNAME);

        if(new File(LOGGERFILE).createNewFile()) {
            logger.info(msgCreatedLogFile);
        }

        //defines handles for the logger
        Handler fileHand = new FileHandler(LOGGERFILE);
        Handler consoleHand = new ConsoleHandler();

        fileHand.setLevel(Level.ALL);
        consoleHand.setLevel(Level.SEVERE);

        //sets the formatting style of the logs
        consoleHand.setFormatter(new SimpleFormatter());

        logger.addHandler(fileHand);
        logger.addHandler(consoleHand);
    }
}
