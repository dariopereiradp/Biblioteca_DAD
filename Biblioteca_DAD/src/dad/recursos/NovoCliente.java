package dad.recursos;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import com.toedter.calendar.JDateChooser;

import dad.biblioteca.User;
import dad.biblioteca.gui.DataGui;
import dad.biblioteca.gui.Main;
import dad.biblioteca.table.TableModelUser;
import mdlaf.utils.MaterialColors;

/**
 * Classe que apresenta um diálogo para criar um novo cliente.
 * @author Dário Pereira
 *
 */
public class NovoCliente {

	private static JFormattedTextField telefone;

	public static void novoCliente(String cpfString, RealizarEmprestimo emp) {

		int ok = JOptionPane.showOptionDialog(DataGui.getInstance(),
				"O cliente ainda não existe! Deseja criar um novo cliente com esse CPF?", "Criar cliente",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(NovoCliente.class.getResource("/DAD_SS.jpg")), Main.SIM_NAO, Main.SIM_NAO[0]);
		if (ok == JOptionPane.YES_OPTION) {
			JDialog novo = new JDialog(DataGui.getInstance(), ModalityType.DOCUMENT_MODAL);
			novo.setTitle("Criar novo cliente");
			novo.setMinimumSize(new Dimension(400, 150));
			novo.setResizable(false);
			novo.getContentPane().setLayout(new GridLayout(5, 2));

			JLabel lCPF = new JLabel("CPF:");
			lCPF.setFont(new Font("Roboto", Font.PLAIN, 12));
			novo.getContentPane().add(lCPF);

			MaskFormatter mascaraCpf;
			JFormattedTextField cpfN;
			
			try {
				mascaraCpf = new MaskFormatter("###.###.###-##");
				mascaraCpf.setCommitsOnValidEdit(true);
				cpfN = new JFormattedTextField(mascaraCpf);
			} catch (ParseException e1) {
				cpfN = new JFormattedTextField();
				e1.printStackTrace();
			}

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

			MaskFormatter maskPhone;

			try {
				maskPhone = new MaskFormatter("(##) # ####-####");
				maskPhone.setCommitsOnValidEdit(true);
				telefone = new JFormattedTextField(maskPhone);
			} catch (ParseException e1) {
				telefone = new JFormattedTextField();
				e1.printStackTrace();
			}

			JLabel lPhone = new JLabel("Telefone: ");
			lPhone.setFont(new Font("Roboto", Font.PLAIN, 12));

			novo.getContentPane().add(lPhone);
			novo.getContentPane().add(telefone);

			JButton cancel = new JButton("Cancelar");
			cancel.setFont(new Font("Roboto", Font.PLAIN, 12));
			cancel.setBackground(MaterialColors.RED_300);
			Utils.personalizarBotao(cancel);
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
			Utils.personalizarBotao(okButton);
			novo.getContentPane().add(okButton);
			okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (nomeN.getText().trim().equals(""))
						JOptionPane.showMessageDialog(novo, "Escreva um nome!", "Nome vazio", JOptionPane.ERROR_MESSAGE,
								new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
					else {
						User user = new User(nomeN.getText(), date_nasc.getDate(), cpfString,
								telefone.getText().replace("-", "").replace("(", "").replace(")", "").replace(" ", ""),
								0, false);
						TableModelUser.getInstance().addUser(user);
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
