package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import dad.biblioteca.table.TableModelFuncionario;

import javax.swing.JTable;

public class GerirFuncionarios extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7591257121742629450L;
	private final JPanel contentPanel = new JPanel();
	private JTable table;


	/**
	 * Create the dialog.
	 */
	public GerirFuncionarios() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Gerir Funcionários");
		setBounds(100, 100, 1100, 350);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			table = TableModelFuncionario.getInstance().getSmallTable();
			JScrollPane jsp = new JScrollPane(table);
			contentPanel.add(jsp, BorderLayout.CENTER);
		}
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
	
	public void open(){
		setVisible(true);
	}

}
