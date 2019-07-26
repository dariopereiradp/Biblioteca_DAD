package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

public class Restauro extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7863423688679744400L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public Restauro() {
		setBounds(100, 100, 350, 300);
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel title = new JLabel("RESTAURAR C\u00D3PIA DE SEGURAN\u00C7A");
			title.setBounds(0, 10, 334, 23);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setFont(new Font("Dialog", Font.PLAIN, 17));
			title.setBackground(new Color(60, 179, 113));
			contentPanel.add(title);
		}
		
		JCheckBox conf = new JCheckBox("Configura\u00E7\u00F5es");
		conf.setSelected(true);
		conf.setFont(new Font("Dialog", Font.PLAIN, 14));
		conf.setBounds(6, 56, 142, 25);
		contentPanel.add(conf);
		
		JCheckBox func = new JCheckBox("Funcion\u00E1rios");
		func.setSelected(true);
		func.setFont(new Font("Dialog", Font.PLAIN, 14));
		func.setBounds(6, 91, 142, 25);
		contentPanel.add(func);
		
		JCheckBox livros = new JCheckBox("Livros");
		livros.setSelected(true);
		livros.setFont(new Font("Dialog", Font.PLAIN, 14));
		livros.setBounds(6, 126, 142, 25);
		contentPanel.add(livros);
		
		JCheckBox emprestimos = new JCheckBox("Empr\u00E9stimos");
		emprestimos.setSelected(true);
		emprestimos.setFont(new Font("Dialog", Font.PLAIN, 14));
		emprestimos.setBounds(6, 161, 142, 25);
		contentPanel.add(emprestimos);
		
		JCheckBox clientes = new JCheckBox("Clientes");
		clientes.setSelected(true);
		clientes.setFont(new Font("Dialog", Font.PLAIN, 14));
		clientes.setBounds(6, 196, 142, 25);
		contentPanel.add(clientes);
		
		JLabel lInfo = new JLabel("Selecione quais itens deseja restaurar");
		lInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lInfo.setBounds(0, 35, 334, 14);
		contentPanel.add(lInfo);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
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
	
	public void open(){
		setVisible(true);
	}
}
