/***
 * ClientIHM
 * IHM for TCP Chat system
 * Organise les elements dans une fenetre
 * 
 * Date: 13/10/2020
 * @author B4412, Yoyo et Tintin
 * @see JFrame
 * @param container
 *          Container principal
 * @param top
 *          Container du haut
 * @param mid
 *          Container du milieu
 * @param bot
 *          Container du bas
 * @param saisieMessage
 *          Aire de texte pour ecrire le message
 * @param affichageArea
 *          Aire de texte pour afficher les messages
 * @param afficageChat
 *          Pour affiche le chat
 * @param boutonEnvoie
 *          pour envoyer un message
 * @param boutonHistorique
 *          pour mettre a jour l'historique
 * @param socOut      
 * @param echoSocket
 *          pour communiquer avec le serveur
 * @param pseudo pseudo de l'utilisateur
 * @param screenDimension dimensions de l'écran
 * @param jFrameWidth largeur de l'écran
 * @param jFrameHeight hauteur de l'écran
 */
 
package stream;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;

public class ClientIHM extends JFrame {
  
    private JPanel container = new JPanel();
    private JPanel top = new JPanel();
    private JPanel mid = new JPanel();
    private JPanel bot = new JPanel();

    private JTextArea saisieMessage;
    private JTextArea affichageArea;
    private JScrollPane affichageChat;
    
    private JButton boutonEnvoi;
    private JButton boutonHistorique;

    private PrintStream socOut;
    private Socket echoSocket;
    private String pseudo;

    private Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    private double jFrameWidth = screenDimension.width/1440.0;
    private double jFrameHeight = screenDimension.height/800.0; // permettent d'adapter la taille de tous les composants à l'écran

    //pour gérer la fermeture de la fenetre et du client
    /**
     * Constructeur de Client IHM
     * 
     * Initialise la fenetre.
     * @param sock la socket de connexion au serveur
     * @param pseudo le pseudo de l'utilisateur
     */
    public ClientIHM(Socket sock, String pseudo) {
        this.setTitle("Chat TCP");
        this.setSize((int)(jFrameWidth*1440), (int)(jFrameHeight*820));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        container.setLayout(new BorderLayout((int)jFrameWidth*15, (int)jFrameWidth*10));
        this.setContentPane(container);
        
        //this.getContentPane().setPreferredSize(new Dimension((int)jFrameWidth*1000, (int)jFrameHeight*600));
        
        echoSocket = sock;
        this.pseudo = pseudo;
        try {
            socOut = new PrintStream(echoSocket.getOutputStream());
        } catch (IOException e) {
           System.err.println("Couldn't get I/O for "
                              + "the connection to the server");
           System.exit(1);
        }
        
        socOut.println(pseudo);

        top.add(new JLabel("Projet de 4IF - Chat TCP"));

        bot.add(new JLabel("Message à envoyer : "));
        saisieMessage = new JTextArea();
        saisieMessage.setPreferredSize(new Dimension((int)jFrameWidth*400, (int)jFrameHeight*40));
        bot.add(saisieMessage);

        boutonEnvoi = new JButton("Envoyer");
        boutonEnvoi.addActionListener(new BoutonListener(this, 0));
        bot.add(boutonEnvoi);
        
        boutonHistorique = new JButton("Supprimer l'historique");
        boutonHistorique.addActionListener(new BoutonListener(this, 1));
        bot.add(boutonHistorique);
        
        affichageArea = new JTextArea(5, 20);
        affichageArea.setEditable(false);
        affichageChat = new JScrollPane(affichageArea);
        affichageChat.setPreferredSize(new Dimension((int)jFrameWidth*800, (int)jFrameHeight*300));
        mid.add(affichageChat);

        container.add(top, BorderLayout.NORTH);
        container.add(mid, BorderLayout.CENTER);
        container.add(bot, BorderLayout.SOUTH);
        
        this.pack();
        this.addWindowListener(new FenetreListener(this));

        this.setVisible(true);
    }
    
    /**
     * Methode qui envoie le message au serveur
     */
    public void envoyerMessage () {
        String message = saisieMessage.getText();
        if (!message.equals("$$delete$$")) {
            socOut.println(message);
        }
        saisieMessage.setText("");
    }
    
    /**
     * Met à jour les messages recus dans le pannel affichageArea
     * @param line message reçu du serveur
     */
    public void messageRecu (String line) {
        affichageArea.append(line+"\n");
    }
    
    /**
     * Supprime l'historique du serveur
     */
    public void supprimerHistorique () {
        socOut.println("$$delete$$");
    }
    
    /**
     * Se deconnecte du serveur et ferme la socket
     */
    public void quitter () {        
        socOut.println(".");
        try {
            socOut.close();
            echoSocket.close();
        } catch (IOException e) {
           System.err.println("Couldn't close socket");        
           this.dispose();
        }
        this.dispose();
    }
}
