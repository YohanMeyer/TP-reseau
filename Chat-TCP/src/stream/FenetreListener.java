/***
 * FenetreListener
 * Listener for ClientIHM 
 * Date: 13/10/2020
 * Authors: B4412
 */
 
package stream;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class FenetreListener implements WindowListener {
    private ClientIHM fenetre;
    
    public FenetreListener (ClientIHM fenetre) {
        this.fenetre = fenetre;
    }
    
    public void windowClosing(WindowEvent e) {
        fenetre.quitter();
    }

    public void windowClosed(WindowEvent e) {}

    public void windowOpened(WindowEvent e) {}

    public void windowIconified(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowActivated(WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}

    public void windowGainedFocus(WindowEvent e) {}

    public void windowLostFocus(WindowEvent e) {}

    public void windowStateChanged(WindowEvent e) {}
}