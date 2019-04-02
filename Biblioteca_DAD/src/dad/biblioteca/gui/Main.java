package dad.biblioteca.gui;

import java.awt.EventQueue;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import dad.biblioteca.Item;
import dad.biblioteca.table.TableModelLivro;
import dad.recursos.ConexaoLivros;
import dad.recursos.ConexaoLogin;
import dad.recursos.ConexaoUser;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;
import mdlaf.MaterialLookAndFeel;

public class Main {

	public static final String user = "admin";
	public static final String pass = "dad";
	public static long inicialTime;
	private Connection con;

	public Main() {
		try {
			UIManager.setLookAndFeel(new MaterialLookAndFeel());
			Splash screen = new Splash();
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					screen.setVisible(true);
				}
			});

			Thread t1 = new Thread(new Runnable() {

				@Override
				public void run() {
					createTables();
					TableModelLivro.getInstance().uploadDataBase();
				}
			});
			t1.start();

			try {
				Thread.sleep(500);
				for (int i = 0; i <= 100; i++) {
					Thread.sleep(30);
					EventQueue.invokeLater(new Incrementar(i, screen));
				}
			} catch (InterruptedException e) {
				String message = "Ocorreu um erro ao abrir o programa. Tenta novamente!\n" + e.getMessage();
				JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
						new ImageIcon(getClass().getResource("DAD_SS.jpg")));
				Log.getInstance().printLog(message);
			}

			t1.join();

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					screen.setVisible(false);
					Login.getInstance().open();
					inicialTime = System.currentTimeMillis();
				}
			});

			Log.getInstance().printLog("O programa iniciou");

		} catch (Exception e1) {
			String message = "Ocorreu um erro ao abrir o programa. Tenta novamente!\n" + e1.getMessage();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_SS.jpg")));
			Log.getInstance().printLog(message);
			System.exit(1);
			e1.printStackTrace();
		}
	}

	private class Incrementar implements Runnable {
		private int i;
		private Splash screen;

		private Incrementar(int i, Splash screen) {
			this.i = i;
			this.screen = screen;
		}

		public void run() {
			screen.incrementar(i);
		}
	}

	private void createTables() {
		File dir = new File(System.getenv("APPDATA") + "/BibliotecaDAD/Databases/");
		if (!dir.exists())
			dir.mkdirs();

		try {

			File livros = new File(ConexaoLivros.dbFile);
			if (!livros.exists()) {
				con = DriverManager
						.getConnection("jdbc:ucanaccess://" + ConexaoLivros.dbFile + ";newdatabaseversion=V2003");
				DatabaseMetaData dmd = con.getMetaData();
				try (ResultSet rs = dmd.getTables(null, null, "Livros", new String[] { "TABLE" })) {
					try (Statement s = con.createStatement()) {
						s.executeUpdate("CREATE TABLE Livros (ID int NOT NULL,Título varchar(255) NOT NULL,"
								+ "Autor varchar(255),Editora varchar(255),Classificação varchar(255),"
								+ "Exemplares int,Disponíveis int,Disponível varchar(5),Local varchar(255),CONSTRAINT PK_Livros PRIMARY KEY (ID,Título));");
						Log.getInstance().printLog("Base de dados livros.mbd criada com sucesso");
					}
				}
			}

			File logins = new File(ConexaoLogin.dbFile);
			if (!logins.exists()) {
				con = DriverManager
						.getConnection("jdbc:ucanaccess://" + ConexaoLogin.dbFile + ";newdatabaseversion=V2003");
				DatabaseMetaData dmd = con.getMetaData();
				try (ResultSet rs = dmd.getTables(null, null, "Logins", new String[] { "TABLE" })) {
					try (Statement s = con.createStatement()) {
						s.executeUpdate("CREATE TABLE Logins (Nome varchar(255) NOT NULL,"
								+ "Pass varchar(50) NOT NULL, Num_acessos int, CONSTRAINT PK_Logins PRIMARY KEY (Nome));");
						Log.getInstance().printLog("Base de dados logins.mbd criada com sucesso");
					}
					CriptografiaAES.setKey(pass);
					CriptografiaAES.encrypt(pass);
					PreparedStatement pst = con
							.prepareStatement("insert into logins(Nome,Pass,Num_acessos) values (?,?,?)");
					pst.setString(1, user);
					pst.setString(2, CriptografiaAES.getEncryptedString());
					pst.setInt(3, 0);
					pst.execute();
					Log.getInstance().printLog("Utilizador admin criado com sucesso!");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			File users = new File(ConexaoUser.dbFile);
			if (!users.exists()) {
				con = DriverManager
						.getConnection("jdbc:ucanaccess://" + ConexaoUser.dbFile + ";newdatabaseversion=V2003");
				DatabaseMetaData dmd = con.getMetaData();
				try (ResultSet rs = dmd.getTables(null, null, "Usuários", new String[] { "TABLE" })) {
					try (Statement s = con.createStatement()) {
						s.executeUpdate("CREATE TABLE Usuarios (ID int NOT NULL,Nome varchar(255) NOT NULL,"
								+ "Idade int,CPF bigint);");
						Log.getInstance().printLog("Base de dados users.mbd criada com sucesso");
					}
				}
			}

			File imgs = new File(Item.imgPath);
			if (!imgs.exists())
				imgs.mkdirs();

		} catch (SQLException e) {
			String message = "Ocorreu um erro ao criar a base de dados... Tenta novamente!\n" + e.getMessage() + "\n"
					+ this.getClass();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_SS.jpg")));
			Log.getInstance().printLog(message);
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new Main();
	}

}
