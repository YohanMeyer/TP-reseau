package stream;

import java.io.*;
import java.net.*;

public class ThreadEcouteServer
    extends Thread {
    
        private Socket serverSocket;

        ThreadEcouteServer(Socket s) {
            this.serverSocket = s;
        }

    /**
  	* receives a request from server then dispays it on client's console
  	* @param serverSocket the server socket
  	**/
	public void run() {
        try{
            BufferedReader socIn = null;
            socIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            
            while (true) {
                String line = socIn.readLine();//bloquante
                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error in ThreadEcouteServer :" + e); 
        }
    }


}