package csp.gomoku; 
import java.sql.Connection; 
import java.sql.DriverManager; 
public class SingletonConnection { 
 
private static Connection connection; 
// Un blocs static s�ex�cute une seule fois lors du chargement de la classe
// et ne s�ex�cute pas lors des instanciation de la classe ,ils sont lanc�s avant l'appel des constructeurs.
 
static{ 
 
	 try { 
		// Class.forName("com.mysql.jdbc.Driver"); 
			 Class.forName("com.mysql.cj.jdbc.Driver");
	 connection= 
	DriverManager.getConnection("jdbc:mysql://localhost:3306/mygame","root",""); 
	 } catch (Exception e) { 
	 // TODO Auto-generated catch block
	 e.printStackTrace(); 
	 } 
  } 
	public static Connection getConnection() { 
	 return connection; 
	 } 
 
} 