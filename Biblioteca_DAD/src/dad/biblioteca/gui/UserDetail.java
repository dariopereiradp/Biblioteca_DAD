package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import com.toedter.calendar.JDateChooser;

import dad.biblioteca.User;
import dad.biblioteca.table.AtualizaUser;
import dad.biblioteca.table.CompositeCommand;
import dad.biblioteca.table.EmprestimoPanel;
import dad.biblioteca.table.TableModelUser;
import dad.recursos.Log;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

public class UserDetail {

	private JFormattedTextField cpfN;
	private MaskFormatter mascaraCpf;
	private boolean editCancel = false;
	private JDialog novo;

	public User user;

	public UserDetail(User user) {
		this.user = user;
		novo = new JDialog();
		novo.setTitle("Cliente - " + user.getNome());
		novo.setLocationRelativeTo(null);
		novo.setMinimumSize(new Dimension(600, 300));

		JPanel cima = new JPanel(new GridLayout(3, 2));
		JPanel baixo = new JPanel(new BorderLayout());
		JPanel botoes = new JPanel();
		novo.getContentPane().setLayout(new BorderLayout());

		JLabel lCPF = new JLabel("CPF:");
		lCPF.setFont(new Font("Roboto", Font.PLAIN, 12));
		cima.add(lCPF);

		try {
			mascaraCpf = new MaskFormatter("###.###.###-##");
			mascaraCpf.setCommitsOnValidEdit(true);
			cpfN = new JFormattedTextField(mascaraCpf);
		} catch (ParseException e1) {
			cpfN = new JFormattedTextField();
			e1.printStackTrace();
		}

		cpfN.setFont(new Font("Arial", Font.PLAIN, 15));
		cpfN.setBounds(90, 162, 181, 20);
		cpfN.setColumns(10);
		cpfN.setText(user.getCpf());
		cpfN.setEditable(false);
		cima.add(cpfN);

		JLabel lNome = new JLabel("Nome completo:");
		lNome.setFont(new Font("Roboto", Font.PLAIN, 12));
		cima.add(lNome);

		JTextField nomeN = new JTextField();
		nomeN.setText("");
		nomeN.setFont(new Font("Roboto", Font.PLAIN, 12));
		nomeN.setText(user.getNome());
		nomeN.setEditable(false);
		cima.add(nomeN);

		JLabel lDataNasc = new JLabel("Data de nascimento: ");
		lDataNasc.setFont(new Font("Roboto", Font.PLAIN, 12));
		cima.add(lDataNasc);

		JDateChooser date_nasc = new JDateChooser();
		date_nasc.setEnabled(false);
		date_nasc.setLocale(new Locale("pt", "BR"));
		date_nasc.setDateFormatString("dd/MM/yyyy");
		date_nasc.setMaxSelectableDate(new Date());
		date_nasc.setDate(user.getData_nascimento());
		cima.add(date_nasc);

		JButton editar = new JButton("Editar");
		editar.setFont(new Font("Roboto", Font.PLAIN, 12));
		editar.setBackground(MaterialColors.RED_300);
		MaterialUIMovement.add(editar, MaterialColors.GRAY_300, 5, 1000 / 30);
		botoes.add(editar);

		JButton salvar = new JButton("Salvar");
		salvar.setFont(new Font("Roboto", Font.PLAIN, 12));
		salvar.setBackground(MaterialColors.LIGHT_GREEN_400);
		MaterialUIMovement.add(salvar, MaterialColors.GRAY_300, 5, 1000 / 30);
		salvar.setEnabled(false);
		botoes.add(salvar);
		salvar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (nomeN.getText().trim().equals(""))
					JOptionPane.showMessageDialog(novo, "Escreva um nome!", "Nome vazio", JOptionPane.ERROR_MESSAGE,
							new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				else {
//					user.setNome(nomeN.getText());
//					user.setData_nascimento(date_nasc.getDate());
					TableModelUser.getInstance().getUndoManager()
							.execute(new CompositeCommand("Atualizar dados do cliente",
									new AtualizaUser(TableModelUser.getInstance(), "Nome", user, nomeN.getText()),
									new AtualizaUser(TableModelUser.getInstance(), "Data_Nascimento", user, new SimpleDateFormat("dd/MM/yyyy").format(date_nasc.getDate()))));
					TableModelUser.getInstance().fireTableDataChanged();
					Log.getInstance().printLog("Dados do cliente adicionados com sucesso!\n" + user.toText());
					novo.dispose();
				}
			}
		});

		editar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!editCancel) {
					nomeN.setEditable(true);
					date_nasc.setEnabled(true);
					editCancel = true;
					editar.setText("Cancelar");
					salvar.setEnabled(true);
				} else if (editCancel) {
					novo.dispose();
				}
			}
		});

		baixo.add(botoes, BorderLayout.SOUTH);

		JScrollPane jsp = new JScrollPane(EmprestimoPanel.getInstance().getSmallTable(user));
		jsp.setPreferredSize(new Dimension(596, 150));

		baixo.add(jsp, BorderLayout.CENTER);

		novo.getContentPane().add(cima, BorderLayout.NORTH);

		novo.getContentPane().add(baixo, BorderLayout.CENTER);

		novo.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				novo.dispose();
			}
		});
	}

	public void open() {
		novo.setVisible(true);
	}
}
