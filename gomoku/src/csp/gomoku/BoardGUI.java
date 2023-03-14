package csp.gomoku;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
 
public class BoardGUI extends JPanel {
	
	public Graphics2D g2D;
	private BufferedImage image;
 	
	private static final long serialVersionUID = 1L;
	
	private int sideLength; // Longueur du côté de la grille carrée en pixels
	private int boardSize; // Nombre de cases d'un côté (e.g. 19 for a 19x19 board)
	private final int cellLength; // Longueur du côté d'une seule case en pixels
	
	public BoardGUI(int sideLength, int boardSize) {
		this.sideLength = sideLength;
		this.boardSize = boardSize;
		this.cellLength  = sideLength / boardSize;
		
 		image = new BufferedImage(sideLength, sideLength, BufferedImage.TYPE_INT_ARGB);
		
		g2D = (Graphics2D)image.getGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2D.setColor(Color.GREEN);
		g2D.fillRect(0,0,sideLength, sideLength);
		
		g2D.setColor(Color.black);
		// Tracage des lignes
		for(int i=1; i<=boardSize; i++) {
			g2D.drawLine(i*cellLength, 0, i*cellLength, sideLength);
		}
		// Tracage des colonne
		for(int i=1; i<=boardSize; i++) {
			g2D.drawLine(0, i*cellLength, sideLength, i*cellLength);

		}
 	}
	
	public int getRelativePos(int x) {
		if(x >= sideLength) x = sideLength-1;
		
		return (int) ( x * boardSize / sideLength );
	}
	public Dimension getPreferredSize() {
		return new Dimension(sideLength, sideLength);
	}
	public void printWinner(String winner) {
		FontMetrics metrics = g2D.getFontMetrics(g2D.getFont());
		String text = winner;// == 2 ? "Le noir Gagne!" : (winner == 1 ? "Le blanc Gagne!" : "TIED!");
		JOptionPane.showMessageDialog(this, text, "Félicitations", JOptionPane.INFORMATION_MESSAGE);
 		repaint();
  	}
	 
	public void printPosition(int posX, int posY, boolean black) {
		
		if(posX >= boardSize || posY >= boardSize) return;
		// trace les cercles ( pions)
		g2D.setColor(black ? Color.black : Color.white);
		g2D.fillOval((int)(cellLength*(posX+0.05)), 
					 (int)(cellLength*(posY+0.05)), 
					 (int)(cellLength*0.9), 
					 (int)(cellLength*0.9));
		g2D.setColor(Color.black);
		g2D.setStroke(new BasicStroke(2));
		g2D.drawOval((int)(cellLength*(posX+0.05)), 
					 (int)(cellLength*(posY+0.05)), 
					 (int)(cellLength*0.9), 
					 (int)(cellLength*0.9));
		
		repaint();
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2D = (Graphics2D) g.create();
 		g2D.drawImage(image, 0, 0, sideLength, sideLength, null);// Draw the board
  		
		g2D.setColor(Color.black);           // Draw the border
        g2D.drawRect(0, 0, sideLength, sideLength);
 		 
	}
	 	
	public void attachListener(MouseListener listener) {
		addMouseListener(listener);
	}
 }