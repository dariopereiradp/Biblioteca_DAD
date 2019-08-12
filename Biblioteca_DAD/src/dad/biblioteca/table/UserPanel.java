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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;

import com.toedter.calendar.JDateChooser;
import dad.biblioteca.User;
import dad.biblioteca.gui.LivroDetail;
import dad.biblioteca.gui.UserDetail;
import dad.recursos.CellRenderer;
import dad.recursos.CellRendererNoImage;
import dad.recursos.CpfValidator;
import dad.recursos.Log;
import dad.recursos.SairAction;
import mdlaf.utils.MaterialColors;

/**
 * Classe que representa a tabela de Clientes na DataGui.
 * 
 * @author Dário Pereira
 *
 */
public class UserPanel extends JPanel {

	private static UserPanel INSTANCE;
	private JTable users;
	private TableModelUser modelUser;
	private JPanel panelAdd, pInferior, panel2, panel3;
	private JTextField nome;
	private JFormattedTextField cpf, telefone;
	private MaskFormatter mascaraCpf, maskPhone;
	private JTextField jtfTotal;
	private JDateChooser date_nasc;
	private JButton bAdd;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5439324224974968781L;
	private String[] columnToolTips = { "CPF do cliente", "Nome do cliente", "Data de Nascimento do Cliente",
			"Telefone do cliente", "Número de Empréstimos que o cliente fez" };

	public UserPanel() {
		super();
		INSTANCE = this;
		setLayout(new BorderLayout());
		modelUser = TableModelUser.getInstance();
		users = new JTable(modelUser) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 776311897765510270L;

			@Override
			public boolean isCellEditable(int data, int columns) {
				if (columns == 0 || columns == 4)
					return false;
				return true;
			}

			@Override
			public Component prepareRenderer(TableCellRenderer r, int data, int columns) {
				// int row = convertRowIndexToModel(data);
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

			// Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				int realColumnIndex = convertColumnIndexToModel(colIndex);
				if (rowIndex != -1) {
					int realRowIndex = convertRowIndexToModel(rowIndex);
					tip = String.valueOf(modelUser.getValueAt(realRowIndex, realColumnIndex));
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
					private static final long serialVersionUID = -6962458419476848334L;

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
		TableCellRenderer tcr = users.getTableHeader().getDefaultRenderer();
		users.getTableHeader().setDefaultRenderer(new TableCellRenderer() {

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

		users.setPreferredScrollableViewportSize(new Dimension(800, 600));
		users.setFillsViewportHeight(true);
		users.setAutoCreateRowSorter(true);
		users.getTableHeader().setReorderingAllowed(false);
		users.setRowHeight(30);

		users.getColumnModel().getColumn(0).setCellRenderer(new CellRendererNoImage());
		users.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer());
		users.getColumnModel().getColumn(2).setCellRenderer(new CellRenderer());
		users.getColumnModel().getColumn(3).setCellRenderer(new CellRenderer());
		users.getColumnModel().getColumn(4).setCellRenderer(new CellRendererNoImage());

		MaskFormatter mascaraData;
		JFormattedTextField data;

		try {
			mascaraData = new MaskFormatter("##/##/####");
			mascaraData.setCommitsOnValidEdit(true);
			data = new JFormattedTextField(mascaraData);
		} catch (ParseException e1) {
			data = new JFormattedTextField();
			e1.printStackTrace();
		}
		data.setFont(new Font("Arial", Font.PLAIN, 15));

		final TableCellEditor dataEditor = new DefaultCellEditor(data);

		users.getColumnModel().getColumn(2).setCellEditor(dataEditor);

		InputMap iMap = data.getInputMap(JComponent.WHEN_FOCUSED);
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), KeyEvent.getKeyText(KeyEvent.VK_ENTER));
		ActionMap aMap = data.getActionMap();
		aMap.put(KeyEvent.getKeyText(KeyEvent.VK_ENTER), new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dataEditor.stopCellEditing();
			}
		});

