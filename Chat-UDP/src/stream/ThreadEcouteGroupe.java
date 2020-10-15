/***
 * ThreadEcouteGroupe
 * A UDP client thread for multicast listening
 * Date: 15/10/2020
 * Authors: B4412
 */

package stream;

import java.io.*;
import java.net.*;

public class ThreadEcouteGroupe
    extends Thread {
    
    private MulticastSocket multiSocket;
    private DatagramPacket messageRecu = null;
    private final Integer taille = 1024; 
    private final byte buffer[] = new byte[taille];
    //private Integer numeroClient;
    private boolean flag = false;

    public ThreadEcouteGroupe (MulticastSocket multiSocket, InetAddress groupAddress) {//, Integer numeroClient) {
        this.multiSocket = multiSocket;
        //this.numeroClient = numeroClient;
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
            while (true && !flag) {
                multiSocket.receive(messageRecu);
                
                String line = new String(messageRecu.getData(), 0, messageRecu.getLength());
                
                /*Integer index = 0;
                String numeroClientRecuString = new String();
                while (Character.isDigit(line.charAt(index))) {
                    numeroClientRecuString += line.charAt(index);
                    index++;
                }
                Integer numeroClientRecu = new Integer(numeroClientRecuString).intValue();
                
                System.out.println(numeroClientRecu);
                */
                
                System.out.println(line);

                messageRecu = new DatagramPacket(buffer, buffer.length);
            }
        } catch (Exception e) {
            System.err.println("Error in ThreadEcouteServer :" + e); 
        }
    }
    
    public void setFlag(boolean quit) {
        flag = quit;
    }
}