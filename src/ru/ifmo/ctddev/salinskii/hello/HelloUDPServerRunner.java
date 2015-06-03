package ru.ifmo.ctddev.salinskii.hello;

/**
 * Created by Катя on 28.05.2015.
 */
public class HelloUDPServerRunner {
    public static void main(String[] args){
        HelloUDPServer server = new HelloUDPServer();
        for(int i = 0; i < args.length / 2; i ++){
            server.start(Integer.getInteger(args[i]), Integer.getInteger(args[i + 1]));
        }
        try {
            Thread.currentThread().wait(1000000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
