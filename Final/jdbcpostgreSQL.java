package dbManager;
import java.sql.*;

//import javax.swing.JOptionPane;

/*
Robert lightfoot
CSCE 315
9-25-2019 Original
2/7/2020 Update for AWS
 */

public class jdbcpostgreSQL {
	
  public static void main(String args[]) {
    //dbSetup hides my username and password
    dbSetup my = new dbSetup();
    //Building the connection
     Connection conn = null;
     try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/section900team1",
           my.user, my.pswd);
     } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
     }//end try catch
     System.out.println("Opened database successfully");
     System.out.println();
     
   //QUESTION 1
   Q1 answerQ1 = new Q1("Akron","Ohio",conn);
   System.out.println(answerQ1.toString());  
   answerQ1.setEnemyTeam("Alabama");
   answerQ1.rebuildResults();
   System.out.println(answerQ1.toString());  
   //Q1 answerQ1b = new Q1("Akron","Alabama",conn);
   //System.out.println(answerQ1b.toString());
     
    try {
      conn.close();
      System.out.println("Connection Closed.");
    } catch(Exception e) {
      System.out.println("Connection NOT Closed.");
    }//end try catch
  }//end main
}//end Class
