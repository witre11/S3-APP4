import java.io.*;
import java.net.*;
import java.util.*;

public class coucheTransport extends couche {

    private static coucheTransport instance;

    private coucheTransport(){}

    public static coucheTransport getInstance(){
        if (instance == null) instance = new coucheTransport();
        return instance;
    }

    @Override
    protected void receptionHaut(byte[] PDU) {
        System.out.println("Reception du PDU de la couche application");

        //code...
        System.out.println("Envoi du PDU vers la couche liaison");
        envoiBas(PDU);
    }

    @Override
    protected void receptionBas(byte[] PDU) {
        System.out.println("Reception du PDU de la couche liaison");
        System.out.println("Envoi du PDU vers la couche transport");
        envoiHaut(PDU);
    }





}
