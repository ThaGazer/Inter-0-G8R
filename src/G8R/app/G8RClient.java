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

    private static final String msgConsoleEnding = "> ";

    private static final String valStatOK = "OK";
    private static final String valStatERROR = "ERROR";
    protected static final String delim_Space = " ";

    private static CookieList clientCookie = new CookieList();
    private static InetAddress sIdent;
    private static int sPort;
    private static String cFileName;
    private static G8RMessage message;

    public static void main(String[] args)
            throws FileNotFoundException, UnknownHostException {
        if(args.length != 3) {
            throw new IllegalArgumentException(errNumParams);
        }

        sIdent = InetAddress.getByName(args[0]);
        sPort = Integer.parseInt(args[1]);
        cFileName = args[2];
        Scanner scn = new Scanner(System.in);

        File cfile = new File(cFileName);
        if (!cfile.isFile()) {
            throw new FileNotFoundException(errFNF);
        }

        try (Socket soc = new Socket(sIdent, sPort)) {
            try {
                initFunct(scn);
            } catch (ValidationException e) {
                System.err.println("Could not set initial function");
                e.printStackTrace();
            }

            boolean clientStop = false;
            do {
                try {
                    clientStop = clientOp(soc);
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            } while (!clientStop);

            scn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initFunct(Scanner scn) throws ValidationException {
        System.out.println("Function" + msgConsoleEnding);
        String funct = scn.nextLine();
        System.out.println("Parameters" + msgConsoleEnding);
        String[] param = scn.nextLine().split(delim_Space);
        message = new G8RRequest(funct, param, null);
    }

    private static boolean clientOp(Socket soc)
            throws ValidationException, IOException {
        MessageInput in = new MessageInput(soc.getInputStream());
        MessageOutput out = new MessageOutput(soc.getOutputStream());

        //send message to server
        message.encode(out);

        //receives message from server
        message = G8RMessage.decode(in);

        //saves all cookies sent from server
        clientCookie.addall(((G8RResponse)message).getCookieList());

        //prints out message from server
        switch(((G8RResponse)message).getStatus()) {
            case valStatOK:
                System.out.println(((G8RResponse)message).getMessage());
                break;
            case valStatERROR:
                System.err.println(((G8RResponse)message).getMessage());
                break;
        }

        //terminates client if function from server was null
        if(((G8RResponse)message).getFunction() == null) {
            return true;
        }

        //prompt for response to server


        return false;
    }
}
