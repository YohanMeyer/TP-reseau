package http.client;

import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class WebPing {
    public static void main(String[] args) {
  
        if (args.length != 2) {
        	System.err.println("Usage java WebPing <server host name> <server port number>");
        	return;
        }	
  
        String httpServerHost = args[0];
        int httpServerPort = Integer.parseInt(args[1]);

        try {
            InetAddress addr;      
            Socket sock = new Socket(httpServerHost, httpServerPort);
            addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(
                sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream());
            
            out.println("GET /index.html HTTP/1.1");
            out.println("Host: www.example.com");
            out.println("");
            
            String line = ".";
            System.out.println("lisons");
            
            while (line != null && !line.equals(""))
            {
                System.out.println("je lis");
                
                line = in.readLine();
                System.out.println(line);
                
            }
            sock.close();
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
            System.out.println(e);
        }
    }
}