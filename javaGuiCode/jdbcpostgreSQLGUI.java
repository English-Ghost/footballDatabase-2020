import java.sql.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
//import java.sql.DriverManager;
/*
Robert lightfoot
CSCE 315
9-25-2019
 */
public class jdbcpostgreSQLGUI {
	
	//Will build natural left joins for all foreign keys of the given table
	private static String buildJoinStatement(Connection conn, String mainTableChoice, String joinType) {
	     //GET FIRST-CHOICE TABLE FOREIGN KEYS
	     Vector<String> pkTableNames = new Vector<String>();
	     Vector<String> pkColumnNames = new Vector<String>();
	     Vector<String> fkTableNames = new Vector<String>();
	     Vector<String> fkColumnNames = new Vector<String>();
	     try {
	    	 DatabaseMetaData dbmd = conn.getMetaData();	     
		     ResultSet rs = dbmd.getImportedKeys(null, null, mainTableChoice);
		     while (rs.next()) {
		    	 pkTableNames.add(rs.getString("PKTABLE_NAME"));
		    	 pkColumnNames.add(rs.getString("PKCOLUMN_NAME"));
		    	 fkTableNames.add(rs.getString("FKTABLE_NAME"));
		    	 fkColumnNames.add(rs.getString("FKCOLUMN_NAME"));
		     }
	     }
	     catch (SQLException e) {
	    	 e.printStackTrace();
	     }
	     
	    Set<String> noDups = new HashSet<String>(pkTableNames);
	    String joinStatement = " ";
	    for(String join : noDups) {
			joinStatement+=joinType + " " + join + " ";	
	    }	
		return joinStatement;
	}

	private static String buildWhereStatement(Connection conn, String mainTableChoice) {
	    //GET FIRST-CHOICE TABLE COLUMNS
	    Vector<String> columnNames = new Vector<String>();
	    try {
	   	 DatabaseMetaData dbmd = conn.getMetaData();	     
		     ResultSet rs = dbmd.getColumns(null, null, mainTableChoice, null);
		     while (rs.next()) {
		         columnNames.add(rs.getString("COLUMN_NAME"));
		     }
	    }
	    catch (SQLException e) {
	   	 e.printStackTrace();
	    }
	    String filterColumn;
	    filterColumn = columnNames.get(new Random().nextInt(columnNames.size()));

	    Scanner myObj = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter filter term for column " + filterColumn + ": ");

	    Vector<String> filterTerms = new Vector<String>();
	    filterTerms.add(myObj.nextLine());  // Read user input   	    
	    myObj.close();
	    String whereStatement = " ";
	    if(filterTerms.size()>0) {
	    	whereStatement += "WHERE ";
		    whereStatement += filterColumn + " = " + filterTerms.get(0);
	    }
	    return whereStatement;
	}
	
	 public static Vector<String> getColumnNames(Connection conn, String tableChoice){
		    Vector<String> columnNames = new Vector<String>();
			    try {
			   	 DatabaseMetaData dbmd = conn.getMetaData();	     
				     ResultSet rs = dbmd.getColumns(null, null, "rush", null);
				     while (rs.next()) {
				         columnNames.add(rs.getString("COLUMN_NAME"));
				     }
			    }
			    catch (SQLException e) {
			   	 e.printStackTrace();
		      }
		      return columnNames;
		  }
	
  public static void main(String args[]) {
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
     JOptionPane.showMessageDialog(null,"Opened database successfully");
     
     // Get all tablenames
     // try-catch block adapted from https://stackoverflow.com/questions/2780284/how-to-get-all-table-names-from-a-database
     Vector<String> tableNames = new Vector<String>();
     try {
         DatabaseMetaData dbmd = conn.getMetaData();
         String[] types = {"TABLE"};
         ResultSet rs = dbmd.getTables(null, null, "%", types);
         while (rs.next()) {
             tableNames.add(rs.getString("TABLE_NAME"));
         }
     } 
         catch (SQLException e) {
         e.printStackTrace();
     }
     
     //RANDOMLY PICK "FIRST-CHOICE" TABLE
     String mainTableChoice;
     mainTableChoice = tableNames.get(new Random().nextInt(tableNames.size()));
     //mainTableChoice = "playergamestatistics";
     

         
     System.out.println("Table: " + mainTableChoice);
//     System.out.println("FOREIGN KEYS");
//     for(int i=0; i<pkTableNames.size(); i++) {
//    	 System.out.println(pkTableNames.get(i) + "\t" + pkColumnNames.get(i) + "\t" + fkTableNames.get(i) + "\t" + fkColumnNames.get(i));
//     }    
//     //PRINT FIRST CHOICE TABLE NAME AND COLUMNS
//     System.out.println("Table: " + mainTableChoice);
//     for(String column : columnNames) {
//    	 System.out.println(column);
//     }   

     try{
     //create a statement object
       Statement stmt = conn.createStatement();
       //create an SQL statement
       String FROMStmt = "SELECT * FROM " + mainTableChoice + " ";
       String JOINStmt = buildJoinStatement(conn, mainTableChoice, "NATURAL LEFT JOIN"); 
       String WHEREStmt = buildWhereStatement(conn, mainTableChoice);
       String CONDITIONStmt = " LIMIT 3";
       String sqlStatement = FROMStmt + JOINStmt + WHEREStmt + CONDITIONStmt;
       System.out.println("SQL Statement: " + sqlStatement);
       //send statement to DBMS
       ResultSet result = stmt.executeQuery(sqlStatement);    
       ResultSetMetaData metadata = result.getMetaData();
       
       //PRINT EVERYTHING
       //Adapted from https://stackoverflow.com/questions/24943894/how-do-you-get-values-from-all-columns-using-resultset-getbinarystream-in-jdbc
       int columns = metadata.getColumnCount();
       for (int i = 1; i <= columns; i++) {
           System.out.print(metadata.getColumnName(i) + ", ");      
       }
       System.out.println();
       while (result.next()) {
           String row = "";
           for (int i = 1; i <= columns; i++) {
               row += result.getString(i) + ", ";          
           }
           System.out.println(row);
       }
       

//       //OUTPUT
//       JOptionPane.showMessageDialog(null,"Customer Last names from the Database.");
//       //System.out.println("______________________________________");
//       while (result.next()) {
//         //System.out.println(result.getString("cus_lname"));
//         cus_lname += result.getString("playtype")+"\n";
//       }
   } catch (Exception e){
     JOptionPane.showMessageDialog(null,"Error accessing Database.");
   }
//   JOptionPane.showMessageDialog(null,cus_lname);
    //closing the connection
     
    try {
      conn.close();
      JOptionPane.showMessageDialog(null,"Connection Closed.");
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
    }//end try catch 
  }//end main
}//end Class