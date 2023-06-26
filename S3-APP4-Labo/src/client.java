import java.io.IOException;
import java.util.Arrays;

public class client {


    public static void main (String[] args) throws IOException, ErreurTransmissionException {
        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }

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


        physique.setDestAdresseIP("localhost");
        physique.setDestPort(25678);
        application.envoiFichier(nomFichier);

    }
}
