package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * 
 * La classe ThreadEcouteGroupe permet d'ecouter les messages envoyes par 
 * les membres du groupe en meme temps que que la classe Client ecoute une entree sur la console.
 * 
 * @see Client
 * @see Thread 
 * @author B4412, Yoyo et Tintin
 * 
 */

public class ThreadEcouteGroupe
    extends Thread {
    
    private MulticastSocket multiSocket;
    private DatagramPacket messageRecu = null;
    private Integer taille;
    private final byte buffer[];// = new byte[taille];
    private boolean flagQuit = false;

    /**
     * Constructeur de la classe ThreEcouteGroupe
     * 
     * On crée un multisocket pour rejoindre le groupe.
     * @param multiSocket la socket de connexion au groupe
     * @param groupAddress l'addresse de connexion au groupe
     * @param tailleMax limite de taille de message
     */
    public ThreadEcouteGroupe (MulticastSocket multiSocket, InetAddress groupAddress, Integer tailleMax) {
        this.multiSocket = multiSocket;
        taille = tailleMax;
        buffer = new byte[taille];
        messageRecu = new DatagramPacket(buffer, buffer.length);
            
        try
        {
            multiSocket.joinGroup(groupAddress);
            System.out.println("Connection to the group successful");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:" + groupAddress.toString());
            System.exit(1);
        }
    }

    /**
     * Methode run
     * <p>
     * Recoit un message du client et l'affiche sur la console
      </p>
  	**/
	public void run() {
        try{
            while (true && !flagQuit) {
                multiSocket.receive(messageRecu);
                
                String line = new String(messageRecu.getData(), 0, messageRecu.getLength());
                
                if(line.charAt(0) == ' ') {
                    line = line.substring(1);
                }
                
                System.out.println(line);
                messageRecu = new DatagramPacket(buffer, buffer.length);
            }
        } catch (Exception e) {
            System.err.println("Error in ThreadEcouteServer :" + e); 
        }
    }
    
    /**
     * Met a jour le flagQuit (utilisé depuis la classe client)
     * @param quit
     *          Boolean pour indiquer la valeur du flag
     */
    public void setFlagQuit(boolean quit) {
        flagQuit = quit;
    }
}