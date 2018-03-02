/*
 * G8R.app:G8RServer
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class G8RServer {

    //private static final String LOGGERCONFIG = "./logs/.properties";
    private static final String LOGGERNAME = G8RServer.class.getName();
    private static final String LOGGERFILE = "./logs/connections.log";

    private static final String errParams =
            "Usage: <server port> <thread count>";
    private static final String errCrash = "Server crashed";

    private static final String msgServerStart = "Server started on port: ";
    private static final String msgServerEnd = "Server closed";

    private static final int server_Timeout = 20000;

    private static Logger logger = null;

    /**
     * sends and receives messages from multiple clients
     * @param argv arguments to passed to server
     * @throws IOException if I/O problem
     */
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
            setup_Server(server);
            logger.info(msgServerStart + server.getLocalPort());

            while(true) {
                pool.execute(new G8RClientHandler(server.accept()));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, errCrash, e);
        } finally {
            logger.log(Level.INFO, msgServerEnd);
        }
    }

    /**
     * sets up the configureation of tbe server
     * @param server the server connection
     * @throws SocketException if socket problem
     */
    private static void setup_Server(ServerSocket server)
            throws SocketException {
        server.setReuseAddress(true);
    }

    /**
     * sets up the logger for the server
     * @throws IOException if I/O problem
     */
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
        consoleHand.setLevel(Level.INFO);

        //sets the formatting style of the logs
        consoleHand.setFormatter(new SimpleFormatter());

        logger.addHandler(fileHand);
        logger.addHandler(consoleHand);
    }
}
