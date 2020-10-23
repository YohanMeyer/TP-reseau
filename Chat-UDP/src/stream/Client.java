package stream;

import java.io.*;
import java.net.*;

/**
 * La classe CLient inscris un client, cree un thread d'ecoute du groupe puis envoi le texte ecrit par le client a tout le groupe. 
 * 
 * @see ThreadEcouteGroupe
 * @author B4412, Yoyo et Tintin
 */


public class Client {
  
/**
 * Main method
 * 
 * <p>
 * On commence par créer un multicast socket avec la bonne adresse ip et le bon port pour rejoindre le groupe.
 * On demande aussi un pseudo pour communiquer plus facilement avec le groupe.
 * </p>
 * <p>
 * Ensuite, on crée un thread ThreadEcouteGroupe qui va ecouter les messages emis par les membres du groupe.
 * </p<p>
 * Quand le client ceut quiiter le groupe en ecrivant le caractere "." on quitte le groupe et on ferme la socket.
 * </p>
 * 
 * @param args 
 *          On attend un tableau de taille 2 : l'adresse et le port du groupe
 * @throws  IOException
 */
    public static void main (String[] args) throws IOException {
        
        MulticastSocket multiSocket = null;   
        InetAddress groupAddress = null;  
        Integer groupPort = null;
        DatagramPacket message = null;
        String line = null;
        String pseudo = null;
        BufferedReader stdIn = null;
        final Integer taille = 1024;

        if (args.length != 2) {
          System.out.println("Usage: java Client <group host> <group port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
            groupAddress = InetAddress.getByName(args[0]);//228.5.6.7
            groupPort = new Integer(args[1]).intValue();
            
      	    multiSocket = new MulticastSocket(groupPort);
            
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        }
                
        while (pseudo == null || pseudo.isEmpty() || pseudo.length() > 20) { 
            System.out.println("Veuillez entrer votre pseudo (moins de 20 car.) :");
            pseudo = stdIn.readLine();
            if (pseudo.charAt(0) == ' ') {
                System.out.println("Votre pseudo ne peut pas commencer par un espace.");
                pseudo = null;
            }
        }
        
        //Creation thread d'ecoute du groupe 
        ThreadEcouteGroupe groupListener = new ThreadEcouteGroupe(multiSocket, groupAddress, taille);
        
        line = " " + pseudo + " vient de rejoindre le chat.";
        message = new DatagramPacket(line.getBytes(), line.length(), groupAddress, groupPort);
        multiSocket.send(message);
        try { // laisser le temps au premier client de recevoir le message
            Thread.sleep(100); 
        } catch (Exception e) {
            System.err.println("Error in Client" + e);
        }
        groupListener.start();

        //Ecoute le clavier pour envoyer le message au serveur
        while (true) {
        	line = stdIn.readLine();
        	if (line.equals(".")) {
                groupListener.setFlagQuit(true);
                line = pseudo + " vient de quitter le chat.";
                message = new DatagramPacket(line.getBytes(), line.length(), groupAddress, groupPort);
            	multiSocket.send(message);
                break;
            }
            line = pseudo + " : " + line;
            if(line.length() <= taille){
                message = new DatagramPacket(line.getBytes(), line.length(), groupAddress, groupPort);
                multiSocket.send(message);
            }
            else{
                System.out.println("*****\nLe message ne peut pas etre envoye car il contient trop de caracteres ! ");
                System.out.println("Veuillez envoyer votre message en plusieurs fois.\n***** ");
            }
        }
        
        multiSocket.leaveGroup(groupAddress);
        multiSocket.close();
    }
}


