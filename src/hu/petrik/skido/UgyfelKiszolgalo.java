package hu.petrik.skido;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class UgyfelKiszolgalo implements Runnable {
    private HashMap<String, Idojaras> elorejelzesek;
    private HashMap<String, Idojaras> holnapiHavasak;
    private HashMap<String, Idojaras> tiznelNagyobbMaximumMaiHomersekletuek;
    private Socket kapcsolat;

    public UgyfelKiszolgalo(Socket kapcsolat) {
        elorejelzesek = new HashMap<>();
        holnapiHavasak = new HashMap<>();
        tiznelNagyobbMaximumMaiHomersekletuek = new HashMap<>();
        this.kapcsolat = kapcsolat;
        beolvasas();
    }

    @Override
    public void run() {
        try {
            System.out.println("Kapcsolat létrejött: " + kapcsolat.getInetAddress().getHostName());
            DataInputStream fromClient = new DataInputStream(kapcsolat.getInputStream());
            DataOutputStream toClient = new DataOutputStream((kapcsolat.getOutputStream()));
            int menu;
            do {
                menu = fromClient.readInt();
                switch (menu) {
                    case 1:
                        toClient.writeUTF("Előrejelzések száma: " + getElorejelzesekSzama());
                        break;
                    case 2:
                        toClient.writeUTF("25 van a hőmérsékletek között: " + vanIlyenHomerseklet(25));
                        break;
                    case 3:
                        toClient.writeUTF("Holnapi havas megyék: " + holnapiHavasok());
                        break;
                    case 4:
                        toClient.writeUTF("Mai tíznél nagyobb maximum hőmérsékletek és szöveges előrejelzéseik: \n" + tiznelNagyobbMaiMaximumHomersekletuAdatok());
                        break;
                    case 5:
                        toClient.writeUTF("Holnapi minimum hőmérsékletek: \n" + osszesMinimumHolnapiHomerseklet());
                        break;
                    case 6:
                        toClient.writeUTF("Holnapi maximum hőmérsékletek összege: " + holnapiMaximumHomersekletekOsszege());
                        break;
                    }
                    toClient.flush();
                } while (menu != -1);
            }

        catch (SocketException e) {
            System.out.println("Kapcsolat lezárult: " + kapcsolat.getInetAddress().getHostName());
        } catch (IOException e) {
            System.out.println(e);
        }


    }



    public void beolvasas() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("weather.txt"));
            br.readLine();
            String sor = br.readLine();
            while (sor != null) {
                Idojaras i = new Idojaras(sor);
                String megye = i.getMegye();
                elorejelzesek.put(megye, i);
                sor = br.readLine();

                if (i.getHolnapi().getSzovegesElorejelzes().equals("Snow")) {
                    holnapiHavasak.put(megye, i);
                }

                if (i.getMai().getMax() > 10) {
                    tiznelNagyobbMaximumMaiHomersekletuek.put(megye, i);
                }

            }

            for (Map.Entry<String, Idojaras> entry: elorejelzesek.entrySet()) {
                System.out.println(entry.getValue());
            }

        }
        catch (FileNotFoundException ex) {
            System.out.println(ex);

        }
        catch (IOException ex) {
            System.out.println(ex);
        }

    }

    public int getElorejelzesekSzama() {
        return elorejelzesek.entrySet().size();
    }

    public String vanIlyenHomerseklet(int homerseklet) {
        String szoveg = "";
        int i = 0;
        while (i < elorejelzesek.entrySet().size() && !elorejelzesek.containsValue(elorejelzesek.values().contains(homerseklet))) {
            i++;
        }
        if (i < elorejelzesek.entrySet().size()) {
            szoveg += "Igen";

        }
        else {
            szoveg += "Nem";
        }
        return szoveg;


    }


    public String holnapiHavasok() {
        String szoveg = "";
        for (Map.Entry<String, Idojaras> entry: holnapiHavasak.entrySet()) {
            szoveg += entry.getKey() + ",";
        }
        return szoveg;
    }

    public String tiznelNagyobbMaiMaximumHomersekletuAdatok() {
        String szoveg = "";
        for (Map.Entry<String, Idojaras> entry: tiznelNagyobbMaximumMaiHomersekletuek.entrySet()) {
            szoveg += entry.getValue().getMai().getMax() + " " + entry.getValue().getMai().getSzovegesElorejelzes() + "\n";
        }
        return szoveg;
    }

    public String osszesMinimumHolnapiHomerseklet() {
        String szoveg = "";
        for (Map.Entry<String, Idojaras> entry: elorejelzesek.entrySet()) {
            szoveg += entry.getValue().getHolnapi().getMin() + ";";
        }
        return szoveg;
    }

    public int holnapiMaximumHomersekletekOsszege() {
        int osszeg = 0;
        for (Map.Entry<String, Idojaras> entry: elorejelzesek.entrySet()) {
            osszeg += entry.getValue().getHolnapi().getMax();
        }
        return osszeg;
    }


}