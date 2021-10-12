package me.camden.server.threads;

import me.camden.server.AsynchronousServer;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

/**
 * @author Camden on 10/7/2021
 **/

public class SocketClientConnection implements Runnable {

    private final AsynchronousServer asynchronousServer;
    private final AsynchronousSocketChannel socketChannel;


    public SocketClientConnection(AsynchronousServer asynchronousServer, AsynchronousSocketChannel socketChannel) {
        this.asynchronousServer = asynchronousServer;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        // Dispatch Connection Event
        getAsynchronousServer().getSocketListener().onConnection(getAsynchronousServer(), this);

        // Send Test Packet
        getSocketChannel().write(ByteBuffer.wrap("Test Packet to Client".getBytes()));

        while (getSocketChannel().isOpen()) {
            try {

                // Allocate Byte Buffer For Future Packet
                final ByteBuffer buffer = ByteBuffer.allocate(1024);

                // Read Future Packet
                final Future<Integer> read = getSocketChannel().read(buffer);
                read.get();

                // Convert Byte Buffer to String
                final String packetString = new String(buffer.array()).trim();

                // Dispatch Packet Event
                getAsynchronousServer().getSocketListener().onPacket(getAsynchronousServer(), this, packetString);

                // Clear Byte Buffer
                buffer.clear();

            } catch (Exception e) {
                // Break on Error
                break;
            }
        }

        // Dispatch Disconnection Event
        getAsynchronousServer().getSocketListener().onDisconnection(getAsynchronousServer(), this);
    }

    public AsynchronousServer getAsynchronousServer() {
        return asynchronousServer;
    }

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }
}