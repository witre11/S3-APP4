import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;

public class coucheTransport extends couche {

    private static coucheTransport instance;

    private static final int size = 191;
    private static final int header_size= 9;
    private int next_seq_number;
    private boolean transmission_complete;
    private String filename;
    private byte[][] tpdu;
    private coucheTransport(){
        next_seq_number = 1;
        transmission_complete = false;
    }

    public static coucheTransport getInstance(){
        if (instance == null) instance = new coucheTransport();
        return instance;
    }

    @Override
    protected void receptionHaut(byte[] PDU) {
        int nb_fragmentation = (int) Math.ceil((double) PDU.length / size);
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
        System.out.println(Arrays.toString(tpdu));


    }

    @Override
    protected void receptionBas(byte[] PDU) {
        char debutTrame = (char) PDU[0];
        int numeroSequence = Integer.parseInt(new String(Arrays.copyOfRange(PDU, 1, 5)));
        int taillePDU = Integer.parseInt(new String(Arrays.copyOfRange(PDU, 5, 9)));
        byte[] contenuFichier = Arrays.copyOfRange(PDU, 9, PDU.length);

        envoiHaut(contenuFichier);
    }



}
