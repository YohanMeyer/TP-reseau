/***
 * ThreadEcouteGroupe

 * Date: 15/10/2020
 * Authors: B4412
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ThreadEcouteGroupe
    extends Thread {
    
    private MulticastSocket multiSocket;
    private DatagramPacket messageRecu = null;
    //private final Integer taille = 1024;
    private Integer taille;
    private final byte buffer[];// = new byte[taille];
    //private Integer numeroClient;
    private boolean flagQuit = false;

    public ThreadEcouteGroupe (MulticastSocket multiSocket, InetAddress groupAddress, Integer tailleMax) {//, Integer numeroClient) {
        this.multiSocket = multiSocket;
        taille = tailleMax;
        buffer = new byte[taille];
        messageRecu = new DatagramPacket(buffer, buffer.length);
            
        try
        {
            multiSocket.joinGroup(groupAddress);
            System.out.println("Connection to the group successful");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:" + groupAddress.toString());
            System.exit(1);
        }
    }

    /**
  	* receives a message from server and displays it on client's console
  	* @param BufferedReader the input data stream 
  	**/
	public void run() {
        try{
            while (true && !flagQuit) {
                multiSocket.receive(messageRecu);
                
                String line = new String(messageRecu.getData(), 0, messageRecu.getLength());
                
                if(line.charAt(0) == ' ') {
                    line = line.substring(1);
                }
                
                System.out.println(line);
                messageRecu = new DatagramPacket(buffer, buffer.length);
            }
        } catch (Exception e) {
            System.err.println("Error in ThreadEcouteServer :" + e); 
        }
    }
    
    public void setFlagQuit(boolean quit) {
        flagQuit = quit;
    }
}