import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;  // Import the File class
public class coucheLiaison extends couche{

    private static coucheLiaison instance;

    private coucheLiaison(){}

    public static coucheLiaison getInstance(){
        if (instance == null) instance = new coucheLiaison();
        return instance;
    }


    @Override
    protected void receptionHaut(byte[] PDU) {
        System.out.println("Reception du PDU de la couche transport");
        System.out.println("Envoi du PDU vers la couche physique");
        envoiBas(PDU);
    }

    @Override
    protected void receptionBas(byte[] PDU) {
        System.out.println("Reception du PDU de la couche physique");
        System.out.println("Envoi du PDU vers la couche transport");
        envoiHaut(PDU);
    }
}
