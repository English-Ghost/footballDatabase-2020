
	  
	package dbManager;

	import java.math.BigInteger;
	import java.sql.Connection;
	import java.sql.ResultSet;
	import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
	import java.util.ArrayList;
	import java.util.HashMap;
	//import java.util.LinkedList;
	import java.util.Map;
	import java.util.Vector;

	//Calculates all victory chains at the smallest possible length
	//eg if the smallest length chain is 3, calculates ALL chains of length three
	//Setters and getters to set home and away teams, rebuildResults
	//Maximum chain length defaults to 4 but can be changed; can also print sql queries for troubleshooting
	public class Q2
	{	
		private ArrayList<String> results;
		static Connection conn;
		private boolean printQuery;    	    
//	    private ArrayList<LinkedList<Victory>> victoryChainArray = new ArrayList<LinkedList<Victory>>();
	    private String firstPlayerfName;
	    private String firstPlayerlName;
	    private String secondPlayerfName;
	    private String secondPlayerlName;
		private Integer minimumNumberOfConnections;
		private int count;
		private boolean connected;
	    //CONSTRUCTOR: build results of query
	    public Q2(String firstPlayerfName,String firstPlayerlName, String secondPlayerfName, String secondPlayerlName, Connection conn)
		{
			super();
	    	Q2.conn=conn;
	    	this.firstPlayerfName = firstPlayerfName;
		    this.firstPlayerlName = firstPlayerlName;
		    this.secondPlayerfName = secondPlayerfName;
		    this.secondPlayerlName = secondPlayerlName;
	    	count = 1;
	    	connected = false;
		}
		
	    private void checkConnections(ArrayList<String>possibleConnections1,ArrayList<String>possibleConnections2) {
	    	int i = 0;
	    	int j = 0;
	    	for(i = 0;i < possibleConnections1.size();i++) {
	    		 for(j = 0;i < possibleConnections2.size();j++) {
	    			 if(possibleConnections1.get(i) == possibleConnections2.get(j)) {
	    				 connected = true;
	    				 break;
	    			 }
	    		 }
	    		 if(connected) {
	    			 break;
	    		 }
	    	 }
	    	if(!connected) {
	    		for(int k = 0;k < possibleConnections2.size();k++) {
	    			ArrayList<String> fNames = null;
	    			ArrayList<String> lNames = null;
	    			String sqlStatement = "SELECT DISTINCT(firstName) from player WHERE hometown = " + possibleConnections2.get(i);
	    	    	try {
	    	    		
	    	    		Statement stmt = conn.createStatement();
	    				ResultSet result = stmt.executeQuery(sqlStatement);
	    				i = 0;
	    				while (result.next()) {
	    					fNames.add(result.getString(i));
	    					i++;
	    				}
	    			} catch (SQLException e) {
	    	// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    	    	sqlStatement = "SELECT DISTINCT(lastName) from player WHERE hometown = " + possibleConnections2.get(i);
	    	    	try {
	    	    		
	    	    		Statement stmt = conn.createStatement();
	    				ResultSet result = stmt.executeQuery(sqlStatement);
	    				i = 0;
	    				while (result.next()) {
	    					lNames.add(result.getString(i));
	    					i++;
	    				}
	    			} catch (SQLException e) {
	    	// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
		    		//write sql query to get new player name from kth point in array
	    	    	for(j = 0;j < fNames.size();j++) {
	    	    		possibleConnections2 = getPossibleConnections(fNames.get(i),lNames.get(i));
			    		checkConnections(possibleConnections1,possibleConnections2);
	    	    	}
	    		}
	    	}
	    	else {
	    		results.add(possibleConnections2.get(j).toString());
	    	}
	    }
	    private ArrayList<String> getPossibleConnections(String playerfName,String playerlName){
	    	ResultSet result;
	    	ArrayList<String> possConnections = null;
	    	//get playercode
	    	ArrayList<String> playercodes = null;
	    	String teams  = "SELECT DISTINCT(playercode) from player WHERE lastname = '" +playerlName + "' AND firstname = '"+playerfName+"'";
	    	//get all hometowns
	    	String sqlStatement = "";
	    	try {
	    		
	    		Statement stmt = conn.createStatement();
				result = stmt.executeQuery(sqlStatement);
				int i = 0;
				while (result.next()) {
					playercodes.add(result.getString(i));
					i++;
				}
			} catch (SQLException e) {
	// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	String hometowns = "SELECT DISTINCT(hometown) from player WHERE lastname = '" +playerlName + "' AND firstname = '"+playerfName+"'";
	    	try {
	    		Statement stmt = conn.createStatement();
	    		sqlStatement = hometowns;
				result = stmt.executeQuery(sqlStatement);
				ResultSetMetaData metadata = result.getMetaData();
				int i = 0;
				while (result.next()) {
					possConnections.add(result.getString(i));
					i++;
				}
			} catch (SQLException e) {
	// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	for(int i = 0;i < playercodes.size();i++) {
	    		try {
		    		Statement stmt = conn.createStatement();
		    		sqlStatement = "SELECT DISTINCT(gamecode) from playergamestatistics WHERE playercode = " + playercodes.get(i).toString();
					result = stmt.executeQuery(sqlStatement);
					ResultSetMetaData metadata = result.getMetaData();
					i = 0;
					while (result.next()) {
						possConnections.add(result.getString(i));
						i++;
					}
				} catch (SQLException e) {
		// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	try {
	    		Statement stmt = conn.createStatement();
				result = stmt.executeQuery(sqlStatement);
				ResultSetMetaData metadata = result.getMetaData();
				int i = 0;
				while (result.next()) {
					possConnections.add(result.getString(i));
					i++;
				}
			} catch (SQLException e) {
	// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return possConnections;
	    }
		private void buildQ2() {
		     boolean connected = false;
		     ArrayList<String> possibleConnections1 = getPossibleConnections(firstPlayerfName,firstPlayerlName);//get possible connections(Games, common coaches, common teams, and common home towns)
	    	 ArrayList<String> possibleConnections2 = getPossibleConnections(secondPlayerfName,secondPlayerlName);
		     checkConnections(possibleConnections1,possibleConnections2);
		}
	    //Print all 
		public static Connection getConn()
		{
			return conn;
		}

		public static void setConn(Connection conn)
		{
			Q2.conn = conn;
		}
		
	}
