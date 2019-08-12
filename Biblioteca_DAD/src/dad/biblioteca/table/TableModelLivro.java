package dad.biblioteca.table;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import dad.biblioteca.Item;
import dad.biblioteca.Livro;
import dad.biblioteca.gui.DataGui;
import dad.recursos.Command;
import dad.recursos.CompositeCommand;
import dad.recursos.ConexaoLivros;
import dad.recursos.Log;
import dad.recursos.UndoManager;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe que representa o TableModel para os livros.
 * 
 * @author Dário Pereira
 *
 */
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

	/**
	 * Faz upload da base de dados e cria o ArrayList com os livros que
	 * existirem na base de dados Livros.
	 */
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
					if (f.exists())
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

	/**
	 * Devolve um livro igual ao livro passado como parâmetro e que existe no
	 * ArrayList.
	 * 
	 * @param l
	 *            - livro que se pretende verificar
	 * @return se existir um livro igual, devolve esse livro; se não existir,
	 *         devolve o próprio livro passado como argumento
	 */
	public Livro getLivro(Livro l) {
		for (int i = 0; i < livros.size(); i++) {
			if (livros.get(i).equals(l))
				return livros.get(i);
		}
		return l;
	}

	/**
	 * 
	 * @param id
	 *            - id que se pretende consultar para devolver o livro
	 *            correspondente.
	 * @return - o livro que tem o id passado como parâmetro, caso exista. Caso
	 *         contrário devolve null.
	 */
	public Livro getLivroById(int id) {
		for (int i = 0; i < livros.size(); i++) {
			if (livros.get(i).getId() == id)
				return livros.get(i);
		}
		return null;
	}

	/**
	 * Configura os listeners para mudar o estado dos menus undo e redo.
	 */
	public void addListeners() {
		undoManager.addPropertyChangeListener(e -> updateItems());
		updateItems();
	}

	/**
	 * Adiciona um livro à base de dados.
	 * 
	 * @param livro
	 *            - livro que se pretende adicionar.
	 */
	public void addLivro(Livro livro) {
		if (livros.contains(livro))
			incrementarLivro(livro, false);
		else {
			undoManager.execute(new AddLivro(livro));
			LivroPanel.getInstance().clearTextFields();
		}

	}

	/**
	 * Remove os livros que têm os indexes passados no array rows.
	 * 
	 * @param rows
	 *            - array que contém os indexes dos livros para apagar.
	 */
	public void removeLivros(int[] rows) {
		undoManager.execute(new RemoverLivro(rows));
	}

	/**
	 * Remove um exemplar dos livros que têm os indexes passados no array rows.
	 * 
	 * @param rows
	 *            - array que contém os indexes dos livros para apagar o
	 *            exemplar.
	 */
	public void removeExemplar(int[] rows) {
		undoManager.execute(new RemoverExemplar(rows));
	}

	public Livro getLivro(int rowIndex) {
		return livros.get(rowIndex);
	}

	/**
	 * Pergunta se quer incrementar um exemplar ao livro já existente.
	 * 
	 * @param l
	 * @return true - se pretende incrementar e apagar os empréstimos ligados
	 *         àquele livro (se existirem) <br>
	 *         - false caso contrário
	 */
	public boolean perguntaIncrementar(Livro l) {
		int ok = JOptionPane.showConfirmDialog(DataGui.getInstance(),
				"Esse livro já existe! Deseja aumentar um exemplar ao livro já existente?", "Livro já existe",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		if (ok == JOptionPane.OK_OPTION) {
			if (TableModelEmprestimo.getInstance().getEmprestimosByItem(l).length > 0) {
				int ok1 = JOptionPane.showConfirmDialog(null,
						"ATENÇÃO! O livro " + l.getNome()
								+ " tem empréstimos registados na base de dados!\nSe clicar em 'OK' todos os empréstimos ligados a esse livro serão apagados!\n"
								+ "Embora seja possível anular a ação de apagar o livro, os histórico de empréstimos para esse livro será perdido definitivamente!\n"
								+ "Tem a certeza que quer apagar?",
						"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				if (ok1 == JOptionPane.OK_OPTION)
					return true;
				else
					return false;
			} else
				return true;
		} else
			return false;
	}

	/**
	 * Incrementa um exemplar ao livro passado como parâmetro, se deseja
	 * confirmar.
	 * 
	 * @param livro
	 * @param undo
	 *            - verifica se o método é chamado manualmente ou através de uma
	 *            operação de undo.
	 */
	public void incrementarLivro(Livro livro, boolean undo) {
		if (!undo) {
			if (perguntaIncrementar(livro)) {
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

	/**
	 *
	 * @param l
	 * @return a linha que o livro está posicionado no ArrayList.
	 */
	public int getRow(Livro l) {
		for (int i = 0; i < livros.size(); i++) {
			if (livros.get(i).getId() == l.getId())
				return i;
		}
		return -1;
	}

	/**
	 * 
	 * @return o número de livros que estão disponíveis para empréstimo.
	 */
	public int getNumLivrosDisponiveis() {
		int n = 0;
		for (Livro l : livros) {
			if (l.isDisponivel())
				n++;
		}
		return n;
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
							if (perguntaIncrementar(livro)) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Título e Juntar", new IncLivro(l),
										new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this, "Título", livro, valor));
					}
					break;
				case 2:
					l.setAutor((String) valor);
					if (!(l.getAutor().equals(livro.getAutor()))) {
						if (livros.contains(l)) {
							if (perguntaIncrementar(livro)) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Autor e Juntar", new IncLivro(l),
										new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this, "Autor", livro, valor));
					}
					break;
				case 3:
					l.setEditora((String) valor);
					if (!(l.getEditora().equals(livro.getEditora()))) {
						if (livros.contains(l)) {
							if (perguntaIncrementar(livro)) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Editora e Juntar", new IncLivro(l),
										new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this, "Editora", livro, valor));
					}
					break;
				case 4:
					l.setClassificacao((String) valor);
					if (!(l.getClassificacao().equals(livro.getClassificacao()))) {
						if (livros.contains(l)) {
							if (perguntaIncrementar(livro)) {
								int[] rows = { rowIndex };
								undoManager.execute(new CompositeCommand("Atualizar Classificação e Juntar",
										new IncLivro(l), new RemoverLivro(rows)));
							}
						} else
							undoManager.execute(new AtualizaLivro(this, "Classificação", livro, valor));
					}
					break;
				case 5:
					undoManager.execute(new AtualizaExemplares(disponivel, livro, valor));
					break;
				case 8:
					undoManager.execute(new AtualizaLivro(this, "Local", livro, valor));
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

	/**
	 * Comando para atualizar o número de exemplares disponíveis e a coluna Disponível na base de dados.
	 * @param livro - livro que se pretende atualizar.
	 */
	public void atualizaExemplaresDisponiveis(Livro livro) {
		try {
			pst = con.prepareStatement("update livros set Disponíveis=? where ID=" + livro.getId());
			pst.setString(1, String.valueOf(livro.getN_exemp_disponiveis()));
			pst.execute();
			pst = con.prepareStatement("update livros set Disponível=? where ID=" + livro.getId());
			if (livro.isDisponivel())
				pst.setString(1, "Sim");
			else
				pst.setString(1, "Não");
			pst.execute();
			TableModelLivro.getInstance().fireTableDataChanged();
		} catch (

		SQLException e) {
			Log.getInstance().printLog("Erro ao atualizar número de exemplares disponíveis");
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas[columnIndex];
	}

	public UndoManager getUndoManager() {
		return undoManager;
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

	/**
	 * Método para inserir um livro na base de dados, na posição pretendida.
	 * @param livro - livro que se pretende inserir.
	 * @param row - linha em que se pretende inserir o livro.
	 */
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

	/**
	 * Método para incrementar os exemplares de um livro na base de dados.
	 * @param livro - livro que se pretende incrementar
	 */
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

	/**
	 * Classe que representa um comando para adicionar um livro.
	 * 
	 * @author Dário Pereira
	 *
	 */
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

	/**
	 * Classe que representa um comando para incrementar os exemplares de um livro.
	 * 
	 * @author Dário Pereira
	 *
	 */
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

	/**
	 * Classe que representa um comando para remover um ou vários livros.
	 * 
	 * @author Dário Pereira
	 *
	 */
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
					Livro l = livros.get(rows[i]);
					if (TableModelEmprestimo.getInstance().getEmprestimosByItem(l).length > 0) {
						int ok = JOptionPane.showConfirmDialog(null,
								"ATENÇÃO! O livro " + l.getNome()
										+ " tem empréstimos registados na base de dados!\nSe clicar em 'OK' todos os empréstimos ligados a esse livro serão apagados!\n"
										+ "Embora seja possível anular a ação de apagar o livro, os histórico de empréstimos para esse livro será perdido definitivamente!\n"
										+ "Tem a certeza que quer apagar?",
								"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
								new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
						if (ok == JOptionPane.OK_OPTION) {
							pst = con.prepareStatement("delete from livros where ID=" + livros.get(rows[i]).getId());
							pst.execute();
							remover.add(livros.get(rows[i]));
							TableModelEmprestimo.getInstance()
									.removeEmprestimos(TableModelEmprestimo.getInstance().getEmprestimosByItem(l));
						}
					} else {
						pst = con.prepareStatement("delete from livros where ID=" + livros.get(rows[i]).getId());
						pst.execute();
						remover.add(livros.get(rows[i]));
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

	/**
	 * Classe que representa um comando para remover um exemlar de um ou vários
	 * livros ou remover o livro, se existir apenas um exemplar.
	 * 
	 * @author Dário Pereira
	 *
	 */
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
						Livro l = livros.get(rows[i]);
						if (TableModelEmprestimo.getInstance().getEmprestimosByItem(l).length > 0) {
							int ok = JOptionPane.showConfirmDialog(null,
									"ATENÇÃO! O livro " + l.getNome()
											+ " tem empréstimos registados na base de dados!\nSe clicar em 'OK' todos os empréstimos ligados a esse livro serão apagados!\n"
											+ "Embora seja possível anular a ação de apagar o livro, os histórico de empréstimos para esse livro será perdido definitivamente!\n"
											+ "Tem a certeza que quer apagar?",
									"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
									new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
							if (ok == JOptionPane.OK_OPTION) {
								pst = con
										.prepareStatement("delete from livros where ID=" + livros.get(rows[i]).getId());
								pst.execute();
								remover.add(livros.get(rows[i]));
								modificou.add(livros.get(rows[i]));
								TableModelEmprestimo.getInstance()
										.removeEmprestimos(TableModelEmprestimo.getInstance().getEmprestimosByItem(l));
							}
						}
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

	/**
	 * Ordena a tabela de livros pela ordem natural.
	 */
	public void ordenar() {
		livros.sort(null);
		fireTableDataChanged();
		Log.getInstance().printLog("Livros ordenados com sucesso");
	}

	public static TableModelLivro getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TableModelLivro();
		return INSTANCE;
	}

}
