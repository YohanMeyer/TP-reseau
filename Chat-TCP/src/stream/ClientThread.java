/***
 * Client Thread
 * La classe ecoute un client depuis le cote serveur.
 * La classe ecoute les messages envoyes par les clients et les stocke. Ensuite les messages recus sont 
 * transférs à tous les autres clients.
 * Un thread est crée pour chaque client.
 * Date: 13/10/2020
 * @author B4412, Yoyo et Tintin
 * @see Thread
 * 
 * @param clientSocket
 * 				la socket pour communiquer avec le client associé
 * @param canSendMessage
 *				flag pour eviter que plusieurs messages soient envoyés en même temsp
 * @param usersOutput
 * 				liste de tous les users stockées en static pour envoyer le message arrivant à tous les clients
 * @param numClient
 * 				identifie un client
 * @param pseudoClient
 * @param chatHistory
 * 				permet de récupérer tout l'historique
 * @param out
 * 				permet d'écrire l'historique sur un fichier .txt
 * 
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
	
	/**
	 * Constructeur de la classe ClientThread.
	 * @param socket
	 * 			représnete a socket qui permet de communiquer avec le client associé à ce thread.
	 */
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
  	* recoit un message du client et transfère à tous les client. Appelle un méthode pour sauvegarder l'historique.
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
	
	/**
	 * Envoie le nouveau message recu par le thread à tous les autres clients. 
	 * Envoie de manière synchronisée tel que deux messages ne puissent pas être envoyés en même temps.
	 * @param mssage
	 * 			le message à envoyer à tout le monde
	 */
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
	
	/**
	 * Enregistre les nouveaux messages dans un fichier .txt
	 * @param line
	 * 			le message a enregistrer
	 */
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

	/**
	 * Supprime le fichier .txt pour remettre à jour l'historique
	 */
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
