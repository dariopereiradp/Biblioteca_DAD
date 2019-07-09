package dad.biblioteca.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import dad.biblioteca.Emprestimo;
import dad.biblioteca.Item;
import dad.biblioteca.User;
import dad.recursos.ConexaoEmprestimos;
import dad.recursos.Log;

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
//	private UndoManager undoManager;
	
	private TableModelEmprestimo() {
		INSTANCE = this;
//		undoManager = new UndoManager();
	}
	
	
	
	public void uploadDataBase() {
		emprestimos = new ArrayList<>();
		int maior = 0;
		try {
			con = ConexaoEmprestimos.getConnection();
			pst = con.prepareStatement("select * from emprestimos order by ID");
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					int id_item = Integer.parseInt(rs.getString(2));
					Item item = Item.getItemById(id_item);
					Date data_emprestimo = rs.getTimestamp(4);
					Date data_devolucao = rs.getTimestamp(5);
					String cpf = rs.getString(6);
					User user = User.getUser(cpf);
					String funcionario = rs.getString(7);
					String ativo = rs.getString(8);
					Emprestimo emp = new Emprestimo(user, item, data_emprestimo, data_devolucao, funcionario);
					emp.setId(Integer.parseInt(rs.getString(1)));
					if(ativo.equals("Não"))
						emp.entregar();
					if (emp.getId() > maior)
						maior = emp.getId();
					emprestimos.add(emp);
				} while (rs.next());
			}
			fireTableDataChanged();
			Log.getInstance().printLog("Base de dados empréstimos carregada com sucesso!");
			if (emprestimos.size() > 0)
				Emprestimo.countId = maior;
		} catch (Exception e) {
			Log.getInstance()
					.printLog("Erro ao carregar a base de dados dos Empréstimos: " + e.getMessage() + "\n" + getClass());
			e.printStackTrace();
		}
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
	
	public ArrayList<Emprestimo> getEmprestimos() {
		return emprestimos;
	}
	
	public void addEmprestimo (Emprestimo emp){
		emprestimos.add(emp);
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
			return new SimpleDateFormat("dd/MMM/yyyy").format(emprestimos.get(rowIndex).getData_emprestimo());
		case 4:
			return new SimpleDateFormat("dd/MMM/yyyy").format(emprestimos.get(rowIndex).getData_entrega());
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
