import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.Date;


public class couchePhysique extends couche {

    private int destPort;
    protected BufferedReader in = null;
    private DatagramSocket socket;
    private InetAddress destAdresseIP = null;


    private static couchePhysique instance;

    private couchePhysique() {
    }

    public static couchePhysique getInstance() {
        if (instance == null) instance = new couchePhysique();
        return instance;
    }

    public void setDestAdresseIP(String adresseIP) throws UnknownHostException {
        this.destAdresseIP = InetAddress.getByName(adresseIP);
    }


    public void setDestPort(int port) {
        this.destPort = port;
    }

    @Override
    protected void receptionHaut(byte[] PDU) {
        System.out.println("Reception du PDU de la couche liaison");

        try {
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatagramPacket packet = new DatagramPacket(PDU, PDU.length, destAdresseIP, destPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        socket.close();

        //DatagramPacket paquet = new DatagramPacket(PDU);

        System.out.println("Devrait normalement envoyer vers serveur");
    }

    @Override
    protected void receptionBas(byte[] PDU) throws ErreurTransmissionException {
        System.out.println("Devrait normalement recevoir de client");
        System.out.println("Envoi du PDU vers la couche liaison");
        envoiHaut(PDU);
    }

    public void demarrerServeurThread() throws IOException {
        Thread thread =new receptionServeurThread(this.destPort);
        thread.start();
    }

    private class receptionServeurThread extends Thread {
        protected DatagramSocket socketS;

        public receptionServeurThread(int port) throws SocketException {
            this.socketS = new DatagramSocket(port);
        }

        public void run(){

            while (true) {

                try {
                    byte[] buf = new byte[204];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socketS.receive(packet);
                    System.out.println(buf);
                    receptionBas(buf);

                } catch (IOException | ErreurTransmissionException e) {
                    e.printStackTrace();
                    socketS.close();
                }
            }
        }
    }
}



