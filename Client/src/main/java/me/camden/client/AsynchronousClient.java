package me.camden.client;

import me.camden.client.listener.SocketListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Camden on 10/7/2021
 **/
public class AsynchronousClient implements Runnable {

    private AsynchronousSocketChannel asynchronousSocketChannel;

    private final String address;
    private final int port;

    private SocketListener socketListener;

    public AsynchronousClient(String address,int port) {
        this.address = address;
        this.port = port;
    }

    public Thread start() {
        final Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        reconnection_attempt:
        while (true) {
            try {
                // Connect to Socket Channel
                if(getAsynchronousSocketChannel() == null) {

                    setAsynchronousSocketChannel(AsynchronousSocketChannel.open());

                    final Future<Void> result = getAsynchronousSocketChannel().connect(new InetSocketAddress(getAddress(), getPort()));
                    result.get();

                    getSocketListener().onConnection(this);
                }
            } catch (IOException e) {
                e.printStackTrace();

                // Retry Connection
                this.delayReconnection();
                continue reconnection_attempt;
            } catch (InterruptedException | ExecutionException e) {
                if(e.getMessage().contains("computer refused")) {
                    System.exit(-1);
                }
                e.printStackTrace();
            }

            // Send Test Packet
            getAsynchronousSocketChannel().write(ByteBuffer.wrap("Test Packet to Server".getBytes()));

            while (getAsynchronousSocketChannel().isOpen()) {
                try {
                    // Allocate Byte Buffer For Future Packet
                    final ByteBuffer packetBuffer = ByteBuffer.allocate(1024);

                    // Read Future Packet
                    final Future<Integer> futureRead = getAsynchronousSocketChannel().read(packetBuffer);
                    futureRead.get();

                    // Convert Byte Buffer to String
                    final String packetString = new String(packetBuffer.array()).trim();

                    // Dispatch Packet Event
                    getSocketListener().onPacket(this,packetString);

                    // Clear Byte Buffer
                    packetBuffer.clear();
                } catch (Exception e) {
                    if(e.getMessage().contains("forcibly closed by")) {
                        // Dispatch Disconnection Event
                        getSocketListener().onDisconnection(this);
                        break;
                    }
                }
            }

            this.delayReconnection();
            continue reconnection_attempt;
        }
    }

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public SocketListener getSocketListener() {
        return socketListener;
    }

    private void delayReconnection() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }
}
