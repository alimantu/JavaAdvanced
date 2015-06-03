package ru.ifmo.ctddev.salinskii.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Катя on 21.05.2015.
 */
public class HelloUPDClient implements HelloClient{

    @Override
    public void start(String host, int port, String prefix, int requests, int threads) {
        try {
            final InetAddress inetAddress = InetAddress.getByName(host);
            for(int i = 0; i < threads; i++){
                new ClientThread(i, prefix, inetAddress, port, requests);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
