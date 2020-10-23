/***
 * EchoClient
 * Etabli la connexion entre le client et le serveur et crée un thread pour ecouter les messages du serveur.
 * Date: 13/10/2020
 * 
 * @author B4412, Yoyo et Tintin
 * @see ThreadEcouteServeur
 * 
 * @param fenetreParams
 * @param jtfAdresse
 * @param jtfPort
 * @param jtfPseudo
 */
 
package stream;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;


public class EchoClient {
 
  /**
  *  main method
  *  accepts a connection, waits for keyboard input and creates an instance of ThreadEcouteServer
  **/
  
    private JFrame fenetreParams;
    private JTextField jtfAddresse;
    private JTextField jtfPort;
    private JTextField jtfPseudo;
    
    /**
     * Constructeur de EchoClient. 
     * Initialise l'IHM
     */
    public EchoClient () {
        
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        double jFrameWidth = screenDimension.width/1440.0;
        double jFrameHeight = screenDimension.height/800.0; // permettent d'adapter la taille de tous les composants à l'écran
        
        fenetreParams = new JFrame();
        JPanel container = new JPanel();
        JPanel top = new JPanel();
        JPanel mid = new JPanel();
        JPanel bot = new JPanel();
        
        fenetreParams.setTitle("Paramètres");
        fenetreParams.setSize((int)(jFrameWidth*400), (int)(jFrameHeight*300));
        fenetreParams.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetreParams.setLocationRelativeTo(null);
        fenetreParams.setResizable(false);

        container.setLayout(new BorderLayout((int)jFrameWidth*15, (int)jFrameWidth*10));
        fenetreParams.setContentPane(container);
        
        
        JLabel labelAdresse = new JLabel("Addresse IP du serveur : ");
        JLabel labelPort = new JLabel("Port de communication : ");
        JLabel labelPseudo = new JLabel("Votre pseudo : ");
        
        jtfAddresse = new JTextField();
        jtfAddresse.setPreferredSize(new Dimension((int)jFrameWidth*100, (int)jFrameHeight*26));
        jtfPort = new JTextField();
        jtfPort.setPreferredSize(new Dimension((int)jFrameWidth*50, (int)jFrameHeight*26));
        jtfPseudo = new JTextField();
        jtfPseudo.setPreferredSize(new Dimension((int)jFrameWidth*200, (int)jFrameHeight*26));

        JButton boutonOK = new JButton("OK");
        boutonOK.addActionListener(new BoutonListener(this));

        top.add(labelAdresse);
        top.add(jtfAddresse);
        top.add(labelPort);
        top.add(jtfPort);
        
        mid.add(labelPseudo);
        mid.add(jtfPseudo);
        
        bot.add(boutonOK);
        
        container.add(top, BorderLayout.NORTH);
        container.add(mid, BorderLayout.CENTER);
        container.add(bot, BorderLayout.SOUTH);
        
        fenetreParams.pack();

        fenetreParams.setVisible(true);
        
    }
    
    /**
     * recupère les données pour l'ihm
     */
    public void recupererDonnees() {
        String addresse = jtfAddresse.getText();
        String port = jtfPort.getText();
        String pseudo = jtfPseudo.getText();
        this.init(addresse, Integer.parseInt(port), pseudo);
        fenetreParams.dispose();
    }
    
    /**
     * Etabli la connexion client-serveur. Crée un thread pour ecouter  le serveur
     * @param addresse l'addresse de connexion au serveur
     * @param port le port de connexion
     * @param pseudo le pseudo de l'utilisateur
     */
    public void init(String addresse, Integer port, String pseudo) {
        Socket echoSocket = null;
        BufferedReader socIn = null;
        
        try {
      	    // creation socket ==> connexion
      	    echoSocket = new Socket(addresse, port);
            socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));  
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + addresse);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:" + addresse);
            System.exit(1);
        }
        
        System.out.println("Connection to the server successful");
        
        ClientIHM fenetre = new ClientIHM(echoSocket, pseudo);
        
        //Creation thread d'ecoute du server 
        ThreadEcouteServer serverListener = new ThreadEcouteServer(socIn, fenetre);
        serverListener.start();     
    }

    public static void main (String[] args) {
        EchoClient client = new EchoClient();
    }
}





