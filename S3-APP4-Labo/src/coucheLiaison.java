import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;  // Import the File class
import java.util.zip.CRC32;

public class coucheLiaison extends couche{

    private static coucheLiaison instance;
    private int erreursCRC = 0;
    private int packetsReceived =0;
    private int packetsSent = 0;
    private PrintWriter logWriter;

    public coucheLiaison(){
        try {
            logWriter = new PrintWriter(new FileWriter("liaisonDeDonnees.log", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ecrirelog(String operation) {
        LocalDateTime heureAjd = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = heureAjd.format(format);

        String log = formattedTimestamp + " - " + operation;
        logWriter.println(log);
        logWriter.flush();
    }

    public static coucheLiaison getInstance(){
        if (instance == null) instance = new coucheLiaison();
        return instance;
    }


    @Override
    protected void receptionHaut(byte[] PDU) throws ErreurTransmissionException{
        ecrirelog("Reception du PDU de la couche transport");
        byte[] CRCpdu = new byte[204];
        CRC32 crc = new CRC32();
        crc.update(PDU);
        long valeurCRC = crc.getValue();
        //Permet de stocker la valeur du crc dans un byte
        byte[] tamponCRC = new byte[] {
                (byte) (valeurCRC >> 24),
                (byte) (valeurCRC >> 16),
                (byte) (valeurCRC >> 8),
                (byte) valeurCRC};
        //System.out.println(valeurCRC);
        System.arraycopy(tamponCRC, 0, CRCpdu, 0, 4);
        System.arraycopy(PDU, 0, CRCpdu, 4, PDU.length);
        packetsSent++;
        ecrirelog("Envoi du PDU vers la couche physique, paquet transmis : "+ packetsSent);
        envoiBas(CRCpdu);
    }

    @Override
    protected void receptionBas(byte[] PDU) throws ErreurTransmissionException{
        ecrirelog("Reception du PDU de la couche physique");
        byte[] CRCtrame = Arrays.copyOfRange(PDU, 0, 4);
        byte[] data = Arrays.copyOfRange(PDU, 4, 204);
        CRC32 crc = new CRC32();
        crc.update(data);
        long dataCRC = crc.getValue();
        ByteBuffer receivedCRCBuffer = ByteBuffer.wrap(CRCtrame);
        long  valeurCRC= receivedCRCBuffer.getInt() & 0xFFFFFFFFL;
        packetsReceived++;
        if(dataCRC != valeurCRC) {
            ecrirelog("Erreur de CRC : "+ erreursCRC);
            erreursCRC++;
        }

        ecrirelog("Envoi du PDU vers la couche transport, paquets reçus : " + packetsReceived);
        envoiHaut(data);
    }
}
