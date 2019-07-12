package dad.biblioteca.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.time.DurationFormatUtils;

import dad.biblioteca.Emprestimo;
import dad.biblioteca.Item;
import dad.biblioteca.Livro;
import dad.biblioteca.gui.DataGui;
import dad.biblioteca.gui.Login;
import dad.recursos.CellRenderer;
import dad.recursos.CellRenderer2;
import dad.recursos.CellRendererNoImage;
import dad.recursos.Log;
import dad.recursos.RealizarEmprestimo;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

public class EmprestimoPanel extends JPanel {

	private static EmprestimoPanel INSTANCE;
	private JTable emprestimos;
	private TableModelEmprestimo modelEmprestimo;
	private JPanel pInferior, panel2, panel3;
	private JTextField jtfTotal;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5439324224974968781L;
	private String[] columnToolTips = { "ID do empréstimo", "ID do item", "Título do item",
			"Data em que o empréstimo foi realizado", "Data máxima de devolução do empréstimo",
			"CPF do cliente a quem foi feito o empréstimo", "Funcionário que realizou o empréstimo",
			"O empréstimo está ativo? (Sim -> O item nao foi devolvido / Não -> O item foi devolvido)",
			"Valor da multa a ser paga, caso esteja em atraso" };

	public EmprestimoPanel() {
		super();
		INSTANCE = this;
		setLayout(new BorderLayout());
		modelEmprestimo = TableModelEmprestimo.getInstance();
		emprestimos = new JTable(modelEmprestimo) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 776311897765510270L;

			@Override
			public boolean isCellEditable(int data, int columns) {
				return false;
			}

			@Override
			public Component prepareRenderer(TableCellRenderer r, int data, int columns) {
				int row = convertRowIndexToModel(data);
				Component c = super.prepareRenderer(r, data, columns);
				if (data % 2 == 0)
					c.setBackground(Color.WHITE);
				else
					c.setBackground(MaterialColors.GRAY_100);
				if (isCellSelected(data, columns)) {
					if (TableModelEmprestimo.getInstance().getValueAt(row, 7).equals("Sim"))
						c.setBackground(MaterialColors.GREEN_A100);
					else
						c.setBackground(MaterialColors.RED_300);
				}
				if (columns == 7) {
					if (TableModelEmprestimo.getInstance().getValueAt(row, columns).equals("Sim"))
						c.setBackground(MaterialColors.GREEN_A100);
					else
						c.setBackground(MaterialColors.RED_300);
				}
				if (columns == 8) {
					if (TableModelEmprestimo.getInstance().getValueAt(row, columns).equals(0.0))
						c.setBackground(MaterialColors.GREEN_A100);
					else
						c.setBackground(MaterialColors.RED_300);
				}
				return c;
			}

			// Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				int realColumnIndex = convertColumnIndexToModel(colIndex);
				if (rowIndex != -1) {
					int realRowIndex = convertRowIndexToModel(rowIndex);
					tip = String.valueOf(modelEmprestimo.getValueAt(realRowIndex, realColumnIndex));
				} else
					tip = null;
				return tip;
			}

