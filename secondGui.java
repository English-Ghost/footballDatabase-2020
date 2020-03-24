import java.sql.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.*;
import java.math.BigInteger;
import java.sql.Connection;



public class secondGui extends JFrame {

	// all JFframe variables and other vraibles
	static JFrame choiceFrame;
	static String[] tableNames = { "conferences", "drive", "foreigner", "game", "gamestatistics", "kickoff",
			"kickoffreturn", "pass", "play", "player", "playergamestatistics", "playerseason", "punt", "puntreturn",
			"receptions", "rush", "stadium", "team", "teamgamestatistics" };

	static String[] tableConstraints = { "DISTINCT", "ORDER BY", "WHERE", "AND", "OR", "COUNT" };

	static String[] questionChoices = { "Question 1", "Question 2", "Question 3", "Question 4" };

	static Vector<String> columnNames = new Vector<String>();
	static Vector<String> rowNames = new Vector<String>();

	static String serverResponse = "NO CHANGES";
	static JPanel userEntryPan = new JPanel();
	static JLabel dropBoxLabel = new JLabel();
	static JComboBox<String> baseTable = new JComboBox<String>(tableNames);
	static String baseTableOption;
	static JPanel joinPan = new JPanel();
	static JLabel checkBoxLabel = new JLabel();
	static JCheckBox confTableCheck = new JCheckBox(tableNames[0]);
	static boolean confTableFlag = false;
	static JCheckBox driveTableCheck = new JCheckBox(tableNames[1]);
	static boolean driveTableFlag = false;
	static JCheckBox foreTableCheck = new JCheckBox(tableNames[2]);
	static boolean foreTableFlag = false;
	static JCheckBox gameTableCheck = new JCheckBox(tableNames[3]);
	static boolean gameTableFlag = false;
	static JCheckBox gamestatTableCheck = new JCheckBox(tableNames[4]);
	static boolean gamestatTableFlag = false;
	static JCheckBox koTableCheck = new JCheckBox(tableNames[5]);
	static boolean koTableFlag = false;
	static JCheckBox korTableCheck = new JCheckBox(tableNames[6]);
	static boolean korTableFlag = false;
	static JCheckBox passTableCheck = new JCheckBox(tableNames[7]);
	static boolean passTableFlag = false;
	static JCheckBox playTableCheck = new JCheckBox(tableNames[8]);
	static boolean playTableFlag = false;
	static JCheckBox playerTableCheck = new JCheckBox(tableNames[9]);
	static boolean playerTableFlag = false;
	static JCheckBox pgsTableCheck = new JCheckBox(tableNames[10]);
	static boolean pgsTableFlag = false;
	static JCheckBox psTableCheck = new JCheckBox(tableNames[11]);
	static boolean psTableFlag = false;
	static JCheckBox puntTableCheck = new JCheckBox(tableNames[12]);
	static boolean puntTableFlag = false;
	static JCheckBox prTableCheck = new JCheckBox(tableNames[13]);
	static boolean prTableFlag = false;
	static JCheckBox recepTableCheck = new JCheckBox(tableNames[14]);
	static boolean recepTableFlag = false;
	static JCheckBox rushTableCheck = new JCheckBox(tableNames[15]);
	static boolean rushTableFlag = false;
	static JCheckBox stadTableCheck = new JCheckBox(tableNames[16]);
	static boolean stadTableFlag = false;
	static JCheckBox teamTableCheck = new JCheckBox(tableNames[17]);
	static boolean teamTableFlag = false;
	static JCheckBox tgsTableCheck = new JCheckBox(tableNames[18]);
	static boolean tgsTableFlag = false;
	static JCheckBox allTableCheck = new JCheckBox("All possible tables");
	static boolean allTableFlag = false;
	static JPanel tablePan = new JPanel();
	static JButton createTableBut = new JButton();
	static JPanel searchPan = new JPanel();
	static JLabel searchLabel = new JLabel();
	static JTextField searchText = new JTextField(30);
	static JLabel searchLabel2 = new JLabel();
	static JComboBox<String> constraintBox = new JComboBox<String>();
	static String constraintBoxOption = "";
	static JPanel requestPan = new JPanel();
	static JButton requestButton = new JButton();
	static JPanel dataRespPan = new JPanel();
	static JTextArea serverRespText = new JTextArea();
	static JButton sendToFileBut = new JButton();
	static JComboBox<String> columnBox;
	static Connection conn = null;
	static String sqlStatement = "";
	static String FROMStmt = "";
	static String WHEREStmt = "";
	static String JOINStmt = "";
	static String CONDITIONStmt = "";
	static Statement stmt = null;
	static String filterTerm = "";
	static Vector<String> userChoices = new Vector<String>();
	static Vector<String> joinedColumnNames = new Vector<String>();
	static int numRows;
//  static String user = "sunilp";
//  static String pwd = "127001211";
	static String questionBoxOption = questionChoices[0];

	private static void connectDB(String db, String user, String pwd) {
		try {
			Class.forName("org.postgresql.Driver");
			String temp = new String(pwd);
			conn = DriverManager.getConnection("jdbc:" + db, user, temp);

		} catch (Exception e1) {
			e1.printStackTrace();
			System.err.println(e1.getClass().getName() + ": " + e1.getMessage());
			System.exit(0);
		} // end try catch
	}

	private static Set<String> buildJoinStatement(Connection conn, String mainTableChoice, String joinType) {
		// GET FIRST-CHOICE TABLE FOREIGN KEYS
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
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Set<String> noDups = new HashSet<String>(pkTableNames);
		JOINStmt = "";
		for (String join : noDups) {
			if (userChoices.contains(join)) {
				JOINStmt += joinType + " " + join + " ";
			}
		}
		return noDups;
	}

