package dad.recursos;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

import dad.biblioteca.User;
import dad.biblioteca.gui.DataGui;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

public class NovoCliente {


	public static void novoCliente(String cpfString, RealizarEmprestimo emp) {

		int ok = JOptionPane.showConfirmDialog(DataGui.getInstance(),
				"O cliente ainda n�o existe! Deseja criar um novo cliente com esse CPF?", "Criar cliente",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(NovoCliente.class.getResource("/DAD_SS.jpg")));
		if (ok == JOptionPane.YES_OPTION) {
			JDialog novo = new JDialog();
			novo.setTitle("Criar novo cliente");
			novo.setLocationRelativeTo(null);
			novo.setMinimumSize(new Dimension(400, 150));
			novo.setResizable(false);
			novo.getContentPane().setLayout(new GridLayout(4, 2));

			JLabel lCPF = new JLabel("CPF:");
			lCPF.setFont(new Font("Roboto", Font.PLAIN, 12));
			novo.getContentPane().add(lCPF);

			JTextField cpfN = new JTextField();
			cpfN.setText(cpfString);
			cpfN.setEditable(false);
			cpfN.setFont(new Font("Roboto", Font.PLAIN, 12));
			novo.getContentPane().add(cpfN);

			JLabel lNome = new JLabel("Nome completo:");
			lNome.setFont(new Font("Roboto", Font.PLAIN, 12));
			novo.getContentPane().add(lNome);

			JTextField nomeN = new JTextField();
			nomeN.setText("");
			nomeN.setFont(new Font("Roboto", Font.PLAIN, 12));
			novo.getContentPane().add(nomeN);

			JLabel lDataNasc = new JLabel("Data de nascimento: ");
			lDataNasc.setFont(new Font("Roboto", Font.PLAIN, 12));
			novo.getContentPane().add(lDataNasc);

			JDateChooser date_nasc = new JDateChooser();
			date_nasc.setLocale(new Locale("pt", "BR"));
			date_nasc.setDateFormatString("dd/MM/yyyy");
			date_nasc.setMaxSelectableDate(new Date());
			date_nasc.setDate(new Date());
			novo.getContentPane().add(date_nasc);

			JButton cancel = new JButton("Cancelar");
			cancel.setFont(new Font("Roboto", Font.PLAIN, 12));
			cancel.setBackground(MaterialColors.RED_300);
			MaterialUIMovement.add(cancel, MaterialColors.GRAY_300, 5, 1000 / 30);
			novo.getContentPane().add(cancel);
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					novo.dispose();
				}
			});

			JButton okButton = new JButton("Criar");
			okButton.setFont(new Font("Roboto", Font.PLAIN, 12));
			okButton.setBackground(MaterialColors.LIGHT_GREEN_400);
			MaterialUIMovement.add(okButton, MaterialColors.GRAY_300, 5, 1000 / 30);
			novo.getContentPane().add(okButton);
			okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (nomeN.getText().trim().equals(""))
						JOptionPane.showMessageDialog(novo, "Escreva um nome!", "Nome vazio", JOptionPane.ERROR_MESSAGE,
								new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
					else {
						User user = User.newUser(nomeN.getText(), date_nasc.getDate(), cpfString);
						Log.getInstance().printLog("Novo cliente criado com sucesso:\n" + user.toText());
						emp.getNome().setText(nomeN.getText());
						emp.getbConf().setEnabled(true);
						emp.getCpf().setEditable(false);
						emp.getbValidar().setEnabled(false);
						emp.getAlterar().setEnabled(true);
						novo.dispose();
					}
				}
			});
			
			novo.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					novo.dispose();
				}
			});

			novo.setVisible(true);
		}
	}

}
