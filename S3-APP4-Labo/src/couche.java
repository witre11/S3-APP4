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

    private couche currentPlusHaut;
    private couche currentPlusBas;

    //Monter la couche
    public void setCurrentPlusHaut(couche nextPlusHaut){
        currentPlusHaut = nextPlusHaut;
    }

    //Descendre la couche
    public void setCurrentPlusBas(couche nextPlusBas){
        currentPlusBas = nextPlusBas;
    }

    protected abstract void receptionHaut(byte[] PDU);

    protected void envoiBas(byte[] PDU){
        currentPlusBas.receptionHaut(PDU);
    }

    protected abstract void receptionBas(byte[] PDU);

    //appel de l'envoi vers le haut
    protected void envoiHaut(byte[] PDU){
        currentPlusHaut.receptionBas(PDU);
    }



}
