package csp.gomoku;

import java.util.ArrayList;


public class Minimax {
 	// Cette variable est utilis�e pour suivre le nombre d'�valuations � des fins d'�valuation.
	public static int evaluationCount = 0;
	//L'instance de Gomoku
	private GomokuPosition board;
  	// Constructeur
	public Minimax(GomokuPosition board) {
		this.board = board;
	}
	
 	// Cette fonction calcule le score relatif du joueur blanc contre le noir.
	// (i.e. Quelle est la probabilit� que le joueur blanc gagne la partie avant le joueur noir ?)
	// Cette valeur sera utilis�e comme score dans l'algorithme Minimax.
	public static double evaluateBoardForWhite(GomokuPosition board, boolean blacksTurn) {
		evaluationCount++; 
 		// Get board score of both players.
		double blackScore = board.positionEvaluation(true);
		double whiteScore = board.positionEvaluation(false);
		if(blackScore == 0) blackScore = 1.0;
		
		// Calculer le score relatif des blancs par rapport aux noirs
		return whiteScore / blackScore;
	}

	// Cette fonction calcule le score � l'�chiquier du joueur sp�cifi�.
	// (i.e.  La position g�n�rale d'un joueur sur l'�chiquier en consid�rant le nombre de 2, 3, 4 
	//  cons�cutifs qu'il a, combien d'entre eux sont bloqu�s etc...)
	public static float getScore(GomokuPosition board, boolean forBlack) {
 		return board.positionEvaluation(forBlack);
	}
	
	// Cette fonction est utilis�e pour obtenir le prochain mouvement intelligent � effectuer pour l'IA.
	public int[] alphaBeta(int depth) {
		// Bloquez le tableau pour que l'IA prenne une d�cision.
 		int[] move = new int[2];


 			// Utilis� uniquement � des fins d'�valuation.
 	 		// V�rifier si un coup disponible peut terminer la partie pour s'assurer que l'IA 
 			//saisit toujours l'opportunit� de terminer la partie.
 			// saisit toujours l'opportunit� de terminer la partie.

		Object[] bestMove = searchWinningMove(board);

		if(bestMove != null ) {
			// Le coup final est trouv�.
			move[0] = (Integer)(bestMove[1]);
			move[1] = (Integer)(bestMove[2]);
			
		} else {
			// S'il n'y a pas de tel d�placement, on recherche l'arbre minimax avec la profondeur sp�cifi�e.
			bestMove = alphaBetaHelper(depth, board, true, -1.0, 100000000);
			if(bestMove[1] == null) {
				move = null;
			} else {
				move[0] = (Integer)(bestMove[1]);
				move[1] = (Integer)(bestMove[2]);
			}
		}
 		evaluationCount=0;
 		return move;
	}
 	
	/*
	 * alpha : Meilleur move de AI  (Max)
	 * beta : meilleur move du joueur (Min)
	 * returne: {score, move[0], move[1]}
	 * */
	private static Object[] alphaBetaHelper(int depth, GomokuPosition board, boolean max, double alpha, double beta) {

		// Derni�re profondeur (n�ud terminal), �valuer le score actuel du conseil.
		if(depth == 0) {
			Object[] x = {evaluateBoardForWhite(board, !max), null, null};
			return x;
		}
 		// G�n�rer tous les coups possibles � partir de ce n�ud de l'arbre Minimax.
		/*
		 *                  (Move 1)
		 *	               /
		 *  (Noeud actuel) --- (Move 2)
		 *				   \   ...
		 *                  (Move N)
		 */
		ArrayList<int[]> allPossibleMoves = board.possibleMoves();
		
		// S'il n'y a plus de coup possible, traitez ce n�ud comme un n�ud terminal et renvoyez le score.
		if(allPossibleMoves.size() == 0) {
			Object[] x = {evaluateBoardForWhite(board, !max), null, null};
			return x;
		}
		
		Object[] bestMove = new Object[3];
		
		//G�n�rer l'arbre Minimax et calculer les scores des n�uds.
		if(max) {
			// Initialiser le meilleur coup de d�part avec -infini.
			bestMove[0] = -1.0;
			// It�rer pour tous les coups possibles qui peuvent �tre faits.
			for(int[] move : allPossibleMoves) {
				// Cr�er un board temporaire qui est �quivalent au tableau actuel
				GomokuPosition dummyBoard = new GomokuPosition(board);

				// Jouez le coup sur cette grille temporaire sans rien dessiner.
				dummyBoard.addStoneNoGUI(move[1], move[0], false);
  				Object[] tempMove = alphaBetaHelper(depth-1, dummyBoard, !max, alpha, beta);
				
				if((Double)(tempMove[0]) > alpha) {
					alpha = (Double)(tempMove[0]);
				}
				 
				if((Double)(tempMove[0]) >= beta) {
					return tempMove;
				}

				// Trouvez le coup avec le score maximum.
				if((Double)tempMove[0] > (Double)bestMove[0]) {
					bestMove = tempMove;
					bestMove[1] = move[0];
					bestMove[2] = move[1];
				}
			}
		}
		else {
			// Initialiser le meilleur coup de d�part en utilisant le premier coup de la liste et le score +infini.
			bestMove[0] = 100000000.0;
			bestMove[1] = allPossibleMoves.get(0)[0];
			bestMove[2] = allPossibleMoves.get(0)[1];
			
			// It�rer pour tous les coups possibles qui peuvent �tre faits.
			for(int[] move : allPossibleMoves) {
				// Cr�er un board temporaire qui est �quivalent au board actuel
				GomokuPosition dummyBoard = new GomokuPosition(board);

				// Jouez le coup sur cette planche temporaire sans rien dessiner.
				dummyBoard.addStoneNoGUI(move[1], move[0], true);
				 
				Object[] tempMove = alphaBetaHelper(depth-1, dummyBoard, !max, alpha, beta);
				 
				if(((Double)tempMove[0]) < beta) {
					beta = (Double)(tempMove[0]);
				}
				 
				if((Double)(tempMove[0]) <= alpha) {
					return tempMove;
				}
 				// Trouvez le coup avec le score minimum.
				if((Double)tempMove[0] < (Double)bestMove[0]) {
					bestMove = tempMove;
					bestMove[1] = move[0];
					bestMove[2] = move[1];
				}
			}
		}
		// Retourne le meilleur coup trouv� � cette profondeur
		return bestMove;
	}
 
	// Cette fonction recherche un coup qui peut instantan�ment gagner la partie.
	private static Object[] searchWinningMove(GomokuPosition board) {
		ArrayList<int[]> allPossibleMoves = board.possibleMoves();
		Object[] winningMove = new Object[3];
		
		// It�rer pour tous les coups possibles qui peuvent �tre faits.
		for(int[] move : allPossibleMoves) {
			evaluationCount++;

			GomokuPosition dummyBoard = new GomokuPosition(board);

			dummyBoard.addStoneNoGUI(move[1], move[0], false);
			
			// Si le joueur blanc a un score gagnant dans ce tableau temporaire, il retourne le coup.
			if(getScore(dummyBoard,false) >= 20) {
				winningMove[1] = move[0];
				winningMove[2] = move[1];
				return winningMove;
			}
		}
		return null;
	}
 }