	private static void buildWhereStatement(String mainTableChoice) {
		// GET FIRST-CHOICE TABLE COLUMNS
		WHEREStmt = "";
		WHEREStmt += "WHERE ";
		WHEREStmt += constraintBoxOption + " = " + filterTerm;

	}

	private static void populateColumns(String baseTableOption) {
//create a statement object
		try {
			stmt = conn.createStatement();
// create an SQL statement
			FROMStmt = "SELECT * FROM " + baseTableOption + " ";
			Set<String> noDups = buildJoinStatement(conn, baseTableOption, "NATURAL LEFT JOIN");
			if (userChoices.size() == 1 || noDups.size() == 0) {
				try {
					DatabaseMetaData dbmd = conn.getMetaData();
					ResultSet rs = dbmd.getColumns(null, null, baseTableOption, null);
					while (rs.next()) {
						columnNames.add(rs.getString("COLUMN_NAME"));
					}
				} catch (SQLException e3) {
					e3.printStackTrace();
				}
			} else {

				try {
					DatabaseMetaData dbmd = conn.getMetaData();
					for (String i : noDups) {
						if (userChoices.contains(i)) {
							ResultSet rs = dbmd.getColumns(null, null, i, null);
							while (rs.next()) {
								columnNames.add(rs.getString("COLUMN_NAME"));
							}
						}
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			sqlStatement += FROMStmt + JOINStmt;
			System.out.println("SQL Statement: " + sqlStatement);
		} catch (SQLException e) {
// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void executeQuery() {
		ResultSet result;
		FROMStmt = "SELECT * FROM " + baseTableOption + " ";
		sqlStatement += WHEREStmt;
		try {
			result = stmt.executeQuery(sqlStatement);
			ResultSetMetaData metadata = result.getMetaData();

// PRINT EVERYTHING
// Adapted from
// https://stackoverflow.com/questions/24943894/how-do-you-get-values-from-all-columns-using-resultset-getbinarystream-in-jdbc
			int columns = metadata.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				joinedColumnNames.add(metadata.getColumnName(i));
			}
			System.out.println();
			serverResponse = "";
			numRows = 0;
			while (result.next()) {
				numRows++;
				for (int i = 1; i <= columns; i++) {
					if (i != columns) {
						serverResponse += result.getString(i) + ", ";
					} else {
						serverResponse += result.getString(i) + "\n";
					}
				}
			}
		} catch (SQLException e) {
// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static JFrame loginFrame;

	protected static void popupLogin() {
		loginFrame = new JFrame("LOGIN");
		GridBagLayout gridLayout = new GridBagLayout();
		loginFrame.getContentPane().setBackground(new Color(0, 0, 0));
		loginFrame.getContentPane().setLayout(gridLayout);

		GridBagConstraints frameVals = new GridBagConstraints();
		frameVals.fill = GridBagConstraints.HORIZONTAL;
		frameVals.anchor = GridBagConstraints.WEST;
		frameVals.gridx = 0;
		frameVals.gridy = 0;
		frameVals.ipady = 10;
		frameVals.weightx = .5;

		GridBagConstraints panelVals = new GridBagConstraints();
		panelVals.anchor = GridBagConstraints.WEST;
		panelVals.gridx = 0;
		panelVals.gridy = 0;

		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridBagLayout());

		JLabel databaseLabel = new JLabel();
		databaseLabel.setText("DATABASE: ");
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		loginPanel.add(databaseLabel, panelVals);

		JTextField databaseBar = new JTextField(30);
		databaseBar.setText("");
		panelVals.gridx = 1;
		panelVals.gridy = 0;
		loginPanel.add(databaseBar, panelVals);

		JLabel usernameLabel = new JLabel();
		usernameLabel.setText("USERNAME: ");
		panelVals.gridx = 0;
		panelVals.gridy = 1;
		loginPanel.add(usernameLabel, panelVals);

		JTextField usernameBar = new JTextField(30);
		usernameBar.setText("");
		panelVals.gridx = 1;
		panelVals.gridy = 1;
		loginPanel.add(usernameBar, panelVals);

		JLabel passwordLabel = new JLabel();
		passwordLabel.setText("PASSWORD: ");
		panelVals.gridx = 0;
		panelVals.gridy = 2;
		loginPanel.add(passwordLabel, panelVals);

		JPasswordField passwordBar = new JPasswordField(30);
		panelVals.gridx = 1;
		panelVals.gridy = 2;
		loginPanel.add(passwordBar, panelVals);

		JButton connectButtonDatabase = new JButton();
		connectButtonDatabase.setText("<html>CONNECT<br>TO<br>DATABASE</html>");
		connectButtonDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pwd = new String(passwordBar.getPassword());
				connectDB(databaseBar.getText(), usernameBar.getText(), pwd);
				loginFrame.dispose();
				popupDatabaseWindow();
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 3;
		loginPanel.add(connectButtonDatabase, panelVals);

		JButton connectButtonQuestion = new JButton();
		connectButtonQuestion.setText("<html>CONNECT<br>TO<br>QUESTIONS</html>");
		connectButtonQuestion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginFrame.dispose();
				popupQuestionWindow();
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 3;
		panelVals.anchor = GridBagConstraints.EAST;

		loginPanel.add(connectButtonQuestion, panelVals);

		panelVals.anchor = GridBagConstraints.WEST;

		loginFrame.add(loginPanel, frameVals);

		loginFrame.pack();
		loginFrame.setVisible(true);

	}

	static JFrame questFrame;

	public static String q3(String userIn) {
		String output = null;
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/section900team1",
					"jcedillo269", "426004686");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} // end try catch
			// System.out.println("Opened Database Successfully");
		try {
			Statement stmt = conn.createStatement();
			String selectedTeam = null;
			try {
				String modInput = "'" + userIn + "'";
				ResultSet r = stmt.executeQuery("SELECT teamcode from team where year = 2013 and name = " + modInput);
				while (r.next()) {
					selectedTeam = r.getString(1);
				}
			} catch (Exception e) {
				System.out.println("Could not find team name");
			}
			String sqlStatement = "SELECT gamecode\r\n" + "from teamgamestatistics where teamcode = " + selectedTeam;
			// send statement to DBMS
			ResultSet result = stmt.executeQuery(sqlStatement);
			ResultSetMetaData metadata = result.getMetaData();
			int columns = metadata.getColumnCount();
			Vector<String> gameCodes = new Vector<String>(); // vector of gamecodes played by selected team
			while (result.next()) {
				String row = "";
				for (int i = 1; i <= columns; i++) {
					gameCodes.add(result.getString(i));
					row += result.getString(i) + ", ";
				}
				// System.out.println(row);
			}
			Vector<String> oppTeam = new Vector<String>();
			ResultSet result2 = null;
			ResultSetMetaData metadata2 = null;
			for (String i : gameCodes) {
				// System.out.println(i);
				String query = "SELECT teamcode,gamecode,rushyard from teamgamestatistics where " + "teamcode != "
						+ selectedTeam + " and gamecode = " + i;
				result2 = stmt.executeQuery(query);
				metadata2 = result2.getMetaData();
				int columns2 = metadata2.getColumnCount();
				while (result2.next()) {
					String row = "";
					for (int j = 1; j <= columns2; j++) {
						oppTeam.add(result2.getString(j));
						row += result2.getString(j) + ", ";
					}
				}
			}
			columns = metadata2.getColumnCount();
			int max = 0;
			String maxTeam = null;
			HashMap<String, Integer> teamRushYards = new HashMap<String, Integer>();
			for (int i = 0; i < oppTeam.size(); i += 3) {
				teamRushYards.put(oppTeam.elementAt(i), 0);
			}
			for (int i = 2; i < oppTeam.size(); i = i + 3) {
				int temp = teamRushYards.get(oppTeam.elementAt(i - 2));
				teamRushYards.put(oppTeam.elementAt(i - 2), temp + Integer.parseInt(oppTeam.elementAt(i)));
			}
			for (Map.Entry j : teamRushYards.entrySet()) {
				if ((int) j.getValue() > max) {
					max = (int) j.getValue();
					maxTeam = (String) j.getKey();
				}
			}
			// System.out.println("Max Team: " + maxTeam + " Max Value: " + max);
			String query = "SELECT name from team where year = 2005 and teamcode = " + maxTeam;
			ResultSet result3 = null;
			ResultSetMetaData metadata3 = null;
			result3 = stmt.executeQuery(query);
			metadata3 = result3.getMetaData();
			int columns3 = metadata3.getColumnCount();
			while (result3.next()) {
				// System.out.println(result3.getString(1));
				output = result3.getString(1);
			}
			output += ", " + max + " yards.";

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error accessing Database.");
		}
		return output;
	}

	public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin,
			final double limitMax) {
		return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
	}

	public static Vector<String> q4(String conf) {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/section900team1",
					"jcedillo269", "426004686");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} // end try catch
		System.out.println("Opened Database Successfully");
		String selectedConference = null;
		try {
			Statement stmt = conn.createStatement();
			String modInput = "'" + conf + "'";
			ResultSet r = stmt
					.executeQuery("SELECT confcode from conferences where year = 2013 and name = " + modInput);
			while (r.next()) {
				selectedConference = r.getString(1);
			}
		} catch (Exception e) {
			System.out.println("Could not find team name");
		}
		Vector<String> output = new Vector<String>();

