package dad.recursos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConexaoUser {
	
	public static Connection con;

	public static String dbFile = System.getenv("APPDATA") + "/BibliotecaDAD/Databases/users.mdb";
	public static String dbUrl = "jdbc:ucanaccess://" + dbFile + ";memory=true";

	public static Connection getConnection() {
		try {
			File dir = new File(System.getenv("APPDATA") + "/BibliotecaDAD/Databases/");
			if (!dir.exists())
				dir.mkdirs();
			con = DriverManager.getConnection(dbUrl);
		} catch (Exception e) {
			Log.getInstance().printLog("Erro ao criar a base de dados - " + e.getMessage());
			e.printStackTrace();
		}

		return con;
	}

}
