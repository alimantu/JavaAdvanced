package ru.ifmo.ctddev.salinskii.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Катя on 21.05.2015.
 */
public class HelloUDPServer implements HelloServer {
    public static final int BUFF_SIZE = 1024;
    private boolean[] portUsed = new boolean[1 << 16];
    private boolean shutdown = false;

    public HelloUDPServer(){
        for(boolean port: portUsed){
            port = false;
        }
        System.out.print(portUsed[1234]);
    }

    @Override
    public void start(int port, int threads) {
        if(!tryPort(port)){
            throw new IllegalArgumentException("Port " + port + " is already used by server, try another port");
        }

        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            datagramSocket.setSoTimeout(100);
            Executor executor = Executors.newFixedThreadPool(threads);
            for(int thread = 0; thread < threads; thread++){
                executor.execute(new Runnable() {
                                     @Override
                                     public void run() {
                                         try {
                                             int buffSize = datagramSocket.getReceiveBufferSize();
                                             DatagramPacket datagramPacket = new DatagramPacket(new byte[buffSize], buffSize);
                                             while(!shutdown && portUsed[port]){
                                                 try {
                                                     datagramSocket.receive(datagramPacket);
                                                     String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength(), Charset.forName("UTF-8"));
                                                     System.out.println(message);
                                                     byte[] answer = ("Hello, " + message).getBytes("UTF-8");
                                                     datagramSocket.send(new DatagramPacket(answer, answer.length, datagramPacket.getAddress(), datagramPacket.getPort()));
                                                 } catch (IOException e) {
                                                 //    e.printStackTrace();
                                                 }
                                             }
                                         } catch (SocketException e) {
                                             e.printStackTrace();
                                         }

                                     }
                                 }
                );
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean tryPort(int port) {
        if(!portUsed[port]){
            return true;
        }
        portUsed[port] = true;
        return false;
    }

    public void close(int port){
        portUsed[port] = false;
    }


    @Override
    public void close() {
        shutdown = true;
    }
}
