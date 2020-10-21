///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.*;


/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {
    private HashMap<String, String> resources;
    /**
    * WebServer constructor.
    */
    protected void start() {
        ServerSocket s;
        //resources.add()

        System.out.println("Webserver starting up on port 80");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (;;) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());
                PrintWriter sortieFichier=null;

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.
                ArrayList<String> request = new ArrayList<String>();
                ArrayList<String> readFile = null;
                String firstLine = ".";
                String fileName = "";
                boolean first = true;
                boolean fichierExistant = true;
                String method = "";
                Integer contentLength = 0;
                char[] messageBody=null;
                
                while (firstLine != null)
                {            
                    firstLine = in.readLine();
                    System.out.println(first + "LIGNE : " + firstLine);
                    System.out.println("");
                    
                    /*if (response == null) {
                        break;
                    }*/
                    List<String> splitLine = Arrays.asList(firstLine.split(" "));
                    /*for (String st : splitLine) {
                        System.out.print(s);
                    }*/
                    
                    if ((method.equals("GET") || method.equals("HEAD")) && firstLine.equals("")) {
                        break;
                    } else if (method.equals("POST") || method.equals("PUT") && splitLine.get(0).equals("Content-Length:")) {
                        contentLength = Integer.parseInt(splitLine.get(1));
                    } else if( (method.equals("POST") || method.equals("PUT")) && firstLine.equals("")) {
                        System.out.println("on rentre dans le body, content length="+contentLength);
                        
                        char[] body = new char[contentLength];
                        in.read(body, 0, contentLength);
                        firstLine = new String(body);   
                        System.out.println(firstLine);
                        
                        if(method.equals("PUT")){//La structure de base ne fonctionne pas car dans le cas de put 
                                                 //il faut avoir fini de lire le fichier avant d'appeler respondToPut
                            System.out.println("Filename = "+fileName);
                            respondToPut(out, sortieFichier, firstLine, fichierExistant, fileName);
                        }
                             
                        break;
                    }
                    
                    if (first) {
                        System.out.println("Test des méthodes...");
                        
                        fileName = splitLine.get(1);
                        method = splitLine.get(0);
                        if (fileName.equals("/")) {
                            fileName += "accueil.html";
                        }
                        
                        try
                        {
                            readFile = new ArrayList<String>(Files.readAllLines(Paths.get("./http/server"+fileName), Charset.defaultCharset()));
                            System.out.println("file reading ok");   
                        }
                        catch(IOException e)
                        {
                            first = false;
                            respondError(out);
                            fichierExistant = false;
                            continue;
                        }
                        
                        switch (method) {
                            case "GET":
                                System.out.println("GET");
                                respondToGet(out, readFile);
                                
                                break;

                            case "POST":
                                System.out.println("POST");
                                respondToPost(out, readFile);  

                                break;

                            case "HEAD":
                                System.out.println("HEAD");
                                respondToHead(out);  
                                break;

                            case "PUT":
                                System.out.println("PUT");
                                //respondToPut(out, sortieFichier, readFile, fichierExistant, fileName);
                                break;

                            case "DELETE":
                                System.out.println("DELETE");
                                respondToDelete(out, fileName);
                                break;
                            case "OPTIONS":
                                System.out.println("OPTIONS");
                                respondToOptions(out);  
                                break;
                        }
                    } else if (first) {
                        System.out.println("Oh no");
                        respondError(out);
                    }
                    first = false;
                }
                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
    
    protected void respondToGet (PrintWriter out, ArrayList<String> fileContent) {
        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        for (String s : fileContent) {
            out.println(s);
        }   
        out.flush();
    }
    
    protected void respondToPost (PrintWriter out, ArrayList<String> fileContent) {
        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        for (String s : fileContent) {
            out.println(s);
        }   
        out.flush();
    }
    
    protected void respondToHead (PrintWriter out) {
        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        out.flush();
    }

    protected void respondToDelete(PrintWriter out, String fileName){
        boolean erreur = false;
        File file=null;

        //Suppression si fichier existant
        System.out.println("On tente de supprimer le ficher qui existe déjà");
        file = new File("http/server/"+fileName);
        try{
            if(file.delete()){
                System.out.println(file.getName() + " est supprimé.");
                System.out.println("Envoi d'une reponse...");
                out.println("HTTP/1.0 200 OK");//Indique que la suppression a bien été faite
                out.println("Server: Bot");
                // this blank line signals the end of the headers
                out.println("");
                out.flush();
            }else{
                System.out.println("Opération de suppression echouée");
                respondError(out);
            }
        } catch(Exception e) {
            System.err.println("An error occurred while deleting the file.");
            e.printStackTrace();
            respondError(out);
        }
        

    }

    protected void respondToPut(PrintWriter out, PrintWriter sortieFichier,
                                String fileContent, boolean fichierExistant, String fileName){
        boolean erreur = false;
        File file=null;

        //Suppression si fichier existant
        if(fichierExistant){
            System.out.println("On tente de supprimer le ficher qui existe déjà");
            file = new File("http/server/"+fileName);
            try{
                if(file.delete()){
                    System.out.println(file.getName() + " est supprimé.");
                }else{
                    System.out.println("Opération de suppression echouée");
                }
            } catch(Exception e) {
                System.err.println("An error occurred while deleting the file.");
                e.printStackTrace();
                erreur = true;
            }
        }

        //Ecriture du nouveau fichier
        try{
            sortieFichier = new PrintWriter(new BufferedWriter(new FileWriter("http/server/"+fileName, true)));
            sortieFichier.println(fileContent);
            System.out.println("Ecriture dans le nouveau fichier...");
        } catch (Exception e) {
            System.err.println("An error occurred while writing the new file in PUT.");
            e.printStackTrace();
            erreur = true;
        }
        sortieFichier.close();

        //Ecriture sur la sortie response Client
        System.out.println("erruer?:"+erreur);
        if(!erreur){
            System.out.println("Envoi d'une reponse...");
            if(fichierExistant){
                out.println("HTTP/1.0 204 No Content");//Indique que la modification a bien été faite
                out.println("Content-Location: "+fileName);
                out.println("Server: Bot");
            // this blank line signals the end of the headers
            out.println("");
            out.flush();
            } else {
                out.println("HTTP/1.0 201 Created");
                out.println("Content-Location: "+fileName);
                out.println("Server: Bot");
                // this blank line signals the end of the headers
                out.println("");
                out.flush();
            } 
        } else{
            respondError(out);
        }  
    }
    
    protected void respondToOptions (PrintWriter out) {
        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Access-Control-Allow-Headers: Content-Type, Accept, Access-Control-Allow-Origin, Authorization");
        out.println("Access-Control-Allow-Methods: OPTIONS, HEAD, DELETE, POST, GET");
        out.println("Access-Control-Allow-Origin: *");
        out.println("Access-Control-Max-Age: 21600");
        out.println("Allow: OPTIONS, HEAD, DELETE, POST, GET");
        out.println("Server: Bot");
        out.println("Content-Length: 0");
        out.println("Content-Type: text/html");


        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        out.flush();
    }
    
    protected void respondError (PrintWriter out) {
        // Send the response
        // Send the headers
        out.println("HTTP/1.0 400 BAD");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        out.println("BAD ERROR 400");
        out.flush();
    }
    

    /**
    * Start the application.
    * 
    * @param args
    *  Command line parameters are not used.
    */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
