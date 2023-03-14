package csp.gomoku;


public interface GameSearch {

	public void playGame(int k);
	public void setAIDepth(int depth);
	public void setAIStarts(boolean aiStarts);
	public  int winCheck();
	public  boolean makeMove(int posX , int posY, boolean black);
}