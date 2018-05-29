/*
 * G8R.app.test:G8RAIOServer
 *
 * Date Created: May/30/2018
 * Author:
 *   -Justin Ritter
 */
package G8R.app.test;

import G8R.app.FunctionState.G8RFunction;
import G8R.app.FunctionState.G8RFunctionFactory;
import G8R.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class G8RAIOTestServer {

    public static G8RMessage message;
    public static AsynchronousSocketChannel client;

    public static void main(String[] args) {
        if(args.length != 1) {
            throw new IllegalArgumentException("Usage: <port>");
        }

        int servPort = Integer.parseInt(args[0]);

        try {
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
            try(AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group)) {
                server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                server.bind(new InetSocketAddress(servPort));

                    server.accept(server, new handler());
                    group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class handler implements
            CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
        @Override
        public void completed
                (AsynchronousSocketChannel connection, AsynchronousServerSocketChannel attachment) {
            attachment.accept(attachment, this);

            ByteBuffer buff = ByteBuffer.allocate(4096);
            client = connection;
            try {
                System.out.println("connected to:" + client.getRemoteAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
            client.read(buff, 20000, TimeUnit.MILLISECONDS, buff, new reader());
        }

        @Override
        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
            System.err.println("failed to accept");
        }
    }

    public static class reader implements CompletionHandler<Integer, ByteBuffer> {

        @Override
        public void completed(Integer result, ByteBuffer bIn) {
            try {
                message = G8RMessage.decode(new MessageInput(new ByteArrayInputStream(bIn.array())));
                System.out.println("read message: " + message);

                try {
                    Enum e = G8RFunctionFactory.getByName(message.getFunction());
                    if(e != null) {
                        e = ((G8RFunction)e).nextFunct();
                        message = new G8RResponse(G8RResponse.type_OK, message.getFunction(), ((G8RFunction) e).getName(), new CookieList());
                    }
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                try {
                    message.encode(new MessageOutput(bOut));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteBuffer buffStream = ByteBuffer.wrap(bOut.toByteArray());
                System.out.println("sent: " + message);
                client.write(buffStream, buffStream, new writer());
            } catch(IOException | ValidationException e) {
                client.read(bIn, 20000, TimeUnit.MILLISECONDS, bIn, this);
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.err.println("failed to read");
        }
    }

    public static class writer implements CompletionHandler<Integer, ByteBuffer> {

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            if(attachment.hasRemaining()) {
                System.out.println("sending");
                client.write(attachment, attachment, this);
            } else {
                System.out.println("sent");
                attachment.clear();
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.err.println("failed to write");
        }
    }

}
