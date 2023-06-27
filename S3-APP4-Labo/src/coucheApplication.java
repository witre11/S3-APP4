import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;  // Import the File class

import static java.lang.System.arraycopy;

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

        //System.out.println("Reception du fichier de la couche transport");
        String nomFichier = new String(Arrays.copyOfRange(PDU,0,191)).trim();
        byte[] data = Arrays.copyOfRange(PDU, 191,PDU.length);
        try {

            String cheminFichier = new File(" ").getAbsolutePath();
            File monFichier = new File(cheminFichier+" "+nomFichier);

            if (monFichier.exists()) monFichier.delete();

            if (monFichier.createNewFile()) {
                System.out.println("Fichier creer: " + monFichier.getName());
            } else {
                System.out.println("Fichier existe");
            }

            Files.write(Path.of(monFichier.getPath()), data);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void envoiFichier(String a) throws IOException, ErreurTransmissionException, InterruptedException {

        File fichier = new File(a);
        Path cheminFichier = fichier.toPath();
        byte[] contenuFichier = Files.readAllBytes(cheminFichier);

        byte[] aPDU = new byte[191+contenuFichier.length];
        byte[] filename = fichier.getName().getBytes();
        arraycopy(filename, 0, aPDU, 0, filename.length);
        arraycopy(contenuFichier, 0, aPDU, 191, contenuFichier.length);

        System.out.println("Envoi du PDU vers la couche transport");
        envoiBas(aPDU);
        Thread.sleep(1000);
    }
}
