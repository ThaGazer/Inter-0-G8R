/*
 * G8R.app:G8REchoServer
 *
 * Date Created: Feb/19/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.test;


import G8R.serialization.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class G8REchoServer {

    private static final String usage = "Usage: <server port>";
    private static final String serverStatus = "Server running on: ";

    public static void main(String[] argv) {
        if(argv.length != 1) {
            throw new IllegalArgumentException(usage);
        }
        int servPort = Integer.parseInt(argv[0]);

        try (ServerSocket servSock = new ServerSocket(servPort)) {
            System.out.println(serverStatus + servSock.getLocalSocketAddress());

            while(true) {
                try {
                    Thread t = new Handler(servSock.accept());
                    t.start();
                } catch(Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static class Handler extends Thread {

        private Socket client;

        public Handler(Socket socket) {
            client = Objects.requireNonNull(socket);
        }

        @Override
        public void run() {
            try {
                MessageInput in = new MessageInput(client.getInputStream());
                MessageOutput out = new MessageOutput(client.getOutputStream());

                G8RMessage message = G8RMessage.decode(in);
                message.encode(out);
            } catch (IOException | ValidationException e) {
                e.printStackTrace();
            }
        }
    }
}
