package csp.gomoku;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

 public class Game implements GameSearch{

	private GomokuPosition board;
	private boolean isPlayersTurn = true;
	private boolean gameFinished = false;
	private int minimaxDepth = 3;
	private boolean aiStarts = true; // AI makes the first move
	private Minimax ai;
	public int nbreAide;
	//public static final String cacheFile = "score_cache.ser";
	private int winner; // 0: There is no winner yet, 1: AI Wins, 2: Human Wins
 	
	public Game(GomokuPosition board) {
		this.board = board;
		ai = new Minimax(board);
		nbreAide=3;
		winner = 0;
	}
	
	@Override
	public void playGame(int k) {
  		// Si on continue un ancien jeu, c'est IA qui jouera en premier donc éviter de jouer au milieu
		if(k==4) {
			int[] aiMove = ai.alphaBeta(minimaxDepth);
			makeMove(aiMove[1], aiMove[0], false);
 		}
		// S'il s'agit d'un nouveau jeu et c'est l'IA qui commence, jouer au milieu
		if(aiStarts &&k==3) makeMove(board.getBoardSize()/2, board.getBoardSize()/2, false);
 		// Maintenant c'est le player qui joue
  		// lancer le listener de mouse click
		board.startListening(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				if(isPlayersTurn) {
 					isPlayersTurn = false;
 					Thread mouseClickThread = new Thread(new MouseClickHandler(arg0));
					mouseClickThread.start();					
				}
			}
 			public void mouseEntered(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseReleased(MouseEvent arg0) {}
			
		});
	}
	/*
	 * 	Setter de la profondeur de l'arbre de minimax (i.e. combien de  moves l'IA calcule en avance.)
	 */
	@Override
	public void setAIDepth(int depth) {
		this.minimaxDepth = depth;
 	}
	
	@Override
	public void setAIStarts(boolean aiStarts) {
		this.aiStarts = aiStarts;
	}
	// l'orchestre du jeu
	public class MouseClickHandler implements Runnable{
		MouseEvent e;
		public MouseClickHandler(MouseEvent e) {
			this.e = e;
		}
		public void run() {
			if(gameFinished) return;
	
 			int posX = board.getRelativePos( e.getX() );
			int posY = board.getRelativePos( e.getY() );

			System.out.println(" x : "+posX+" ; Y : "+posY);
			// Est-ce le joueur a déjà un pion à cet endroit ?
			if(!makeMove(posX, posY, true)) {
 				isPlayersTurn = true;
				return;
			}
			
			winner = winCheck(); // vérifier si le dernier move met fin au jeu.
			
			if(winner == 2) {
				System.out.println("Player WON!");
				board.printWinner(2);
				gameFinished = true;
				return;
			}
 			// IA calcule le move stratégique à faire
			int[] aiMove = ai.alphaBeta(minimaxDepth);
			
			if(aiMove == null || board.getBoardMatrix().length==60) {
				System.out.println("No possible moves left. Game Over.");
				board.printWinner(0); // Prints "TIED!"
				gameFinished = true;
				return;
			}
  			// Place un  stone sur la grille pour IA.
			makeMove(aiMove[1], aiMove[0], false);
 			System.out.println("IA : "+aiMove[1] +","+ aiMove[0]);
			
			winner = winCheck(); // vérifier si le dernier move met fin au jeu.
			
			if(winner == 1) {
				System.out.println("L'IA gagne la partie !!");
				board.printWinner(1);
				gameFinished = true;
				return;
			}
			
			if(board.possibleMoves().size() == 0 ||board.history.size()==60) {  // on arrête si possibleMoves ne trouve plus d'espace 
				System.out.println("No possible moves left. Game Over.");		// ou si un joueur a déjà placé 60 Stones
				board.printWinner(0); 
				gameFinished = true;
				return;
 			}
 			isPlayersTurn = true;  // lancer la main au joueur 
		}
 	}
	// méthode de vérification du gagneur de la partie du jeu
	@Override
	public int winCheck() {
		if(board.positionEvaluation( true) >20) return 2;
		if(board.positionEvaluation( false) >20) return 1;
		return 0;
	}
	// méthode permettant de placer les stones joués par les joueurs 
	@Override
	public boolean makeMove(int posX, int posY, boolean black) {
		return board.addStone(posX, posY, black);
	}
	 // méthode qui fournit d'aide à l'humain
	public void recevoirAide() {
		String text;
		if(nbreAide>0) {
			nbreAide--;
		  text = "Jouez au point ("+ai.alphaBeta(minimaxDepth)[0]+
				" , "+ai.alphaBeta(minimaxDepth)[1]+")\n Nombre d'aide restant : "+nbreAide;
		}
		else 
		{
			text= "Nombre d'aide\n épusé";
		} // Afficher le point à jouer au joueur
		JOptionPane.showMessageDialog(board.getGUI(), text, "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
 	}
}