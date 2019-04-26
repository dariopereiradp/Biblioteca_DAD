package dad.biblioteca.table;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import dad.biblioteca.Item;
import dad.biblioteca.Livro;
import dad.biblioteca.gui.DataGui;
import dad.recursos.Command;
import dad.recursos.ConexaoLivros;
import dad.recursos.Log;
import dad.recursos.UndoManager;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableModelLivro extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2606366351185706974L;

	private static TableModelLivro INSTANCE;
	private ArrayList<Livro> livros;
	private String[] colunas = { "ID", "Título", "Autor", "Editora", "Classificação", "Exemplares", "Disponíveis",
			"Disponível", "Local" };
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	private UndoManager undoManager;

	private TableModelLivro() {
		INSTANCE = this;
		undoManager = new UndoManager();
	}

	public void uploadDataBase() {
		livros = new ArrayList<>();
		int maior = 0;
		try {
			con = ConexaoLivros.getConnection();
			pst = con.prepareStatement("select * from livros order by Título");
			rs = pst.executeQuery();
			String titulo, autor, editora, classificacao, disponivel, n_exemplares, disponiveis, local;
			if (rs.next()) {
				do {
					titulo = rs.getString(2);
					autor = rs.getString(3);
					editora = rs.getString(4);
					classificacao = rs.getString(5);
					n_exemplares = rs.getString(6);
					disponiveis = rs.getString(7);
					disponivel = rs.getString(8);
					local = rs.getString(9);
					Livro l = new Livro(titulo, autor, editora, classificacao, local);
					l.setNumero_exemplares(Integer.parseInt(n_exemplares));
					l.setDisponivel(disponivel);
					l.setN_exemp_disponiveis(Integer.parseInt(disponiveis));
					l.setId(Integer.parseInt(rs.getString(1)));
					File f = new File(Item.imgPath + l.getId() + ".jpg");
					if(f.exists())
						l.setImg(new ImageIcon(f.getPath()));
					if (l.getId() > maior)
						maior = l.getId();
					livros.add(l);
				} while (rs.next());
			}
			fireTableDataChanged();
			Log.getInstance().printLog("Base de dados livros carregada com sucesso!");
			if (livros.size() > 0)
				Item.countID = maior;
		} catch (Exception e) {
			Log.getInstance()
					.printLog("Erro ao carregar a base de dados dos Livros" + e.getMessage() + "\n" + getClass());
			e.printStackTrace();
		}
	}

	public Livro getLivro(Livro l) {
		for (int i = 0; i < livros.size(); i++) {
			if (livros.get(i).equals(l))
				return livros.get(i);
		}
		return l;
	}

	public void addListeners() {
		undoManager.addPropertyChangeListener(e -> updateItems());
		updateItems();
	}

	public void addLivro(Livro livro) {
		if (livros.contains(livro))
			incrementarLivro(livro, false);
		else {
			undoManager.execute(new AddLivro(livro));
			LivroPanel.getInstance().clearTextFields();
		}

	}

	public void removeLivros(int[] rows) {
		undoManager.execute(new RemoverLivro(rows));
	}

	public void removeExemplar(int[] rows) {
		undoManager.execute(new RemoverExemplar(rows));
	}

	public Livro getLivro(int rowIndex) {
		return livros.get(rowIndex);
	}

	public boolean perguntaIncrementar() {
		int ok = JOptionPane.showConfirmDialog(DataGui.getInstance(),
				"Esse livro já existe! Deseja aumentar uma unidade ao livro já existente?", "Livro já existe",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		if (ok == JOptionPane.OK_OPTION)
			return true;
		else
			return false;
	}

	public void incrementarLivro(Livro livro, boolean undo) {
		if (!undo) {
			if (perguntaIncrementar()) {
				undoManager.execute(new IncLivro(livro));
				LivroPanel.getInstance().clearTextFields();
			} else
				incLivro(livro);
		} else
			incLivro(livro);
	}

	@Override
	public int getColumnCount() {
		return colunas.length;
	}

	@Override
	public int getRowCount() {
		return livros.size();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return Integer.class;
		case 5:
			return Integer.class;
		case 6:
			return Integer.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return livros.get(rowIndex).getId();
		case 1:
			return livros.get(rowIndex).getNome();
		case 2:
			return livros.get(rowIndex).getAutor();
		case 3:
			return livros.get(rowIndex).getEditora();
		case 4:
			return livros.get(rowIndex).getClassificacao();
		case 5:
			return livros.get(rowIndex).getNumero_exemplares();
		case 6:
			return livros.get(rowIndex).getN_exemp_disponiveis();
		case 7:
			if (livros.get(rowIndex).isDisponivel())
				return "Sim";
			else
				return "Não";
		case 8:
			return livros.get(rowIndex).getLocal();
		default:
			return livros.get(rowIndex);
		}
	}

	@Override
	public void setValueAt(Object valor, int rowIndex, int columnIndex) {
		try {
			if (!(columnIndex == 1 && (String.valueOf(valor)).trim().equals(""))) {
				if ((String.valueOf(valor).trim().equals("")))
					valor = "-";
				Livro livro = livros.get(rowIndex);
				Livro l = new Livro(livro.getNome(), livro.getAutor(), livro.getEditora(), livro.getClassificacao(),
						livro.getLocal());
				boolean disponivel = livro.isDisponivel();
				switch (columnIndex) {
				case 1:
					l.setNome((String) valor);
					if (!(l.getNome().equals(livro.getNome()))) {
						if (livros.contains(l)) {
							if (perguntaIncrementar()) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Título e Juntar", new IncLivro(l),
										new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this,"Título", livro, valor));
					}
					break;
				case 2:
					l.setAutor((String) valor);
					if (!(l.getAutor().equals(livro.getAutor()))) {
						if (livros.contains(l)) {
							if (perguntaIncrementar()) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Autor e Juntar", new IncLivro(l),
										new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this,"Autor", livro, valor));
					}
					break;
				case 3:
					l.setEditora((String) valor);
					if (!(l.getEditora().equals(livro.getEditora()))) {
						if (livros.contains(l)) {
							if (perguntaIncrementar()) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Editora e Juntar", new IncLivro(l),
										new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this,"Editora", livro, valor));
					}
					break;
				case 4:
					l.setClassificacao((String) valor);
					if (!(l.getClassificacao().equals(livro.getClassificacao()))) {
						if (livros.contains(l)) {
							if (perguntaIncrementar()) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Classificação e Juntar",
										new IncLivro(l), new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this,"Classificação", livro, valor));
					}
					break;
				case 5:
					undoManager.execute(new AtualizaExemplares(disponivel, livro, valor));
					break;
				case 8:
					undoManager.execute(new AtualizaLivro(this,"Local", livro, valor));
					break;
				default:
					livros.get(rowIndex);
					break;
				}
				fireTableDataChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.getInstance().printLog("Erro no setValue()\n" + e.getMessage() + "\n" + getClass());
		}
	}

	public static TableModelLivro getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TableModelLivro();
		return INSTANCE;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas[columnIndex];
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	private void updateItems() {
		DataGui.getInstance().getMenuAnular().setEnabled(undoManager.isUndoAvailable());
		DataGui.getInstance().getMenuAnular().setText("Anular (Ctrl+Z) - (" + undoManager.getUndoName() + ")");
		DataGui.getInstance().getMenuRefazer().setEnabled(undoManager.isRedoAvailable());
		DataGui.getInstance().getMenuRefazer().setText("Refazer (Ctrl+Y) - (" + undoManager.getRedoName() + ")");
	}

	private void insertLivro(Livro livro, int row) {
		try {
			pst = con.prepareStatement(
					"insert into livros(ID,Título,Autor,Editora,Classificação,Exemplares,Disponíveis,Disponível,Local) values (?,?,?,?,?,?,?,?,?)");
			pst.setString(1, String.valueOf(livro.getId()));
			pst.setString(2, livro.getNome());
			pst.setString(3, livro.getAutor().toString());
			pst.setString(4, livro.getEditora().toString());
			pst.setString(5, livro.getClassificacao().toString());
			pst.setString(6, String.valueOf(livro.getNumero_exemplares()));
			pst.setString(7, String.valueOf(livro.getN_exemp_disponiveis()));
			pst.setString(8, "Sim");
			pst.setString(9, livro.getLocal());
			pst.execute();
			if (row != -2) {
				if (row > -1)
					livros.add(row, livro);
				else
					livros.add(livro);
			}
			fireTableDataChanged();
			LivroPanel.getInstance().getJtfTotal().setText(String.valueOf(livros.size()));
			Log.getInstance().printLog("Livro adicionado com sucesso!\n" + livro.toString());
		} catch (Exception e) {
			Log.getInstance().printLog("Erro ao adicionar o livro!\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void incLivro(Livro livro) {
		try {
			Livro l = getLivro(livro);
			pst = con.prepareStatement("update livros set Exemplares=? where ID=" + l.getId());
			pst.setString(1, String.valueOf(l.getNumero_exemplares() + 1));
			pst.execute();
			pst = con.prepareStatement("update livros set Disponíveis=? where ID=" + l.getId());
			pst.setString(1, String.valueOf(l.getN_exemp_disponiveis() + 1));
			pst.execute();
			l.incrementar_exemplares();
			fireTableDataChanged();
			Log.getInstance().printLog("Livro já existe. Foi incrementado uma unidade.\n" + livro.toString());
		} catch (SQLException e) {
			Log.getInstance().printLog("Erro ao adicionar o livro!\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	private class AddLivro implements Command {

		private Livro livro;

		public AddLivro(Livro livro) {
			this.livro = livro;
		}

		@Override
		public void execute() {
			insertLivro(livro, -1);
		}

		@Override
		public void undo() {
			try {
				pst = con.prepareStatement("delete from livros where ID=" + livro.getId());
				pst.execute();
				livros.remove(livro);
				fireTableDataChanged();
			} catch (SQLException e) {
				Log.getInstance().printLog("Erro ao adicionar o livro!\n" + e.getMessage());
				e.printStackTrace();
			}

		}

		@Override
		public void redo() {
			execute();
		}

		@Override
		public String getName() {
			return "Adicionar Livro";
		}
	}

	private class IncLivro implements Command {

		private Livro livro;

		public IncLivro(Livro livro) {
			this.livro = livro;
		}

		@Override
		public void execute() {
			incLivro(livro);
		}

		@Override
		public void undo() {
			try {
				Livro l = getLivro(livro);
				pst = con.prepareStatement("update livros set Exemplares=? where ID=" + l.getId());
				pst.setString(1, String.valueOf(l.getNumero_exemplares() - 1));
				l.setNumero_exemplares(l.getNumero_exemplares() - 1);
				pst.execute();
				pst = con.prepareStatement("update livros set Disponíveis=? where ID=" + l.getId());
				pst.setString(1, String.valueOf(l.getN_exemp_disponiveis() - 1));
				pst.execute();
				fireTableDataChanged();
			} catch (SQLException e) {
				Log.getInstance().printLog("Erro ao adicionar o livro!\n" + e.getMessage());
				e.printStackTrace();
			}

		}

		@Override
		public void redo() {
			execute();
		}

		@Override
		public String getName() {
			return "Incrementar Livro";
		}
	}

	private class RemoverLivro implements Command {

		private int[] rows;
		private ArrayList<Livro> remover = new ArrayList<>();

		public RemoverLivro(int[] rows) {
			this.rows = rows;
		}

		@Override
		public void execute() {
			try {
				for (int i = 0; i < rows.length; i++) {
					pst = con.prepareStatement("delete from livros where ID=" + livros.get(rows[i]).getId());
					pst.execute();
					remover.add(livros.get(rows[i]));
				}
				livros.removeAll(remover);
				fireTableDataChanged();
				LivroPanel.getInstance().getJtfTotal().setText(String.valueOf(livros.size()));
				Log.getInstance().printLog("Livros apagados com sucesso!");
			} catch (Exception e) {
				Log.getInstance().printLog("Erro ao apagar o(s) livro(s)\n" + e.getMessage());
			}
		}

		@Override
		public void undo() {
			for (int i = 0; i < rows.length; i++) {
				insertLivro(remover.get(i), rows[i]);
			}
		}

		@Override
		public void redo() {
			execute();
		}

		@Override
		public String getName() {
			return "Remover Livro";
		}
	}

	private class RemoverExemplar implements Command {

		private int[] rows;
		private ArrayList<Livro> modificou = new ArrayList<>();

		public RemoverExemplar(int[] rows) {
			this.rows = rows;
		}

		@Override
		public void execute() {
			try {
				ArrayList<Livro> remover = new ArrayList<>();
				for (int i = 0; i < rows.length; i++) {
					if (livros.get(rows[i]).getNumero_exemplares() == 1) {
						pst = con.prepareStatement("delete from livros where ID=" + livros.get(rows[i]).getId());
						pst.execute();
						remover.add(livros.get(rows[i]));
						modificou.add(livros.get(rows[i]));
					} else {
						pst = con.prepareStatement(
								"update livros set Exemplares=? where ID=" + livros.get(rows[i]).getId());
						pst.setString(1, String.valueOf(livros.get(rows[i]).getNumero_exemplares() - 1));
						pst.execute();
						pst = con.prepareStatement(
								"update livros set Disponíveis=? where ID=" + livros.get(rows[i]).getId());
						pst.setString(1, String.valueOf(livros.get(rows[i]).getN_exemp_disponiveis() - 1));
						pst.execute();
						livros.get(rows[i]).decrementar_exemplares();
						modificou.add(livros.get(rows[i]));
					}
				}
				livros.removeAll(remover);
				fireTableDataChanged();
				LivroPanel.getInstance().getJtfTotal().setText(String.valueOf(livros.size()));
				Log.getInstance().printLog("Livros apagados com sucesso!");
			} catch (Exception e) {
				Log.getInstance().printLog("Erro ao apagar o(s) livro(s)\n" + e.getMessage());
			}
		}

		@Override
		public void undo() {
			for (int i = 0; i < rows.length; i++) {
				Livro l = modificou.get(i);
				if (livros.contains(l))
					incrementarLivro(l, true);
				else {
					insertLivro(l, rows[i]);
				}

			}
		}

		@Override
		public void redo() {
			execute();
		}

		@Override
		public String getName() {
			return "Remover Exemplar";
		}
	}


	public void ordenar() {
		livros.sort(null);
		fireTableDataChanged();
		Log.getInstance().printLog("Livros ordenados com sucesso");
	}

}
