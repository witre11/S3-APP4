/* Utilise modele:
    Client:
        -Application
        -Transport
        -Liaison de donnees
        -Physique
    Serveur:
        -Physique
        -Liaison de donnees
        -Transport
        -Application
*/

//Classe qui utilise le concept de "chain of responsability"
public abstract class couche {
    protected String fileName;
    private couche currentPlusHaut;
    private couche currentPlusBas;
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    //Monter la couche
    public void setCurrentPlusHaut(couche nextPlusHaut){
        currentPlusHaut = nextPlusHaut;
    }

    //Descendre la couche
    public void setCurrentPlusBas(couche nextPlusBas){
        currentPlusBas = nextPlusBas;
    }

    protected abstract void receptionHaut(byte[] PDU) throws ErreurTransmissionException;

    protected void envoiBas(byte[] PDU) throws ErreurTransmissionException {
        currentPlusBas.receptionHaut(PDU);
    }

    protected abstract void receptionBas(byte[] PDU)throws ErreurTransmissionException;

    //appel de l'envoi vers le haut
    protected void envoiHaut(byte[] PDU) throws ErreurTransmissionException {
        currentPlusHaut.receptionBas(PDU);
    }



}
