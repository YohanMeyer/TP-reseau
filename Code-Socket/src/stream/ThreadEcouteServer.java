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

    public ThreadEcouteServer (BufferedReader in) {
        this.socIn = in;
    }

    /**
  	* receives a message from server and displays it on client's console
  	* @param BufferedReader the input data stream 
  	**/
	public void run() {
        try{
            while (true) {
                String line = socIn.readLine();
                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error in ThreadEcouteServer :" + e); 
        }
    }
}