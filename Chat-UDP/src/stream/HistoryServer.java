/***
 * HistoryServer

 * Date: 15/10/2020
 * Authors: B4412
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class HistoryServer 
    extends Thread {

    private MulticastSocket multiSocket = null;   
    private InetAddress groupAddress = null;  
    private Integer groupPort = null;
    private PrintWriter out = null;
    private ArrayList<String> chatHistory = new ArrayList<String>();
    private boolean flagQuit = false;
    
    public HistoryServer(String address, String port) {
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("chat-history.txt", true)));
            chatHistory = new ArrayList<String>(Files.readAllLines(Paths.get("chat-history.txt"), Charset.defaultCharset()));
            for(String s : chatHistory){
                System.out.println(s);
            }
        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
        
        try
        {
            groupAddress = InetAddress.getByName(address);
            groupPort = new Integer(port).intValue();
            multiSocket = new MulticastSocket(groupPort);            

            System.out.println("Connection to the group successful");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:" + groupAddress.toString());
            System.exit(1);
        }
    }
    
    public void run () {
        String line = null;
        final Integer taille = 1024; 
        final byte buffer[] = new byte[taille];
        DatagramPacket messageRecu = messageRecu = new DatagramPacket(buffer, buffer.length);
        
        try {
            while (!flagQuit) {
                System.out.println("Waiting for message...");
                
                multiSocket.receive(messageRecu);
                line = new String(messageRecu.getData(), 0, messageRecu.getLength());
                if(line.charAt(0) == ' ') {
                    line = line.substring(1);
                }
                System.out.println(line);
                chatHistory.add(line);
                out.println(line);
                
                messageRecu = new DatagramPacket(buffer, buffer.length);
            }
            out.close();
        } catch (IOException e) {
            System.err.println("Error while updating history file : " + e);
        } catch (Exception e) {
            System.err.println("Error in HistoryServer :" + e); 
        }
    }
    
    public void setFlagQuit(boolean quit) {
        flagQuit = quit;
    }
}