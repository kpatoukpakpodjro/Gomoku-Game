package csp.gomoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class MainGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	public ArrayList<String[]> paramsG;  // Les paramètres 
	private int difficulty;				// niveau du jeu
	private boolean computerStarts,jouera2,ctoldg;   // Qui commence, jouer à 2, continuer un ancien jeu
 	
	private JPanel boardPanel;
	private final JPanel setupPanel,difficultyPanel,startingPlayerPanel,startingOldgame,listParties;
	
	private final JButton buttonStart,auth;
	private final JRadioButton rbNormal,rbHard,rbHuman,rbComputer,rbjj,rboldgame;	
	 
	private final ButtonGroup bgDifficulty,bgStartingPlayer ;
	ImageIcon icon = new ImageIcon("images/img3.png");
	private JLabel taDifficulty;
	public JTextField joueur1,joueur2;
 
	private Connection conn=SingletonConnection.getConnection();
	protected Statement statement ;
	protected PreparedStatement preparedStatement ;
	protected ResultSet resultSet ;
	JMenuBar menuBar;
    JMenu menu;
    JMenu help;
    JMenuItem item1, item2, item3, item4;
    GoMenuEvent gme;
    public JFrame windowL, frame = new JFrame("GOMOKU");
    public JFrame window = new JFrame("Authentification");
    public GomokuPosition gof;
    private String nom, adversaire="IA";
    
	public MainGUI(int width, int height, String title, GomokuPosition bd) {
		setSize(700, 500);
		setTitle(title);   this.setIconImage(icon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.gof=bd;	 
			paramsG = new ArrayList<String[]>();
 		setupPanel = new JPanel();
 		listParties = new JPanel();
		setupPanel.setSize(700,700);
		setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.PAGE_AXIS));
		difficultyPanel = new JPanel(new GridLayout(3, 3));
		joueur1 = new JTextField(); 
		joueur2 = new JTextField();
		GridLayout gridl= new GridLayout(6, 2);
		startingPlayerPanel = new JPanel();
		startingPlayerPanel.setLayout(gridl);
		startingPlayerPanel.setBackground(new Color(255, 240, 245));
		startingPlayerPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 255, 255), new Color(160, 160, 160)), "Jeu",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		startingPlayerPanel.setBounds(78, 190, 128, 116);
		startingOldgame = new JPanel();
		
		buttonStart = new JButton("Start Game");
		auth= new JButton("LOGIN");
		rbNormal = new JRadioButton("Normal (Rapide)");
		rbHard = new JRadioButton("Difficile (Lent)");
		
		rbHuman = new JRadioButton("Humain débute");  
		rbComputer = new JRadioButton("IA  débute");  
		rbjj = new JRadioButton("Jouer à 2");         
 		rboldgame = new JRadioButton("Reprendre");
		bgDifficulty = new ButtonGroup();
		bgStartingPlayer = new ButtonGroup();
 		
		bgDifficulty.add(rbNormal);
		bgDifficulty.add(rbHard);
		
		bgStartingPlayer.add(rbHuman);
		bgStartingPlayer.add(rbComputer);
		bgStartingPlayer.add(rbjj);
		taDifficulty = new JLabel("Difficulté: ");
		taDifficulty.setFont(new Font("Tahoma", Font.BOLD, 16));
		taDifficulty.setForeground(Color.red);
		
		rbNormal.setSelected(true);     // Sélectionner par défaut
		rbComputer.setSelected(true);   // Sélectionner par défaut
		rboldgame.setSelected(false);   // Sélectionner par défaut
		
		difficultyPanel.add(taDifficulty);
		difficultyPanel.add(rbNormal);
		difficultyPanel.add(rbHard);
		taDifficulty =new JLabel("Jouer contre l'IA ");
 		taDifficulty.setFont(new Font("Tahoma", Font.BOLD, 16));
		taDifficulty.setForeground(Color.red);
		startingPlayerPanel.add(rbjj); 
  		startingPlayerPanel.add( taDifficulty );
  		startingPlayerPanel.add(rbComputer);
		startingPlayerPanel.add(rbHuman);
		 		
		taDifficulty =new JLabel("Continuer le dernier jeu ");
		taDifficulty.setFont(new Font("Tahoma", Font.BOLD, 16));
		taDifficulty.setForeground(Color.blue);
		startingOldgame.add(taDifficulty );
		startingOldgame.add(rboldgame);
 		
		setupPanel.add(difficultyPanel);
  		setupPanel.add(startingPlayerPanel);
   		setupPanel.add(startingOldgame);
		setupPanel.add(buttonStart);
				
		this.add(setupPanel);
		
 		//show();      						// Rendre visible le JPanel 
	}
	/*
 	 * Lire les composants choisis et retourner les paramètres 
	 */
	public Object[] fetchSettings() {
		
 		System.out.println("Match entre "+nom+"  et  "+adversaire);
		
		difficulty = 3;
		if( rbHard.isSelected() ) {
			difficulty = 4;
		}  		
		computerStarts = rbComputer.isSelected();
		jouera2 = rbjj.isSelected();
		ctoldg = rboldgame.isSelected();
		
 		if(ctoldg) return fetchSettings(1); // S'il s'agit de la reprise 
 									// d'une ancienne partie,consulter la bdd : ligne 237
		
 		Object[] x = {difficulty, computerStarts, jouera2};
		return x;
	}
	public void listenGameStartButton(ActionListener listener) {
		buttonStart.addActionListener(listener);
	}
	public void attachBoard(JPanel board) {
		boardPanel = board;
	}
	public String getNom() { return nom;}
	public String getAdversaire() { return adversaire;}
	public void setNom(String nomp) { 
 		if(!"".equals(nomp)) this.nom=nomp;
		else this.nom = "internaute";	
		paramsG.add(new String[] {""+nom+""} );
	}
	public void setAdversaire(String adver) {
		if(!"".equals(adver) && !adversaire.equals(adver.toUpperCase()) )this.adversaire=adver;
		paramsG.add(new String[] {""+adversaire,""} );	
	}
	public void showBoard() {
		setContentPane(boardPanel);
		invalidate();
		validate();
		pack();
	}	
	public void aide(boolean AvAi) {
		gme=new GoMenuEvent(this);
		Container cont = frame.getContentPane();
        cont.setLayout(new BorderLayout());
        cont.setBackground(Color.cyan );
        //cont.add(this, BorderLayout.CENTER);
        cont.add(boardPanel, BorderLayout.SOUTH);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        help = new JMenu("Help");
        item1 = new JMenuItem("Sauvegarder");
        item1.setActionCommand("Sauvegarder");
        item1.addActionListener(gme);
        item2 = new JMenuItem("Restart");
        item2.setActionCommand("Restart");
        item2.addActionListener(gme);
        item3 = new JMenuItem("besoin_d_aide?");
        item3.setActionCommand("besoin_d_aide?");
        item3.addActionListener(gme);
        item4 = new JMenuItem("Mes parties jouées");
        item4.addActionListener(gme);
        menu.add(item1);
        menu.add(item2);
        menu.add(item4);
        help.add(item3);
        menuBar.add(menu);
        if(AvAi) { menuBar.add(help);  }
        frame.setJMenuBar(menuBar);
        frame.setIconImage(icon.getImage());
        frame.setResizable(true);
        frame.pack();
        this.setVisible(false);
        frame.setVisible(true);
  	}
	public void setFrame(String k, String op) {
		 
  		frame.dispose();	  		// détruire l'ancien frame
 		Gomoku mcl = new Gomoku();   // redémarrer le jeu
 		mcl.board.setPlayers(k, op);
 		mcl.gui.setNom(k);
 		mcl.gui.setAdversaire(op);
 		      mcl.lancer();
 		     mcl.board.setPlayers(k, op);
 	 		mcl.gui.setNom(k);
 	}
	public Object [] fetchSettings(int k) {    //
  		 
		String idj =""+this.getNom() +""+this.getAdversaire();
		try {
 			this.preparedStatement = conn.prepareStatement("select * from encours where idjeu like ?");
			this.preparedStatement.setString(1, "%"+idj+"%");
			this.resultSet =    this.preparedStatement.executeQuery( );

			while(resultSet.next()) {
				 
				jouera2 = Boolean.parseBoolean(resultSet.getString(4));
				jouera2=!jouera2; // car c'est le contraire de jouera2 qui est stocké dans la bdd
				computerStarts = Boolean.parseBoolean(resultSet.getString(5));
				difficulty=Integer.parseInt(resultSet.getString(6));
			}
		} catch (SQLException e) {
  			JOptionPane.showMessageDialog(this,"Vérifiez le formulaire svp!",null,JOptionPane.ERROR_MESSAGE);
  			//return 0;
		}	
		gof.Reprise(idj);  // cette méthode va ajouter tous les anciens points boardMatrice et aussi,
							//dessiner les stones via BoardGUI

		Object[] x = {difficulty,computerStarts, jouera2 ,gof};
 		return x;
	}
	public void enregistrerParams(int nb) {

		if(jouera2) nb=0;     // si le jeu est Homme-Homme, pas demande d'aide 
		String idj=this.nom +""+this.adversaire;
		String req= "insert into encours (nom, adversaire,idjeu,contreia,iacommence,difficulte,nbaiderestant)"
				+ " values(?,?,?,?,?,?,?)";
		try {
			this.preparedStatement = conn.prepareStatement(req);
			preparedStatement.setString(1,nom);preparedStatement.setString(2,adversaire);
			preparedStatement.setString(3,idj);preparedStatement.setString(4,""+(!jouera2));
			preparedStatement.setString(5,""+computerStarts);preparedStatement.setString(6,""+difficulty);
			preparedStatement.setString(7,""+nb); 
			preparedStatement.executeUpdate();
			JOptionPane.showMessageDialog(null,"Insertion reussie!",null,JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException e) {
			
			req= "update encours set nbaiderestant = ? where idjeu = ?";
			try {
				this.preparedStatement = conn.prepareStatement(req);
				preparedStatement.setString(1,""+(nb+1));
				preparedStatement.setString(2,idj);
				preparedStatement.executeUpdate();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null,"Completez le formulaire svp!",null,JOptionPane.ERROR_MESSAGE);
			}
  		}
			
	}
	public void authentification() {

		window.setPreferredSize(new Dimension(400,300));
		GridLayout layout = new GridLayout(8,1);
		window.setLayout(layout);

		taDifficulty =new JLabel("Nom du Joueur  ");taDifficulty.setFont(new Font("Tahoma", Font.BOLD, 16));
		taDifficulty.setForeground(Color.green); 
		window.add(taDifficulty).setLocation(0,0);
		window.add(joueur1).setLocation(0, 1);
		taDifficulty =new JLabel("Nom du Joueur adverse ");taDifficulty.setFont(new Font("Tahoma", Font.BOLD, 16));
		taDifficulty.setForeground(Color.red); 
		 
		window.add(taDifficulty).setLocation(1,0);
		window.add(joueur2).setLocation(1, 1);
		window.add(new JLabel(""));
		taDifficulty =new JLabel("Cliquez si pas besoin de sauvegarde ");taDifficulty.setFont(new Font("Tahoma", Font.BOLD, 16));
		window.add(taDifficulty).setLocation(1,0);
		window.add(new JLabel(""));
		window.add(auth);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);

	}
	public void add1(ActionListener listener) {
		auth.addActionListener(listener);
		}
	public void AffichageList(String idj) {
		JTable table2=new JTable();
		JScrollPane scroll2=new JScrollPane();
		scroll2.setBounds(450,370,460,220);
		scroll2.setViewportView(table2);
		windowL = new JFrame("Vos parties terminées ");
		windowL.setPreferredSize(new Dimension(700,500));
		windowL.setBackground(Color.blue);   windowL.setIconImage(icon.getImage());
 		DefaultTableModel df2=new  DefaultTableModel();
 		listParties.setBackground(new Color(180,200,240));
		listParties.add(scroll2);
		 df2.addColumn("votre adersaire");
		 df2.addColumn("Gagnant");
		 df2.addColumn("votre score");
		 df2.addColumn("Son score");
		 df2.addColumn("joué le");
		 table2.setModel(df2);

		 try{
			 this.preparedStatement = conn.prepareStatement("select * from partiesgagnees where nom like ?");
				this.preparedStatement.setString(1, "%"+idj+"%");
				this.resultSet =   this.preparedStatement.executeQuery( );
			 while(resultSet.next()){
				 df2.addRow(new Object[]{
						 resultSet.getString("adversaire"),resultSet.getString("gagnant"),
						 resultSet.getString("score1"),resultSet.getString("score2"),
						 resultSet.getString("datejeu")
						 });
			 } }
 		 catch(SQLException ex){
		    	JOptionPane.showMessageDialog(null,"Erreur !",null,JOptionPane.ERROR_MESSAGE);	
		    }
		 windowL.add(listParties);
		 windowL.pack();
		windowL.setVisible(true);
	}
}