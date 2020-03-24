import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class Question4 {
	public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin,
			final double limitMax) {
		return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
	}

	public static void q4(String conf) {
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

		try {
			Statement stmt = conn.createStatement();
			System.out.println("Conference: " + selectedConference);
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

			System.out.println();
			Vector<String> data = new Vector<String>();
			while (result.next()) {
				String row = ""; // resets row to be blank
				for (int i = 1; i <= columns; i++) {
					row += result.getString(i) + ", \t"; // appends the next column
					data.add(result.getString(i));
				}
				//System.out.println(row);
			}
			System.out.println();
			HashMap<Double, String> nameTable = new HashMap<Double, String>();
			Vector<String> output = new Vector<String>();
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
				//System.out.println(nameTable.get(i) + ": " + i + "--------------------------------------" + out);
			}

			//------------------------------------------PRINTS TABLE TO CONSOLE. CHANGE TO PRINT TABLE TO GUI---------------------------------------
			for (int i = 0; i < output.size(); i += 2) {
				System.out.println(output.elementAt(i) + "," + output.elementAt(i + 1));
			}

		} catch (Exception e) {
			System.out.println("Error getting data");
		}

		return;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
		q4("Big 12 Conference");

	}
}
