/***
 * ClientIHM
 * IHM for TCP Chat system
 * Date: 13/10/2020
 * Authors: B4412
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
    
    public BoutonListener(ClientIHM fen, int numBouton) {
        fenetre = fen;
        numFenetre = numBouton; // 0 pour envoi, 1 pour suppression de l'historique
    }
    public BoutonListener(EchoClient par) {
        params = par;
        numFenetre = 2;
    }
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
