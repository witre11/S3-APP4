import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;  // Import the File class

public class coucheApplication extends couche {

    private static coucheApplication instance;
    private coucheApplication(){}
    public static coucheApplication getInstance(){
        if (instance == null) instance = new coucheApplication();
        return instance;
    }
    //Impossible..., commence l'envois seulement
    @Override
    public void receptionHaut(byte[] PDU) {}

    @Override
    public void receptionBas(byte[] PDU){
        System.out.println("Reception du fichier de la couche transport");
        String nomFichier = "fichierRecu.txt";

        try {

            String cheminFichier = new File(" ").getAbsolutePath();
            File monFichier = new File(cheminFichier+nomFichier);

            if (monFichier.exists()) monFichier.delete();

            if (monFichier.createNewFile()) {
                System.out.println("Fichier creer: " + monFichier.getName());
            } else {
                System.out.println("Fichier existe");
            }

            Files.write(Path.of(monFichier.getPath()), PDU);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void envoiFichier(String a) throws IOException {

        File fichier = new File(a);
        String nomFichier = fichier.getName();
        //System.out.println("le nom du fichier à envoyer est:" + nomFichier);

        Path cheminFichier = fichier.toPath();
        //System.out.println("le chemin du fichier est:" + cheminFichier);

        long longueurFichier = fichier.length();
        byte[] aPDU = Files.readAllBytes(cheminFichier);

        System.out.println("Envoi du PDU vers la couche transport");
        envoiBas(aPDU);
    }
}
