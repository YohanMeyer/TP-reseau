/***
 * EchoClient
 * Example of a TCP client 
 * Date: 13/10/2020
 * Authors: B4412
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	private static int nbUsers = 0;
	private static boolean peutEnvoyer = true;
	private static PrintStream [] usersOutput = new PrintStream[100];
	private int numClient;
	
	public ClientThread (Socket socket) {
		this.clientSocket = socket;
		this.numClient = nbUsers;
		try{
			usersOutput[nbUsers] = new PrintStream(clientSocket.getOutputStream());
			nbUsers++;
			System.out.println("Affichage des "+nbUsers+" sockets enregistrees :");
			for(int i = 0; i<nbUsers; i++){
				System.out.println(i+"dans le tab : "+usersOutput[i]);
			}
		} catch(Exception e) {
			System.err.println("Error in ClientThread:" + e); 
		}
	}

 	/**
  	* receives a message from client and sends an echo to the client
  	* @param socket the client socket
  	**/
	
	public void run() {
    	  try {
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
    		while (true) {
				String line = socIn.readLine();
				if(!line.equals("null")){	//==>> pas très propre, plutot fermer la socket !!
					//socOut.println("Message \""+line+"\" bien recu par le serveur");
					System.out.println("User "+numClient+" a envoye un nouveau message : "+line);
					sendNewMessage(line);
				}
    		}
		} catch (Exception e) {
			System.err.println("Error in ClientThread:" + e); 
		}
	}
	
	private synchronized void sendNewMessage(String message)
	{
		while(!peutEnvoyer){
			try { 
				System.out.println("Appel à wait!");
				wait();
			} catch (InterruptedException e)  {
				Thread.currentThread().interrupt(); 
				System.err.println("Thread interrupted : "+ e);
			}
		}

		peutEnvoyer = false;
		System.out.println("Envoi du message à tous les clients");
		try {sleep(2000);}catch(Exception e){e.printStackTrace();}
		for(int i = 0; i<nbUsers; i++){
			if (i != numClient) {
				usersOutput[i].println("User"+numClient+": "+message);
			}
		}
		peutEnvoyer = true;
		notify();
	

	}
}

  
