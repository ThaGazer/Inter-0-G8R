/*
 * G8R.app:G8RSeverAIO
 * Created on 4/21/2018
 *
 * Author(s):
 * -Justin Ritter
 */
/*
 * G8R.app:G8RServer
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.app.FunctionState.G8RFunction;
import G8R.app.FunctionState.G8RFunctionFactory;
import G8R.serialization.*;
import N4M.app.N4MClientHandler;
import N4M.serialization.ApplicationEntry;
import N4M.serialization.N4MException;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class G8RServerAIO {

    //private static final String LOGGERCONFIG = "./logs/.properties";
    private static final String LOGNAME = G8RServer.class.getName();
    private static final String LOGDIR = "logs";
    private static final String G8RLOGFILE = LOGDIR + "/onnections.log";
    private static final String N4MLOGFILE = LOGDIR + "/n4m.log";

    private static final String errParams =
            "Usage: <server port>";
    private static final String errServerCrash = "Server crashed";
    private static final String errThread = "could not close thread";
    private static final String errServerSetup = "error setting up server";
    private static final String errLogger = "could not create logger";
    private static final String errAppList = "could not create " +
            "the application list";

    private static final String msgServerStart = "server started on port: ";
    private static final String msgServerEnd = "server closed";
    private static final String msgG8R = "G8R ";
    private static final String msgN4M = "N4M ";

    private static Logger logger = null;
    private static ArrayList<ApplicationEntry> appList = new ArrayList<>();
    private static ArrayList<Thread> threadList = new ArrayList<>();
    private static int servPort;
    private static long lastAccess = 0;

    /**
     * sends and receives messages from multiple clients
     * @param argv arguments to passed to server
     */
    public static void main(String[] argv) {
        if(argv.length != 1) {
            throw new IllegalArgumentException(errParams);
        }

        servPort = Integer.parseInt(argv[0]);

        //initializes logger
        try {
            setup_logger();
        } catch(IOException ioe) {
            logger.severe(errLogger);
            System.exit(-1);
        }

        try {
            setup_applicationList();
        } catch(N4MException e) {
            logger.severe(errAppList);
            System.exit(-1);
        }

        //client handlers
        handle_G8R();
        //handle_N4M();

        for(Thread t : threadList) {
            try {
                t.join();
            } catch(InterruptedException e) {
                logger.severe(errThread);
            }
        }
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

        //creates a log files
        new File(LOGDIR).mkdirs();
        new File(G8RLOGFILE).createNewFile();
        new File(N4MLOGFILE).createNewFile();

        //initializes the logger
        logger = Logger.getLogger(LOGNAME);

        //defines handles for the logger
        Handler fileHand1 = new FileHandler(G8RLOGFILE);
        Handler fileHand2 = new FileHandler(N4MLOGFILE);
        Handler consoleHand = new ConsoleHandler();

        //filters
        fileHand1.setFilter(record -> record.getMessage().contains(msgG8R));
        fileHand2.setFilter(record -> record.getMessage().contains(msgN4M));

        fileHand1.setLevel(Level.ALL);
        fileHand2.setLevel(Level.ALL);
        consoleHand.setLevel(Level.INFO);

        //sets the formatting style of the logs
        consoleHand.setFormatter(new SimpleFormatter());

        logger.addHandler(fileHand1);
        logger.addHandler(fileHand2);
        logger.addHandler(consoleHand);
    }

    /**
     * creates an application list of all applications
     * @throws N4MException is could not get application
     */
    private static void setup_applicationList() throws N4MException {
        appList.addAll(G8RFunctionFactory.values());
    }

    private static void setup_sever(AsynchronousServerSocketChannel serv) {
        try {
            serv.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serv.bind(new InetSocketAddress(servPort));
        } catch(IOException ioe) {
            logger.severe(errServerSetup);
        }
    }

    /**
     * handles a G8R request
     */
    private static void handle_G8R() {
        logger.info(msgG8R + msgServerStart + servPort);
        try (AsynchronousServerSocketChannel server =
                     AsynchronousServerSocketChannel.open()) {

            //server setup
            setup_sever(server);

            while(true) {
                Attachment a = new Attachment(0L, server);
                server.accept(a, new G8RClientHandlerAIO());
                System.in.read();
            }
        } catch (IOException e) {
            System.err.println(msgG8R + errServerCrash);
            logger.severe(msgG8R + errServerCrash);
        }
    }

    public static class Attachment {
        long lassAccess;
        AsynchronousServerSocketChannel server;

        public Attachment(long aTime, AsynchronousServerSocketChannel aServ) {
            lassAccess = aTime;
            server = aServ;
        }
    }
}