			// Implement table header tool tips.
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
						return columnToolTips[realIndex];
					}
				};
			}
		};
		TableCellRenderer tcr = emprestimos.getTableHeader().getDefaultRenderer();
		emprestimos.getTableHeader().setDefaultRenderer(new TableCellRenderer() {

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

		emprestimos.setPreferredScrollableViewportSize(new Dimension(800, 600));
		emprestimos.setFillsViewportHeight(true);
		emprestimos.setAutoCreateRowSorter(true);
		emprestimos.getTableHeader().setReorderingAllowed(false);
		emprestimos.setRowHeight(30);
		emprestimos.getColumnModel().getColumn(0).setMaxWidth(90);
		emprestimos.getColumnModel().getColumn(1).setMaxWidth(120);
		emprestimos.getColumnModel().getColumn(7).setMaxWidth(120);
		emprestimos.getColumnModel().getColumn(8).setMaxWidth(80);
		emprestimos.getColumnModel().getColumn(0).setMinWidth(90);
		emprestimos.getColumnModel().getColumn(1).setMinWidth(120);
		emprestimos.getColumnModel().getColumn(7).setMinWidth(120);
		emprestimos.getColumnModel().getColumn(8).setMinWidth(80);
		emprestimos.setDefaultRenderer(Object.class, new CellRenderer());

		emprestimos.getColumnModel().getColumn(0).setCellRenderer(new CellRendererNoImage());
		emprestimos.getColumnModel().getColumn(1).setCellRenderer(new CellRendererNoImage());
		emprestimos.getColumnModel().getColumn(7).setCellRenderer(new CellRendererNoImage());
		emprestimos.getColumnModel().getColumn(8).setCellRenderer(new CellRenderer2());

		JScrollPane jsEmprestimos = new JScrollPane(emprestimos);
		add(jsEmprestimos, BorderLayout.CENTER);

		emprestimos.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				emprestimos.scrollRectToVisible(emprestimos.getCellRect(emprestimos.getRowCount() - 1, 0, true));
			}

		});

		pInferior = new JPanel(new BorderLayout());
		add(pInferior, BorderLayout.SOUTH);
		panel2 = new JPanel(new GridLayout(2, 1));
		panel3 = new JPanel();
		JLabel total = new JLabel("Total: ");
		jtfTotal = new JTextField(String.valueOf(modelEmprestimo.getRowCount()));
		jtfTotal.setEditable(false);
		panel3.add(total);
		panel3.add(jtfTotal);
		panel2.add(panel3);

		inicializarBotoes();

		JMenuItem deleteItem = new JMenuItem("Apagar");
		deleteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removerEmprestimos();
			}
		});

		JMenuItem info = new JMenuItem("Informações");
		info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				abrir(modelEmprestimo.getEmprestimo(emprestimos.convertRowIndexToModel(emprestimos.getSelectedRow())));

			}
		});

		JMenuItem atualizar = new JMenuItem("Atualizar Tabela");
		atualizar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TableModelEmprestimo.getInstance().fireTableDataChanged();
				TableModelEmprestimo.getInstance().atualizarMultas();
			}
		});

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int rowAtPointOriginal = emprestimos
								.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), emprestimos));
						if (rowAtPointOriginal > -1) {
							int rowAtPoint = emprestimos.convertRowIndexToModel(rowAtPointOriginal);
							if (rowAtPoint > -1) {
								int[] rows = convertRowsIndextoModel();
								if (rows.length <= 1) {
									info.setVisible(true);
									emprestimos.setRowSelectionInterval(rowAtPointOriginal, rowAtPointOriginal);
								} else {
									info.setVisible(false);
								}
								deleteItem.setVisible(true);
							}
						} else {
							info.setVisible(false);
							deleteItem.setVisible(false);
							atualizar.setVisible(true);
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

		popupMenu.add(info);
		popupMenu.add(deleteItem);
		popupMenu.add(atualizar);

		popupMenu.setPopupSize(225, 75);

		emprestimos.setComponentPopupMenu(popupMenu);

		emprestimos.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteRow");
		emprestimos.getActionMap().put("deleteRow", new DeleteAction());

		emprestimos.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int column = table.columnAtPoint(point);
				int rowAtPoint = table.rowAtPoint(point);
				if (rowAtPoint != -1) {
					int row = table.convertRowIndexToModel(rowAtPoint);
					if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
						if (column == 0 || column == 2 || column == 3 || column == 4 || column == 6 || column == 7
								|| column == 8)
							abrir(modelEmprestimo.getEmprestimo(row));
						else if (column == 1) {
							try {
								LivroPanel.getInstance().abrir(TableModelLivro.getInstance()
										.getLivroById((Integer) table.getValueAt(rowAtPoint, column)));
							} catch (NullPointerException e) {
								JOptionPane.showMessageDialog(null,
										"Atenção! O livro já não existe na base de dados. Deve ter sido apagado!",
										"ERRO", JOptionPane.ERROR_MESSAGE,
										new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
							}
						} else if (column == 5) {
							// TODO abrir cliente
						}
					}
				}
			}
		});

		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		emprestimos.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "solve");
		emprestimos.getActionMap().put("solve", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -833616209546223519L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (emprestimos.getSelectedRows().length == 1)
					abrir(modelEmprestimo
							.getEmprestimo(emprestimos.convertRowIndexToModel(emprestimos.getSelectedRow())));

			}
		});

	}

	public void inicializarBotoes() {
		pInferior.add(panel2, BorderLayout.WEST);
		JButton bSair = new JButton("SAIR");
		bSair.setBackground(new Color(247, 247, 255));
		bSair.setForeground(MaterialColors.LIGHT_BLUE_400);
		personalizarBotao(bSair);
		bSair.addActionListener(new SairAction());
		panel2.add(bSair);

		JPanel panel4 = new JPanel(new GridLayout(2, 1));
		pInferior.add(panel4, BorderLayout.EAST);

	}

	public void personalizarBotao(JButton jb) {
		jb.setFont(new Font("Roboto", Font.PLAIN, 15));
		MaterialUIMovement.add(jb, MaterialColors.GRAY_300, 5, 1000 / 30);
	}

	public int[] convertRowsIndextoModel() {
		int[] rows = emprestimos.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			rows[i] = emprestimos.convertRowIndexToModel(rows[i]);
		}
		return rows;
	}

	public void removerEmprestimos() {
		int[] rows = convertRowsIndextoModel();
		if (rows.length > 0) {
			int ok = JOptionPane.showConfirmDialog(this,
					"Tem certeza que quer apagar o(s) empréstimo(s) selecionado(s)?\nO recibo relacionado a cada empréstimo também será apagado!\nATENÇÃO: ESSA AÇÃO NÃO PODE SER ANULADA!\nObs: pode marcar o empréstimo como 'entregue' sem precisar apagar!",
					"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			if (ok == JOptionPane.OK_OPTION) {
				modelEmprestimo.removeEmprestimos(rows);
			}
		}
	}

	public JTable getEmprestimos() {
		return emprestimos;
	}

	public void setTable(JTable emprestimos) {
		this.emprestimos = emprestimos;
	}

	public void abrir(Emprestimo emp) {
		new RealizarEmprestimo(emp).open();
	}

	public void realizarEmprestimo(Livro l) {
		new RealizarEmprestimo(l).open();

	}

	public JTextField getJtfTotal() {
		return jtfTotal;
	}

	private class SairAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			DataGui.getInstance().setVisible(false);
			long time = System.currentTimeMillis() - Login.inicialTime;
			Log.getInstance().printLog("Usuário " + Login.NOME + " saiu!\nTempo de Uso: "
					+ DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's"));
			Login.getInstance().open();
		}
	}

	private class DeleteAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5018711156829284772L;

		@Override
		public void actionPerformed(ActionEvent e) {
			removerEmprestimos();
		}
	}

	public static EmprestimoPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new EmprestimoPanel();
		return INSTANCE;
	}

	public JTable getSmallTable(Item item) {
		if (item instanceof Livro) {
			Livro l = (Livro) item;
			JTable small = new JTable(TableModelEmprestimo.getInstance()){
				/**
				 * 
				 */
				private static final long serialVersionUID = -7294828362296077489L;

				@Override
				public Component prepareRenderer(TableCellRenderer r, int data, int columns) {
					int rowIndex = convertRowIndexToModel(data);
					Component c = super.prepareRenderer(r, data, columns);
					if (data % 2 == 0)
						c.setBackground(Color.WHITE);
					else
						c.setBackground(MaterialColors.GRAY_100);
					if (isCellSelected(data, columns)) {
						if (!TableModelEmprestimo.getInstance().getEmprestimo(rowIndex).isEntregue())
							c.setBackground(MaterialColors.GREEN_A100);
						else
							c.setBackground(MaterialColors.RED_300);
					}
					if (columns == 5) {
						if (!TableModelEmprestimo.getInstance().getEmprestimo(rowIndex).isEntregue())
							c.setBackground(MaterialColors.GREEN_A100);
						else
							c.setBackground(MaterialColors.RED_300);
					}
					if (columns == 6) {
						if (TableModelEmprestimo.getInstance().getEmprestimo(rowIndex).getMulta()==0)
							c.setBackground(MaterialColors.GREEN_A100);
						else
							c.setBackground(MaterialColors.RED_300);
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
							return columnToolTips[realIndex];
						}
					};
				}
			};
			small.setRowHeight(30);
			small.removeColumn(small.getColumnModel().getColumn(1));
			small.removeColumn(small.getColumnModel().getColumn(1));
			DataGui.getInstance().filtrarEmprestimos(l, small);
			small.getColumnModel().getColumn(6).setCellRenderer(new CellRenderer2());
			
			return small;
		} else
			return emprestimos;
	}
}
