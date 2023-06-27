import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;

public class coucheTransport extends couche {
    private int previous = -1;
    private int erreur =0;
    private static coucheTransport instance;
    StringBuilder accumulatedData = new StringBuilder();
    private static final int size = 191;
    private static final int header_size= 9;
    private int next_seq_number;
    private String filename;
    private byte[][] tpdu;
    private coucheTransport(){
        next_seq_number = 1;
    }
    private int nb_fragmentation;
    private int maxVal=0;
    private int comptePaquets=0;

    public static coucheTransport getInstance(){
        if (instance == null) instance = new coucheTransport();
        return instance;
    }

    @Override
    protected void receptionHaut(byte[] PDU) throws ErreurTransmissionException{
        nb_fragmentation = (int) Math.ceil((double) PDU.length / size);
        char debut_trame = 'd';
        tpdu = new byte[nb_fragmentation][200];
        for(int i = 0;  i < nb_fragmentation;i++)
        {
            int taille = size;
            if(i == nb_fragmentation -1)
            {
                taille = PDU.length % size;
                debut_trame = 'f';
            }

            arraycopy(PDU, i * size, tpdu[i], header_size, taille);

            if(i != 0 && i != nb_fragmentation -1){debut_trame = 'n';}
            //debut, fin ou normal
            tpdu[i][0] = (byte) debut_trame;
            String sequenceString = String.format("%04d", next_seq_number);
            byte[] sequenceBytes = sequenceString.getBytes(StandardCharsets.US_ASCII);
            arraycopy(sequenceBytes, 0, tpdu[i], 1, 4);

            String lengthString = String.format("%04d", taille);
            byte[] lengthBytes = lengthString.getBytes(StandardCharsets.US_ASCII);
            arraycopy(lengthBytes, 0, tpdu[i], 5,  4);
            next_seq_number++;
            envoiBas(tpdu[i]);
        }
        //System.out.println(Arrays.toString(tpdu));


    }

    @Override
    protected void receptionBas(byte[] PDU) throws ErreurTransmissionException{
        char debutTrame = (char) PDU[0];
        int numeroSequence = Integer.parseInt(new String(Arrays.copyOfRange(PDU, 1, 5)));
        System.out.println(numeroSequence);
        int taillePDU = Integer.parseInt(new String(Arrays.copyOfRange(PDU, 5, 9)));

        if (previous != -1 && numeroSequence != previous + 1) {

            //creer la demande de retransmission r-seq-taille-retransmission
            byte[] requestPDU = new byte[200];
            requestPDU[0] = 'r';
            String sequenceString = String.format("%04d", previous);
            byte[] sequenceBytes = sequenceString.getBytes(StandardCharsets.US_ASCII);
            arraycopy(sequenceBytes, 0, requestPDU, 1, 4);

            String lengthString = String.format("%04d", 200);
            byte[] lengthBytes = lengthString.getBytes(StandardCharsets.US_ASCII);
            arraycopy(lengthBytes, 0, requestPDU, 5,  4);
            String requestMessage = "retransmission";
            byte[] requestMessageBytes = requestMessage.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(requestMessageBytes, 0, requestPDU, 9, requestMessageBytes.length);
            //System.out.print(requestMessageBytes);
            envoiBas(requestPDU);
        }
        previous = numeroSequence;
        if(debutTrame == 'd')
        {
            envoiBas(sendAck(numeroSequence));

            byte[] contenuFichier = Arrays.copyOfRange(PDU, 9, PDU.length);
            String message = new String(contenuFichier, StandardCharsets.US_ASCII);
            accumulatedData.append(message);
        }
        if(debutTrame == 'r')
        {
            erreur++;
            if(erreur >= 3)
            {
                throw new ErreurTransmissionException("3 erreurs");
            }
            envoiBas(tpdu[previous-1]);
        }

        if (debutTrame == 'f'){
            envoiBas(sendAck(numeroSequence));

            byte[] contenuFichier = Arrays.copyOfRange(PDU, 9, PDU.length);
            String message = new String(contenuFichier, StandardCharsets.US_ASCII);
            accumulatedData.append(message);
            String allData = accumulatedData.toString().trim();
            byte[] data = allData.getBytes(StandardCharsets.US_ASCII);
            envoiHaut(data);
            accumulatedData.setLength(0);

        }
        if (debutTrame == 'n')
        {
            envoiBas(sendAck(numeroSequence));

            byte[] contenuFichier = Arrays.copyOfRange(PDU, 9, PDU.length);
            String message = new String(contenuFichier, StandardCharsets.US_ASCII);
            accumulatedData.append(message);
        }


        if(debutTrame == 'a')
        {
            System.out.println("Accusé "+numeroSequence);
        }

    }
    private byte[] sendAck(int seq)
    {
        // Envoi de l'accusé de réception
        byte[] ackPDU = new byte[200];
        ackPDU[0] = 'a';
        String ackMessage = "succes";
        byte[] ackMessageBytes = ackMessage.getBytes(StandardCharsets.US_ASCII);
        String sequenceString = String.format("%04d", seq);
        byte[] sequenceBytes = sequenceString.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(sequenceBytes, 0, ackPDU, 1, 4);

        String lengthString = String.format("%04d", 200);
        byte[] lengthBytes = lengthString.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(lengthBytes, 0, ackPDU, 5,  4);
        System.arraycopy(ackMessageBytes, 0, ackPDU, 9, ackMessageBytes.length);
        return ackPDU;
    }
    private void resetTransportLayer() {
        erreur =0;
        accumulatedData.setLength(0);
        previous =-1;
        next_seq_number =1;
        for (int i = 0; i < tpdu.length; i++) {
            byte[] subArray = tpdu[i];
            if (subArray != null) {
                Arrays.fill(subArray, (byte) 0);
            }
        }
    }
}

