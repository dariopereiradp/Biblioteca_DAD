package dad.biblioteca.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import dad.biblioteca.Funcionario;
import dad.biblioteca.gui.ChangePassword;
import dad.recursos.ConexaoLogin;
import dad.recursos.Log;
import mdlaf.utils.MaterialColors;

/**
 * Classe que representa o TableModel para os funcionários.
 * 
 * @author Dário Pereira
 *
 */
public class TableModelFuncionario extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3247984074345998765L;
	private static TableModelFuncionario INSTANCE;
	private ArrayList<Funcionario> funcionarios;
	private String[] colunas = { "Nome (Login)", "Senha", "Número de Acessos", "Data do Último Acesso",
			"Data de Criação" };
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;

	private TableModelFuncionario() {
		INSTANCE = this;
	}

	/**
	 * Faz upload da base de dados e cria o ArrayList com os funcionários que
	 * existirem na base de dados Logins.
	 */
	public void uploadDataBase() {
		funcionarios = new ArrayList<>();
		try {
			con = ConexaoLogin.getConnection();
			pst = con.prepareStatement("select * from logins");
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					String nome = rs.getString(1);
					int num_acessos = rs.getInt(3);
					Date data_ultimo_acesso = rs.getTimestamp(4);
					Date data_criacao = rs.getTimestamp(5);
					Funcionario func = new Funcionario(nome, num_acessos, data_ultimo_acesso, data_criacao);
					funcionarios.add(func);
				} while (rs.next());
			}
			fireTableDataChanged();
			Log.getInstance().printLog("Base de dados funcionarios carregada com sucesso!");
		} catch (Exception e) {
			Log.getInstance().printLog(
					"Erro ao carregar a base de dados dos Funcionários: " + e.getMessage() + "\n" + getClass());
			e.printStackTrace();
		}
	}

	@Override
	public int getRowCount() {
		return funcionarios.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas[columnIndex];
	}

	public ArrayList<Funcionario> getFuncionarios() {
		return funcionarios;
	}

	/**
	 * Adiciona um funcionário.
	 * @param func - funcionário a adicionar
	 */
	public void addFuncionario(Funcionario func) {
		funcionarios.add(func);
	}

	public Funcionario getFuncionario(int rowIndex) {
		return funcionarios.get(rowIndex);
	}
	
	/**
	 * @param table - tabela que se pretende converter os indexes
	 * @return um array com todos os indexes do modelo dos funcionários que estão
	 *         selecionados.
	 */
	public int[] convertRowsIndextoModel(JTable table) {
		int[] rows = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			rows[i] = table.convertRowIndexToModel(rows[i]);
		}
		return rows;
	}

	/**
	 * Remove os funcionários que estão nas posições indicadas pelo array rows.
	 * @param rows - array que contém as posições dos funcionários a remover.
	 */
	public void removeFuncionarios(int[] rows) {
		ArrayList<Funcionario> toDelete = new ArrayList<>();
		for (int i = 0; i < rows.length; i++) {
			Funcionario func = funcionarios.get(rows[i]);
			if (!func.getNome().equals("admin"))
				apagar(func, toDelete);
		}
		funcionarios.removeAll(toDelete);
	}

	/**
	 * Remove um funcionário da base de dados.
	 * @param func - funcionários a remover
	 * @param toDelete
	 */
	private void apagar(Funcionario func, ArrayList<Funcionario> toDelete) {
		try {
			con = ConexaoLogin.getConnection();
			pst = con.prepareStatement("delete from logins where Nome=?");
			pst.setString(1, func.getNome());
			pst.execute();
			toDelete.add(func);
			fireTableDataChanged();
		} catch (SQLException e1) {
			Log.getInstance().printLog("Erro ao apagar o funcionário! - " + e1.getMessage());
			e1.printStackTrace();
		}

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return funcionarios.get(rowIndex).getNome();
		case 1:
			return "********";
		case 2:
			return funcionarios.get(rowIndex).getNum_acessos();
		case 3:
			return new SimpleDateFormat("dd/MM/yyyy 'às' HH'h'mm'm'ss's'")
					.format(funcionarios.get(rowIndex).getData_ultimo_acesso());
		case 4:
			return new SimpleDateFormat("dd/MM/yyyy 'às' HH'h'mm'm'ss's'")
					.format(funcionarios.get(rowIndex).getData_criacao());
		default:
			return funcionarios.get(rowIndex);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int column) {
		if (column == 2) {
			return Integer.class;
		}
		return String.class;
	}

	/**
	 * Devolve uma nova tabela de Funcionarios
	 * 
	 * @return - a nova tabela
	 */
	public JTable getSmallTable() {
		JTable small = new JTable(TableModelFuncionario.getInstance()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7294828362296077489L;

			@Override
			public Component prepareRenderer(TableCellRenderer r, int data, int columns) {
				Component c = super.prepareRenderer(r, data, columns);
				if (data % 2 == 0)
					c.setBackground(Color.WHITE);
				else
					c.setBackground(MaterialColors.GRAY_100);
				if (isCellSelected(data, columns)) {
					c.setBackground(MaterialColors.GREEN_A100);
				}

				return c;
			}

			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -8247305580277890952L;

					public String getToolTipText(MouseEvent e) {
						@SuppressWarnings("unused")
						String tip = null;
						Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index).getModelIndex();
						return colunas[realIndex];
					}
				};
			}

		};

		TableCellRenderer tcr = small.getTableHeader().getDefaultRenderer();
		small.getTableHeader().setDefaultRenderer(new TableCellRenderer() {

			private Icon ascendingIcon = UIManager.getIcon("Table.ascendingSortIcon");
			private Icon descendingIcon = UIManager.getIcon("Table.descendingSortIcon");

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocused, int row, int column) {

				Component comp = tcr.getTableCellRendererComponent(table, value, isSelected, hasFocused, row, column);
				if (comp instanceof JLabel) {
					JLabel label = (JLabel) comp;
					label.setPreferredSize(new Dimension(100, 30));
					label.setIcon(getSortIcon(table, column));
					label.setHorizontalAlignment(SwingConstants.CENTER);
					label.setBackground(MaterialColors.YELLOW_300);
					label.setFont(new Font("Roboto", Font.BOLD, 15));
					label.setBorder(BorderFactory.createMatteBorder(0, 1, 3, 1, MaterialColors.GREEN_300));
					return label;
				}
				return comp;
			}

			private Icon getSortIcon(JTable table, int column) {
				SortOrder sortOrder = getColumnSortOrder(table, column);
				if (SortOrder.UNSORTED == sortOrder) {
					return new ImageIcon(getClass().getResource("/sort.png"));
				}
				return SortOrder.ASCENDING == sortOrder ? ascendingIcon : descendingIcon;
			}

			private SortOrder getColumnSortOrder(JTable table, int column) {
				if (table == null || table.getRowSorter() == null) {
					return SortOrder.UNSORTED;
				}
				List<? extends SortKey> keys = table.getRowSorter().getSortKeys();
				if (keys.size() > 0) {
					SortKey key = keys.get(0);
					if (key.getColumn() == table.convertColumnIndexToModel(column)) {
						return key.getSortOrder();
					}
				}
				return SortOrder.UNSORTED;
			}
		});

		small.setPreferredScrollableViewportSize(new Dimension(1100, 350));
		small.setFillsViewportHeight(true);
		small.setAutoCreateRowSorter(true);
		small.getTableHeader().setReorderingAllowed(false);
		small.setRowHeight(30);
		small.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3355270436133061350L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
				if (row != 0)
					this.setIcon(new ImageIcon(getClass().getResource("/edit.png")));
				else
					this.setIcon(null);
				return this;
			}
		});

		small.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				small.scrollRectToVisible(small.getCellRect(small.getRowCount() - 1, 0, true));
			}

		});

		small.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int column = table.columnAtPoint(point);
				int rowAtPoint = table.rowAtPoint(point);
				if (rowAtPoint != -1) {
					int row = table.convertRowIndexToModel(rowAtPoint);
					if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
						if (rowAtPoint != 0 && column == 1)
							new ChangePassword(TableModelFuncionario.getInstance().getFuncionario(row).getNome(), true)
									.open();
						;

					}
				}
			}

		});

		JMenuItem mudarPassword = new JMenuItem("Alterar senha");
		mudarPassword.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ChangePassword(TableModelFuncionario.getInstance()
						.getFuncionario(small.convertRowIndexToModel(small.getSelectedRow())).getNome(), true).open();
			}
		});

		JMenuItem apagarItem = new JMenuItem("Apagar");
		apagarItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = TableModelFuncionario.getInstance().convertRowsIndextoModel(small);
				if (rows.length > 0) {
					int ok = JOptionPane.showConfirmDialog(small,
							"Tem certeza que quer apagar o(s) funcionário(s) selecionado(s)?\nATENÇÃO: ESSA AÇÃO NÃO PODE SER ANULADA!",
							"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
							new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
					if (ok == JOptionPane.OK_OPTION) {
						TableModelFuncionario.getInstance().removeFuncionarios(rows);
					}
				}
			}
		});

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int rowAtPointOriginal = small
								.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), small));
						if (rowAtPointOriginal > -0) {
							int rowAtPoint = small.convertRowIndexToModel(rowAtPointOriginal);
							if (rowAtPoint > 0) {
								popupMenu.setVisible(true);
								int[] rows = TableModelFuncionario.getInstance().convertRowsIndextoModel(small);
								if (rows.length == 1) {
									mudarPassword.setVisible(true);
									small.setRowSelectionInterval(rowAtPointOriginal, rowAtPointOriginal);
								} else {
									mudarPassword.setVisible(false);
								}
								apagarItem.setVisible(true);
							}
						} else {
							popupMenu.setVisible(false);
							mudarPassword.setVisible(false);
							apagarItem.setVisible(false);
						}
					}
				});
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {

			}
		});

		popupMenu.add(mudarPassword);
		popupMenu.add(apagarItem);
		popupMenu.setPopupSize(100, 60);
		small.setComponentPopupMenu(popupMenu);

		small.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteRow");
		small.getActionMap().put("deleteRow", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = TableModelFuncionario.getInstance().convertRowsIndextoModel(small);
				if (rows.length > 0) {
					if (!(rows.length == 1 && rows[0] == 0)) {
						int ok = JOptionPane.showConfirmDialog(small,
								"Tem certeza que quer apagar o(s) funcionário(s) selecionado(s)?\nATENÇÃO: ESSA AÇÃO NÃO PODE SER ANULADA!",
								"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
								new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
						if (ok == JOptionPane.OK_OPTION) {
							TableModelFuncionario.getInstance().removeFuncionarios(rows);
						}
					}
				}
			}
		});

		return small;
	}
	
	public static TableModelFuncionario getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TableModelFuncionario();
		}
		return INSTANCE;
	}
}
