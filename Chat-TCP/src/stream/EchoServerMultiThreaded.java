/***
 * EchoServerMultiThreaded
 * Recoit une demande de connexion de la part d'un client et crée pour chaque client in thread qui ecoute les messages envoyés.
 * Date: 13/10/2020
 * @author B4412, Yoyo et Tintin
 */

package stream;

import java.io.*;
import java.net.*;

public class EchoServerMultiThreaded  {

 	/**
  	* main method
	* @param args server port
  	* 
  	**/
    public static void main (String args[]) {
        ServerSocket listenSocket;
        
        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }

        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            System.out.println("Server ready..."); 
            
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                
                ClientThread ct = new ClientThread(clientSocket);
                ct.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServerMultiThreaded:" + e);
        }
    }
}