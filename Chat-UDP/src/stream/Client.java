/***
 * Client

 * Date: 15/10/2020
 * Authors: B4412
 */
 
package stream;

import java.io.*;
import java.net.*;

public class Client {

    private static int test = 0;
  /**
  *  main method
  *  accepts a connection, waits for keyboard input and creates an instance of ThreadEcouteClient
  **/
  
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
        ThreadEcouteGroupe groupListener = new ThreadEcouteGroupe(multiSocket, groupAddress, taille);//, numeroClient);
        
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
            //System.out.println("Le message a une taille de : "+line.length());
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


