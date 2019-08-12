package dad.recursos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import dad.biblioteca.gui.Main;

/**
 * Classe para fazer a conexão com a tabela Empréstimos.
 * @author Dário Pereira
 *
 */
public class ConexaoEmprestimos {

	public static Connection con;

	public static String dbFile = Main.DATABASE_DIR + "emprestimos.mdb";
	public static String dbUrl = "jdbc:ucanaccess://" + dbFile + ";memory=true;immediatelyReleaseResources=true";

	public static Connection getConnection() {
		try {
			File dir = new File(Main.DATABASE_DIR);
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
