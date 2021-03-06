/*
 * G8R.app:G8RClient
 *
 * Date Created: Feb/01/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.serialization.*;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class G8RClient {

    private static final String errNumParams =
            "Usage: <server identity> <server port> <cookie file>";
    private static final String errReadingCookie = "Failed to read cookies";

    private static final String msgConsoleEnding = "> ";

    private static final String valStatOK = "OK";
    private static final String valStatERROR = "ERROR";
    private static final String delim_Space = " ";

    private static CookieList clientCookie = new CookieList();
    private static G8RMessage message;

    public static void main(String[] args) {
        if(args.length != 3) {
            throw new IllegalArgumentException(errNumParams);
        }

        InetAddress sIdent = null;
        try {
            sIdent = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        int sPort = Integer.parseInt(args[1]);
        String cFileName = args[2];
        Scanner scn = new Scanner(System.in);

        //initialize cookies
        try {
            readInCookie(cFileName);
        } catch(IOException | ValidationException ve) {
            ve.printStackTrace();
            System.exit(-1);
        }

        try (Socket soc = new Socket(sIdent, sPort)) {
            MessageInput in = new MessageInput(soc.getInputStream());
            MessageOutput out = new MessageOutput(soc.getOutputStream());

            //sending and receiving initial message from server
            try {
                initFunct(out, scn);
            } catch (ValidationException | IOException e) {
                System.err.println(e.getMessage());
                soc.close();
            }

            while (!soc.isClosed()) {
                try {
                    if(clientOp(in, out, scn)) {
                        soc.close();
                    }
                } catch (ValidationException e) {
                    System.err.println(e.getMessage());
                }
            }

            writeOutCookie(cFileName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * reads in the cookie list file
     * @param fileName name of cookie file
     * @throws IOException if I/O problem
     * @throws ValidationException if G8R problem
     */
    private static void readInCookie(String fileName)
            throws IOException, ValidationException {
        File file = new File(fileName);
        if(file.exists()) {
            if(file.length() > 0) {
                clientCookie = new CookieList(new MessageInput(
                        new BufferedInputStream(
                                new FileInputStream(fileName))));
            }
        } else {
            throw new IOException(errReadingCookie);
        }
    }

    /**
     * saves cookielist to file
     * @param fileName name of cookie file
     * @throws IOException if I/O problem
     */
    private static void writeOutCookie(String fileName)
            throws IOException {
        clientCookie.encode(
                new MessageOutput(new FileOutputStream(fileName)));
    }

    /**
     * constructs a new intial message
     * @param out output sink
     * @param scn input sink
     * @throws ValidationException if G8R error
     * @throws IOException if I/O problem
     */
    private static void initFunct(MessageOutput out, Scanner scn)
            throws ValidationException, IOException {
        System.out.print("Function" + msgConsoleEnding);
        String funct = scn.nextLine();
        message = new G8RRequest(funct, new String[]{}, clientCookie);

        //send message to server
        message.encode(out);
    }

    /**
     * handles client operations
     * @param in input sink
     * @param out output sink
     * @param scn input sink
     * @return
     * @throws ValidationException
     * @throws IOException
     */
    private static boolean clientOp(MessageInput in, MessageOutput out,
                                    Scanner scn)
            throws ValidationException, IOException {

        //read response from server
        receiveFromServer(in);

        //terminates client if function from server was null
        if(message.getFunction().equals("NULL")) {
            System.out.println();
            return true;
        }

        //prompt for request to server
        while(!sendToServer(out, scn)) {
            System.err.flush();
            printResponse(message);
        }
        return false;
    }

    private static void receiveFromServer(MessageInput in)
            throws IOException, ValidationException {

        //decodes message from server
        message = G8RMessage.decode(in);

        //saves all cookies sent from server
        clientCookie.addAll(message.getCookieList());

        //prints out message from server
        printResponse(message);
    }

    /**
     * reads information from user and sends it to the server
     * @param out output stream to server
     * @param scn to read user input
     * @return true if successful send or false if unable to create message
     * @throws IOException if I/O problems
     */
    private static boolean sendToServer(MessageOutput out, @NotNull Scanner scn)
            throws IOException {
        //reads the request of the user to the servers response
        String[] parameters = scn.nextLine().split(delim_Space);

        //initialize a request message from a known message
        try {
            G8RRequest req = new G8RRequest
                    (message.getFunction(), parameters, clientCookie);

            //sends request to server
            req.encode(out);
        } catch(ValidationException ve) {
            System.err.println(ve.getReason());
            return false;
        }
        return true;
    }

    /**
     * prints the message in a message
     * @param message response G8R message
     */
    private static void printResponse(G8RMessage message) {
        G8RResponse res = (G8RResponse)message;

        switch(res.getStatus()) {
            case valStatOK:
                System.out.print(res.getMessage());
                break;
            case valStatERROR:
                System.err.print(res.getMessage());
                break;
        }
    }
}
