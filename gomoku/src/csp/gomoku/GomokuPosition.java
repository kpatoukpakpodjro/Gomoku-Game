package csp.gomoku;

import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

public class GomokuPosition {
	
	private BoardGUI gui;
	private int[][] boardMatrix; // 0: Empty 1: White 2: Black
	public ArrayList<String[]> history;
	private Connection conn=SingletonConnection.getConnection();
	protected Statement statement ;
	protected PreparedStatement preparedStatement ;
	protected ResultSet resultSet ;
	protected String player1="internaute", player2="IA";
 	
	public GomokuPosition(int sideLength, int boardSize) {
		gui = new BoardGUI(sideLength, boardSize);
		boardMatrix = new int[boardSize][boardSize];
		 history = new ArrayList<String[]>();
 	}
 	// Constructeur par recopie ( copie simplement la matrice de bord ).
	//l'IA l'utilise pour générer des jeux possibles afin de
	// calculer un point stratégique où jouer
	public GomokuPosition(GomokuPosition board) {
		int[][] matrixToCopy = board.getBoardMatrix();
		boardMatrix = new int[matrixToCopy.length][matrixToCopy.length];
		for(int i=0;i<matrixToCopy.length; i++) {
			for(int j=0; j<matrixToCopy.length; j++) {
				boardMatrix[i][j] = matrixToCopy[i][j];
				 history = new ArrayList<String[]>();
			}
		}
	}

