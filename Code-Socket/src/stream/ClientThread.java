/***
 * EchoClient
 * Example of a TCP client 
 * Date: 13/10/2020
 * Authors: B4412
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	private static boolean canSendMessage = true;
	private static ArrayList<PrintStream> usersOutput = new ArrayList<PrintStream>();
	private int numClient;
	private String pseudoClient;
	
	public ClientThread (Socket socket) {
		this.clientSocket = socket;
		this.numClient = usersOutput.size();
		this.pseudoClient = null;
		
		try {
			usersOutput.add(new PrintStream(clientSocket.getOutputStream()));
			System.out.println("Affichage des " + usersOutput.size() + " sockets enregistrees :");
			for(PrintStream ps : usersOutput){
				System.out.println(ps);
			}
		} catch(Exception e) {
			System.err.println("Error in ClientThread:" + e); 
		}
	}

 	/**
  	* receives a message from client and sends an echo to the client
  	* @param socket the client socket
  	**/
	
	public void run () {
    	  try {
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
    		while (true) {
				String line = socIn.readLine();
				if (pseudoClient == null) {
					pseudoClient = line;
				} else if (!line.equals("null")){	//==>> pas très propre, plutot fermer la socket !!
					System.out.println(pseudoClient + " (num :" + numClient + ") a envoye un nouveau message : " + line);
					sendNewMessage(line);
				}
    		}
		} catch (Exception e) {
			System.err.println("Error in ClientThread:" + e); 
		}
	}
	
	private synchronized void sendNewMessage (String message)
	{
		while (!canSendMessage); // un autre utilisateur est en train d'envoyer un message

		canSendMessage = false;
		System.out.println("Envoi du message à tous les clients");
		int nbUsers = usersOutput.size();
		for (int i = 0; i < nbUsers ; i++) {
			if (i != numClient) {
				usersOutput.get(i).println(pseudoClient + " : " + message);
			}
		}
		canSendMessage = true;
	}
}

  
