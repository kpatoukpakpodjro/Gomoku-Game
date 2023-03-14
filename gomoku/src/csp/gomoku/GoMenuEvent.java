package csp.gomoku;
  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GoMenuEvent implements ActionListener {
    private MainGUI goFrame;
    GomokuPosition gof;
    Game gm;
    public GoMenuEvent(MainGUI go) {
        goFrame = go;
        this.gof=goFrame.gof;
        gm = new Game(gof);
    }
    
    public void actionPerformed(ActionEvent e) {
         String command = e.getActionCommand();
        if(command.equals("Sauvegarder")){
         	goFrame.enregistrerParams(gm.nbreAide);     // lancer l'enregistrement des paramètres du jeu
         	String id=goFrame.getNom()+""+goFrame.getAdversaire();
        	gof.panelSet(id);					// lancer le sauvegarde de l'historique des points joués
            }
        
        if(command.equals("Restart")){     // recommencer un nouveau
         	goFrame.setFrame( gof.player1, gof.player2);    // lancer la destruction de la fenêtre 
         }													// actuelle puis l'instanciation d'une nouvelle
        if(command.equals("Mes parties jouées")){     // recommencer un nouveau
         	goFrame.AffichageList( gof.player1);    // lancer la destruction de la fenêtre actuelle puis l'instanciation d'une nouvelle
         }
        
        if(command.equals("besoin_d_aide?")){
         	gm.recevoirAide();               // appel à la méthode d'aide de la classe Game
        }
        gof.getGUI().repaint();
    }
}