package csp.gomoku;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

 public class Gamea2 implements GameSearch{

	private GomokuPosition board;
	private boolean isPlayersTurn = true;
	private boolean gameFinished = false;
  	private int winner; // 0: There is no winner yet, 1: AI Wins, 2: Human Wins
 	
	public Gamea2(GomokuPosition board ) {
		this.board = board;
 		winner = 0;
	}
	//méthode de lancement de la partie du jeu
	@Override
	public void playGame(int k) {
 		k=board.history.size();
		if(k%2==1)              // si la longueur du history est impaire, alors c'est le blanc qui 
 			isPlayersTurn=false;       //commence ( si on veut conitue un ancien )

 		// Make the board start listening for mouse clicks.
		board.startListening(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if(gameFinished) return;
				
				// Find out which cell of the board do the clicked coordinates belong to.
				int posX = board.getRelativePos( e.getX() );
				int posY = board.getRelativePos( e.getY() );

				System.out.println(" x : "+posX+" ; Y : "+posY);
				// Place a black stone to that cell.
 				if(!makeMove(posX, posY, isPlayersTurn)) {
					// If the cell is already populated, do nothing.
					isPlayersTurn = !isPlayersTurn;
					return;
				}
				
				// Check if the last move ends the game.
				winner = winCheck();
				if(isPlayersTurn)
				if(winner == 2) {
					System.out.println("Le noir gagne la partie !!");
					board.printWinner(2);
					gameFinished = true;
					return;
				}
				if(!isPlayersTurn)
					if(winner == 1) {
						System.out.println("Le blanc gagne la partie !!");
						board.printWinner(1);
						gameFinished = true;
						return;
					}
				
				if(board.possibleMoves().size() == 0 ||board.history.size()==60) {
					System.out.println("No possible moves left. Game Over.");
					board.printWinner(0); // Prints "TIED!"
					gameFinished = true;
					return;
				}
				isPlayersTurn = !isPlayersTurn;
			}

			public void mouseEntered(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseReleased(MouseEvent arg0) {}
		});
	}
	 
	@Override
	public int winCheck() {
		if(board.positionEvaluation(true)>20) return 2;
		if(board.positionEvaluation(false)>20) return 1;
		return 0;
	}
	@Override
	public boolean makeMove(int posX, int posY, boolean black) {
		return board.addStone(posX, posY, black);
	}
	@Override
	public void setAIDepth(int depth) {}
	@Override
	public void setAIStarts(boolean aiStarts) {	}
	
}