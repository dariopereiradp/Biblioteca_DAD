package dad.biblioteca.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import dad.biblioteca.User;
import dad.biblioteca.gui.DataGui;
import dad.recursos.Command;
import dad.recursos.ConexaoUser;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;
import dad.recursos.UndoManager;

/**
 * Classe que representa o TableModel para os clientes.
 * 
 * @author Dário Pereira
 *
 */
public class TableModelUser extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3247984074345998765L;
	private static TableModelUser INSTANCE;
	private ArrayList<User> users;
	private String[] colunas = { "CPF", "Nome", "Data de Nascimento", "Telefone", "Número de Empréstimos" };
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	private UndoManager undoManager;

	private TableModelUser() {
		INSTANCE = this;
		undoManager = new UndoManager();
	}

	/**
	 * Faz upload da base de dados e cria o ArrayList com os clientes que
	 * existirem na base de dados Users.
	 */
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

	public UndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * Configura os listeners para mudar o estado dos menus undo e redo.
	 */
	public void addListeners() {
		undoManager.addPropertyChangeListener(e -> updateItems());
		updateItems();
	}

	/**
	 * Atualiza a disponibilidade e o texto dos menus Undo e Redo
	 */
	public void updateItems() {
		DataGui.getInstance().getMenuAnular().setEnabled(undoManager.isUndoAvailable());
		DataGui.getInstance().getMenuAnular().setText("Anular (Ctrl+Z) - (" + undoManager.getUndoName() + ")");
		DataGui.getInstance().getMenuRefazer().setEnabled(undoManager.isRedoAvailable());
		DataGui.getInstance().getMenuRefazer().setText("Refazer (Ctrl+Y) - (" + undoManager.getRedoName() + ")");
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

	/**
	 * Adiciona um cliente à base de dados.
	 * @param user - cliente que se pretende adicionar.
	 */
	public void addUser(User user) {
		undoManager.execute(new AddUser(user));
		UserPanel.getInstance().clearTextFields();
		fireTableDataChanged();
	}

	public User getUser(int rowIndex) {
		return users.get(rowIndex);
	}

	/**
	 * 
	 * @param cpf - CPF que do cliente que se pretende consultar.
	 * @return o cliente que tem o CPF dado, se existir; caso contrário retorna null
	 */
	public User getUserByCpf(String cpf) {
		for (User user : users) {
			if (user.getCpf().equals(cpf))
				return user;
		}
		return null;
	}

	/**
	 * Remove os cliente que têm os indexes passados no array rows.
	 * 
	 * @param rows
	 *            - array que contém os indexes dos clientes para apagar.
	 */
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
			String phone = users.get(rowIndex).getTelefone();
			return "(" + phone.substring(0, 2) + ") " + phone.substring(2, 3) + " " + phone.substring(3, 7) + "-"
					+ phone.substring(7);
		case 4:
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
		case 4:
			return Integer.class;
		default:
			return String.class;
		}
	}

	@Override
	public void setValueAt(Object valor, int rowIndex, int columnIndex) {
		try {
			if (!(columnIndex == 1 && (String.valueOf(valor)).trim().equals(""))) {
				if ((String.valueOf(valor).trim().equals("")))
					valor = "-";
				User user = users.get(rowIndex);
				switch (columnIndex) {
				case 1:
					if (!((String) valor).equals(user.getNome())) {
						undoManager.execute(new AtualizaUser(this, "Nome", user, valor));
					}
					break;
				case 2:
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					Date data_nasc = dateFormat.parse((String) valor);
					if (!dateFormat.format(user.getData_nascimento()).equals(dateFormat.format(data_nasc)))
						undoManager.execute(new AtualizaUser(this, "Data_Nascimento", user, valor));
					break;
				case 3:
					String telefone = ((String) valor).replace("-", "").replace("(", "").replace(")", "").replace(" ",
							"");
					if (!user.getTelefone().equals(telefone))
						if (telefone.length() == 11)
							undoManager.execute(new AtualizaUser(this, "Telefone", user, valor));
					break;
				default:
					users.get(rowIndex);
					break;
				}
				fireTableDataChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.getInstance().printLog("Erro no setValue()\n" + e.getMessage() + "\n" + getClass());
		}
	}

	/**
	 * Método para inserir um cliente na base de dados, na posição pretendida.
	 * @param user - cliente que se pretende inserir.
	 * @param row - linha em que se pretende inserir o cliente.
	 */
	public void insertUser(User user, int pos) {
		user.adicionarNaBaseDeDados();
		users.add(pos, user);

	}

	/**
	 * Classe que representa um comando para remover um ou vários clientes.
	 * 
	 * @author Dário Pereira
	 *
	 */
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
					users.get(rows[i]).removerBaseDeDados();
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
			fireTableDataChanged();
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

	/**
	 * Classe que representa um comando para adicionar um cliente.
	 * 
	 * @author Dário Pereira
	 *
	 */
	private class AddUser implements Command {

		private User user;

		public AddUser(User user) {
			this.user = user;
		}

		@Override
		public void execute() {
			try {
				user.adicionarNaBaseDeDados();
				users.add(user);
				UserPanel.getInstance().getJtfTotal().setText(String.valueOf(users.size()));
				fireTableDataChanged();
			} catch (Exception e) {
				Log.getInstance().printLog("Erro ao criar cliente! " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public void undo() {
			user.removerBaseDeDados();
			users.remove(user);
			UserPanel.getInstance().getJtfTotal().setText(String.valueOf(users.size()));
			fireTableDataChanged();
		}

		@Override
		public void redo() {
			execute();
		}

		@Override
		public String getName() {
			return "Adicionar Cliente";
		}
	}
	
	public static TableModelUser getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TableModelUser();
		}
		return INSTANCE;
	}

}
