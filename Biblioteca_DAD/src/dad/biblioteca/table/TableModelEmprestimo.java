package dad.biblioteca.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import dad.biblioteca.Emprestimo;

public class TableModelEmprestimo extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3247984074345998765L;
	private static TableModelEmprestimo INSTANCE;
	private ArrayList<Emprestimo> emprestimos;
	private String[] colunas = { "ID", "ID do Item", "Título", "Data do Empréstimo", "Data de Devolução", "Cliente", "Funcionário",
			"Ativo", "Multa" };

	@Override
	public int getRowCount() {
		return emprestimos.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return Integer.class;
		case 1:
			return Integer.class;
		case 8:
			return Double.class;
		default:
			return String.class;
		}
	}

}
