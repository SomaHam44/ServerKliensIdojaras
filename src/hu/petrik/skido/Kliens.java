package hu.petrik.skido;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Kliens {
    public static void main(String[] args) {
        try {
            Socket kapcsolat = new Socket("localhost", 9999);
            DataInputStream fromServer = new DataInputStream(kapcsolat.getInputStream());
            DataOutputStream toServer = new DataOutputStream(kapcsolat.getOutputStream());
            Scanner sc = new Scanner(System.in);
            System.out.print("Kérek egy számot: ");
            int menu;
            do {
                menu = sc.nextInt();
                toServer.writeInt(menu);
                toServer.flush();
                String eredmeny = fromServer.readUTF();
                System.out.println("Szerver válasza: " + eredmeny);

            } while (menu != 5);

        }

        catch (IOException e) {
            System.err.println(e);
        }

    }
}
