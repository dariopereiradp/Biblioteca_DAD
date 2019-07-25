package dad.recursos;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;

import org.apache.commons.lang.time.DurationFormatUtils;

import dad.biblioteca.gui.Login;
import dad.biblioteca.gui.Main;
import dad.recursos.ConexaoLogin;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

public class RegistoLogin {

	private JDialog dialog;
	private JTextField user;
	private JPasswordField pass;
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	private boolean login;
	private static RegistoLogin INSTANCE;

	private RegistoLogin() {
		INSTANCE = this;
		dialog = new JDialog();
		dialog.setTitle("Biblioteca Dádiva de Deus - Registo");
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("/DAD.jpg"))));
		dialog.setBounds(100, 100, 500, 300);
		dialog.setLocationRelativeTo(null);
		dialog.setResizable(false);
		dialog.getContentPane().setLayout(null);

		JLabel txUser = new JLabel("USU\u00C1RIO:");
		txUser.setFont(new Font("Roboto", Font.PLAIN, 13));
		txUser.setBounds(10, 165, 70, 15);
		dialog.getContentPane().add(txUser);

		JLabel txSenha = new JLabel("SENHA:");
		txSenha.setFont(new Font("Roboto", Font.PLAIN, 13));
		txSenha.setBounds(10, 200, 70, 15);
		dialog.getContentPane().add(txSenha);

		JLabel titulo = new JLabel("BIBLIOTECA D\u00C1DIVA DE DEUS");
		titulo.setFont(new Font("Roboto Black", Font.PLAIN, 20));
		titulo.setHorizontalAlignment(SwingConstants.CENTER);
		titulo.setBounds(10, 93, 480, 39);
		dialog.getContentPane().add(titulo);

		JLabel image = new JLabel("");
		image.setHorizontalAlignment(SwingConstants.CENTER);
		image.setIcon(new ImageIcon(RegistoLogin.class.getResource("/DAD_T.png")));
		image.setBounds(150, 11, 200, 87);
		dialog.getContentPane().add(image);

		user = new JTextField();
		user.setFont(new Font("Roboto", Font.PLAIN, 15));
		user.setBounds(85, 163, 399, 20);
		user.setBorder(new LineBorder(Color.WHITE, 1));
		user.setMargin(new Insets(0, 0, 20, 0));
		dialog.getContentPane().add(user);
		user.setColumns(10);

		pass = new JPasswordField();
		pass.setFont(new Font("Roboto", Font.PLAIN, 14));
		pass.setBounds(85, 198, 399, 20);
		pass.setBorder(new LineBorder(Color.WHITE, 1));
		pass.setMargin(new Insets(0, 0, 20, 0));
		dialog.getContentPane().add(pass);

		JButton registar = new JButton("REGISTRAR");
		registar.setFont(new Font("Roboto", Font.BOLD, 12));
		registar.setBounds(190, 245, 120, 23);
		registar.setBackground(MaterialColors.LIGHT_BLUE_600);
		MaterialUIMovement.add(registar, MaterialColors.GRAY_300, 5, 1000 / 30);
		dialog.getContentPane().add(registar);
		registar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				registo();

			}
		});

		JLabel texto = new JLabel(
				"Escreva o nome de utilizador e a senha que vai ficar associada e clique no bot\u00E3o abaixo para fazer o registro");
		texto.setHorizontalAlignment(SwingConstants.CENTER);
		texto.setFont(new Font("Dialog", Font.PLAIN, 9));
		texto.setBounds(5, 126, 490, 20);
		dialog.getContentPane().add(texto);

		JLabel warning = new JLabel(
				"ATEN\u00C7\u00C3O: N\u00E3o esque\u00E7a da senha! \u00C9 imposs\u00EDvel recuper\u00E1-la!");
		warning.setFont(new Font("Roboto", Font.PLAIN, 12));
		warning.setForeground(Color.RED);
		warning.setHorizontalAlignment(SwingConstants.CENTER);
		warning.setBounds(85, 225, 399, 14);
		dialog.getContentPane().add(warning);

		JCheckBox showPass = new JCheckBox("Mostrar senha");
		pass.setEchoChar('*');
		showPass.setFont(new Font("Roboto", Font.PLAIN, 10));
		showPass.setBounds(10, 241, 110, 23);
		dialog.getContentPane().add(showPass);
		showPass.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					pass.setEchoChar((char) 0);

				} else {
					pass.setEchoChar('*');
				}
			}
		});

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(login){
				long time = System.currentTimeMillis() - Main.inicialTime;
				Log.getInstance().printLog("Tempo de Uso: " + DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's")
						+ "\nPrograma Terminou");
				System.exit(0);
				} else
					dialog.dispose();
			}
		});

		dialog.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					registo();
			}

		});

		user.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					registo();
			}

		});

		pass.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					registo();
			}

		});
	}

	public static RegistoLogin getInstance() {
		if (INSTANCE == null) {
			new RegistoLogin();
		}
		return INSTANCE;
	}

	public void registo() {
		String username = user.getText();
		String password = String.valueOf(pass.getPassword());
		if (username.trim().equals("") || password.trim().equals("")) {
			JOptionPane.showMessageDialog(dialog, "Preencha os campos de registo", "ERRO", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		} else {
			con = ConexaoLogin.getConnection();
			try {
				pst = con.prepareStatement("select * from logins where nome = ?");
				pst.setString(1, username);
				rs = pst.executeQuery();
				if (!rs.next()) {
					inserir(username, password);
				} else
					JOptionPane.showMessageDialog(dialog, "O usuário '" + username + "' já existe!", "ERRO",
							JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			} catch (SQLException e) {
				e.printStackTrace();
				Log.getInstance().printLog("RegistoLogin - " + e.getMessage());
			} finally {
				try {
					rs.close();
					pst.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
					Log.getInstance().printLog("RegistoLogin - " + e.getMessage());
				}
			}
		}

	}

	private void inserir(String username, String password) {
		try {
			CriptografiaAES.setKey(password);
			CriptografiaAES.encrypt(password);
			password = CriptografiaAES.getEncryptedString();

			con = ConexaoLogin.getConnection();
			pst = con.prepareStatement("insert into logins(Nome,Pass,Num_acessos) values (?,?,?)");
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setInt(3, 0);
			pst.execute();
			JOptionPane.showMessageDialog(dialog, "O usuário '" + username + "' foi criado com sucesso!",
					"USUÁRIO CRIADO", JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			Log.getInstance().printLog("O usuário '" + username + "' foi criado com sucesso!");
			if (login)
				Login.getInstance().openDirect();
			dialog.dispose();
		} catch (Exception e) {
			e.printStackTrace();
			Log.getInstance().printLog("RegistoLogin - " + e.getMessage());
		}

	}

	public void open(boolean login) {
		this.login = login;
		dialog.setVisible(true);
		user.setText("");
		pass.setText("");
	}
}
