import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.Date;


public class couchePhysique extends couche {

    private int destPort;
    public int compteur = 0;
    private static boolean erreur;
    private int portThread;
    protected receptionServeurThread thread;

    private DatagramSocket socket;
    private InetAddress destAdresseIP = null;

    public static boolean erreurbit = false;


    //private int currentPort;

    public void setErreur(boolean val){
        erreurbit = val;
    }

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

    public void setDestPortRecep(int port){ this.portThread = port;}

    @Override
    protected void receptionHaut(byte[] PDU) {
        System.out.println("Reception du PDU de la couche liaison");

        try {
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Etat de l'erreur: "+ erreurbit);
        if (erreurbit && compteur == 5) {
            PDU[5] &= ~(1<< compteur);
        }
        compteur++;
        DatagramPacket packet = new DatagramPacket(PDU, PDU.length, destAdresseIP, destPort);

        try {
            socket.send(packet);
            Thread.sleep(3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        socket.close();

        //System.out.println("Devrait normalement envoyer vers serveur");
    }

    @Override
    protected void receptionBas(byte[] PDU) throws ErreurTransmissionException {
        //System.out.println("Devrait normalement recevoir de client");
        //System.out.println("Envoi du PDU vers la couche liaison");
        envoiHaut(PDU);
    }

    public void demarrerServeurThread() throws IOException {
        Thread thread =new receptionServeurThread(this.portThread, this);
        thread.start();
    }

    private class receptionServeurThread extends Thread {
        protected DatagramSocket socketS;
        private couchePhysique coucheCourante;

        public receptionServeurThread(int port, couchePhysique couche) throws SocketException {
            this.socketS = new DatagramSocket(port);
            this.coucheCourante = couche;
        }

        public void run(){

            while (true) {

                try {
                    byte[] buf = new byte[204];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socketS.receive(packet);
                    //System.out.println(buf);
                    coucheCourante.receptionBas(buf);


                } catch (IOException | ErreurTransmissionException e) {
                    e.printStackTrace();
                    socketS.close();
                }
            }
        }
    }
}



