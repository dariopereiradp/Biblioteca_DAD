package dad.biblioteca.gui;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

import dad.biblioteca.Emprestimo;
import dad.biblioteca.Item;
import dad.biblioteca.table.TableModelEmprestimo;
import dad.biblioteca.table.TableModelFuncionario;
import dad.biblioteca.table.TableModelLivro;
import dad.biblioteca.table.TableModelUser;
import dad.recursos.ConexaoEmprestimos;
import dad.recursos.ConexaoLivros;
import dad.recursos.ConexaoLogin;
import dad.recursos.ConexaoUser;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;
import mdlaf.MaterialLookAndFeel;
import net.ucanaccess.jdbc.UcanaccessSQLException;

/**
 * Classe responsável por inicializar todo o programa e bases de dados.
 * 
 * @author Dário Pereira
 *
 */
public class Main {

	public static final String TITLE = "BIBLIOTECA DÁDIVA DE DEUS";
	public static final String VERSION = "1.0";
	public static final String DATA_PUBLICACAO = "13 de Agosto de 2019";
	public static final String EMAIL_SUPORTE = "pereira13.dario@gmail.com";
	public static final String ADMIN_USER = "admin";
	public static final String ADMIN_PASS = "dad";
	public static final String BACKUP_DIR = System.getProperty("user.home") + System.getProperty("file.separator")
			+ "Documents/BibliotecaDAD/Backups/";
	public static final String BUG_REPORTS_DIR = System.getProperty("user.home") + System.getProperty("file.separator")
			+ "Documents/BibliotecaDAD/BugReports/";
	public static final String DATA_DIR = System.getenv("APPDATA") + "/BibliotecaDAD/";
	public static final String DATABASE_DIR = DATA_DIR + "Databases/";
	public static final String[] SIM_NAO = {"Sim", "Não"};
	public static long inicialTime;
	private Connection con;

