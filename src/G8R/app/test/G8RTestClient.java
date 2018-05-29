/*
 * G8R.app.test:G8RTestClient
 *
 * Date Created: Feb/21/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.test;

import G8R.serialization.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class G8RTestClient {
    static int read = 0;
    static String function = "";

    public static void main(String[] args) throws IOException {
        Scanner scn = new Scanner(System.in);
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        int port = 12345;

        System.out.println("Enter number of runs and function: ");

        try {
            read = scn.nextInt();
            function = scn.next();
        } catch(InputMismatchException e) {
            System.out.println("What did you mean?");
        }

        for(int i = 0; i < read; i++) {
            System.out.println("connection: " + i);
            connectToServer(addr, port);
        }
    }

    private static void connectToServer(InetAddress addr, int port) {
        try(Socket soc = new Socket(addr, port)) {
            if (!soc.isClosed()) {
                MessageOutput out = new MessageOutput(soc.getOutputStream());
                MessageInput in = new MessageInput(soc.getInputStream());

                //initFunct(out, function);

                String line = "G8R/1.0 Q RUN " + function + "\r\n\r\n";
                    out.write(line);
                    G8RMessage message = G8RMessage.decode(in);
                    printResponse(message);
                    new G8RRequest(message.getFunction(), new String[]{"mex", "mex"}, message.getCookieList()).encode(out);
                    message = G8RMessage.decode(in);
                    printResponse(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initFunct(MessageOutput out, String funct)
            throws ValidationException, IOException {
        //G8RMessage message = new G8RRequest(funct, new String[]{}, new CookieList());
        //send message to server
        //message.encode(out);

        //out.write();
        out.write("G8R/1.0 Q RUN " + funct + "\r\n\r\n");
    }

    private static void printResponse(G8RMessage message) {
        G8RResponse res = (G8RResponse)message;
        if(res.getStatus().equals(G8RResponse.type_ERROR)) {
            System.err.println(res);
        }
    }
}
