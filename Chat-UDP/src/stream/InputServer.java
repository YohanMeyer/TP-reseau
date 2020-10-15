/***
 * InputServer
 
 * Date: 15/10/2020
 * Authors: B4412
 */
 
package stream;

import java.io.*;

public class InputServer {

  /**
  *  main method
  *  accepts a connection, waits for keyboard input and creates an instance of HistoryServer
  **/
  
    public static void main (String[] args) throws IOException {
        
        
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        HistoryServer history = new HistoryServer(args[0], args[1]);
        history.start();
        
        String line;

        //Ecoute le clavier pour envoyer le message au serveur
        while (true) {
        	line = stdIn.readLine();
        	if (line.equals(".") || line.equals("exit")) {                
                history.setFlagQuit(true);
                break;
            }
        }        
    }
}