	public int getBoardSize() {
		return boardMatrix.length;
	}
	public void addStoneNoGUI(int posX, int posY, boolean black) {  // permet d'ajouter des stones virtuellement ( par l'IA)
		boardMatrix[posY][posX] = black ? 2 : 1;
	}
	// cette méthode ajoute les points joués à boardMatrice, à la liste historique puis appelle le GUI pour le dessiner
	public boolean addStone(int posX, int posY, boolean black) {
		
		// vérifier si le joueur a cliqué hors de la grille du jeu ou si il a joué avec ambigüté
		if(boardMatrix[posY][posX] != 0) return false;
		
		gui.printPosition(posX, posY, black);
 		history.add(new String[] {""+posX, ""+posY});
		boardMatrix[posY][posX] = black ? 2 : 1;
		gui.repaint();
  		return true;
 		
 	}
	 // générateur des parties possibles à jouer : retour des 8 voisins
	public ArrayList<int[]> possibleMoves() {
		ArrayList<int[]> moveList = new ArrayList<int[]>();
		
		int boardSize = boardMatrix.length;

		for(int i=0; i<boardSize; i++) {
			for(int j=0; j<boardSize; j++) {
 				if(boardMatrix[i][j] > 0) continue;
 				if(i > 0) {
					if(j > 0) {
						if(boardMatrix[i-1][j-1] > 0 || boardMatrix[i][j-1] > 0) {
							int[] move = {i,j};
							moveList.add(move);
							continue;
						}
					}
					if(j < boardSize-1) {
						if(boardMatrix[i-1][j+1] > 0 ||  boardMatrix[i][j+1] > 0) {
							int[] move = {i,j};
							moveList.add(move);
							continue;
						}
					}
					if(boardMatrix[i-1][j] > 0) {
						int[] move = {i,j};
						moveList.add(move);
						continue;
					}
				}
				if( i < boardSize-1) {
					if(j > 0) {
						if(boardMatrix[i+1][j-1] > 0 || boardMatrix[i][j-1] > 0) {
							int[] move = {i,j};
							moveList.add(move);
							continue;
						}
					}
					if(j < boardSize-1) {
						if(boardMatrix[i+1][j+1] > 0 ||  boardMatrix[i][j+1] > 0) {
							int[] move = {i,j};
							moveList.add(move);
							continue;
						}
					}
					if(boardMatrix[i+1][j] > 0) {
						int[] move = {i,j};
						moveList.add(move);
						continue;
					}
				}}
		}
  		return moveList;		
	}
	// getter de la matrice de bords
	public int[][] getBoardMatrix() {
		return boardMatrix;
	}
	// attacher le mouseListener à la grille
	public void startListening(MouseListener listener) {
		gui.attachListener(listener);
	}
	// getter du GUI du jeu
	public BoardGUI getGUI() {
		return gui;
	}
	// Vérifier le point jouer est bon
	public int getRelativePos(int x) {
		return gui.getRelativePos(x);
	}
	// afficher le gagneur de la partie
	public void printWinner(int winner) {
		String idj=this.player1;
		if(winner==1)
			idj=this.player2;  
		idj=idj+" a gagné";
 			if(winner==0) idj="Match null ";
		String req= "insert into partiesgagnees (nom, adversaire,gagnant,score1,score2,datejeu)"
				+ " values(?,?,?,?,?,?)";
		try {
			this.preparedStatement = conn.prepareStatement(req);
			preparedStatement.setString(1,player1);preparedStatement.setString(2,player2);
			preparedStatement.setString(3,idj);preparedStatement.setString(4,""+(positionEvaluation(true)));
			preparedStatement.setString(5,""+(positionEvaluation(false)));
			preparedStatement.setString(6,""+ java.time.LocalDate.now()); 
 			preparedStatement.executeUpdate();

		} catch (SQLException e) {}

		gui.printWinner(idj);
		String sql="delete from stoneshistory where proprio = ?"; // si la partie était sauvegardée, il faut donc la supprimer
		try {
			this.preparedStatement = conn.prepareStatement(sql);   
			preparedStatement.setString(1, player1+""+player2);
			this.preparedStatement.executeUpdate();
		}catch (SQLException e) {		}
 	}
	 // Reprendre un ancien jeu	
	public void Reprise(String idp)
	{
    		try {
 		this.preparedStatement = conn.prepareStatement("select * from stoneshistory where proprio  like ?");		
		// mapping objet relation

		this.preparedStatement.setString(1, "%"+idp+"%"); 	
		this.resultSet =   this.preparedStatement.executeQuery();		

		boolean j=true;
		while (this.resultSet.next()) {
			
			System.out.println("("+this.resultSet.getString(2) +","+this.resultSet.getString(3) +")" + this.resultSet.getString(4) );
			this.addStone(Integer.parseInt(resultSet.getString(2)), Integer.parseInt(resultSet.getString(3)), j);
			j=!j;

		}}
  		catch (SQLException e) {		}
 	}
	// cette méthode permet de sauvegarder la partie du jeu
	public void panelSet(String proprio)
	{
		
  		String sql="delete from stoneshistory where proprio = ?"; // deux joueurs ne peuvent sauvegarder qu'un seul jeu
		try {
			this.preparedStatement = conn.prepareStatement(sql);   // pour se faire, on supprime donc leurs anciens points dans la bdd
			preparedStatement.setString(1, proprio);
			this.preparedStatement.executeUpdate();
		}catch (SQLException e) {		}
		
		String req= "insert into stoneshistory (pointx, pointy,proprio) values(?,?,?)";
		 
		try {
			
			for(int i=0;i<history.size();i++) {
				this.preparedStatement = conn.prepareStatement(req);
				preparedStatement.setString(1, history.get(i)[0]);
				preparedStatement.setString(2, history.get(i)[1]);
				preparedStatement.setString(3, proprio);
				preparedStatement.executeUpdate();
			}
 		} catch (SQLException e) {		}  
		
	}
	// méthode d'évaluation
	public float positionEvaluation(boolean black) {
		int b,m;
		int len,i,j,k=0;
		float score=0;
		len=boardMatrix.length;
		m=k;
		b= black ? 2 : 1;
		// evaluation de la ligne
		for( i=0; i<len; i++) {
			for( j=0; j<len-4; j++) {  k=0;
				if(boardMatrix[i][j]==b) 
				{ k++;
					if(boardMatrix[i][j+1]==b) 
					{ k++;
						if(boardMatrix[i][j+2]==b)
						{k++;
							if(boardMatrix[i][j+3]==b) 
							{k++;
							if(boardMatrix[i][j+4]==b) {k++;
							}
 							}}}}
				 if(k==5) 
					 return 21.0f;
				 m= k>m ? k : m;
				}}	
		score+=getConsecutiveStones(m);
		
 		k=0;  m=k;// evaluation de la colonne
		for(j=0; j<len; j++) {
			for(i=0;i<len-4; i++) {  k=0;
				if(boardMatrix[i][j]==b) 
				{ k++;
					if(boardMatrix[i+1][j]==b) 
					{ k++;
						if(boardMatrix[i+2][j]==b)
						{k++;
							if(boardMatrix[i+3][j]==b) 
							{k++;
							if(boardMatrix[i+4][j]==b) {k++;
							}}}}}
				if(k==5) 
					 return 21.0f;
				 m= k>m ? k : m;
			}}	
	    score+=getConsecutiveStones(m);
	    
		k=0;  m=k;// evaluation de la diagonale decendante \\\\
		for(i=0; i<len-4; i++) {
			for(j=0;j<len-4; j++) { k=0;
				if(boardMatrix[i][j]==b) 
				{ k++;
					if(boardMatrix[i+1][j+1]==b) 
					{ k++;
						if(boardMatrix[i+2][j+2]==b)
						{k++;
							if(boardMatrix[i+3][j+3]==b) 
							{k++;
							if(boardMatrix[i+4][j+4]==b) {k++;
							}}}}}
				if(k==5) 
					 return 21.0f;
				 m= k>m ? k : m;
			}}	
	    score+=getConsecutiveStones(m);
	    
 		k=0;  m=k;// evaluation de la diagonale montante ///
		for(i=len-1; i>3; i--) {   
			for(j=0;j<=i; j++) { k=0;
				if(boardMatrix[i][j]==b) 
				{ k++;
					if(boardMatrix[i-1][j+1]==b) 
					{ k++;
						if(boardMatrix[i-2][j+2]==b)
						{k++;
							if(boardMatrix[i-3][j+3]==b) 
							{k++;
							if(boardMatrix[i-4][j+4]==b) {k++;
							}}}}}
				if(k==5) 
					 return 21.0f;
				 m= k>m ? k : m;
			}}	
	   score+=getConsecutiveStones(m);
		 
 		return score;
	}
	public float getConsecutiveStones(int nbre) {
		float k=0.0f;
		float base =1.0f;
		if(boardMatrix[9][9]!=0 ) base+=0.4;
		switch(nbre) {
		case 5 : k= 21.0f; break;
		case 4 :k= 3.5f + base; break;
		case 3 :k= 2.0f + base; break;
		case 2 :k= 1.5f + base; break;
		case 1 :k= 0.0f + base; break;
		case 0 : k=0.0f ;        break;
		
		}
		return k;
	}
	public void setPlayers(String pl1, String pl2) {
		if(!"".equals(pl1)) player1=pl1;
		if(!"".equals(pl2) && !"IA".equalsIgnoreCase(pl2)) player2=pl2;
	}
}