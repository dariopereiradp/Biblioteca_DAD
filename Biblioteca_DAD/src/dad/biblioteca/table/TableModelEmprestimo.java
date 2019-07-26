package dad.biblioteca.table;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import dad.biblioteca.Emprestimo;
import dad.biblioteca.Item;
import dad.biblioteca.User;
import dad.recursos.ConexaoEmprestimos;
import dad.recursos.Log;
import dad.recursos.RealizarEmprestimo;

public class TableModelEmprestimo extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3247984074345998765L;
	private static TableModelEmprestimo INSTANCE;
	private ArrayList<Emprestimo> emprestimos;
	private String[] colunas = { "ID", "ID do Item", "Título", "Data do Empréstimo", "Data de Devolução", "Cliente",
			"Funcionário", "Ativo", "Multa" };
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	// private UndoManager undoManager;

	private TableModelEmprestimo() {
		INSTANCE = this;
		// undoManager = new UndoManager();
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
					Date data_emprestimo = rs.getTimestamp(3);
					Date data_devolucao = rs.getTimestamp(4);
					String cpf = rs.getString(5);
					User user = TableModelUser.getInstance().getUserByCpf(cpf);
					String funcionario = rs.getString(6);
					String ativo = rs.getString(7);
					String pago = rs.getString(8);
					Emprestimo emp;
					emp = new Emprestimo(user, item, data_emprestimo, data_devolucao, funcionario);
					emp.setId(Integer.parseInt(rs.getString(1)));
					if (pago.equals("Sim"))
						emp.pagar();
					if (ativo.equals("Não"))
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
			atualizarMultas();
		} catch (Exception e) {
			Log.getInstance().printLog(
					"Erro ao carregar a base de dados dos Empréstimos: " + e.getMessage() + "\n" + getClass());
			e.printStackTrace();
		}
	}

	public void atualizarMultas() {
		for (int i = 0; i < emprestimos.size(); i++) {
			try {
				if (!emprestimos.get(i).isEntregue()) {
					pst = con.prepareStatement(
							"update emprestimos set Multa=?,Pago=? where ID=" + emprestimos.get(i).getId());
					pst.setString(1, String.valueOf(emprestimos.get(i).getMulta()));
					if (emprestimos.get(i).isPago())
						pst.setString(2, "Sim");
					else
						pst.setString(2, "Não");
					pst.execute();
				}
			} catch (Exception e) {
				Log.getInstance()
						.printLog("Erro ao carregar ao atualizar multas!: " + e.getMessage() + "\n" + getClass());
				e.printStackTrace();
			}
		}
	}

	public void atualizarMultas(Emprestimo emp) throws Exception {
		if (!emp.isEntregue()) {
			pst = con.prepareStatement("update emprestimos set Multa=?,Pago=? where ID=" + emp.getId());

			pst.setString(1, String.valueOf(emp.getMulta()));
			if (emp.isPago())
				pst.setString(2, "Sim");
			else
				pst.setString(2, "Não");
			pst.execute();
		}
	}

	public static TableModelEmprestimo getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TableModelEmprestimo();
		}
		return INSTANCE;
	}

	@Override
	public int getRowCount() {
		return emprestimos.size();
	}
	
	public int getNumEmprestimosAtivos(){
		int n = 0;
		for(Emprestimo emp: emprestimos){
			if(!emp.isEntregue())
				n++;
		}
		return n;
	}
	
	public int getNumEmprestimosAtivosComMulta(){
		int n = 0;
		for(Emprestimo emp: emprestimos){
			if(!emp.isEntregue() && emp.getMulta()>0 && !emp.isPago())
				n++;
		}
		return n;
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

	public void addEmprestimo(Emprestimo emp) {
		emprestimos.add(emp);
	}

	public Emprestimo getEmprestimo(int rowIndex) {
		return emprestimos.get(rowIndex);
	}

	public int[] getEmprestimosByItem(Item item) {
		ArrayList<Integer> apagar = new ArrayList<>();
		for (int i = 0; i < emprestimos.size(); i++) {
			Emprestimo emp = emprestimos.get(i);
			if (emp.getItem().getId() == item.getId())
				apagar.add(i);
		}
		return apagar.stream().mapToInt(Integer::intValue).toArray();
	}

	public void removeEmprestimos(int[] rows) {
		// undoManager.execute(new RemoverExemplar(rows));
		ArrayList<Emprestimo> toDelete = new ArrayList<>();
		for (int i = 0; i < rows.length; i++) {
			Emprestimo emp = emprestimos.get(rows[i]);
			apagar(emp, toDelete);
		}
		emprestimos.removeAll(toDelete);
	}

	private void apagar(Emprestimo emp, ArrayList<Emprestimo> toDelete) {
		try {
			if (!emp.isEntregue()) {
				emp.entregar();
			}
			con = ConexaoEmprestimos.getConnection();
			pst = con.prepareStatement("delete from emprestimos where ID=" + emp.getId());
			pst.execute();
			String month_year = new SimpleDateFormat("MMMyyyy").format(emp.getData_emprestimo()).toUpperCase();
			String dirPath = RealizarEmprestimo.EMPRESTIMOS_PATH + month_year + "/";
			File recibo = new File(dirPath + emp.toString() + ".pdf");
			if (recibo.exists())
				recibo.delete();
			toDelete.add(emp);
			emp.getUser().decrementar_emprestimos();
			fireTableDataChanged();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (SecurityException e2) {
			Log.getInstance().printLog("Erro ao apagar o recibo! - " + e2.getMessage());
		}

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
			return new SimpleDateFormat("dd/MM/yyyy").format(emprestimos.get(rowIndex).getData_emprestimo());
		case 4:
			return new SimpleDateFormat("dd/MM/yyyy").format(emprestimos.get(rowIndex).getData_entrega());
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
			double multa = 0.0;
			try {
				con = ConexaoEmprestimos.getConnection();
				pst = con.prepareStatement(
						"select Multa from emprestimos where ID=" + emprestimos.get(rowIndex).getId());
				rs = pst.executeQuery();
				if (rs.next())
					multa = rs.getDouble(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (emprestimos.get(rowIndex).isEntregue())
				return multa;
			else
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
