/***
 * EchoClient
 * Example of a TCP client 
 * Date: 15/10/2020
 * Authors: B4412
 */
 
package stream;

import java.io.*;
import java.net.*;

public class Client {
 
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

        if (args.length != 2) {
          System.out.println("Usage: java Client <group host> <group port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
            groupAddress = InetAddress.getByName("228.5.6.7");
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
            /*if (pseudo.matches(".*\\d.*")) {
                System.out.println("Votre pseudo ne peut pas contenir de chiffres.");
                pseudo = null;
            }*/
        }
        
        //Creation thread d'ecoute du groupe 
        ThreadEcouteGroupe groupListener = new ThreadEcouteGroupe(multiSocket, groupAddress);//, numeroClient);
        groupListener.start();
        
        line = pseudo + " vient de rejoindre le chat.";
        message = new DatagramPacket(line.getBytes(), line.length(), groupAddress, groupPort);
        multiSocket.send(message);

        //Ecoute le clavier pour envoyer le message au serveur
        while (true) {
        	line = stdIn.readLine();
        	if (line.equals(".")) 
            {
                groupListener.setFlag(true);
                line = pseudo + " vient de quitter le chat.";
                message = new DatagramPacket(line.getBytes(), line.length(), groupAddress, groupPort);
            	multiSocket.send(message);
                break;
            }
            line = pseudo + " : " + line;
            message = new DatagramPacket(line.getBytes(), line.length(), groupAddress, groupPort);
        	multiSocket.send(message);
        }
        
        multiSocket.leaveGroup(groupAddress);
        multiSocket.close();
    }
}