		try {
			Statement stmt = conn.createStatement();
			//System.out.println("Conference: " + selectedConference);
			String sqlStatement = "SELECT team.teamcode, team.name, conferences.name AS conference, T1.roadwins, T2.roadlosses, T3.homewins, T4.homelosses, T5.homepoints, T6.roadpoints\r\n"
					+ "FROM team\r\n" + "INNER JOIN\r\n" + "	conferences\r\n"
					+ "	ON team.conferencecode = conferences.confcode AND team.year = conferences.year\r\n"
					+ "INNER JOIN\r\n" + "	(SELECT G1.teamcode AS teamcode, count(G1.teamcode) AS roadwins\r\n"
					+ "	FROM teamgamestatistics AS G1 \r\n"
					+ "	INNER JOIN teamgamestatistics AS G2 ON G1.gamecode = G2.gamecode AND G1.year = G2.year AND G1.points > G2.points \r\n"
					+ "	INNER JOIN game ON game.gamecode = G1.gamecode AND game.year = G1.year\r\n"
					+ "	WHERE visitteam = G1.teamcode\r\n" + "	GROUP BY G1.teamcode) AS T1\r\n"
					+ "	ON team.teamcode = T1.teamcode\r\n" + "INNER JOIN \r\n"
					+ "	(SELECT G1.teamcode AS teamcode, count(G1.teamcode) AS roadlosses\r\n"
					+ "	FROM teamgamestatistics AS G1 \r\n"
					+ "	INNER JOIN teamgamestatistics AS G2 ON G1.gamecode = G2.gamecode AND G1.year = G2.year AND G1.points < G2.points \r\n"
					+ "	INNER JOIN game ON game.gamecode = G1.gamecode AND game.year = G1.year\r\n"
					+ "	WHERE visitteam = G1.teamcode\r\n" + "	GROUP BY G1.teamcode) AS T2\r\n"
					+ "	ON team.teamcode = T2.teamcode\r\n" + "INNER JOIN \r\n"
					+ "	(SELECT G1.teamcode AS teamcode, count(G1.teamcode) AS homewins\r\n"
					+ "	FROM teamgamestatistics AS G1 \r\n"
					+ "	INNER JOIN teamgamestatistics AS G2 ON G1.gamecode = G2.gamecode AND G1.year = G2.year AND G1.points > G2.points \r\n"
					+ "	INNER JOIN game ON game.gamecode = G1.gamecode AND game.year = G1.year\r\n"
					+ "	WHERE hometeam = G1.teamcode\r\n" + "	GROUP BY G1.teamcode) AS T3\r\n"
					+ "	ON team.teamcode = T3.teamcode\r\n" + "INNER JOIN\r\n"
					+ "	(SELECT G1.teamcode AS teamcode, count(G1.teamcode) AS homelosses\r\n"
					+ "	FROM teamgamestatistics AS G1 \r\n"
					+ "	INNER JOIN teamgamestatistics AS G2 ON G1.gamecode = G2.gamecode AND G1.year = G2.year AND G1.points < G2.points \r\n"
					+ "	INNER JOIN game ON game.gamecode = G1.gamecode AND game.year = G1.year\r\n"
					+ "	WHERE hometeam = G1.teamcode\r\n" + "	GROUP BY G1.teamcode) AS T4\r\n"
					+ "	ON team.teamcode = T4.teamcode\r\n" + "INNER JOIN\r\n"
					+ "	(SELECT G1.teamcode AS teamcode, sum(G1.points) AS homepoints\r\n"
					+ "	FROM teamgamestatistics AS G1 \r\n"
					+ "	INNER JOIN game ON game.gamecode = G1.gamecode AND game.year = G1.year\r\n"
					+ "	WHERE hometeam = G1.teamcode\r\n" + "	GROUP BY G1.teamcode) AS T5\r\n"
					+ "	ON team.teamcode = T5.teamcode\r\n" + "INNER JOIN\r\n"
					+ "	(SELECT G1.teamcode AS teamcode, sum(G1.points) AS roadpoints\r\n"
					+ "	FROM teamgamestatistics AS G1 \r\n"
					+ "	INNER JOIN game ON game.gamecode = G1.gamecode AND game.year = G1.year\r\n"
					+ "	WHERE visitteam = G1.teamcode\r\n" + "	GROUP BY G1.teamcode) AS T6\r\n"
					+ "	ON team.teamcode = T6.teamcode\r\n"
					+ "WHERE team.year=2013 AND (T1.roadwins + T2.roadlosses) > 9 AND (T3.homewins + T4.homelosses) > 9 AND team.conferencecode="
					+ selectedConference + "\r\n";
			ResultSet result = stmt.executeQuery(sqlStatement);
			ResultSetMetaData metadata = result.getMetaData();
			int columns = metadata.getColumnCount();

			//System.out.println();
			Vector<String> data = new Vector<String>();
			while (result.next()) {
				String row = ""; // resets row to be blank
				for (int i = 1; i <= columns; i++) {
					row += result.getString(i) + ", \t"; // appends the next column
					data.add(result.getString(i));
				}
				// System.out.println(row);
			}
			//System.out.println();
			HashMap<Double, String> nameTable = new HashMap<Double, String>();
			Vector<Double> Calculations = new Vector<Double>();
			for (int i = 1; i < data.size(); i += columns) {
				// System.out.print(data.elementAt(i)+": ");
				String teamName = data.elementAt(i);
				int awayWins = Integer.parseInt(data.elementAt(i + 2));
				int homeWins = Integer.parseInt(data.elementAt(i + 4));
				int totalWins = awayWins + homeWins;
				// System.out.print(totalWins + " , ");
				Double awayWinRatio = (double) awayWins / (double) totalWins;
				Double homeWinRatio = (double) homeWins / (double) totalWins;
				// System.out.println(awayWinRatio + " , " + homeWinRatio);
				double calc = (50 + 100 * (homeWinRatio - awayWinRatio));
				Calculations.add(calc);
				nameTable.put(calc, teamName);
			}

			Collections.sort(Calculations);
			double baseMin = Calculations.firstElement() - 35;
			double baseMax = Calculations.lastElement() + 7.5;
			double limitMin = 0.0;
			double limitMax = 100.0;
			Collections.reverse(Calculations);
			for (double i : Calculations) {
				double out = scale(i, baseMin, baseMax, limitMin, limitMax);
				Integer x = (int) Math.round(out);
				// output.add(teamName);
				output.add(nameTable.get(i));
				output.add(x.toString());
				// System.out.println(nameTable.get(i) + ": " + i +
				// "--------------------------------------" + out);
			}

			// ------------------------------------------PRINTS TABLE TO CONSOLE. CHANGE TO
			// PRINT TABLE TO GUI---------------------------------------
			for (int i = 0; i < output.size(); i += 2) {
				//System.out.println(output.elementAt(i) + "," + output.elementAt(i + 1));
			}

		} catch (Exception e) {
			System.out.println("Error getting data");
		}

