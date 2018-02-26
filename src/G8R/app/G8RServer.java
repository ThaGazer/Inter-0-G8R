/*
 * G8R.app:G8RServer
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.serialization.G8RMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class G8RServer {

    private static final String LOGGERNAME = G8RServer.class.getName();
    private static final String LOGGERCONFIG = "./logs/.properties";
    private static final String LOGGERFILE = "./logs/server.log";

    private static final String errParams =
            "Usage: <server port> <thread count>";
    private static final String errCrash = "Server crashed";

    private static Logger logger = null;

    public static void main(String[] argv) throws IOException {
        if(argv.length != 2) {
            throw new IllegalArgumentException(errParams);
        }

        int servPort = Integer.parseInt(argv[0]);
        int numThread = Integer.parseInt(argv[1]);

        ExecutorService pool = Executors.newFixedThreadPool(numThread);

        //initializes logger
        setup_logger();

        try(ServerSocket server = new ServerSocket(servPort)) {
            while(true) {
                pool.execute(new G8RClientHandler(server.accept()));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, errCrash, e);
        }

    }

    private static void setup_logger() throws IOException {
        LogManager manager = LogManager.getLogManager();
        manager.reset();

        /*future implementation maybe
        manager.readConfiguration(new FileInputStream(LOGGERCONFIG));*/

        //initializes the logger
        logger = Logger.getLogger(LOGGERNAME);

        //defines handles for the logger
        Handler fileHand = new FileHandler(LOGGERFILE);
        Handler consoleHand = new ConsoleHandler();

        fileHand.setLevel(Level.ALL);
        consoleHand.setLevel(Level.SEVERE);

        //sets the formatting style of the logs
        fileHand.setFormatter(new SimpleFormatter());
        consoleHand.setFormatter(new SimpleFormatter());

        logger.addHandler(fileHand);
        logger.addHandler(consoleHand);
    }
}
