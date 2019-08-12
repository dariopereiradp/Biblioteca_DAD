package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;

import dad.recursos.ConexaoLogin;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;

/**
 * Classe que representa um diálogo para alterar a senha de um funcionário.
 * @author Dário Pereira
 *
 */
public class ChangePassword extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1195390014795006787L;
	private final JPanel contentPanel = new JPanel();
	private JPasswordField passAtual;
	private JPasswordField newPass;
	private JPasswordField confPass;
	private String nome;

	/**
	 * Create the dialog.
	 */
	public ChangePassword(String nome, boolean admin) {
		this.nome = nome;
		setBounds(100, 100, 450, 210);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblAlterarSenhaDo = new JLabel("ALTERAR SENHA DO '" + nome + "'");
			lblAlterarSenhaDo.setHorizontalAlignment(SwingConstants.CENTER);
			lblAlterarSenhaDo.setFont(new Font("Dialog", Font.PLAIN, 17));
			lblAlterarSenhaDo.setBackground(new Color(60, 179, 113));
			lblAlterarSenhaDo.setBounds(0, 0, 444, 25);
			contentPanel.add(lblAlterarSenhaDo);
		}
		{
			JLabel lAtual = new JLabel("Senha Atual:");
			lAtual.setFont(new Font("Dialog", Font.PLAIN, 14));
			lAtual.setBounds(10, 35, 111, 25);
			contentPanel.add(lAtual);
			if (admin) {
				lAtual.setVisible(false);
			}
		}
		{
			JLabel lblNovaSenha = new JLabel("Nova Senha:");
			lblNovaSenha.setFont(new Font("Dialog", Font.PLAIN, 14));
			lblNovaSenha.setBounds(10, 70, 111, 25);
			contentPanel.add(lblNovaSenha);
		}
		{
			JLabel lblConfirmarSenha = new JLabel("Confirmar Senha:");
			lblConfirmarSenha.setFont(new Font("Dialog", Font.PLAIN, 14));
			lblConfirmarSenha.setBounds(10, 105, 120, 25);
			contentPanel.add(lblConfirmarSenha);
		}

		passAtual = new JPasswordField();
		passAtual.setMargin(new Insets(0, 0, 20, 0));
		passAtual.setFont(new Font("Roboto", Font.PLAIN, 14));
		passAtual.setEchoChar('*');
		passAtual.setBorder(new LineBorder(new Color(50, 205, 50)));
		passAtual.setBounds(131, 35, 303, 25);
		contentPanel.add(passAtual);
		if (admin) {
			passAtual.setVisible(false);
		}
		{
			newPass = new JPasswordField();
			newPass.setMargin(new Insets(0, 0, 20, 0));
			newPass.setFont(new Font("Roboto", Font.PLAIN, 14));
			newPass.setEchoChar('*');
			newPass.setBorder(new LineBorder(new Color(50, 205, 50)));
			newPass.setBounds(131, 71, 303, 25);
			contentPanel.add(newPass);
		}
		{
			confPass = new JPasswordField();
			confPass.setMargin(new Insets(0, 0, 20, 0));
			confPass.setFont(new Font("Roboto", Font.PLAIN, 14));
			confPass.setEchoChar('*');
			confPass.setBorder(new LineBorder(new Color(50, 205, 50)));
			confPass.setBounds(132, 106, 302, 25);
			contentPanel.add(confPass);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JCheckBox showPass = new JCheckBox("Mostrar senha");
				showPass.setFont(new Font("Roboto", Font.PLAIN, 10));
				buttonPane.add(showPass);
				showPass.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							passAtual.setEchoChar((char) 0);
							newPass.setEchoChar((char) 0);
							confPass.setEchoChar((char) 0);

						} else {
							passAtual.setEchoChar('*');
							newPass.setEchoChar('*');
							confPass.setEchoChar('*');
						}
					}
				});
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!admin)
							verify();
						else
							change();

					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancelar");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();

					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Verifica se a 'oldPass' corresponde à senha antiga.
	 */
	public void verify() {
		String oldPass = String.valueOf(passAtual.getPassword());
		String pass = String.valueOf(newPass.getPassword());
		String conf = String.valueOf(confPass.getPassword());
		if (oldPass.trim().equals("") || pass.trim().equals("") || conf.trim().equals("")) {
			JOptionPane.showMessageDialog(DataGui.getInstance(), "Preencha todos os campos!", "ALTERAR SENHA",
					JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		} else {
			try {
				CriptografiaAES.setKey(oldPass);
				CriptografiaAES.encrypt(oldPass);
				Connection con = ConexaoLogin.getConnection();
				PreparedStatement pst = con.prepareStatement("select Pass from logins where Nome=?");
				pst.setString(1, nome);
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					if (!rs.getString(1).equals(CriptografiaAES.getEncryptedString())) {
						JOptionPane.showMessageDialog(DataGui.getInstance(),
								"A senha atual está incorreta. Tente novamente!", "ALTERAR SENHA",
								JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
					} else {
						change();
					}
				} else {
					JOptionPane.showMessageDialog(DataGui.getInstance(), "Ocorreu um erro ao alterar a senha...",
							"ALTERAR SENHA", JOptionPane.ERROR_MESSAGE,
							new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
					Log.getInstance().printLog("Erro ao mudar senha! (ResultSet is empty)");
				}

			} catch (Exception e) {
				JOptionPane.showMessageDialog(DataGui.getInstance(), "Ocorreu um erro ao alterar a senha...",
						"ALTERAR SENHA", JOptionPane.ERROR_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				Log.getInstance().printLog("Erro ao mudar senha! - " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Verifica se as senhas introduzidas coincidem entre si. Se sim, altera a senha atual.
	 */
	public void change() {
		String pass = String.valueOf(newPass.getPassword());
		String conf = String.valueOf(confPass.getPassword());
		try {
			if (!pass.equals(conf)) {
				JOptionPane.showMessageDialog(DataGui.getInstance(),
						"A senha de confirmação está diferente da nova senha! Devem ser iguais! (pode clicar em 'mostrar senha' para confirmar)",
						"ALTERAR SENHA", JOptionPane.ERROR_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			} else {
				Connection con = ConexaoLogin.getConnection();
				PreparedStatement pst;
				CriptografiaAES.setKey(pass);
				CriptografiaAES.encrypt(pass);
				pst = con.prepareStatement("update logins set Pass=? where Nome=?");
				pst.setString(1, CriptografiaAES.getEncryptedString());
				pst.setString(2, nome);
				pst.execute();
				dispose();
				JOptionPane.showMessageDialog(DataGui.getInstance(), "Senha alterada com sucesso!", "ALTERAR SENHA",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(DataGui.getInstance(), "Ocorreu um erro ao alterar a senha...",
					"ALTERAR SENHA", JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			Log.getInstance().printLog("Erro ao mudar senha! - " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Torna o diálogo visível.
	 */
	public void open() {
		setVisible(true);
	}
}
