/***
 * EchoClient
 * Example of a TCP client 
 * Date: 13/10/2020
 * Authors: B4412
 */

package stream;

import java.io.*;

public class ThreadEcouteServer
    extends Thread {
    
    private BufferedReader socIn;
    private ClientIHM fenetre;

    public ThreadEcouteServer (BufferedReader in, ClientIHM fenetre) {
        this.socIn = in;
        this.fenetre = fenetre;
    }

    /**
  	* receives a message from server and displays it on client's console
  	* @param BufferedReader the input data stream 
  	**/
	public void run() {
        try{
            while (true) {
                String line = socIn.readLine();
                if (line.equals(".")) {
                    return;
                }
                fenetre.messageRecu(line);
            }
        } catch (Exception e) {
            System.out.println("Closing socket"); 
        }        
    }
}