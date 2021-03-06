/*
 * G8R.app:G8RServer
 *
 * Date Created: Feb/20/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;


import G8R.app.FunctionState.G8RFunctionFactory;
import N4M.app.N4MClientHandler;
import N4M.serialization.ApplicationEntry;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class G8RServer {

    //private static final String LOGGERCONFIG = "LOGDIR\\.properties";
    private static final String LOGNAME = G8RServer.class.getName();
    private static final String LOGDIR = "logs";
    private static final String G8RLOGFILE = LOGDIR + "\\connections.log";
    private static final String N4MLOGFILE = LOGDIR + "\\n4m.log";

    private static final String errParams =
            "Usage: <server port> <thread count>";
    private static final String errServerCrash = "Server crashed";
    private static final String errThread = "could not close thread";
    private static final String errLogger = "could not create logger";

    private static final String msgServerStart = "Server started on port: ";
    private static final String msgServerEnd = " Server closed";
    private static final String msgG8R = "G8R ";
    private static final String msgN4M = "N4M ";

    private static Logger logger = null;
    private static ArrayList<ApplicationEntry> appList = new ArrayList<>();
    private static ArrayList<Thread> threadList = new ArrayList<>();
    private static int servPort;
    private static int numThread;
    private static long lastAccess = 0;

    /**
     * sends and receives messages from multiple clients
     * @param argv arguments to passed to server
     */
    public static void main(String[] argv) {
        if(argv.length != 2) {
            throw new IllegalArgumentException(errParams);
        }

        servPort = Integer.parseInt(argv[0]);
        numThread = Integer.parseInt(argv[1]);

        //initializes logger
        try {
            setup_logger();
        } catch(IOException ioe){
            logger.severe(errLogger);
            System.exit(-1);
        }

        //store all the applications that can be used
        setup_applicationList();


        //G8R server
        handle_G8R();

        //N4M server
        handle_N4M();

        for(Thread t : threadList) {
            try {
                t.join();
            } catch(InterruptedException e) {
                logger.severe(errThread);
            }
        }
    }

    /**
     * sets up the configuration of tbe server
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
     */
    private static void setup_applicationList() {
        appList.addAll(G8RFunctionFactory.values());
    }

    /**
     * handles a G8R request
     */
    private static void handle_G8R() {
        Thread t = new Thread(() -> {
            ExecutorService pool = Executors.newFixedThreadPool(numThread);

            try(ServerSocket servTCP = new ServerSocket(servPort)) {
                setup_Server(servTCP);
                logger.info(msgG8R + msgServerStart + servTCP.getLocalPort());

                //G8RClients
                while (true) {
                    pool.execute(new G8RClientHandler(servTCP.accept(),
                            appList));
                    //timestamp in seconds
                    lastAccess = TimeUnit.MILLISECONDS.toSeconds
                            (new Date().getTime());
                }
            } catch(Exception e) {
                logger.log(Level.SEVERE, msgG8R + errServerCrash, e);
            } finally {
                logger.log(Level.INFO, msgG8R + msgServerEnd);
            }
        });
        threadList.add(t);
        t.start();
    }

    /**
     * handles a N4M request
     */
    private static void handle_N4M() {
        Thread t = new Thread(() -> {
            try(DatagramSocket servUDP = new DatagramSocket(servPort)) {
                logger.info(msgN4M + msgServerStart + servUDP.getLocalPort());
                int maxPacketSize = servUDP.getReceiveBufferSize()-20;

                while(true) {
                    try {
                        DatagramPacket p = new DatagramPacket
                                (new byte[maxPacketSize], maxPacketSize);

                        servUDP.receive(p);
                        N4MClientHandler handler =
                                new N4MClientHandler(p, appList, lastAccess);

                        p.setData(handler.response());
                        servUDP.send(p);
                    } catch(IOException ioe) {
                        logger.warning(msgN4M + ioe.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, msgN4M + errServerCrash, e);
            } finally {
                logger.info(msgN4M + msgServerEnd);
            }
        });
        threadList.add(t);
        t.start();
    }
}