		MaskFormatter maskPhone;
		JFormattedTextField phone;

		try {
			maskPhone = new MaskFormatter("(##) # ####-####");
			maskPhone.setCommitsOnValidEdit(true);
			phone = new JFormattedTextField(maskPhone);
		} catch (ParseException e1) {
			phone = new JFormattedTextField();
			e1.printStackTrace();
		}
		phone.setFont(new Font("Arial", Font.PLAIN, 15));

		final TableCellEditor phoneEditor = new DefaultCellEditor(phone);
		users.getColumnModel().getColumn(3).setCellEditor(phoneEditor);

		InputMap iMap1 = phone.getInputMap(JComponent.WHEN_FOCUSED);
		iMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), KeyEvent.getKeyText(KeyEvent.VK_ENTER));
		ActionMap aMap1 = phone.getActionMap();
		aMap1.put(KeyEvent.getKeyText(KeyEvent.VK_ENTER), new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				phoneEditor.stopCellEditing();
			}
		});

		JScrollPane jsLivros = new JScrollPane(users);
		add(jsLivros, BorderLayout.CENTER);

		users.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				users.scrollRectToVisible(users.getCellRect(users.getRowCount() - 1, 0, true));
			}

		});

		pInferior = new JPanel(new BorderLayout());
		add(pInferior, BorderLayout.SOUTH);
		panel2 = new JPanel(new GridLayout(2, 1));
		panel3 = new JPanel();
		JLabel total = new JLabel("Total: ");
		jtfTotal = new JTextField(String.valueOf(modelUser.getRowCount()));
		jtfTotal.setEditable(false);
		panel3.add(total);
		panel3.add(jtfTotal);
		panel2.add(panel3);

		inicializarBotoes();

		inicializarPanelAdd();

		inicializarMenus();

		users.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteRow");
		users.getActionMap().put("deleteRow", new DeleteAction());

		users.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int column = table.columnAtPoint(point);
				int rowAtPoint = table.rowAtPoint(point);
				if (rowAtPoint != -1) {
					int row = table.convertRowIndexToModel(rowAtPoint);
					if (mouseEvent.getClickCount() == 2 && !table.isCellEditable(row, column)
							&& table.getSelectedRow() != -1) {
						abrir(modelUser.getUser(row));
					}
				}
			}
		});

		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		users.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "solve");
		users.getActionMap().put("solve", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -833616209546223519L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (users.getSelectedRows().length == 1)
					abrir(modelUser.getUser(users.convertRowIndexToModel(users.getSelectedRow())));

			}
		});

	}

	private void inicializarMenus() {
		JMenuItem delete = new JMenuItem("Apagar");
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removerUsers();
			}
		});

		JMenuItem info = new JMenuItem("Informações");
		info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				abrir(modelUser.getUser(users.convertRowIndexToModel(users.getSelectedRow())));

			}
		});

		JMenuItem atualizar = new JMenuItem("Atualizar Tabela");
		atualizar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TableModelUser.getInstance().fireTableDataChanged();
			}
		});

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int rowAtPointOriginal = users
								.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), users));
						if (rowAtPointOriginal > -1) {
							int rowAtPoint = users.convertRowIndexToModel(rowAtPointOriginal);
							if (rowAtPoint > -1) {
								int[] rows = convertRowsIndextoModel();
								if (rows.length <= 1) {
									info.setVisible(true);
									users.setRowSelectionInterval(rowAtPointOriginal, rowAtPointOriginal);
									delete.setVisible(true);
								}
							} else {
								info.setVisible(false);
								delete.setVisible(true);
							}
						} else {
							info.setVisible(false);
							delete.setVisible(false);
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
		popupMenu.add(delete);
		popupMenu.add(atualizar);

		popupMenu.setPopupSize(350, 150);

		users.setComponentPopupMenu(popupMenu);
	}

	public void inicializarBotoes() {
		pInferior.add(panel2, BorderLayout.WEST);
		JButton bSair = new JButton("SAIR");
		bSair.setBackground(new Color(247, 247, 255));
		bSair.setForeground(MaterialColors.LIGHT_BLUE_400);
		LivroDetail.personalizarBotao(bSair);
		bSair.addActionListener(new SairAction());
		panel2.add(bSair);

		JPanel panel4 = new JPanel(new GridLayout(2, 1));
		pInferior.add(panel4, BorderLayout.EAST);

		bAdd = new JButton("ADICIONAR");
		bAdd.setForeground(MaterialColors.WHITE);
		bAdd.setBackground(MaterialColors.LIGHT_GREEN_500);
		LivroDetail.personalizarBotao(bAdd);
		bAdd.setEnabled(false);
		bAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				adicionarUser();

			}
		});
		panel4.add(bAdd);

		JButton bLimpar = new JButton("Limpar campos");
		bLimpar.setForeground(MaterialColors.WHITE);
		bLimpar.setBackground(MaterialColors.RED_300);
		LivroDetail.personalizarBotao(bLimpar);
		bLimpar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearTextFields();
			}
		});
		panel4.add(bLimpar);
	}

	private void inicializarPanelAdd() {
		panelAdd = new JPanel(new GridLayout(1, 5));

		JPanel panelNome = new JPanel(new BorderLayout());
		JLabel lNome = new JLabel("Nome: ");
		lNome.setFont(new Font("Roboto", Font.BOLD, 15));
		panelNome.add(lNome, BorderLayout.WEST);

		nome = new JTextField();
		nome.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarUser();
			}
		});
		panelNome.add(nome, BorderLayout.CENTER);

		panelAdd.add(panelNome);

		JPanel panelDataNascimento = new JPanel(new BorderLayout());
		JLabel lDateNasc = new JLabel("Data de Nascimento: ");
		lDateNasc.setFont(new Font("Roboto", Font.BOLD, 15));
		panelDataNascimento.add(lDateNasc, BorderLayout.WEST);

		date_nasc = new JDateChooser();
		date_nasc.setLocale(new Locale("pt", "BR"));
		date_nasc.setDateFormatString("dd/MM/yyyy");
		date_nasc.setMaxSelectableDate(new Date());
		date_nasc.setDate(new Date());

		date_nasc.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarUser();
			}
		});
		panelDataNascimento.add(date_nasc, BorderLayout.CENTER);
		panelAdd.add(panelDataNascimento);

		JPanel last = new JPanel();

		try {
			mascaraCpf = new MaskFormatter("###.###.###-##");
			mascaraCpf.setCommitsOnValidEdit(true);
			cpf = new JFormattedTextField(mascaraCpf);
		} catch (ParseException e1) {
			cpf = new JFormattedTextField();
			e1.printStackTrace();
		}

		cpf.setFont(new Font("Arial", Font.PLAIN, 15));
		cpf.setBounds(90, 162, 181, 20);
		cpf.setColumns(10);

		cpf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				validar();
				super.focusLost(e);
			}

		});

		cpf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarUser();
			}
		});

		JLabel lCpf = new JLabel("CPF: ");
		lCpf.setFont(new Font("Roboto", Font.BOLD, 15));

		last.add(lCpf);
		last.add(cpf);

		try {
			maskPhone = new MaskFormatter("(##) # ####-####");
			maskPhone.setCommitsOnValidEdit(true);
			telefone = new JFormattedTextField(maskPhone);
		} catch (ParseException e1) {
			telefone = new JFormattedTextField();
			e1.printStackTrace();
		}

		telefone.setFont(new Font("Arial", Font.PLAIN, 15));
		telefone.setBounds(120, 162, 181, 20);
		telefone.setColumns(12);
		
		telefone.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarUser();
			}
		});

		JLabel lPhone = new JLabel("Telefone: ");
		lPhone.setFont(new Font("Roboto", Font.BOLD, 15));

		last.add(lPhone);
		last.add(telefone);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					adicionarUser();
			}
		});

		JPanel both = new JPanel(new GridLayout(2, 1));
		both.add(last);
		both.add(panelAdd);

		pInferior.add(both, BorderLayout.CENTER);
	}

	/**
	 * Verifica se o número de CPF introduzido é válido e se já não está
	 * registrado na base de dados.
	 * 
	 * @return - true se o CPF é válido e ainda não está registrado <br>
	 *         - false caso contrário
	 */
	public boolean validar() {
		String cpfString;
		cpfString = cpf.getText();
		cpfString = cpfString.replace(".", "").replace("-", "");
		if (!cpfString.trim().equals("")) {
			if (CpfValidator.isCPF(cpfString)) {
				if (User.existe(cpfString)) {
					bAdd.setEnabled(false);
					String nomeUser = User.getUser(cpfString).getNome();
					JOptionPane.showMessageDialog(this, "Já existe um usuário registado com esse CPF! - " + nomeUser,
							"Erro", JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
					return false;
				} else {
					bAdd.setEnabled(true);
					return true;
				}
			} else {
				bAdd.setEnabled(false);
				JOptionPane.showMessageDialog(this, "Número de CPF inválido!", "Erro", JOptionPane.ERROR_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				return false;
			}
		}
		return false;
	}

	/**
	 * Verifica se os campos introduzidos estão certos e cria um novo cliente
	 * com os dados introduzidos.
	 */
	public void adicionarUser() {
		if (nome.getText().trim().equals(""))
			JOptionPane.showMessageDialog(this, "Deve inserir um nome para o cliente!", "ADICIONAR",
					JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		else {
			if (validar()) {
				String cpfS = cpf.getText().replace(".", "").replace("-", "");
				TableModelUser.getInstance()
						.addUser(new User(nome.getText(), date_nasc.getDate(), cpfS,
								telefone.getText().replace("-", "").replace("(", "").replace(")", "").replace(" ", ""),
								TableModelEmprestimo.getInstance().getNumEmprestimosParaCliente(cpfS), false));
				Log.getInstance().printLog("Cliente adicionado com sucesso!");
			} else
				JOptionPane.showMessageDialog(this, "CPF inválido!", "ADICIONAR", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		}
	}

	/**
	 * Limpa os campos de introdução de detalhes após introduzir um novo User.
	 */
	public void clearTextFields() {
		nome.setText("");
		date_nasc.cleanup();
		cpf.setText("");
		telefone.setText("");
	}

	public int[] convertRowsIndextoModel() {
		int[] rows = users.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			rows[i] = users.convertRowIndexToModel(rows[i]);
		}
		return rows;
	}

	/**
	 * Remove os clientes selecionados.
	 */
	public void removerUsers() {
		int[] rows = convertRowsIndextoModel();
		if (rows.length > 0) {
			int ok = JOptionPane.showConfirmDialog(this, "Tem certeza que quer apagar o(s) cliente(s) selecionado(s)?",
					"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			if (ok == JOptionPane.OK_OPTION) {
				modelUser.removeUser(rows);
			}
		}
	}

	public JTable getUsers() {
		return users;
	}

	public JButton getbAdd() {
		return bAdd;
	}

	public JTextField getJtfTotal() {
		return jtfTotal;
	}

	/**
	 * Inicializa uma nova instância da classe UserDetail com o User passado
	 * como parâmetro
	 * 
	 * @param user
	 *            - cliente que se quer ver os detalhes
	 */
	public void abrir(User user) {
		new UserDetail(user).open();
	}

	private class DeleteAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5018711156829284772L;

		@Override
		public void actionPerformed(ActionEvent e) {
			removerUsers();
		}
	}

	public static UserPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new UserPanel();
		return INSTANCE;
	}

}