		return output;
	}

	public static class Q1
	{	
		private ArrayList<ArrayList<String>> results;
		static Connection conn;
		private boolean printQuery;    	    
//	    private ArrayList<LinkedList<Victory>> victoryChainArray = new ArrayList<LinkedList<Victory>>();
	    private String myTeam;
	    private String enemyTeam;
		private Integer maxVictoryChainLength;
		
	    //CONSTRUCTOR: build results of query
	    public Q1(String myTeam, String enemyTeam, String chain, Connection conn)
		{
			super();
	    	this.myTeam = myTeam;
	    	this.enemyTeam = enemyTeam;
	    	Q1.conn=conn;
	    	this.printQuery = false;
	    	this.maxVictoryChainLength= Integer.parseInt(chain);
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
//		private void buildVictoryChainArray() {
//		}
		  
	    
	    //Print all 
		public String toString()
		{
			String toPrint = new String();
			toPrint += myTeam + " IS FACTUALLY, OBJECTIVELY BETTER THAN " + enemyTeam + ":\n";		

	// // uncomment to print all available data
//	    	for	(int i = 0; i< results.size(); i++) {
//	    	   	 for(int j=0; j< results.get(i).size();j++){
//	    	   		 toPrint += results.get(i).get(j) + "\t";
//	    	   	 }
//	    	   	 toPrint+="\n";
//	    	}
	    	    
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
	   	 	
//	   	   	 	System.out.println("num team columns: " + numTeamColumns);
//	   	   	 	System.out.println("num year columns: " + numYearColumns);
//	   	   	 	System.out.println("num point columns: " + numPointColumns);
	   	 	
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

//		public ArrayList<LinkedList<String>> getVictoryChainArray()
//		{
//			return victoryChainArray;
//		}
	//
//		public void setVictoryChainArray(ArrayList<LinkedList<String>> victoryChainArray)
//		{
//			this.victoryChainArray = victoryChainArray;
//		}

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
	
	
	
	protected static void popupQuestionWindow() {

		questFrame = new JFrame("Question Window");
		GridBagLayout gridLayout = new GridBagLayout();
		questFrame.getContentPane().setBackground(new Color(0, 0, 0));
		questFrame.getContentPane().setLayout(gridLayout);

		// layout design of frame
		GridBagConstraints frameVals = new GridBagConstraints();
		frameVals.fill = GridBagConstraints.HORIZONTAL;
		frameVals.anchor = GridBagConstraints.WEST;
		frameVals.gridx = 0;
		frameVals.gridy = 0;
		frameVals.ipady = 10;
		frameVals.weightx = .5;

		// layout design of panels
		GridBagConstraints panelVals = new GridBagConstraints();
		panelVals.anchor = GridBagConstraints.WEST;
		panelVals.gridx = 0;
		panelVals.gridy = 0;

		JPanel questionPanel = new JPanel();
		questionPanel.setLayout(new GridBagLayout());

		JLabel questionOneTeamOneLabel = new JLabel();
		questionOneTeamOneLabel.setText("Team 1:");
		panelVals.gridx = 1;
		panelVals.gridy = 0;
		questionPanel.add(questionOneTeamOneLabel, panelVals);

		JLabel questionOneTeamTwoLabel = new JLabel();
		questionOneTeamTwoLabel.setText("Team 2:");
		panelVals.gridx = 2;
		panelVals.gridy = 0;
		questionPanel.add(questionOneTeamTwoLabel, panelVals);

		JLabel questionOneVicChainLabel = new JLabel();
		questionOneVicChainLabel.setText("Max victory chain length:");
		panelVals.gridx = 3;
		panelVals.gridy = 0;
		questionPanel.add(questionOneVicChainLabel, panelVals);

		JLabel questionOneLabel = new JLabel();
		questionOneLabel.setText("Given 2 teams, create a victory chain");
		panelVals.gridx = 0;
		panelVals.gridy = 1;
		questionPanel.add(questionOneLabel, panelVals);

		JTextField questionOneTeamOne = new JTextField(20);
		questionOneTeamOne.setText("");
		panelVals.gridx = 1;
		panelVals.gridy = 1;
		questionPanel.add(questionOneTeamOne, panelVals);

		JTextField questionOneTeamTwo = new JTextField(20);
		questionOneTeamTwo.setText("");
		panelVals.gridx = 2;
		panelVals.gridy = 1;
		questionPanel.add(questionOneTeamTwo, panelVals);

		JTextField questionOneVicChain = new JTextField(20);
		questionOneVicChain.setText("");
		panelVals.gridx = 3;
		panelVals.gridy = 1;
		questionPanel.add(questionOneVicChain, panelVals);

		JLabel questionTwoPlayerOneLabel = new JLabel();
		questionTwoPlayerOneLabel.setText("Player 1:");
		panelVals.gridx = 1;
		panelVals.gridy = 2;
		questionPanel.add(questionTwoPlayerOneLabel, panelVals);

		JLabel questionTwoPlayerTwoLabel = new JLabel();
		questionTwoPlayerTwoLabel.setText("Player 2:");
		panelVals.gridx = 2;
		panelVals.gridy = 2;
		questionPanel.add(questionTwoPlayerTwoLabel, panelVals);

		JLabel questionTwoLabel = new JLabel();
		questionTwoLabel.setText("What is the shortest chain between 2 players");
		panelVals.gridx = 0;
		panelVals.gridy = 3;
		questionPanel.add(questionTwoLabel, panelVals);

		JTextField questionTwoPlayerOne = new JTextField(20);
		questionTwoPlayerOne.setText("");
		panelVals.gridx = 1;
		panelVals.gridy = 3;
		questionPanel.add(questionTwoPlayerOne, panelVals);

		JTextField questionTwoPlayerTwo = new JTextField(20);
		questionTwoPlayerTwo.setText("");
		panelVals.gridx = 2;
		panelVals.gridy = 3;
		questionPanel.add(questionTwoPlayerTwo, panelVals);

		JLabel questionThreeTeamLabel = new JLabel();
		questionThreeTeamLabel.setText("Team:");
		panelVals.gridx = 1;
		panelVals.gridy = 4;
		questionPanel.add(questionThreeTeamLabel, panelVals);

		JLabel questionThreeLabel = new JLabel();
		questionThreeLabel.setText("Rushing yards against a given team");
		panelVals.gridx = 0;
		panelVals.gridy = 5;
		questionPanel.add(questionThreeLabel, panelVals);

		JTextField questionThreeTeam = new JTextField(20);
		questionThreeTeam.setText("");
		panelVals.gridx = 1;
		panelVals.gridy = 5;
		questionPanel.add(questionThreeTeam, panelVals);

		JLabel questionFourConfLabel = new JLabel();
		questionFourConfLabel.setText("Conference:");
		panelVals.gridx = 1;
		panelVals.gridy = 6;
		questionPanel.add(questionFourConfLabel, panelVals);

		JLabel questionFourLabel = new JLabel();
		questionFourLabel.setText("Average home field advantage of teams in a conference");
		panelVals.gridx = 0;
		panelVals.gridy = 7;
		questionPanel.add(questionFourLabel, panelVals);

		JTextField questionFourConf = new JTextField(20);
		questionFourConf.setText("");
		panelVals.gridx = 1;
		panelVals.gridy = 7;
		questionPanel.add(questionFourConf, panelVals);

		questFrame.add(questionPanel, frameVals);

		JPanel questionChoicePanel = new JPanel();
		questionChoicePanel.setLayout(new GridBagLayout());

		JComboBox<String> questChoice = new JComboBox<String>(questionChoices);
		questChoice.setSelectedIndex(0);
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		questionChoicePanel.add(questChoice, panelVals);

		JButton questChoiceButton = new JButton();
		questChoiceButton.setText("<html>SELECT<br>QUESTION</html>");
		questChoiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*-------------------------------------------------------------------------------------------------------------------*/
				questionBoxOption = (String) questChoice.getSelectedItem();
				serverRespText.setText("Hit: " + questionBoxOption);

				if (questionBoxOption == "Question 1") {
					
					Connection conn = null;
				     try {
				        Class.forName("org.postgresql.Driver");
				        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/section900team1",
				           "jcedillo269", "426004686");
				     } catch (Exception e1) {
				        e1.printStackTrace();
				        System.err.println(e1.getClass().getName()+": "+e1.getMessage());
				        System.exit(0);
				     }//end try catch
				     System.out.println("Opened database successfully");
				     System.out.println();
					   Q1 answerQ1 = new Q1(questionOneTeamOne.getText(),questionOneTeamTwo.getText(),questionOneVicChain.getText(),conn);
					   //System.out.println(answerQ1.toString());
					serverRespText.setText(answerQ1.toString());
				} else if (questionBoxOption == "Question 2") {
					serverRespText.setText("Here 2");

				} else if (questionBoxOption == "Question 3") {
					String in = questionThreeTeam.getText();
					System.out.println(in);
					String resp = q3(in);
					serverRespText.setText("Here 3: " + resp);

				} else if (questionBoxOption == "Question 4") {
					
					Vector<String> out = q4(questionFourConf.getText());
					String print = "";
					for (int i = 0; i < out.size(); i += 2) {
						//System.out.println(out.elementAt(i) + "," + out.elementAt(i + 1));
						print += out.elementAt(i) + "," + out.elementAt(i + 1);
						print += "\n";
					}
					serverRespText.setText(print);

				}
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 0;

		questionChoicePanel.add(questChoiceButton, panelVals);

		frameVals.gridx = 0;
		frameVals.gridy = 1;
		questFrame.add(questionChoicePanel, frameVals);

		serverRespText.append(serverResponse);
		serverRespText.setRows(4);
		serverRespText.setColumns(30);
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		dataRespPan.add(serverRespText, panelVals);

		// button to send output to a file
		sendToFileBut.setText("<html> SEND <br> TO <br> FILE </html>");
		sendToFileBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				serverResponse = "send to file button hit";
				serverRespText.setText(serverResponse);
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 0;
		dataRespPan.add(sendToFileBut, panelVals);

		frameVals.gridy = 2;
		questFrame.add(dataRespPan, frameVals);

		questFrame.pack();
		questFrame.setVisible(true);
	}

	// creates the popup for the database window
	protected static void popupDatabaseWindow() {

		// Frame creation and layout
		choiceFrame = new JFrame("Database window");
		GridBagLayout gridLayout = new GridBagLayout();
		choiceFrame.getContentPane().setBackground(new Color(0, 0, 0));
		choiceFrame.getContentPane().setLayout(gridLayout);

		// layout design of frame
		GridBagConstraints frameVals = new GridBagConstraints();
		frameVals.fill = GridBagConstraints.HORIZONTAL;
		frameVals.anchor = GridBagConstraints.WEST;
		frameVals.gridx = 0;
		frameVals.gridy = 0;
		frameVals.ipady = 10;
		frameVals.weightx = .5;

		// layout design of panels
		GridBagConstraints panelVals = new GridBagConstraints();
		panelVals.anchor = GridBagConstraints.WEST;
		panelVals.gridx = 0;
		panelVals.gridy = 0;

		// set layout of first panel
		userEntryPan.setLayout(new GridBagLayout());

		// drop box label for the base table
		dropBoxLabel.setText("Please enter a base table to work with: ");
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		userEntryPan.add(dropBoxLabel, panelVals);

		// drop box selection for base table
		baseTable.setSelectedIndex(0);
		baseTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				baseTableOption = (String) baseTable.getSelectedItem();
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 0;
		userEntryPan.add(baseTable, panelVals);

		// make columns after user chooses base table
		/*
		 * { dbSetup my = new dbSetup(); //Building the connection //int x = 10;
		 * Connection conn = null; try { Class.forName("org.postgresql.Driver"); conn =
		 * DriverManager.getConnection(
		 * "jdbc:postgresql://csce-315-db.engr.tamu.edu/section900team1", my.user,
		 * my.pswd); } catch (Exception e) { e.printStackTrace();
		 * System.err.println(e.getClass().getName()+": "+e.getMessage());
		 * System.exit(0); }//end try catch columnNames =
		 * jdbcpostgreSQLGUI.getColumnNames(conn,baseTableOption);
		 *
		 *
		 *
		 * } System.out.println(baseTableOption); for (String i : columnNames) {
		 * System.out.println(i); }
		 *
		 * columnBox = new JComboBox<String>(columnNames);
		 */

		// input the first panel
		choiceFrame.add(userEntryPan, frameVals);

		// set layout of the second panel
		joinPan.setLayout(new GridBagLayout());

		// check box labels
		checkBoxLabel.setText("Please Choose which tables to join");
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		joinPan.add(checkBoxLabel, panelVals);

		// conference table check box items
		confTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					confTableFlag = true;
					System.out.println(confTableFlag);
				}
				if (e.getStateChange() != 1) {
					confTableFlag = false;
					System.out.println(confTableFlag);
				}
			}
		});
		panelVals.gridx = 0;
		panelVals.gridy = 1;
		joinPan.add(confTableCheck, panelVals);

		// drive table check box items
		driveTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					driveTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					driveTableFlag = false;
				}
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 1;
		joinPan.add(driveTableCheck, panelVals);

		// foreigner table check box items
		foreTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					foreTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					foreTableFlag = false;
				}
			}
		});
		panelVals.gridx = 2;
		panelVals.gridy = 1;
		joinPan.add(foreTableCheck, panelVals);

		// game table check box items
		gameTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					gameTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					gameTableFlag = false;
				}
			}
		});
		panelVals.gridx = 3;
		panelVals.gridy = 1;
		joinPan.add(gameTableCheck, panelVals);

		// game statistic check box items
		gamestatTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					gamestatTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					gamestatTableFlag = false;
				}
			}
		});
		panelVals.gridx = 4;
		panelVals.gridy = 1;
		joinPan.add(gamestatTableCheck, panelVals);

		// kickoff table check box items
		koTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					koTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					koTableFlag = false;
				}
			}
		});
		panelVals.gridx = 0;
		panelVals.gridy = 2;
		joinPan.add(koTableCheck, panelVals);

		// kickoff return check box items
		korTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					korTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					korTableFlag = false;
				}
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 2;
		joinPan.add(korTableCheck, panelVals);

		// pass table check box items
		passTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					passTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					passTableFlag = false;
				}
			}
		});
		panelVals.gridx = 2;
		panelVals.gridy = 2;
		joinPan.add(passTableCheck, panelVals);

		// play table check box items
		playTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					playTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					playTableFlag = false;
				}
			}
		});
		panelVals.gridx = 3;
		panelVals.gridy = 2;
		joinPan.add(playTableCheck, panelVals);

		// player table check box items
		playerTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					playerTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					playerTableFlag = false;
				}
			}
		});
		panelVals.gridx = 4;
		panelVals.gridy = 2;
		joinPan.add(playerTableCheck, panelVals);

		// player game statistics table check box items
		pgsTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					pgsTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					pgsTableFlag = false;
				}
			}
		});
		panelVals.gridx = 0;
		panelVals.gridy = 3;
		joinPan.add(pgsTableCheck, panelVals);

		// player statistics table check box items
		psTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					psTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					psTableFlag = false;
				}
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 3;
		joinPan.add(psTableCheck, panelVals);

		// punt table punt check box items
		puntTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					puntTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					puntTableFlag = false;
				}
			}
		});
		panelVals.gridx = 2;
		panelVals.gridy = 3;
		joinPan.add(puntTableCheck, panelVals);

		// punt return table check box items
		prTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					prTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					prTableFlag = false;
				}
			}
		});
		panelVals.gridx = 3;
		panelVals.gridy = 3;
		joinPan.add(prTableCheck, panelVals);

		// reception table check box items
		recepTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					recepTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					recepTableFlag = false;
				}
			}
		});
		panelVals.gridx = 4;
		panelVals.gridy = 3;
		joinPan.add(recepTableCheck, panelVals);

		// rush table check box items
		rushTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					rushTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					rushTableFlag = false;
				}
			}
		});
		panelVals.gridx = 0;
		panelVals.gridy = 4;
		joinPan.add(rushTableCheck, panelVals);

		// stadium check box items
		stadTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					stadTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					stadTableFlag = false;
				}
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 4;
		joinPan.add(stadTableCheck, panelVals);

		// team table check box items
		teamTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					teamTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					teamTableFlag = false;
				}
			}
		});
		panelVals.gridx = 2;
		panelVals.gridy = 4;
		joinPan.add(teamTableCheck, panelVals);

		// team game statistics check box items
		tgsTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					tgsTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					tgsTableFlag = false;
				}
			}
		});
		panelVals.gridx = 3;
		panelVals.gridy = 4;
		joinPan.add(tgsTableCheck, panelVals);

		// all table check box items
		allTableCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					allTableFlag = true;
				}
				if (e.getStateChange() != 1) {
					allTableFlag = false;
				}
			}
		});
		panelVals.gridx = 4;
		panelVals.gridy = 4;
		joinPan.add(allTableCheck, panelVals);

		// add the second panel to the frame
		frameVals.gridy = 1;
		choiceFrame.add(joinPan, frameVals);

		// table panel set up
		tablePan.setLayout(new GridBagLayout());

		// button for creation of join table
		createTableBut.setText("<html>CREATE<br>TABLE</html>");
		createTableBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (allTableFlag) {
					confTableFlag = true;
					driveTableFlag = true;
					gameTableFlag = true;
					gamestatTableFlag = true;
					koTableFlag = true;
					korTableFlag = true;
					passTableFlag = true;
					playTableFlag = true;
					playerTableFlag = true;
					pgsTableFlag = true;
					psTableFlag = true;
					puntTableFlag = true;
					prTableFlag = true;
					recepTableFlag = true;
					rushTableFlag = true;
					stadTableFlag = true;
					teamTableFlag = true;
					tgsTableFlag = true;
				}
				if (confTableFlag) {
					userChoices.add("conferences");
				}
				if (driveTableFlag) {
					userChoices.add("drive");
				}
				if (gameTableFlag) {
					userChoices.add("game");
				}
				if (gamestatTableFlag) {
					userChoices.add("gamestatistics");
				}
				if (koTableFlag) {
					userChoices.add("kickoff");
				}
				if (korTableFlag) {
					userChoices.add("kickoffreturn");
				}
				if (passTableFlag) {
					userChoices.add("pass");
				}
				if (playTableFlag) {
					userChoices.add("play");
				}
				if (playerTableFlag) {
					userChoices.add("player");
				}
				if (pgsTableFlag) {
					userChoices.add("playergamestatistics");
				}
				if (psTableFlag) {
					userChoices.add("playerseason");
				}
				if (puntTableFlag) {
					userChoices.add("punt");
				}
				if (prTableFlag) {
					userChoices.add("puntreturn");
				}
				if (recepTableFlag) {
					userChoices.add("receptions");
				}
				if (stadTableFlag) {
					userChoices.add("stadium");
				}
				if (teamTableFlag) {
					userChoices.add("team");
				}
				if (tgsTableFlag) {
					userChoices.add("teamgamestatistics");
				}
				userChoices.add(baseTableOption);
				populateColumns(baseTableOption);
				constraintBox.removeAllItems();
				for (String i : columnNames) {
					constraintBox.addItem(i);
				}

				serverResponse = "CHANGE HAS BEEN MADE";
				serverRespText.setText(serverResponse);
			}
		});
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		tablePan.add(createTableBut, panelVals);

		// adds the thrid panel to the frame
		frameVals.gridy = 2;
		choiceFrame.add(tablePan, frameVals);

		// search panel set up
		searchPan.setLayout(new GridBagLayout());

		// label for the search bar
		searchLabel.setText("What are you searching for?");
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		searchPan.add(searchLabel, panelVals);

		// empty text box next to search bar
		searchText.setText("");
		panelVals.gridx = 1;
		panelVals.gridy = 0;
		searchPan.add(searchText, panelVals);

		// label for drop down box of constraints
		searchLabel2.setText("Add constraints for Search: ");
		panelVals.gridx = 0;
		panelVals.gridy = 1;
		searchPan.add(searchLabel2, panelVals);

		// drop down box of constraints

		constraintBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				constraintBoxOption = (String) constraintBox.getSelectedItem();
			}
		});
		panelVals.gridx = 1;
		panelVals.gridx = 1;
		searchPan.add(constraintBox, panelVals);

		// add the fourth panel to the frame
		frameVals.gridy = 3;
		choiceFrame.add(searchPan, frameVals);

		// request panel set up
		requestPan.setLayout(new GridBagLayout());

		// button used for requesting data from database
		requestButton.setText("Request From Database");
		requestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterTerm = searchText.getText();
				buildWhereStatement(baseTableOption);
				executeQuery();
				if (numRows > 5) {
					serverRespText.setText("Too much data please output to file to see");
				} else {
					serverRespText.setText(serverResponse);
				}

			}
		});
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		requestPan.add(requestButton, panelVals);

		// add the fifth panel to the frame
		frameVals.gridy = 4;
		choiceFrame.add(requestPan, frameVals);

		// data response panel set up
		dataRespPan.setLayout(new GridBagLayout());

		// data response area, where the output is
		serverRespText.append(serverResponse);
		serverRespText.setRows(4);
		serverRespText.setColumns(30);
		panelVals.gridx = 0;
		panelVals.gridy = 0;
		dataRespPan.add(serverRespText, panelVals);

		// button to send output to a file
		sendToFileBut.setText("<html> SEND <br> TO <br> FILE </html>");
		sendToFileBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					FileWriter myWriter = new FileWriter("output.csv");
					for (int i = 0; i < joinedColumnNames.size(); i++) {
						if (i != joinedColumnNames.size() - 1) {
							myWriter.write(joinedColumnNames.elementAt(i) + ",");
						} else {
							myWriter.write(joinedColumnNames.elementAt(i) + "\n");
						}
					}
					myWriter.write(serverResponse);
					myWriter.close();
				} catch (IOException e4) {
					e4.printStackTrace();
				}
			}
		});
		panelVals.gridx = 1;
		panelVals.gridy = 0;
		dataRespPan.add(sendToFileBut, panelVals);

		// add sixth panel to the frame
		frameVals.gridy = 5;
		choiceFrame.add(dataRespPan, frameVals);

		// pack frame and complete
		choiceFrame.setSize(900, 600);
		choiceFrame.pack();
		choiceFrame.setVisible(true);

	}

	public static void main(String[] args) {
		popupLogin();

	}
}