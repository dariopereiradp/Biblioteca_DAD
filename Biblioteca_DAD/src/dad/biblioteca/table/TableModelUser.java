package dad.biblioteca.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import dad.biblioteca.User;
import dad.recursos.Command;
import dad.recursos.ConexaoUser;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;
import dad.recursos.UndoManager;

public class TableModelUser extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3247984074345998765L;
	private static TableModelUser INSTANCE;
	private ArrayList<User> users;
	private String[] colunas = { "CPF", "Nome", "Data de Nascimento", "Número de Empréstmos" };
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	private UndoManager undoManager;

	private TableModelUser() {
		INSTANCE = this;
		undoManager = new UndoManager();
	}

	public void uploadDataBase() {
		users = new ArrayList<>();
		try {
			con = ConexaoUser.getConnection();
			pst = con.prepareStatement("select * from usuarios order by CPF");
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					String cpf = rs.getString(1);
					CriptografiaAES.setKey(User.key);
					CriptografiaAES.decrypt(cpf);
					cpf = CriptografiaAES.getDecryptedString();
					User user = User.getUser(cpf);
					users.add(user);
				} while (rs.next());
			}
			fireTableDataChanged();
			Log.getInstance().printLog("Base de dados users carregada com sucesso!");
		} catch (Exception e) {
			Log.getInstance()
					.printLog("Erro ao carregar a base de dados dos Users: " + e.getMessage() + "\n" + getClass());
			e.printStackTrace();
		}
	}

	public static TableModelUser getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TableModelUser();
		}
		return INSTANCE;
	}

	@Override
	public int getRowCount() {
		return users.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas[columnIndex];
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public User getUser(int rowIndex) {
		return users.get(rowIndex);
	}
	
	public User getUserByCpf(String cpf){
		for(User user: users){
			if(user.getCpf().equals(cpf))
				return user;
		}
		return null;
	}

	public void removeUser(int[] rows) {
		undoManager.execute(new RemoverUser(rows));
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return users.get(rowIndex).getCpf();
		case 1:
			return users.get(rowIndex).getNome();
		case 2:
			return new SimpleDateFormat("dd/MM/yyyy").format(users.get(rowIndex).getData_nascimento());
		case 3:
			return users.get(rowIndex).getN_emprestimos();
		default:
			return users.get(rowIndex);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return Long.class;
		case 3:
			return Integer.class;
		default:
			return String.class;
		}
	}

	private class RemoverUser implements Command {

		private int[] rows;
		private ArrayList<User> remover = new ArrayList<>();

		public RemoverUser(int[] rows) {
			this.rows = rows;
		}

		@Override
		public void execute() {
			try {
				con = ConexaoUser.getConnection();
				for (int i = 0; i < rows.length; i++) {
					pst = con.prepareStatement("delete * from usuarios where CPF=" + users.get(rows[i]).getCpf());
					pst.execute();
					remover.add(users.get(rows[i]));
				}
				users.removeAll(remover);
				fireTableDataChanged();
				UserPanel.getInstance().getJtfTotal().setText(String.valueOf(users.size()));
				Log.getInstance().printLog("Usuários apagados com sucesso!");
			} catch (Exception e) {
				Log.getInstance().printLog("Erro ao apagar o(s) usuário(s)\n" + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public void undo() {
			for (int i = 0; i < rows.length; i++) {
				insertUser(remover.get(i), rows[i]);
			}
		}

		@Override
		public void redo() {
			execute();
		}

		@Override
		public String getName() {
			return "Remover Usuário";
		}
	}

	public void insertUser(User user, int pos) {
		user.adicionarNaBaseDeDados();
		users.add(pos, user);

	}

}
