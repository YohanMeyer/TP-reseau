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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	private static boolean canSendMessage = true;
	private static ArrayList<PrintStream> usersOutput = new ArrayList<PrintStream>();
	private int numClient;
	private String pseudoClient;
	private static ArrayList<String> chatHistory = new ArrayList<String>();
	private PrintWriter out = null;
	
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
		
		try { // récupération et envoi de l'historique
			out = new PrintWriter(new BufferedWriter(new FileWriter("chat-history.txt", true)));
			
            chatHistory = new ArrayList<String>(Files.readAllLines(Paths.get("chat-history.txt"), Charset.defaultCharset()));
            
			for (String s : chatHistory) {
				usersOutput.get(numClient).println(s);
			}
			
			out.close();
        } catch (IOException e) {
            System.err.println("An error occurred while reading chat history.");
            e.printStackTrace();
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
				if (line == null) {
					socIn.close();
				} else if (pseudoClient == null) {
					pseudoClient = line;
					sendNewMessage("a rejoint le salon.");					
				} else if(line.equals("$$delete$$")){
					deleteChatHistory();
				} else if (line.equals(".")) {
					sendNewMessage("a quitté le salon.");
					usersOutput.get(numClient).println(".");
					return;
				} else {
					System.out.println(pseudoClient + " (num :" + numClient + ") a envoye un nouveau message : " + line);
					sendNewMessage(line);
				}
    		}
		} catch (Exception e) {
			System.err.println("Error in ClientThread:" + e); 
		}
	}
	
	private synchronized void sendNewMessage (String message) {		
		while (!canSendMessage){ // un autre utilisateur est en train d'envoyer un message
			System.out.print("");
		}
		canSendMessage = false;
		
		System.out.println("num"+numClient+" : Envoi du message à tous les clients");
		int nbUsers = usersOutput.size();
		message = pseudoClient + " : " + message;
		for (int i = 0; i < nbUsers ; i++) {
			usersOutput.get(i).println(message);
		}
		
		updateChatHistory(message);
		canSendMessage = true;
	}
	
	private void updateChatHistory (String line) {
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter("chat-history.txt", true)));
			out.println(line);
		} catch (IOException e) {
            System.err.println("An error occurred while updating chat history.");
            e.printStackTrace();
        }
		out.close();
	}

	private void deleteChatHistory () {
		File file = new File("chat-history.txt");

		try {
			if (file.delete()) {
				System.out.println(file.getName() + " est supprimé.");
			} else {
				System.out.println("Opération de suppression echouée");
			}
		} catch (Exception e) {
            System.err.println("An error occurred while deleting chat history.");
            e.printStackTrace();
        }
	}
}
