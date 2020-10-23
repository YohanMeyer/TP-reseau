/**
 * Web Server qui gère des reuêtes HTTP
 * 
 * @author B4412, Yoyo et Tintin
 */

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class WebServer {
    /**
     * Crée un serverSocket.
     * Quand une connexion arrive, on regarde le type de requete (Head, put, get, post...) 
     * et on appelle la méthode appropriée
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 80");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(80);
        } catch (Exception e) {
            System.err.println("Error: " + e);
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
                OutputStream out = remote.getOutputStream();

                byte[] readFile;
                String request = ".";
                String fileName = "";
                boolean first = true;
                boolean fichierExistant = true;
                String method = "";
                Integer contentLength = 0;
                char[] messageBody=null;
                String fileType = "";
                
                while (request != null) // reading a request
                {            
                    request = in.readLine();
                    
                    List<String> splitLine = Arrays.asList(request.split(" "));
                    
                    if(fileName.equals("/A_coffee_please")){
                        respond418(out);
                        break;
                    } else if ((method.equals("GET") || method.equals("HEAD") || method.equals("DELETE") || method.equals("OPTIONS")) && request.equals("")) {
                        break;
                    } else if ((method.equals("POST") || method.equals("PUT")) && splitLine.get(0).equals("Content-Length:")) {
                        contentLength = Integer.parseInt(splitLine.get(1));

                    } else if( (method.equals("POST") || method.equals("PUT")) && request.equals("")) {
                        
                        char[] body = new char[contentLength];
                        in.read(body, 0, contentLength);
                        request = new String(body);
                           
                        
                        if (method.equals("PUT")) { 
                            respondToPut(out, request, fichierExistant, fileName);
                        } else if(method.equals("POST")) {
                            System.out.println(request);
                        }
                             
                        break;
                    }
                    
                    if (first) {
                        
                        fileName = splitLine.get(1);
                        method = splitLine.get(0);
                        if (fileName.equals("/")) {
                            fileName += "accueil.html";
                        }
                        
                        try {
                            readFile = Files.readAllBytes(Paths.get("./http/server"+fileName));
                            
                            if (fileName.contains(".css")) {
                                fileType = "text/css";
                            } else if (fileName.contains(".js")) {
                                fileType = "text/javascript";
                            } else if (fileName.contains(".txt")) {
                                fileType = "text/plain";
                            } else if (fileName.contains(".html")) {
                                fileType = "text/html";
                            } else if (fileName.contains(".jpg") || fileName.contains(".jpeg")) {
                                fileType = "image/jpeg";
                            } else if(fileName.contains(".gif")) {
                                fileType = "image/gif";
                            } else if(fileName.contains(".mp3")) {
                                fileType = "audio/mpeg";
                            } else if(fileName.contains(".mp4")) {
                                fileType = "video/mp4";
                            } else if(fileName.contains(".mkv")) {
                                fileType = "video/webm";
                            }
                        } catch(IOException e) {
                            first = false;
                            fichierExistant = false;
                            if (!method.equals("PUT")) {
                                respond404(out);
                            }
                            continue;
                        }
                        
                        switch (method) {
                            case "GET":
                                System.out.println("GET");
                                respondToGet(out, readFile, fileType);
                                
                                break;

                            case "POST":
                                System.out.println("POST");
                                respondToPost(out, readFile, fileType);  

                                break;

                            case "HEAD":
                                System.out.println("HEAD");
                                respondToHead(out);
                                
                                break;

                            case "PUT":
                                System.out.println("PUT");
                                
                                break;

                            case "DELETE":
                                System.out.println("DELETE");
                                respondToDelete(out, fileName);
                                
                                break;
                            case "OPTIONS":
                                System.out.println("OPTIONS");
                                respondToOptions(out, fileType);  
                                
                                break;
                        }
                    }
                    
                    first = false;
                }
                remote.close();
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }
        }
    }
    
    /**
     * Renvoie le fichier demandé
     * @param out
     * @param fileContent
     * @param fileType
     */
    protected void respondToGet (OutputStream out, byte[] fileContent, String fileType) {
        // Send the response
        // Send the headers
        try {
            byte[] header = ("HTTP/1.0 200 OK\nContent-Type: " + fileType + "\nContent-Length:" + fileContent.length + "\nServer: Bot\n\r\n").getBytes("UTF-8");
            byte[] message = new byte[fileContent.length + header.length];
            System.arraycopy(header, 0, message, 0, header.length);
            System.arraycopy(fileContent, 0, message, header.length, fileContent.length);

            out.write(message);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding to GET request: " + e);
        }
    }

    /**
     * Renvoie le fichier demandé
     * @param out
     * @param fileContent
     * @param fileType
     */
    protected void respondToPost (OutputStream out, byte[] fileContent, String fileType) {
        // Send the response
        // Send the headers
        try {
            byte[] header = ("HTTP/1.0 200 OK\nContent-Type: " + fileType + "\nContent-Length:" + fileContent.length + "\nServer: Bot\n\r\n").getBytes("UTF-8");
            byte[] message = new byte[fileContent.length + header.length];
            System.arraycopy(header, 0, message, 0, header.length);
            System.arraycopy(fileContent, 0, message, header.length, fileContent.length);

            out.write(message);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding to POST request: " + e);
        }
    }
    
    /**
     * Renvoie une entete
     * @param out
     */
    protected void respondToHead (OutputStream out) {
        // Send the response
        // Send the headers
        try {
            byte[] header = ("HTTP/1.0 200 OK\nContent-Type:text/plain\nServer: Bot\n\r\n").getBytes("UTF-8");

            out.write(header);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding to HEAD request: " + e);
        }
    }

    /**
     * Supprime le fichier demandé
     * @param out
     * @param fileName
     */
    protected void respondToDelete (OutputStream out, String fileName) {
        File file=null;

        //Suppression si fichier existant
        System.out.println("On tente de supprimer le ficher qui existe déjà");
        file = new File("http/server/"+fileName);
        try {
            if (file.delete()) {
                System.out.println(file.getName() + " est supprimé.");
                System.out.println("Envoi d'une reponse...");
                
                byte[] header = ("HTTP/1.0 200 OK\nServer: Bot\n\r\n").getBytes("UTF-8");

                out.write(header);
                out.flush();
            } else {
                System.err.println("Opération de suppression echouée");
                respond500(out);
            }
        } catch(Exception e) {
            System.err.println("An error occurred while deleting the file.");
            e.printStackTrace();
            respond500(out);
        }
    }

    /**
     * Crée ou met à jour le fichier en parametre
     * @param out
     * @param fileName
     * @param fichierExistant
     * @param fileName
     */
    protected void respondToPut (OutputStream out, String fileContent, boolean fichierExistant, String fileName) {
        File file = null;

        //Suppression si fichier existant
        if (fichierExistant) {
            System.out.println("On tente de supprimer le ficher qui existe déjà");
            file = new File("http/server/"+fileName);
            try {
                if (file.delete()) {
                    System.out.println(file.getName() + " est supprimé.");
                } else {
                    System.err.println("Opération de suppression echouée");
                }
            } catch(Exception e) {
                System.err.println("An error occurred while deleting the file.");
                e.printStackTrace();
                respond500(out);
                return;
            }
        }

        //Ecriture du nouveau fichier
        try {
            FileOutputStream outFile = new FileOutputStream("http/server/" + fileName);
            outFile.write(fileContent.getBytes());
            outFile.close();
            System.out.println("Ecriture dans le nouveau fichier...");
        } catch (Exception e) {
            System.err.println("An error occurred while writing the new file in PUT.");
            e.printStackTrace();
            respond500(out);
            return;
        }

        //Ecriture sur la sortie response Client
        
        System.out.println("Envoi d'une reponse...");
        try {
            if (fichierExistant) { // indiquer que la modification a bien été faite
                byte[] header = ("HTTP/1.0 204 No Content\nContent-Location: " + fileName + "\nServer: Bot\n\r\n").getBytes("UTF-8");

                out.write(header);
                out.flush();
            } else { //indiquer que le fichier a bien été créé
                
                byte[] header = ("HTTP/1.0 201 Created\nContent-Location: " + fileName + "\nServer: Bot\n\r\n").getBytes("UTF-8");

                out.write(header);
                out.flush();
            } 
        } catch(IOException e)
        {
            System.err.println("An error occurred while responding to PUT request.");
            e.printStackTrace();
            respond500(out);
            return;
        }
    }
    
    /**
     * Renvoie ce que peut gérer le serveur */    
    protected void respondToOptions (OutputStream out, String fileType) {
        try {
            byte[] header = ("HTTP/1.0 200 OK\nAccess-Control-Allow-Headers: Content-Type, Accept, Access-Control-Allow-Origin, Authorization\nAccess-Control-Allow-Methods: OPTIONS, HEAD, DELETE, POST, GET\nAccess-Control-Allow-Origin: *\nAllow: OPTIONS, HEAD, DELETE, POST, GET\nContent-Type: " + fileType + "\nContent-Length:0\nServer: Bot\n\r\n").getBytes("UTF-8");

            out.write(header);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding to OPTIONS request: " + e);
        }
    }
    
    /**
     * Envoie message d'erreur 
     */
    protected void respond400 (OutputStream out) {
        // Send the response
        // Send the headers
        try {
            byte[] header = ("HTTP/1.0 400 BAD\nContent-Type:text/plain\nServer: Bot\n\r\nBAD ERROR 400\n").getBytes("UTF-8");

            out.write(header);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding a 400 error: " + e);
        }
    }

    /**
     * Envoie message d'erreur not found
     */
    protected void respond404 (OutputStream out) {
        // Send the response
        // Send the headers
        try {
            byte[] header = ("HTTP/1.0 404 Not found\nContent-Type:text/plain\nServer: Bot\n\r\nNot found - ERROR 404\n").getBytes("UTF-8");

            out.write(header);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding a 404 error: " + e);
        }
    }

    /**
     * Envoie message d'erreur I'm a teapot
     */
    protected void respond418 (OutputStream out) {
        // Send the response
        // Send the headers
        try {
            byte[] header = ("HTTP/1.0 418 I'm a teapot\nContent-Type:text/plain\nServer: Bot\n\r\nI'm a teapot - ERROR 418\n").getBytes("UTF-8");

            out.write(header);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding a 418 error: " + e);
        }
    }

    /**
     * Envoie message d'erreur interne
     */
    protected void respond500 (OutputStream out) {
        // Send the response
        // Send the headers
        try {
            byte[] header = ("HTTP/1.0 500 Internal Server Error\nContent-Type:text/plain\nServer: Bot\n\r\nInternal Server Error - ERROR 500\n").getBytes("UTF-8");

            out.write(header);
            out.flush();

        } catch (IOException e) {            
            System.err.println("Error while responding a 500 error: " + e);
        }
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
