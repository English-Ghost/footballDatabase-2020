import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

public class Question3 {
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
		System.out.println("Opened Database Successfully");
		try {
			Statement stmt = conn.createStatement();
			String selectedTeam = null;
			try {
			String modInput = "'"+userIn+"'";
			ResultSet r = stmt.executeQuery("SELECT teamcode from team where year = 2013 and name = " + modInput);
			while(r.next()) {
				selectedTeam = r.getString(1);
			}
			}catch (Exception e) {
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
			System.out.println("Max Team: " + maxTeam + " Max Value: " + max);
			String query = "SELECT name from team where year = 2005 and teamcode = " + maxTeam;
			ResultSet result3 = null;
			ResultSetMetaData metadata3 = null;
			result3 = stmt.executeQuery(query);
			metadata3 = result3.getMetaData();
			int columns3 = metadata3.getColumnCount();
			while (result3.next()) {
				System.out.println(result3.getString(1));
				output = result3.getString(1); 
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error accessing Database.");
		}

		return output;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
		String o = q3("Virginia Tech");
		
	}
}
