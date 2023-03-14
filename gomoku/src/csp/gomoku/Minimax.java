package csp.gomoku;

import java.util.ArrayList;


public class Minimax {
 	// Cette variable est utilisée pour suivre le nombre d'évaluations à des fins d'évaluation.
	public static int evaluationCount = 0;
	//L'instance de Gomoku
	private GomokuPosition board;
  	// Constructeur
	public Minimax(GomokuPosition board) {
		this.board = board;
	}
	
 	// Cette fonction calcule le score relatif du joueur blanc contre le noir.
	// (i.e. Quelle est la probabilité que le joueur blanc gagne la partie avant le joueur noir ?)
	// Cette valeur sera utilisée comme score dans l'algorithme Minimax.
	public static double evaluateBoardForWhite(GomokuPosition board, boolean blacksTurn) {
		evaluationCount++; 
 		// Get board score of both players.
		double blackScore = board.positionEvaluation(true);
		double whiteScore = board.positionEvaluation(false);
		if(blackScore == 0) blackScore = 1.0;
		
		// Calculer le score relatif des blancs par rapport aux noirs
		return whiteScore / blackScore;
	}

	// Cette fonction calcule le score à l'échiquier du joueur spécifié.
	// (i.e.  La position générale d'un joueur sur l'échiquier en considérant le nombre de 2, 3, 4 
	//  consécutifs qu'il a, combien d'entre eux sont bloqués etc...)
	public static float getScore(GomokuPosition board, boolean forBlack) {
 		return board.positionEvaluation(forBlack);
	}
	
	// Cette fonction est utilisée pour obtenir le prochain mouvement intelligent à effectuer pour l'IA.
	public int[] alphaBeta(int depth) {
		// Bloquez le tableau pour que l'IA prenne une décision.
 		int[] move = new int[2];


 			// Utilisé uniquement à des fins d'évaluation.
 	 		// Vérifier si un coup disponible peut terminer la partie pour s'assurer que l'IA 
 			//saisit toujours l'opportunité de terminer la partie.
 			// saisit toujours l'opportunité de terminer la partie.

		Object[] bestMove = searchWinningMove(board);

		if(bestMove != null ) {
			// Le coup final est trouvé.
			move[0] = (Integer)(bestMove[1]);
			move[1] = (Integer)(bestMove[2]);
			
		} else {
			// S'il n'y a pas de tel déplacement, on recherche l'arbre minimax avec la profondeur spécifiée.
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

		// Dernière profondeur (nœud terminal), évaluer le score actuel du conseil.
		if(depth == 0) {
			Object[] x = {evaluateBoardForWhite(board, !max), null, null};
			return x;
		}
 		// Générer tous les coups possibles à partir de ce nœud de l'arbre Minimax.
		/*
		 *                  (Move 1)
		 *	               /
		 *  (Noeud actuel) --- (Move 2)
		 *				   \   ...
		 *                  (Move N)
		 */
		ArrayList<int[]> allPossibleMoves = board.possibleMoves();
		
		// S'il n'y a plus de coup possible, traitez ce nœud comme un nœud terminal et renvoyez le score.
		if(allPossibleMoves.size() == 0) {
			Object[] x = {evaluateBoardForWhite(board, !max), null, null};
			return x;
		}
		
		Object[] bestMove = new Object[3];
		
		//Générer l'arbre Minimax et calculer les scores des nœuds.
		if(max) {
			// Initialiser le meilleur coup de départ avec -infini.
			bestMove[0] = -1.0;
			// Itérer pour tous les coups possibles qui peuvent être faits.
			for(int[] move : allPossibleMoves) {
				// Créer un board temporaire qui est équivalent au tableau actuel
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
			// Initialiser le meilleur coup de départ en utilisant le premier coup de la liste et le score +infini.
			bestMove[0] = 100000000.0;
			bestMove[1] = allPossibleMoves.get(0)[0];
			bestMove[2] = allPossibleMoves.get(0)[1];
			
			// Itérer pour tous les coups possibles qui peuvent être faits.
			for(int[] move : allPossibleMoves) {
				// Créer un board temporaire qui est équivalent au board actuel
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
		// Retourne le meilleur coup trouvé à cette profondeur
		return bestMove;
	}
 
	// Cette fonction recherche un coup qui peut instantanément gagner la partie.
	private static Object[] searchWinningMove(GomokuPosition board) {
		ArrayList<int[]> allPossibleMoves = board.possibleMoves();
		Object[] winningMove = new Object[3];
		
		// Itérer pour tous les coups possibles qui peuvent être faits.
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