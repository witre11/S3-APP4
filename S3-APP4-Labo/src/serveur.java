import java.io.IOException;

public class serveur {

    public static void main (String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }


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
        physique.setDestPort(26666);
        physique.setDestPortRecep(25555);
        physique.demarrerServeurThread();


        while(true) {
            int command = System.in.read();
            switch (command) {
                case 113://q
                    System.exit(0);
                    break;
            }
        }

    }
}
