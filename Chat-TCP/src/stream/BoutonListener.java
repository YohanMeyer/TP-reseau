/***
 * ClientIHM
 * 
 * IHM for TCP Chat system
 * Ecoute les bes boutons et déclenche les  actions associees.
 * 
 * Date: 13/10/2020
 * @author B4412, Yoyo et Tintin
 * @see ActionListener
 * 
 * @param fenetre
 *          La fenetre de l'IHM
 * @param params
 *          represente le client qui envoie des messages
 * @param numFenetre
 *          défini le type de d'action qui devra etre execute
 */
 
package stream;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class BoutonListener implements ActionListener {

    private ClientIHM fenetre;
    private EchoClient params;
    int numFenetre;
    
    /**
     * Constructeur de la classe BoutonListener a partir des attributs fen et numBouton
     * @param fen
     * @param numBouton
     */
    public BoutonListener(ClientIHM fen, int numBouton) {
        fenetre = fen;
        numFenetre = numBouton; // 0 pour envoi, 1 pour suppression de l'historique
    }
    /**
     * Constructeur de la classe BoutonListener a partir d'un attribut EchClient
     * @param par
     */
    public BoutonListener(EchoClient par) {
        params = par;
        numFenetre = 2;
    }
    /**
     * Execute l'action en fonction du bouton d'ecoute
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        switch(numFenetre) {
            case 0:
                fenetre.envoyerMessage();
                break;
            case 1:
                fenetre.supprimerHistorique();
                break;
            case 2:
                params.recupererDonnees();
                break;
        }
    }
}