	/**
	 * Inicializa o LookAndFeel, as bases de dados e a splash screen.
	 */
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
					TableModelUser.getInstance().uploadDataBase();
					TableModelEmprestimo.getInstance().uploadDataBase();
					TableModelFuncionario.getInstance().uploadDataBase();
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
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
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
			e1.printStackTrace();
			String message = "Ocorreu um erro ao abrir o programa. Tenta novamente!\n" + e1.getMessage();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			Log.getInstance().printLog(message);
			System.exit(1);

		}
	}

	/**
	 * Classe que representa uma thread que incrementa o valor da porcentagem na
	 * splash screen.
	 * 
	 * @author Dário Pereira
	 *
	 */
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

	/**
	 * Cria as pastas e tabelas das bases de dados, caso não existam.
	 */
	private void createTables() {
		File dir = new File(DATABASE_DIR);
		if (!dir.exists())
			dir.mkdirs();

		File backdir = new File(Main.BACKUP_DIR);
		if (!backdir.exists())
			backdir.mkdirs();

		File bugDir = new File(Main.BUG_REPORTS_DIR);
		if (!bugDir.exists())
			bugDir.mkdirs();

		restaurar();

		File conf = null;
		Scanner scan = null;
		try {
			conf = new File(DATABASE_DIR + "conf.dad");
			conf.createNewFile();
			scan = new Scanner(conf);
			scan.useLocale(Locale.US);
			Emprestimo.MULTA = scan.nextDouble();
			scan.close();
		} catch (IOException | InputMismatchException e1) {
			Log.getInstance().printLog("Erro ao carregar configurações! - " + e1.getMessage());
			e1.printStackTrace();
		} catch (NoSuchElementException e) {
			Emprestimo.MULTA = 0.5;
			scan.close();
			PrintWriter pw;
			try {
				pw = new PrintWriter(conf);
				pw.println(Emprestimo.MULTA);
				pw.close();
			} catch (FileNotFoundException e1) {
				Log.getInstance().printLog("Erro ao carregar configurações! - " + e1.getMessage());
				e1.printStackTrace();
			}
		}

		try {
			File livros = new File(ConexaoLivros.dbFile);
			if (!livros.exists()) {
				con = DriverManager.getConnection("jdbc:ucanaccess://" + ConexaoLivros.dbFile
						+ ";newdatabaseversion=V2003;immediatelyReleaseResources=true");
				DatabaseMetaData dmd = con.getMetaData();
				try (ResultSet rs = dmd.getTables(null, null, "Livros", new String[] { "TABLE" })) {
					try (Statement s = con.createStatement()) {
						s.executeUpdate("CREATE TABLE Livros (ID int NOT NULL,Título varchar(255) NOT NULL,"
								+ "Autor varchar(255),Editora varchar(255),Classificação varchar(255),"
								+ "Exemplares int,Disponíveis int,Disponível varchar(5),Local varchar(255),CONSTRAINT PK_Livros PRIMARY KEY (ID,Título));");
						Log.getInstance().printLog("Base de dados livros.mbd criada com sucesso");
					}
				}
				con.close();
			}

			File logins = new File(ConexaoLogin.dbFile);
			if (!logins.exists()) {
				con = DriverManager.getConnection("jdbc:ucanaccess://" + ConexaoLogin.dbFile
						+ ";newdatabaseversion=V2003;immediatelyReleaseResources=true");
				DatabaseMetaData dmd = con.getMetaData();
				try (ResultSet rs = dmd.getTables(null, null, "Logins", new String[] { "TABLE" })) {
					try (Statement s = con.createStatement()) {
						s.executeUpdate("CREATE TABLE Logins (Nome varchar(255) NOT NULL,"
								+ "Pass varchar(50) NOT NULL, Num_acessos int, Ultimo_Acesso date,Data_Criacao date, CONSTRAINT PK_Logins PRIMARY KEY (Nome));");
						Log.getInstance().printLog("Base de dados logins.mbd criada com sucesso");
					}
					CriptografiaAES.setKey(ADMIN_PASS);
					CriptografiaAES.encrypt(ADMIN_PASS);
					PreparedStatement pst = con.prepareStatement(
							"insert into logins(Nome,Pass,Num_acessos,Ultimo_Acesso,Data_Criacao) values (?,?,?,?,?)");
					pst.setString(1, ADMIN_USER);
					pst.setString(2, CriptografiaAES.getEncryptedString());
					pst.setInt(3, 0);
					pst.setDate(4, new Date(System.currentTimeMillis()));
					pst.setDate(5, new Date(System.currentTimeMillis()));
					pst.execute();
					Log.getInstance().printLog("Utilizador admin criado com sucesso!");
				} catch (Exception e) {
					Log.getInstance().printLog("Erro ao criar o utilizador admin!");
					e.printStackTrace();
				}
				con.close();
			}

			File users = new File(ConexaoUser.dbFile);
			if (!users.exists()) {
				con = DriverManager.getConnection("jdbc:ucanaccess://" + ConexaoUser.dbFile
						+ ";newdatabaseversion=V2003;immediatelyReleaseResources=true");
				DatabaseMetaData dmd = con.getMetaData();
				try (ResultSet rs = dmd.getTables(null, null, "Usuários", new String[] { "TABLE" })) {
					try (Statement s = con.createStatement()) {
						s.executeUpdate("CREATE TABLE Usuarios (CPF varchar(255) NOT NULL, Nome varchar(255) NOT NULL,"
								+ "Data_Nascimento date, N_Emprestimos int, Telefone varchar(15), CONSTRAINT PK_Usuarios PRIMARY KEY (CPF));");
						Log.getInstance().printLog("Base de dados users.mbd criada com sucesso");
					}
				}
				con.close();
			} else {
				con = ConexaoUser.getConnection();
				try (Statement s = con.createStatement()) {
					s.executeUpdate("ALTER TABLE Usuarios ADD COLUMN Telefone varchar(15);");
					Log.getInstance().printLog("Base de dados users.mbd atualizada com sucesso");
				} catch (SQLSyntaxErrorException | UcanaccessSQLException e3) {
					System.out.println("Coluna 'Telefone' já existe!");
				}
				con.close();
			}

			File emprestimos = new File(ConexaoEmprestimos.dbFile);
			if (!emprestimos.exists()) {
				con = DriverManager.getConnection("jdbc:ucanaccess://" + ConexaoEmprestimos.dbFile
						+ ";newdatabaseversion=V2003;immediatelyReleaseResources=true");
				DatabaseMetaData dmd = con.getMetaData();
				try (ResultSet rs = dmd.getTables(null, null, "Empréstimos", new String[] { "TABLE" })) {
					try (Statement s = con.createStatement()) {
						s.executeUpdate("CREATE TABLE Emprestimos (ID int NOT NULL,ID_Item int NOT NULL,"
								+ "Data_Emprestimo date,Data_Devolucao date, Cliente varchar(15),Funcionario varchar(255),Ativo varchar(5),"
								+ "Multa double,Pago varchar(5),CONSTRAINT PK_Emprestimos PRIMARY KEY (ID));");
						Log.getInstance().printLog("Base de dados emprestimos.mbd criada com sucesso");
					}
				}
				con.close();
			}

			File imgs = new File(Item.imgPath);
			if (!imgs.exists())
				imgs.mkdirs();

		} catch (SQLException e) {
			String message = "Ocorreu um erro ao criar a base de dados... Tenta novamente!\n" + e.getMessage() + "\n"
					+ this.getClass();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			Log.getInstance().printLog(message);
			e.printStackTrace();
		}

	}

	/**
	 * Caso o programa esteja a iniciar após o restauro de uma cópia de
	 * segurança, esse método trata de substituir as bases de dados e apagar as
	 * temporárias.
	 */
	public void restaurar() {
		String path = DATA_DIR + "temp/";
		File tmp = new File(path);
		if (tmp.exists()) {
			try {
				File confFile = new File(path + "conf.dad");
				File confDest = new File(Main.DATABASE_DIR + "conf.dad");
				if (confFile.exists())
					Files.copy(confFile.toPath(), confDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				File funcFile = new File(path + "logins.mdb");
				File funcDest = new File(Main.DATABASE_DIR + "logins.mdb");
				if (funcFile.exists())
					Files.copy(funcFile.toPath(), funcDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				File livrosFile = new File(path + "livros.mdb");
				File livrosDest = new File(Main.DATABASE_DIR + "livros.mdb");
				if (livrosFile.exists())
					Files.copy(livrosFile.toPath(), livrosDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				File images = new File(path + "Imagens/");
				File imagesDest = new File(Main.DATABASE_DIR + "Imagens/");
				if (images.exists())
					FileUtils.copyDirectory(images, imagesDest);

				File empFile = new File(path + "emprestimos.mdb");
				File empDest = new File(Main.DATABASE_DIR + "emprestimos.mdb");
				if (empFile.exists())
					Files.copy(empFile.toPath(), empDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				File userFile = new File(path + "users.mdb");
				File userDest = new File(Main.DATABASE_DIR + "users.mdb");
				if (userFile.exists())
					Files.copy(userFile.toPath(), userDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				FileUtils.deleteDirectory(tmp);
			} catch (Exception e) {
				Log.getInstance().printLog("Erro ao restaurar bases de dados! - " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		new Main();
	}

}
