package hu.petrik.skido;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Szerver {
    public static void main(String[] args) {
        System.out.println("Szerver indítása...");
        ExecutorService exe = Executors.newCachedThreadPool();
        try {
            ServerSocket socket = new ServerSocket(9999);
            Socket connection = socket.accept();
            DataInputStream fromClient = new DataInputStream(connection.getInputStream());
            DataOutputStream toClient = new DataOutputStream((connection.getOutputStream()));
            InetAddress client = connection.getInetAddress();
            UgyfelKiszolgalo u = new UgyfelKiszolgalo(connection);
            exe.submit(u);
        } catch (IOException e) {
            System.err.println(e);
        }


    }
}
