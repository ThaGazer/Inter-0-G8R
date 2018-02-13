/*
 * G8R.app:G8RClient
 *
 * Date Created: Feb/01/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;

import G8R.serialization.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class G8RClient {

    private static final String errNumParams =
            "Usage: <server identity> <server port> <cookie file>";
    private static final String errReadingCookie = "Failed read cookies";
    private static final String errInitMessage =
            "Failed to create initial message";

    private static final String msgConsoleEnding = "> ";

    private static final String valStatOK = "OK";
    private static final String valStatERROR = "ERROR";
    private static final String delim_Space = " ";

    private static CookieList clientCookie = new CookieList();
    private static G8RResponse resMess;
    private static G8RRequest reqMess;

    public static void main(String[] args)
            throws UnknownHostException {
        if(args.length != 3) {
            throw new IllegalArgumentException(errNumParams);
        }

        InetAddress sIdent = InetAddress.getByName(args[0]);
        int sPort = Integer.parseInt(args[1]);
        String cFileName = args[2];
        Scanner scn = new Scanner(System.in);


        try (Socket soc = new Socket(sIdent, sPort)) {
            //initialize cookies
            try {
                readInCookie(cFileName);
            } catch(ValidationException ve) {
                ve.printStackTrace(errReadingCookie);
            }

            //reading initial function from user
            try {
                initFunct(soc, scn);
            } catch (ValidationException ve) {
                ve.printStackTrace(errInitMessage);
                return;
            }

            //sending and receiving from server
            boolean clientStop = false;
            do {
                try {
                    clientStop = clientOp(soc, scn);
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            } while (!clientStop && !soc.isClosed());

            writeOutCookie(cFileName);
            scn.close();
        } catch (IOException e) {
            writeOutCookie(cFileName);
            e.printStackTrace();
        }
    }

    private static void readInCookie(String fileName)
            throws IOException, ValidationException {
        if(new File(fileName).exists()) {
            clientCookie = new CookieList(new MessageInput(
                    new BufferedInputStream(new FileInputStream(fileName))));
        }
    }

    private static void writeOutCookie(String fileName) {
        try {
            clientCookie.encode(new MessageOutput(
                    new BufferedOutputStream(new FileOutputStream(fileName))));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void initFunct(Socket soc, Scanner scn)
            throws ValidationException, IOException {
        System.out.println("Function" + msgConsoleEnding);
        String funct = scn.nextLine();
        reqMess = new G8RRequest(funct, new String[]{""}, clientCookie);

        //send message to server
        reqMess.encode(new MessageOutput(soc.getOutputStream()));
    }

    private static boolean clientOp(Socket soc, Scanner scn)
            throws ValidationException, IOException {

        //read response from server
        receiveFromServer(new MessageInput(soc.getInputStream()));

        //terminates client if function from server was null
        if(resMess.getFunction() == null) {
            return true;
        }

        //prompt for request to server
        sendToServer(new MessageOutput(soc.getOutputStream()), scn);


        return false;
    }

    private static void receiveFromServer(MessageInput in)
            throws IOException, ValidationException {
        //receives message from server
        resMess = (G8RResponse)G8RMessage.decode(in);

        //saves all cookies sent from server
        clientCookie.addall(reqMess.getCookieList());

        //prints out message from server
        switch(resMess.getStatus()) {
            case valStatOK:
                System.out.println(resMess.getMessage());
                break;
            case valStatERROR:
                System.err.println(resMess.getMessage());
                break;
        }
    }

    private static void sendToServer(MessageOutput out, Scanner scn)
            throws ValidationException, IOException {
        //sets the request function to the function of the response
        reqMess.setFunction(resMess.getFunction());

        //reads the request of the user to the servers response
        reqMess.setParams(scn.nextLine().split(delim_Space));

        //sends request to server
        reqMess.encode(out);
    }
}
