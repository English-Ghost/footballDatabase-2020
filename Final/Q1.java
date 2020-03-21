package dbManager;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class Q1
{	
	private ArrayList<ArrayList<String>> results;
	static Connection conn;
	private boolean printQuery;    	    
//    private ArrayList<LinkedList<Victory>> victoryChainArray = new ArrayList<LinkedList<Victory>>();
    private String myTeam;
    private String enemyTeam;
	private Integer maxVictoryChainLength;
	
    //CONSTRUCTOR: build results of query
    public Q1(String myTeam, String enemyTeam, Connection conn)
	{
		super();
    	this.myTeam = myTeam;
    	this.enemyTeam = enemyTeam;
    	Q1.conn=conn;
    	this.printQuery = false;
    	this.maxVictoryChainLength=4;
    	this.results = buildQ1();
	}
	
	//GENERIC TOOL: looks up code from string
	private BigInteger lookupCodeFromString(String queryWord, String codeColumn, String stringColumn, String table) {
		BigInteger code = new BigInteger("-1");
		try{
	         //create a statement object
	           Statement stmt = conn.createStatement();
	           //create a query
	           String query = "SELECT DISTINCT(" + codeColumn + ") FROM " + table + " WHERE " + stringColumn + "=\'" + queryWord +"\'";
	           //send statement to DBMS
	           ResultSet result = stmt.executeQuery(query);

	           //OUTPUT
	           if (result.next()) {
	             code = BigInteger.valueOf(result.getLong(1));
	           }
	           else {
	        	   System.out.println("Error: lookup of " + queryWord + " in " + stringColumn + "produced no results.");
	           }
	           
	           if(result.next()) {
	        	   System.out.println("Error: lookup of " + queryWord + " in " + stringColumn + "produced more than one result. Returning the first.");
	           }
	       } catch (Exception e){
	         System.out.println("Error accessing Database.");
	     }
		return code;
	}
	
	//GENERIC TOOL: looks up string from code
	private String lookupStringFromCode(BigInteger queryCode, String stringColumn, String codeColumn, String table) {
		String resultString = new String();
		try{
	         //create a statement object
	           Statement stmt = conn.createStatement();
	           //create a query
	           String query = "SELECT DISTINCT(" + stringColumn + ") FROM " + table + " WHERE " + codeColumn + "=" + queryCode.toString();
	           //send statement to DBMS
	           ResultSet result = stmt.executeQuery(query);

	           //OUTPUT
	           if (result.next()) {
	             resultString = result.getString(1);
	           }
	           else {
	        	   System.out.println("Error: lookup of " + queryCode.toString() + " in " + codeColumn + "produced no results.");
	           }
	           
	           if(result.next()) {
	        	   System.out.println("Error: lookup of " + queryCode.toString() + " in " + codeColumn + "produced more than one result. Returning the first.");
	           }
	       } catch (Exception e){
	         System.out.println("Error accessing Database.");
	     }
		return resultString;
	}
	
	
	private ArrayList<ArrayList<String>> buildQ1() {
		
		//take team name and get the corresponding teamcode
		String team1 = lookupCodeFromString(myTeam, "teamcode", "name", "team").toString();
		String team2 = lookupCodeFromString(enemyTeam, "teamcode", "name", "team").toString();		
		ArrayList<ArrayList<String>> finalResults = new ArrayList<ArrayList<String>>();		
		if(team1=="-1"){
			System.out.println("Unable to find team 1");
			return finalResults;
		}
		if(team2 == "-1") {
			System.out.println("Unable to find team 2");
			return finalResults;
		}
	     
	     //BUILD A VICTORY CHAIN OF ONE LINK (team A beats team B)	     
	     /* Basic syntax of a one-link victory chain (example: team 5 and 8) is:
	      	SELECT G1.teamcode, G2.teamcode, G1.gamecode, G2.gamecode, G1.points, G2.points
			FROM teamgamestatistics AS G1 
			INNER JOIN teamgamestatistics AS G2 ON G1.gamecode = G2.gamecode AND G1.year = G2.year AND G1.points > G2.points 
			WHERE G1.teamcode=5 AND G2.teamcode=8
	      */
				
	     String baseTableName = new String("teamgamestatistics");
	     String compactName = new String("G");
	     boolean successful=false;
	     int round = 1;	     
	     String selectTeamcodes = new String();
	     String selectGamecodes = new String();
	     String selectPoints = new String();
	     String selectYear = new String();
	     
	     //Define the foreign keys for the join operations (here: gamecode and year)
	     //example output: "G1.gamecode = G2.gamecode AND G1.year = G2.year"
	     Vector<String> keys = new Vector<String>();
	     keys.add("gamecode");
	     keys.add("year");
	     String onKeys = new String();
	     for(int i=0; i < keys.size(); i++){
		     	onKeys += compactName + "1." + keys.get(i) + " = " + compactName + "2." + keys.get(i);
		     	if(i!= keys.size()-1){
		     		onKeys+=" AND ";
		     	}
		     }	    

	     //SELECT STATEMENTS	     
	     //Example selectTeamCodes: "G1.teamcode, G2.teamcode"
	     for(int i=1; i < 3; i++) {
	    	 selectTeamcodes += compactName + String.valueOf(i) + ".teamcode, ";
	    	 selectPoints += compactName + String.valueOf(i) + ".points";
	    	 if(i!=2) {
	    		 selectPoints += ", ";
	    	 }
	    	 else selectPoints += "\n";
	     }   
	     selectGamecodes += compactName + 1 + ".gamecode, ";
	     selectYear += compactName + 1 + ".year, ";
	     	         
	     String currentSelect = "SELECT " + selectTeamcodes + selectGamecodes + selectYear + selectPoints;
	     String currentFrom = "FROM " + baseTableName + " AS G1 \n";
	     String currentJoin = "INNER JOIN " + baseTableName + " AS G2 ON " + onKeys + " AND G1.points > G2.points \n";
	     String currentWhere = "WHERE G1.teamcode=" + team1 + " AND G2.teamcode=" + team2;
	     String currentQuery = currentSelect + currentFrom + currentJoin + currentWhere;
	     
		 if(printQuery) {System.out.println(currentQuery);}

		 	//Push single-link query to database, mark if successful
		    try{
		         //create a statement object
		           Statement stmt = conn.createStatement();
		           //send statement to DBMS
		           ResultSet result = stmt.executeQuery(currentQuery);
		           ResultSetMetaData metadata = result.getMetaData();
		           int columns = metadata.getColumnCount();
		           
		           //OUTPUT
		           int counter = 0;
		           while (result.next()) {
		        	   if(!successful) {
		        		   finalResults.add(new ArrayList<String>());
		        		   for (int i = 1; i <= columns; i++) {
				        	   finalResults.get(counter).add(metadata.getColumnName(i));
		        			   //System.out.print(metadata.getColumnName(i) + "\t");
		        		   }
		        		   //System.out.println();
		        	   }
		        	   counter++;
	        		   finalResults.add(new ArrayList<String>());
		        	   for (int i = 1; i <= columns; i++) {
		        		   finalResults.get(counter).add(result.getString(i));
		        	   }
			           //System.out.println(result.getString("G1.teamcode"));
			           successful=true;
		           }
		       } catch (Exception e){
		         System.out.println("Malformed query or error accessing database.");
		     }
		    
		 //MULTI-LINK VICTORY CHAIN:
		 //iteratively append inner joins (and associated tweaks to query)
		 //each loop represents the testing of another chain link
		 //entire question answered in single SQL query -- very fast
		 //example query: 
		    /*
		     	SELECT G1.teamcode, G2.teamcode, G4.teamcode, G6.teamcode, G1.gamecode, G3.gamecode, G5.gamecode, G1.year, G3.year, G5.year, G1.points, G2.points, G3.points, G4.points, G5.points, G6.points
				FROM teamgamestatistics AS G1 
				INNER JOIN teamgamestatistics AS G2 ON G1.gamecode = G2.gamecode AND G1.year = G2.year AND G1.points > G2.points 
				INNER JOIN teamgamestatistics AS G3 ON G2.teamcode = G3.teamcode
				INNER JOIN teamgamestatistics AS G4 ON G3.gamecode = G4.gamecode AND G3.points > G4.points
				INNER JOIN teamgamestatistics AS G5 ON G4.teamcode = G5.teamcode
				INNER JOIN teamgamestatistics AS G6 ON G5.gamecode = G6.gamecode AND G5.points > G6.points
				WHERE G1.teamcode=5 AND G6.teamcode=8
		     */
	     
	     while(!successful && round <= maxVictoryChainLength) {	   	 
	 	    round++;
		    selectTeamcodes= compactName + "1.teamcode, ";
		    selectGamecodes= "";
		    selectYear = "";
		    selectPoints="";
		    
		    //Build select statements
		    //Note differences between for loops: 
		    //For example, we want only one gamecode for every two teamcodes
		     for(int i=2; i <= 2*round; i+=2) {
		    	 selectTeamcodes += compactName + String.valueOf(i) + ".teamcode, ";
		     }
		     for(int i=1; i <= 2*round; i+=2) {
		    	 selectGamecodes += compactName + String.valueOf(i) + ".gamecode, ";
		    	 selectYear += compactName + String.valueOf(i) + ".year, ";
		     }
		     for(int i=1; i <= 2*round; i++) {
		    	 selectPoints += compactName + String.valueOf(i) + ".points";
		    	 if(i!=2*round) {
		    		 selectPoints += ", ";
		    	 }
		    	 else selectPoints += "\n";
		     }
		    
		    currentSelect = "SELECT " + selectTeamcodes + selectGamecodes + selectYear + selectPoints;
		    currentWhere = "WHERE G1.teamcode=" + team1 + " AND G" + round*2 + ".teamcode=" + team2;

		    //Finish all appends to this round's SQL query
			currentJoin += "INNER JOIN " + baseTableName + " AS " + compactName + String.valueOf(2*round-1);
			currentJoin += " ON " + compactName + String.valueOf(2*round-2) + ".teamcode = " + compactName + String.valueOf(2*round-1) + ".teamcode\n";
			currentJoin += "INNER JOIN " + baseTableName + " AS " + compactName + String.valueOf(2*round);
			currentJoin += " ON " + compactName + String.valueOf(2*round-1) + ".gamecode = " + compactName + String.valueOf(2*round) + ".gamecode";
			currentJoin += " AND " + compactName + String.valueOf(2*round-1) + ".points > " + compactName + String.valueOf(2*round) + ".points\n";
		    currentQuery = currentSelect + currentFrom + currentJoin + currentWhere;
		    
		    if(printQuery) {System.out.println(currentQuery);}
		    
		    //Try query
		    try{
		         //create a statement object
		           Statement stmt = conn.createStatement();
		           //send statement to DBMS
		           ResultSet result = stmt.executeQuery(currentQuery);
		           ResultSetMetaData metadata = result.getMetaData();
		           int columns = metadata.getColumnCount();
		           int counter = 0;

		           //OUTPUT
		           while (result.next()) {
		        	   //print column names only once
		        	   if(!successful) {
		        		   finalResults.add(new ArrayList<String>());
		        		   for (int i = 1; i <= columns; i++) {
		        			   //System.out.print(metadata.getColumnName(i) + "\t");
		        			   finalResults.get(counter).add(metadata.getColumnName(i));
		        		   }
		        		   //System.out.println();
		        	   }
		        	   counter++;
	        		   finalResults.add(new ArrayList<String>());
		        	   for (int i = 1; i <= columns; i++) {
		        		   finalResults.get(counter).add(result.getString(i));
		        		   //System.out.print(result.getString(i) + "\t");
		        	   }
		             successful=true;
		           }
		       } catch (Exception e){
		         System.out.println("Malformed query or error accessing database..");
		     }
	     }//end of while loop
	     
	     if(!successful) {
	    	 System.out.println("Tested link lengths up to " + maxVictoryChainLength + ": could not find a victory chain");
	     }
	     return finalResults;
	}
	
	//To-Do: if time, build custom objects victory and victory chains
	//Would allow for more finegrained interaction than just single toString() call
//	private void buildVictoryChainArray() {
//	}
	  
    
    //Print all 
	public String toString()
	{
		String toPrint = new String();
		toPrint += myTeam + " IS FACTUALLY, OBJECTIVELY BETTER THAN " + enemyTeam + ":\n";		

// // uncomment to print all available data
//    	for	(int i = 0; i< results.size(); i++) {
//    	   	 for(int j=0; j< results.get(i).size();j++){
//    	   		 toPrint += results.get(i).get(j) + "\t";
//    	   	 }
//    	   	 toPrint+="\n";
//    	}
    	    
		//PRINT VICTORY CHAINS IN NATURAL LANGUAGE
    	    
		//substitute team names for team codes (stores in hashmap to avoid unnecessary database lookups)   	    
    	Map<String,String> teams = new HashMap<String,String>();   	    
    	int numTeamColumns = 0;
    	int numYearColumns = 0; 
    	int numPointColumns = 0;
    	int startingPos = 0;
    	    
   	 	for(int j = 0; j < results.get(0).size(); j++){ 
   	 		if(results.get(0).get(j).contains("teamcode")) { numTeamColumns++; }
   	 		else if(results.get(0).get(j).contains("year")) { numYearColumns++; }
   	 		else if(results.get(0).get(j).contains("points")) { numPointColumns++; }
   	 	}
   	 	
//   	   	 	System.out.println("num team columns: " + numTeamColumns);
//   	   	 	System.out.println("num year columns: " + numYearColumns);
//   	   	 	System.out.println("num point columns: " + numPointColumns);
   	 	
   	 	for(int i = 1; i < results.size(); i++) {
   	 		//Team Columns
   	 		for(int j = 0; j < numTeamColumns; j++){
  	   			String value = teams.get(results.get(i).get(j));
   				if (value==null) {
   					value = lookupStringFromCode(new BigInteger(results.get(i).get(j)), "name", "teamcode", "team");
   					teams.put(results.get(i).get(j), value);
   				}
   				toPrint += value;
   				if(j == 0) {
   					toPrint += " beat ";
   				 }
   				else if (j <= numTeamColumns - 2) {
   					toPrint += " which beat ";
   				}
   				else {
   					toPrint += ".\n";
   				}   	 			
   	 		}
   	 		//Year columns
   	 		startingPos = numTeamColumns;
   	 		while(!results.get(0).get(startingPos).contains("year")) {
   	 			startingPos++;
   	 		}
   	 		for(int j = startingPos; j < numYearColumns + startingPos; j++){
   	 			if (numYearColumns == 1) {
   	 				toPrint += "This game occurred in " + results.get(i).get(j) + ".\n";
   	 			}
   	 			else {
   	   	 			if(j==startingPos) {
   	   	 				toPrint += "These games occurred in " + results.get(i).get(j);
   	   	 			}
  	   				else if (j < numYearColumns + startingPos - 1){
  	   					toPrint += ", " + results.get(i).get(j);
   	   	 			}
  	   				else {
  	   					toPrint += " and " + results.get(i).get(j) + ", respectively.\n";
  	   				}
   	 			}
   	 		}
   			//Point Columns
   	 		startingPos = numTeamColumns + numYearColumns;
   	 		while(!results.get(0).get(startingPos).contains("points")) {
   	 			startingPos++;
   	 		}
   	 		for(int j = startingPos; j < numPointColumns + startingPos - 1; j+=2){
   	 			if (numPointColumns == 2) {
   	 				toPrint += "The score was " + results.get(i).get(j) + " to " + results.get(i).get(j+1) + ".\n";
   	 			}
   	 			else {
	   	 			if(j==startingPos) {
	   	 				toPrint += "The scores were " + results.get(i).get(j) + " to " + results.get(i).get(j+1);
   	   	 			}
  	   				else if (j < numPointColumns + startingPos - 2){
  	   					toPrint += ", " + results.get(i).get(j) + " to " + results.get(i).get(j+1);
   	   	 			}
  	   				else {
  	   					toPrint += " and " + results.get(i).get(j) + " to " + results.get(i).get(j+1) + ", respectively.\n";
  	   				}
   	 			}
   	 		}
	   	 toPrint += "\n";
	    }
		return toPrint;
	}

	public void rebuildResults()
	{
		this.results = buildQ1();
	}

	public static Connection getConn()
	{
		return conn;
	}

	public static void setConn(Connection conn)
	{
		Q1.conn = conn;
	}

	public boolean isPrintQuery()
	{
		return printQuery;
	}

	public void setPrintQuery(boolean printQuery)
	{
		this.printQuery = printQuery;
	}

//	public ArrayList<LinkedList<String>> getVictoryChainArray()
//	{
//		return victoryChainArray;
//	}
//
//	public void setVictoryChainArray(ArrayList<LinkedList<String>> victoryChainArray)
//	{
//		this.victoryChainArray = victoryChainArray;
//	}

	public String getMyTeam()
	{
		return myTeam;
	}

	public void setMyTeam(String myTeam)
	{
		this.myTeam = myTeam;
	}

	public String getEnemyTeam()
	{
		return enemyTeam;
	}

	public void setEnemyTeam(String enemyTeam)
	{
		this.enemyTeam = enemyTeam;
	}

	public Integer getMaxVictoryChainLength()
	{
		return maxVictoryChainLength;
	}

	public void setMaxVictoryChainLength(Integer maxVictoryChainLength)
	{
		this.maxVictoryChainLength = maxVictoryChainLength;
	}
}
