package me.camden.client.listener;

import me.camden.client.AsynchronousClient;

/**
 * @author Camden on 10/7/2021
 **/
public interface SocketListener {

    void onConnection(AsynchronousClient asynchronousClient);

    void onDisconnection(AsynchronousClient asynchronousClient);

    void onPacket(AsynchronousClient asynchronousClient, String packetString);
}