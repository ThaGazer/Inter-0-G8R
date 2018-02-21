/*
 * G8R.app:G8REchoServer
 *
 * Date Created: Feb/19/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app;


import G8R.serialization.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class G8REchoServer {
    public static void main(String[] argv) {
        int servPort = argv.length == 1 ? Integer.parseInt(argv[0]) : 7;

        try (ServerSocket servSock = new ServerSocket(servPort)) {
            while(true) {
                Thread t = new Handler(servSock.accept());
                t.start();
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
