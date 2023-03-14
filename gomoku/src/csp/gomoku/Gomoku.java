package csp.gomoku;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Scanner;

public class Gomoku {
	
	final int width = 685;
	// Crér une grille de 19x19 
	GomokuPosition board = new GomokuPosition(width, 19);
    
     // Instancier la classe MainGUI qui est le menu de démarrage.
 		
 	final MainGUI gui = new MainGUI(width,width, "GoMoku",board);
	public Gomoku() {
		super();
		
  	 	// Attacher le composant GUI de GomokuPosition au frame principal
		gui.attachBoard(board.getGUI());
   		
  	}
	public void athen() {
		gui.authentification();
		// lancer le listener du bouton d'authentification
		gui.add1(new ActionListener(){
			
			public void actionPerformed(ActionEvent arg0) {
				gui.window.dispose();
				gui.setNom(gui.joueur1.getText());
				gui.setAdversaire(gui.joueur2.getText());
				lancer();
		
		}} );
	}
	public  void lancer() {
		gui.show();
		gui.listenGameStartButton(new ActionListener() {  // lancer le listener du bouton de début du jeu
 			public void actionPerformed(ActionEvent arg0) {
				final GameSearch game ;
				GomokuPosition bd=board;
				Object[] settings = gui.fetchSettings();  // Recueillir les reglages du jeu depuis l'interface de MainGUI
				int depth = (Integer)(settings[0]);  // la profondeur du jeu
				boolean computerStarts = (Boolean)(settings[1]);  // Qui débute en premier : IA ou Player
				if(settings.length==4) { // Est-ce qu'on veut continuer un ancien jeu
					bd=(GomokuPosition) settings[3];
					computerStarts=true;  }
				
				boolean jj =(Boolean)(settings[2]);  // Est-ce un jeu Humain contre Human
 				if(jj) {
 					bd.setPlayers(gui.getNom(), gui.getAdversaire());
					game= new Gamea2(bd);     }
				else {								// Il s'agit d'un jeu IA contre Humain
					game = new Game(bd);
					gui.setAdversaire("IA");
 					bd.setPlayers(gui.getNom(), "IA");   }
  				    gui.showBoard();// Rendre le paramètrage invisble, puis la grille de jeu visible
 				// Appliquer les régles de niveau de jeu et qui commence
				game.setAIDepth(depth);
				game.setAIStarts(computerStarts);				
				game.playGame(settings.length); // Démarrer le jeu.
			    gui.aide(!jj);    // Afficher le bouton de demande d'aide si s'agit d'un jeu IA contre Humain
			}
		});
 	}

	public static void main(String[] args) {
		
		Gomoku mcl = new Gomoku();
	    mcl.athen();

 	} 
}