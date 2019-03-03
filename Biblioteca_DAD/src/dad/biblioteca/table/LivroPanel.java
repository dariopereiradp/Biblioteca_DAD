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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.table.TableCellRenderer;
import dad.biblioteca.Livro;
import dad.biblioteca.gui.DataGui;
import dad.biblioteca.gui.Inicial;
import dad.recursos.CellRenderer;
import dad.recursos.CellRendererBollean;
import dad.recursos.CellRendererInt;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

public class LivroPanel extends JPanel {

	private static LivroPanel INSTANCE;
	private JTable livros;
	private TableModelLivro modelLivro;
	private JPanel panelAdd, pInferior, panel2, panel3;
	private JTextField titulo, autor, editora, classificacao, local;
	private JTextField jtfTotal;
	private JButton bAdd;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5439324224974968781L;

	public LivroPanel() {
		super();
		INSTANCE = this;
		setLayout(new BorderLayout());
		modelLivro = TableModelLivro.getInstance();
		livros = new JTable(modelLivro) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 776311897765510270L;

			@Override
			public boolean isCellEditable(int data, int columns) {
				if (columns == 0 || columns == 6 || columns == 7)
					return false;
				return true;
			}

			@Override
			public Component prepareRenderer(TableCellRenderer r, int data, int columns) {
				Component c = super.prepareRenderer(r, data, columns);
				if (data % 2 == 0)
					c.setBackground(Color.WHITE);
				else
					c.setBackground(MaterialColors.GRAY_100);
				if (isCellSelected(data, columns))
					c.setBackground(MaterialColors.GREEN_A100);
				if (columns == 7) {
					if (TableModelLivro.getInstance().getValueAt(data, columns).equals("Sim"))
						c.setBackground(MaterialColors.GREEN_A100);
					else
						c.setBackground(MaterialColors.RED_300);
				}
				return c;
			}
		};
		TableCellRenderer tcr = livros.getTableHeader().getDefaultRenderer();
		livros.getTableHeader().setDefaultRenderer(new TableCellRenderer() {

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
					return new ImageIcon(getClass().getResource("sort.png"));
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

		livros.setPreferredScrollableViewportSize(new Dimension(800, 600));
		livros.setFillsViewportHeight(true);
		livros.setAutoCreateRowSorter(true);
		livros.getTableHeader().setReorderingAllowed(false);
		livros.setRowHeight(30);
		livros.getColumnModel().getColumn(0).setMaxWidth(100);
		livros.getColumnModel().getColumn(5).setMaxWidth(120);
		livros.getColumnModel().getColumn(6).setMaxWidth(120);
		livros.getColumnModel().getColumn(7).setMaxWidth(120);
		livros.getColumnModel().getColumn(5).setMinWidth(120);
		livros.getColumnModel().getColumn(6).setMinWidth(120);
		livros.getColumnModel().getColumn(7).setMinWidth(120);
		livros.setDefaultRenderer(Object.class, new CellRenderer());

		livros.getColumnModel().getColumn(7).setCellRenderer(new CellRendererBollean());
		livros.getColumnModel().getColumn(0).setCellRenderer(new CellRendererInt());
		livros.getColumnModel().getColumn(5).setCellRenderer(new CellRendererInt());
		livros.getColumnModel().getColumn(6).setCellRenderer(new CellRendererInt());

		JScrollPane jsLivros = new JScrollPane(livros);
		add(jsLivros, BorderLayout.CENTER);

		livros.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				livros.scrollRectToVisible(livros.getCellRect(livros.getRowCount() - 1, 0, true));
			}

		});

		pInferior = new JPanel(new BorderLayout());
		add(pInferior, BorderLayout.SOUTH);
		panel2 = new JPanel(new GridLayout(2, 1));
		panel3 = new JPanel();
		JLabel total = new JLabel("Total: ");
		jtfTotal = new JTextField(String.valueOf(modelLivro.getRowCount()));
		jtfTotal.setEditable(false);
		panel3.add(total);
		panel3.add(jtfTotal);
		panel2.add(panel3);

		inicializarBotoes();

		inicializarPanelAdd();

		JMenuItem deleteItem = new JMenuItem("Apagar");
		deleteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removerLivros();
			}
		});

		JMenuItem deleteOneItem = new JMenuItem("Apagar apenas 1 exemplar desse livro");
		deleteOneItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removerExemplar();
			}
		});

		JMenuItem deleteAllItem = new JMenuItem("Apagar todos os exemplares desse livro");
		deleteAllItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removerLivros();
			}
		});

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int rowAtPointOriginal = livros
								.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), livros));
						int rowAtPoint = livros.convertRowIndexToModel(rowAtPointOriginal);
						if (rowAtPoint > -1) {
							int[] rows = convertRowsIndextoModel();
							if (rows.length <= 1) {
								livros.setRowSelectionInterval(rowAtPointOriginal, rowAtPointOriginal);
								if (TableModelLivro.getInstance().getLivro(rowAtPoint).getNumero_exemplares() > 1) {
									deleteItem.setVisible(false);
									deleteOneItem.setVisible(true);
									deleteAllItem.setVisible(true);
								} else {
									deleteOneItem.setVisible(false);
									deleteAllItem.setVisible(false);
									deleteItem.setVisible(true);
								}
							} else {
								boolean exemplares = false;
								for (int i = 0; i < rows.length; i++) {
									if (TableModelLivro.getInstance().getLivro(rows[i]).getNumero_exemplares() > 1)
										exemplares = true;
								}
								if (exemplares) {
									deleteItem.setVisible(false);
									deleteOneItem.setVisible(true);
									deleteAllItem.setVisible(true);
								} else {
									deleteOneItem.setVisible(false);
									deleteAllItem.setVisible(false);
									deleteItem.setVisible(true);
								}
							}
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

		popupMenu.add(deleteItem);
		popupMenu.add(deleteOneItem);
		popupMenu.add(deleteAllItem);

		JMenuItem emprestimoItem = new JMenuItem("Realizar Empr�sitimo");
		emprestimoItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				realizarEmprestimo();
			}
		});
		popupMenu.add(emprestimoItem);

		livros.setComponentPopupMenu(popupMenu);

		livros.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteRow");
		livros.getActionMap().put("deleteRow", new DeleteAction());

	}

	public void inicializarBotoes() {
		pInferior.add(panel2, BorderLayout.WEST);
		JButton bVoltar = new JButton("VOLTAR");
		bVoltar.setBackground(new Color(247, 247, 255));
		bVoltar.setForeground(MaterialColors.LIGHT_BLUE_400);
		personalizarBotao(bVoltar);
		bVoltar.addActionListener(new VoltarAction());
		panel2.add(bVoltar);

		JPanel panel4 = new JPanel(new GridLayout(2, 1));
		pInferior.add(panel4, BorderLayout.EAST);

		JButton bAdd = new JButton("ADICIONAR");
		bAdd.setForeground(MaterialColors.WHITE);
		bAdd.setBackground(MaterialColors.LIGHT_GREEN_500);
		personalizarBotao(bAdd);
		bAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				adicionarLivro();

			}
		});
		panel4.add(bAdd);
	}

	private void inicializarPanelAdd() {
		panelAdd = new JPanel(new GridLayout(1, 5));
		
		JPanel panelTitulo = new JPanel(new BorderLayout());
		JLabel lTitulo = new JLabel("T�tulo: ");
		lTitulo.setFont(new Font("Roboto", Font.BOLD, 15));
		panelTitulo.add(lTitulo, BorderLayout.WEST);

		titulo = new JTextField();
		titulo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarLivro();
			}
		});
		panelTitulo.add(titulo, BorderLayout.CENTER);

		panelAdd.add(panelTitulo);
		
		
		JPanel panelAutor = new JPanel(new BorderLayout());
		JLabel lAutor = new JLabel("Autor: ");
		lAutor.setFont(new Font("Roboto", Font.BOLD, 15));
		panelAutor.add(lAutor, BorderLayout.WEST);

		autor = new JTextField();
		autor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarLivro();
			}
		});
		panelAutor.add(autor, BorderLayout.CENTER);
		panelAdd.add(panelAutor);
		
		JPanel last = new JPanel(new GridLayout(1, 6));

		JPanel panelEditora = new JPanel(new BorderLayout());
		JLabel lEditora = new JLabel("Editora: ");
		lEditora.setFont(new Font("Roboto", Font.BOLD, 15));
		panelEditora.add(lEditora, BorderLayout.WEST);

		editora = new JTextField();
		editora.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarLivro();
			}
		});
		panelEditora.add(editora, BorderLayout.CENTER);
		last.add(panelEditora);

		JPanel panelClass = new JPanel(new BorderLayout());
		JLabel lClassificacao = new JLabel("Classifica��o: ");
		lClassificacao.setFont(new Font("Roboto", Font.BOLD, 15));
		panelClass.add(lClassificacao, BorderLayout.WEST);

		classificacao = new JTextField();
		classificacao.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarLivro();
			}
		});
		panelClass.add(classificacao, BorderLayout.CENTER);
		last.add(panelClass);
		
		JPanel panelLocal = new JPanel(new BorderLayout());
		JLabel lLocal = new JLabel("Localiza��o: ");
		lLocal.setFont(new Font("Roboto", Font.BOLD, 15));
		panelLocal.add(lLocal, BorderLayout.WEST);

		local = new JTextField();
		local.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarLivro();
			}
		});
		panelLocal.add(local, BorderLayout.CENTER);
		last.add(panelLocal);
		

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarLivro();
			}
		});
		
		JPanel both = new JPanel(new GridLayout(2, 1));
		both.add(panelAdd);
		both.add(last);

		pInferior.add(both, BorderLayout.CENTER);
	}

	public void personalizarBotao(JButton jb) {
		jb.setFont(new Font("Roboto", Font.PLAIN, 15));
		MaterialUIMovement.add(jb, MaterialColors.GRAY_300, 5, 1000 / 30);
	}

	public void adicionarLivro() {
		if (titulo.getText().trim().equals(""))
			JOptionPane.showMessageDialog(this, "Deve inserir pelo menos o t�tulo!", "ADICIONAR",
					JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("DAD_S.jpg")));
		else {
			if (autor.getText().trim().equals("") && editora.getText().trim().equals("")
					&& classificacao.getText().trim().equals(""))
				modelLivro.addLivro(new Livro(titulo.getText()));
			else
				modelLivro.addLivro(
						new Livro(titulo.getText(), autor.getText(), editora.getText(), classificacao.getText(), local.getText()));
		}
	}

	public int[] convertRowsIndextoModel() {
		int[] rows = livros.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			rows[i] = livros.convertRowIndexToModel(rows[i]);
		}
		return rows;
	}

	public void clearTextFields() {
		titulo.setText("");
		autor.setText("");
		editora.setText("");
		classificacao.setText("");
	}

	public void removerLivros() {
		int[] rows = convertRowsIndextoModel();
		if (rows.length > 0) {
			int ok = JOptionPane.showConfirmDialog(this, "Tem certeza que quer apagar o(s) livro(s) selecionado(s)?",
					"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_S.jpg")));
			if (ok == JOptionPane.OK_OPTION) {
				modelLivro.removeLivros(rows);
			}
		}
	}

	public void removerExemplar() {
		int[] rows = convertRowsIndextoModel();
		if (rows.length > 0) {
			int ok = JOptionPane.showConfirmDialog(this, "Tem certeza que quer apagar o exemplar selecionado?",
					"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_S.jpg")));
			if (ok == JOptionPane.OK_OPTION) {
				modelLivro.removeExemplar(rows);
			}
		}
	}

	public JTable getLivros() {
		return livros;
	}

	public void realizarEmprestimo() {
		// TODO Auto-generated method stub

	}

	public JTextField getJtfTotal() {
		return jtfTotal;
	}

	private class VoltarAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			DataGui.getInstance().setVisible(false);
			Inicial.getInstance().open();
		}
	}

	private class DeleteAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5018711156829284772L;

		@Override
		public void actionPerformed(ActionEvent e) {
			removerLivros();
		}
	}

	public static LivroPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new LivroPanel();
		return INSTANCE;
	}

	public JButton getbAdd() {
		return bAdd;
	}

}
