import java.io.IOException;

public class serveur {

    public static void main (String[] args) throws IOException {

        String nomFichier = args[0];
        String add = args[1];
        System.out.println(add);

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

        physique.setDestAdresseIP(add);
        physique.setDestPort(25678);
        physique.demarrerServeurThread();


    }
}
