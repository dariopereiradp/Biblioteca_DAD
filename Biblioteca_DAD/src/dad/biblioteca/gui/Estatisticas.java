package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dad.biblioteca.table.TableModelEmprestimo;
import dad.biblioteca.table.TableModelFuncionario;
import dad.biblioteca.table.TableModelLivro;
import dad.biblioteca.table.TableModelUser;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.JTextField;

/**
 * Classe que representa e mostra as estatísticas das bases de dados da
 * biblioteca.
 * 
 * @author Dário Pereira
 *
 */
public class Estatisticas extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3843370061976082815L;
	private static Estatisticas INSTANCE;
	private final JPanel contentPanel = new JPanel();
	private JTextField n_livro;
	private JTextField n_emp;
	private JTextField n_emp_ativos;
	private JTextField n_clientes;
	private JTextField n_func;

	private Estatisticas() {
		setBounds(100, 100, 600, 300);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("/DAD.jpg"))));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lEstatsticas = new JLabel("ESTAT\u00CDSTICAS");
		lEstatsticas.setBackground(new Color(60, 179, 113));
		lEstatsticas.setFont(new Font("Dialog", Font.PLAIN, 17));
		lEstatsticas.setHorizontalAlignment(SwingConstants.CENTER);
		lEstatsticas.setBounds(0, 11, 594, 20);
		contentPanel.add(lEstatsticas);

		JLabel lNumeroDeLivros = new JLabel("N\u00FAmero de Livros:");
		lNumeroDeLivros.setFont(new Font("Dialog", Font.PLAIN, 14));
		lNumeroDeLivros.setBounds(10, 42, 200, 25);
		contentPanel.add(lNumeroDeLivros);

		JLabel lNumeroDeEmprestimos = new JLabel("N\u00FAmero de Empr\u00E9stimos:");
		lNumeroDeEmprestimos.setFont(new Font("Dialog", Font.PLAIN, 14));
		lNumeroDeEmprestimos.setBounds(10, 80, 200, 25);
		contentPanel.add(lNumeroDeEmprestimos);

		JLabel lNumeroDeClientes = new JLabel("N\u00FAmero de Clientes:");
		lNumeroDeClientes.setFont(new Font("Dialog", Font.PLAIN, 14));
		lNumeroDeClientes.setBounds(10, 160, 190, 25);
		contentPanel.add(lNumeroDeClientes);

		n_livro = new JTextField();
		n_livro.setEditable(false);
		n_livro.setText(String.valueOf(TableModelLivro.getInstance().getRowCount()));
		n_livro.setBounds(250, 42, 62, 25);
		contentPanel.add(n_livro);
		n_livro.setColumns(10);

		JLabel lDisponiveis = new JLabel("(dos quais "
				+ String.valueOf(TableModelLivro.getInstance().getNumLivrosDisponiveis()) + " estão disponíveis)");
		lDisponiveis.setHorizontalAlignment(SwingConstants.CENTER);
		lDisponiveis.setFont(new Font("Dialog", Font.PLAIN, 14));
		lDisponiveis.setBounds(325, 42, 243, 25);
		contentPanel.add(lDisponiveis);

		n_emp = new JTextField();
		n_emp.setEditable(false);
		n_emp.setText(String.valueOf(TableModelEmprestimo.getInstance().getRowCount()));
		n_emp.setColumns(10);
		n_emp.setBounds(250, 80, 62, 25);
		contentPanel.add(n_emp);

		JLabel lEmpDisp = new JLabel("N\u00FAmero de Empr\u00E9stimos Ativos:");
		lEmpDisp.setFont(new Font("Dialog", Font.PLAIN, 14));
		lEmpDisp.setBounds(10, 120, 230, 25);
		contentPanel.add(lEmpDisp);

		n_emp_ativos = new JTextField();
		n_emp_ativos.setText(String.valueOf(TableModelEmprestimo.getInstance().getNumEmprestimosAtivos()));
		n_emp_ativos.setEditable(false);
		n_emp_ativos.setColumns(10);
		n_emp_ativos.setBounds(250, 120, 62, 25);
		contentPanel.add(n_emp_ativos);

		JLabel lEmpMulta = new JLabel(
				"(dos quais " + String.valueOf(TableModelEmprestimo.getInstance().getNumEmprestimosAtivosComMulta())
						+ " têm multa pendente)");
		lEmpMulta.setHorizontalAlignment(SwingConstants.CENTER);
		lEmpMulta.setFont(new Font("Dialog", Font.PLAIN, 14));
		lEmpMulta.setBounds(325, 120, 243, 25);
		contentPanel.add(lEmpMulta);

		n_clientes = new JTextField();
		n_clientes.setText(String.valueOf(TableModelUser.getInstance().getRowCount()));
		n_clientes.setEditable(false);
		n_clientes.setColumns(10);
		n_clientes.setBounds(250, 160, 62, 25);
		contentPanel.add(n_clientes);

		JLabel lNumeroFuncionarios = new JLabel("N\u00FAmero de Funcion\u00E1rios:");
		lNumeroFuncionarios.setFont(new Font("Dialog", Font.PLAIN, 14));
		lNumeroFuncionarios.setBounds(10, 200, 190, 25);
		contentPanel.add(lNumeroFuncionarios);

		n_func = new JTextField();
		n_func.setText(String.valueOf(TableModelFuncionario.getInstance().getRowCount()));
		n_func.setEditable(false);
		n_func.setColumns(10);
		n_func.setBounds(250, 200, 62, 25);
		contentPanel.add(n_func);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();

					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	/**
	 * Torna o diálogo visível.
	 */
	public void open() {
		setVisible(true);
	}

	public static Estatisticas getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Estatisticas();
		return INSTANCE;
	}
}