package ru.ifmo.ctddev.salinskii.hello;

import java.net.*;

/**
 * Created by Катя on 28.05.2015.
 */
public class ClientThread implements Runnable{
    public static final int BUFF_SIZE = 1 << 10;
    private final int number;
    private final String prefix;
    private final int requests;
    private final InetAddress inetAddress;
    private final DatagramSocket datagramSocket;
    private Thread thread;
    private final int port;

    public ClientThread(int number, String prefix, InetAddress inetAddress, int port, int requests) throws SocketException {
        this.number = number;
        this.prefix = prefix;
        this.port = port;
        this.requests = requests;
        this.inetAddress = inetAddress;
        this.datagramSocket = new DatagramSocket();
        this.thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
        String sentMess = new String();
        String receivedMess = new String();
        //for(int i = 0; i < requests; i += tryMessage(sentMess, receivedMess)){
        int i = 0;
        while(i < requests){
            sentMess = prefix + number + "_" + i;
            System.out.println(sentMess);
            byte[] byteSentMess = sentMess.getBytes();
            final DatagramPacket sentPacket = new DatagramPacket(byteSentMess, 0, byteSentMess.length, inetSocketAddress);
            try {
                datagramSocket.send(sentPacket);
                byte[] packetBuff = new byte[datagramSocket.getReceiveBufferSize()];
                DatagramPacket recievedPacket = new DatagramPacket(packetBuff, packetBuff.length);
                datagramSocket.receive(recievedPacket);
                receivedMess = new String(recievedPacket.getData(), 0, recievedPacket.getLength());
                System.out.println(receivedMess);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private int tryMessage(String sentMess, String receivedMess) {
        System.out.println("->> " + receivedMess);
        if(receivedMess.equals("Hello, " + sentMess)){
            System.out.println(receivedMess);
            return 1;
        }
        return 0;
    }
}
