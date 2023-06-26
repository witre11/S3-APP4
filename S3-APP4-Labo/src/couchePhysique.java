public class couchePhysique extends couche{
    private static couchePhysique instance;

    private couchePhysique(){}

    public static couchePhysique getInstance(){
        if (instance == null) instance = new couchePhysique();
        return instance;
    }



    @Override
    protected void receptionHaut(byte[] PDU) throws ErreurTransmissionException{
        System.out.println("Reception du PDU de la couche liaison");
        System.out.println("Devrait normalement envoyer vers serveur");
        receptionBas(PDU);
    }

    @Override
    protected void receptionBas(byte[] PDU)throws ErreurTransmissionException {
        System.out.println("Devrait normalement recevoir de client");
        System.out.println("Envoi du PDU vers la couche liaison");
        envoiHaut(PDU);
    }
}
