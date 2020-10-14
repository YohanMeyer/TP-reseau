/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors: Regaud Quentin and Meyer Yohan
 */
package stream;

import java.io.*;
import java.net.*;



public class EchoClient {
 
  /**
  *  main method
  *  accepts a connection, receives a message from server then sends an echo to the server
  **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;
        

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());
	          socIn = new BufferedReader(
	    		          new InputStreamReader(echoSocket.getInputStream()));    
	          socOut= new PrintStream(echoSocket.getOutputStream());
	          stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
        
        System.out.println("Connection to the server successful");
        
        //Création thread d'écoute du server 
        ThreadEcouteServer tes = new ThreadEcouteServer(echoSocket);
        tes.start();

        //Ecoute le clavier pour envoyer message au serveur
        String line;
        while (true) {
        	line=stdIn.readLine();
        	if (line.equals(".")) break;
        	socOut.println(line);//On envoie ce que l'on vient de taper
        }
      socOut.close();
      socIn.close();
      stdIn.close();
      echoSocket.close();
    }
}


