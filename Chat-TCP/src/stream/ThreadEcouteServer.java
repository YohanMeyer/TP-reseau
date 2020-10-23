/***
 * ThreadEcouteServer 
 * Thread côté Client qui ecoute les messages du serveur
 * Date: 13/10/2020
 * 
 * @author B4412, Yoyo et Tintin
 * @see Thread
 * 
 * @param socIn flux d'entrée sur lequel on lit les messages
 * @param fenetre fenetre de l''ihm
 * 
 * 
 */

package stream;

import java.io.*;

public class ThreadEcouteServer
    extends Thread {
    
    private BufferedReader socIn;
    private ClientIHM fenetre;

    /**
     * Constructeur de ThreadEcouteServer
     * @param in flux d'entrée
     * @param fenetre fenetre principale
     */
    public ThreadEcouteServer (BufferedReader in, ClientIHM fenetre) {
        this.socIn = in;
        this.fenetre = fenetre;
    }

    /**
     * Lit le flux d'entrée et l'affiche dans la fenetre
     */
	public void run() {
        try{
            while (true) {
                String line = socIn.readLine();
                if (line.equals(".")) {
                    return;
                }
                fenetre.messageRecu(line);
            }
        } catch (Exception e) {
            System.out.println("Closing socket"); 
        }        
    }
}