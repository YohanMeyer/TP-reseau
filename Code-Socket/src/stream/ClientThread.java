/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	//testts
	private static int nbreUsers = 0;
	private static boolean peutEnvoyer = true;
	private static PrintStream [] tabUsers = new PrintStream[100];
	private int numClient;
	
	ClientThread(Socket s) {
		this.clientSocket = s;
		this.numClient = nbreUsers;
		try{
			tabUsers[nbreUsers] = new PrintStream(clientSocket.getOutputStream());
			nbreUsers++;
			System.out.println("Affichage des "+nbreUsers+" sockets enregistrees :");
			for(int i = 0; i<nbreUsers; i++){
				System.out.println(i+"dans le tab : "+tabUsers[i]);
			}
		} catch(Exception e) {
			System.err.println("Error in EchoServer:" + e); 
		}
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try {
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
    		while (true) {
				String line = socIn.readLine();//bloquante
				if(!line.equals("null")){	//==>> pas très propre, plutot fermer la socket !!
					//socOut.println("Message \""+line+"\" bien recu par le serveur");
					System.out.println("User "+numClient+" a envoye un nouveau message : "+line);
					sendNewMessage(line);
				}
    		}
		} catch (Exception e) {
			System.err.println("Error in EchoServer:" + e); 
		}
	}
	
	private synchronized void sendNewMessage(String message)
	{
		System.out.println("Est-il possible d'envoyer un message ?"+peutEnvoyer);
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
		for(int i = 0; i<nbreUsers; i++){
			tabUsers[i].println("User"+numClient+": "+message);
		}
		peutEnvoyer = true;
		notify();
	

	}
  
  }

  
