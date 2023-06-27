import java.io.IOException;
import java.util.Arrays;

public class client {


    public static void main (String[] args) throws IOException, ErreurTransmissionException, InterruptedException {


        String nomFichier = args[0];

        coucheApplication application;
        coucheTransport transport;
        coucheLiaison liaison;
        couchePhysique physique; //A changer pour sockets

        application = coucheApplication.getInstance();
        transport = coucheTransport.getInstance();
        liaison = coucheLiaison.getInstance();
        physique = couchePhysique.getInstance();

        application.setCurrentPlusBas(transport);
        transport.setCurrentPlusHaut(application);
        transport.setCurrentPlusBas(liaison);
        liaison.setCurrentPlusHaut(transport);
        liaison.setCurrentPlusBas(physique);
        physique.setCurrentPlusHaut(liaison);

        physique.setErreur(true);

        physique.setDestAdresseIP("localhost");
        physique.setDestPort(25555);
        physique.setDestPortRecep(26666);
        physique.demarrerServeurThread();
        application.envoiFichier(nomFichier);


        System.exit(0);
    }
}
