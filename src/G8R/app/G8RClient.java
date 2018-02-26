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
import java.net.*;
import java.util.Scanner;

public class G8RClient {

    private static final String errNumParams =
            "Usage: <server identity> <server port> <cookie file>";
    private static final String errReadingCookie = "Failed read cookies";

    private static final String msgConsoleEnding = "> ";

    private static final String valStatOK = "OK";
    private static final String valStatERROR = "ERROR";
    private static final String delim_Space = " ";

    private static CookieList clientCookie = new CookieList();
    private static G8RMessage message;

    public static void main(String[] args) throws IOException {
        if(args.length != 3) {
            throw new IllegalArgumentException(errNumParams);
        }

        InetAddress sIdent = InetAddress.getByName(args[0]);
        int sPort = Integer.parseInt(args[1]);
        String cFileName = args[2];
        Scanner scn = new Scanner(System.in);


        try (Socket soc = new Socket(sIdent, sPort)) {
            MessageInput in = new MessageInput(soc.getInputStream());
            MessageOutput out = new MessageOutput(soc.getOutputStream());

            //initialize cookies
            try {
                readInCookie(cFileName);
            } catch(ValidationException ve) {
                ve.printStackTrace(errReadingCookie);
            }

            //sending and receiving from server
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
            e.printStackTrace();
        }
    }

    private static void readInCookie(String fileName)
            throws IOException, ValidationException {
        File file = new File(fileName);
        if(file.exists()) {
            if(file.length() > 0) {
                clientCookie = new CookieList(new MessageInput(
                        new BufferedInputStream(
                                new FileInputStream(fileName))));
            }
        }
    }

    private static void writeOutCookie(String fileName)
            throws IOException {
        clientCookie.encode(
                new MessageOutput(new FileOutputStream(fileName)));
    }

    private static void initFunct(MessageOutput out, Scanner scn)
            throws ValidationException, IOException {
        System.out.print("Function" + msgConsoleEnding);
        String funct = scn.nextLine();
        message = new G8RRequest(funct, new String[]{}, clientCookie);

        //send message to server
        message.encode(out);
    }

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

    private static boolean sendToServer(MessageOutput out, Scanner scn)
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

    private static void printResponse(G8RMessage message) {
        G8RResponse res = (G8RResponse)message;

        switch(res.getStatus()) {
            case valStatOK:
                System.out.print(res.getMessage());
                break;
            case valStatERROR:
                System.err.println(res.getMessage());
                break;
        }
    }
}
