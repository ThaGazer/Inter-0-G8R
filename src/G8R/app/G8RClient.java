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
    private static final String errFNF = "file not found";
    private static final String err_InitMessage =
            "Failed to create initial message";

    private static final String msgConsoleEnding = "> ";

    private static final String valStatOK = "OK";
    private static final String valStatERROR = "ERROR";
    protected static final String delim_Space = " ";

    private static CookieList clientCookie = new CookieList();
    private static G8RResponse resMess;
    private static G8RRequest reqMess;

    public static void main(String[] args)
            throws FileNotFoundException, UnknownHostException {
        if(args.length != 3) {
            throw new IllegalArgumentException(errNumParams);
        }

        InetAddress sIdent = InetAddress.getByName(args[0]);
        int sPort = Integer.parseInt(args[1]);
        String cFileName = args[2];
        Scanner scn = new Scanner(System.in);

        File cfile = new File(cFileName);
        if (!cfile.isFile()) {
            throw new FileNotFoundException(errFNF);
        }

        try (Socket soc = new Socket(sIdent, sPort)) {

            try {
                initFunct(scn);
            } catch (ValidationException ve) {
                ve.printStackTrace(err_InitMessage);
                return;
            }

            boolean clientStop = false;
            do {
                try {
                    clientStop = clientOp(soc);
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            } while (!clientStop && !soc.isClosed());

            scn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initFunct(Scanner scn) throws ValidationException {
        System.out.println("Function" + msgConsoleEnding);
        String funct = scn.nextLine();
        reqMess = new G8RRequest(funct, new String[]{""}, null);
    }

    private static boolean clientOp(Socket soc)
            throws ValidationException, IOException {

        //send message to server
        reqMess.encode(new MessageOutput(soc.getOutputStream()));

        receiveFromServer(new MessageInput(soc.getInputStream()));

        //terminates client if function from server was null
        if(resMess.getFunction() == null) {
            return true;
        }

        //prompt for response to server



        return false;
    }

    private static G8RMessage receiveFromServer(MessageInput in)
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
        return resMess;
    }
}
