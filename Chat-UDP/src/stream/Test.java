/***
 * EchoClient
 * Example of a TCP client 
 * Date: 15/10/2020
 * Authors: B4412
 */
 
package stream;

import java.io.*;
import java.net.*;

public class Test {
 
  /**
  *  main method
  *  accepts a connection, waits for keyboard input and creates an instance of ThreadEcouteClient
  **/
  
    public static void main (String[] args) throws IOException {
        
        String msg = "Hello";
        InetAddress group = InetAddress.getByName("228.5.6.7");
        MulticastSocket s = new MulticastSocket(6789);
        s.joinGroup(group);
        DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
                                 group, 6789);
        s.send(hi);
        // get their responses!
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        s.receive(recv);
        System.out.println("received");

        // OK, I'm done talking - leave the group...
        s.leaveGroup(group);

        
    }
}