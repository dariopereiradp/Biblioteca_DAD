package dad.biblioteca.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import dad.biblioteca.Emprestimo;
import dad.recursos.UndoManager;

public class TableModelEmprestimo extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3247984074345998765L;
	private static TableModelEmprestimo INSTANCE;
	private ArrayList<Emprestimo> emprestimos;
	private String[] colunas = { "ID", "ID do Item", "Título", "Data do Empréstimo", "Data de Devolução", "Cliente", "Funcionário",
			"Ativo", "Multa" };
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	private UndoManager undoManager;
	
	private TableModelEmprestimo() {
		INSTANCE = this;
		undoManager = new UndoManager();
	}
	
	public void uploadDatabase(){
		emprestimos = new ArrayList<>();
	}
	
	public static TableModelEmprestimo getInstance(){
		if(INSTANCE == null){
			INSTANCE = new TableModelEmprestimo();
		}
		return INSTANCE;
	}

	@Override
	public int getRowCount() {
		return emprestimos.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.length;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return colunas[columnIndex];
	}
	
	public Emprestimo getEmprestimo(int rowIndex){
		return emprestimos.get(rowIndex);
	}
	
	public void removeEmprestimos(int[] rows) {
//		undoManager.execute(new RemoverExemplar(rows));
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return emprestimos.get(rowIndex).getId();
		case 1:
			return emprestimos.get(rowIndex).getItem().getId();
		case 2:
			return emprestimos.get(rowIndex).getItem().getNome();
		case 3:
			return emprestimos.get(rowIndex).getData_emprestimo();
		case 4:
			return emprestimos.get(rowIndex).getData_entrega();
		case 5:
			return emprestimos.get(rowIndex).getUser().getCpf();
		case 6:
			return emprestimos.get(rowIndex).getFuncionario();
		case 7:
			if (!emprestimos.get(rowIndex).isEntregue())
				return "Sim";
			else
				return "Não";
		case 8:
			return emprestimos.get(rowIndex).getMulta();
		default:
			return emprestimos.get(rowIndex);
		}
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
