package dad.biblioteca.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;

import org.apache.commons.lang.time.DurationFormatUtils;

import dad.recursos.ConexaoLogin;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;
import dad.recursos.RegistoLogin;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

public class Login {

	private JFrame frame;
	private JTextField user;
	private JPasswordField pass;
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	public static String NOME;
	public static long inicialTime;
	private static Login INSTANCE;

	private Login() {
		INSTANCE = this;
		frame = new JFrame("Biblioteca Dádiva de Deus - Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("/DAD.jpg"))));
		frame.setBounds(100, 100, 400, 300);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		JLabel txUser = new JLabel("USUÁRIO:");
		txUser.setFont(new Font("Roboto", Font.PLAIN, 13));
		txUser.setBounds(10, 165, 70, 15);
		frame.getContentPane().add(txUser);

		JLabel txSenha = new JLabel("SENHA:");
		txSenha.setFont(new Font("Roboto", Font.PLAIN, 13));
		txSenha.setBounds(10, 200, 70, 15);
		frame.getContentPane().add(txSenha);

		JLabel titulo = new JLabel("BIBLIOTECA D\u00C1DIVA DE DEUS");
		titulo.setFont(new Font("Roboto Black", Font.PLAIN, 20));
		titulo.setHorizontalAlignment(SwingConstants.CENTER);
		titulo.setBounds(10, 93, 380, 39);
		frame.getContentPane().add(titulo);

		JLabel image = new JLabel("");
		image.setHorizontalAlignment(SwingConstants.CENTER);
		image.setIcon(new ImageIcon(Login.class.getResource("/DAD_T.png")));
		image.setBounds(100, 11, 200, 87);
		frame.getContentPane().add(image);

		user = new JTextField();
		user.setFont(new Font("Roboto", Font.PLAIN, 15));
		user.setBounds(85, 163, 295, 20);
		user.setBorder(new LineBorder(Color.WHITE, 1));
		user.setMargin(new Insets(0, 0, 20, 0));
		frame.getContentPane().add(user);
		user.setColumns(10);

		pass = new JPasswordField();
		pass.setFont(new Font("Roboto", Font.PLAIN, 14));
		pass.setBounds(85, 198, 295, 20);
		frame.getContentPane().add(pass);

		JButton entrar = new JButton("ENTRAR");
		entrar.setFont(new Font("Roboto", Font.BOLD, 12));
		entrar.setBounds(155, 237, 90, 23);
		entrar.setBackground(MaterialColors.LIGHT_BLUE_600);
		MaterialUIMovement.add(entrar, MaterialColors.GRAY_300, 5, 1000 / 30);
		frame.getContentPane().add(entrar);
		entrar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				login();

			}
		});

		JLabel texto = new JLabel(
				"Escreva o seu nome de utilizador e senha e clique no bot\u00E3o abaixo para entrar no programa");
		texto.setHorizontalAlignment(SwingConstants.CENTER);
		texto.setFont(new Font("Roboto", Font.PLAIN, 9));
		texto.setBounds(5, 126, 390, 20);
		frame.getContentPane().add(texto);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				long time = System.currentTimeMillis() - Main.inicialTime;
				Log.getInstance().printLog("Tempo de Uso: " + DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's")
						+ "\nPrograma Terminou");
				System.exit(0);
			}
		});

		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					login();
			}

		});

		user.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					login();
			}

		});

		pass.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					login();
			}

		});
	}

	public static Login getInstance() {
		if (INSTANCE == null) {
			new Login();
		}
		return INSTANCE;
	}

	public void login() {
		String username = user.getText();
		String password = String.valueOf(pass.getPassword());
		if (username.trim().equals("") || password.trim().equals("")) {
			JOptionPane.showMessageDialog(frame, "Preencha os campos de login!", "ERRO", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		} else {
			con = ConexaoLogin.getConnection();
			try {
				pst = con.prepareStatement("select * from logins where nome = ?");
				pst.setString(1, username);
				rs = pst.executeQuery();
				if (!rs.next()) {
					JOptionPane.showMessageDialog(frame, "O usuário não existe!", "ERRO", JOptionPane.ERROR_MESSAGE,
							new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				} else
					checkPassword(username, password);
			} catch (SQLException e) {
				e.printStackTrace();
				Log.getInstance().printLog("Login - " + e.getMessage());
			} finally {
				try {
					rs.close();
					pst.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
					Log.getInstance().printLog("Login - " + e.getMessage());
				}
			}
		}

	}

	private void checkPassword(String username, String password) {
		try {
			CriptografiaAES.setKey(password);
			CriptografiaAES.encrypt(password);
			password = CriptografiaAES.getEncryptedString();

			con = ConexaoLogin.getConnection();
			pst = con.prepareStatement("select * from logins where nome = ? and pass = ?");
			pst.setString(1, username);
			pst.setString(2, password);
			rs = pst.executeQuery();
			if (!rs.next()) {
				JOptionPane.showMessageDialog(frame, "Senha errada!", "ERRO", JOptionPane.ERROR_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			} else {
				Log.getInstance().printLog("Usuário: " + username + " - Conectado com sucesso!");
				pst = con.prepareStatement(
						"update logins set Num_acessos = Num_acessos + 1,Ultimo_Acesso=? where nome = ?");
				pst.setDate(1, new Date(System.currentTimeMillis()));
				pst.setString(2, username);
				pst.execute();
				NOME = username;
				inicialTime = System.currentTimeMillis();
				frame.setVisible(false);
				DataGui.getInstance().open();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.getInstance().printLog("Login - " + e.getMessage());
		} finally {
			try {
				rs.close();
				pst.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				Log.getInstance().printLog("Login - " + e.getMessage());
			}
		}

	}

	public boolean registo() {
		con = ConexaoLogin.getConnection();
		try {
			pst = con.prepareStatement("select count(*) from logins");
			rs = pst.executeQuery();
			rs.next();
			if (rs.getInt(1) == 1) {
				return true;
			} else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			Log.getInstance().printLog("Login - " + e.getMessage());
			return false;
		} finally {
			try {
				rs.close();
				pst.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				Log.getInstance().printLog("Login - " + e.getMessage());
			}
		}
	}

	public void open() {
		if (registo())
			RegistoLogin.getInstance().open(true);
		else {
			frame.setVisible(true);
			user.setText("");
			pass.setText("");
		}
	}

	public void openDirect() {
		frame.setVisible(true);
		user.setText("");
		pass.setText("");
	}
}
