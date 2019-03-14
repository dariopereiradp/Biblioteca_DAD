package dad.biblioteca.gui;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.apache.commons.lang.time.DurationFormatUtils;

import dad.biblioteca.table.LivroPanel;
import dad.biblioteca.table.TableModelLivro;
import dad.recursos.CellRenderer;
import dad.recursos.CellRendererInt;
import dad.recursos.DefaultCellRenderer;
import dad.recursos.Log;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

public class DataGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5748160687318648477L;
	private static DataGui INSTANCE;
	private JTabbedPane tabbedPane;
	private JTable media, outros, users;
	private JMenu mnArquivo;
	private JTable emprestimos;
	private JMenu mnAjuda;
	private JMenuItem menuSobre, menuEstatisticas, menuVoltar, menuAnular, menuRefazer, menuImportar, menuBackup,
			menuOrdenar;
	private JMenu mnEditar;
	private JTextField pesquisa;
	private JPanel filtrosPanel;
	private JCheckBox checkID, checkTitulo, checkAutor, checkEditora, checkClassificacao, checkLocal;
	private JMenuItem menuConfig;

	private DataGui() {
		INSTANCE = this;
		setTitle("Biblioteca - Dádiva de Deus");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("DAD.jpg"))));
		setMinimumSize(new Dimension(800, 600));
		setExtendedState(MAXIMIZED_BOTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				long time = System.currentTimeMillis() - Main.inicialTime;
				Log.getInstance().printLog("Tempo de Uso: " + DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's")
						+ "\nPrograma Terminou");
				System.exit(0);
			}
		});
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel pesquisaPanel = new JPanel(new BorderLayout());
		pesquisa = new JTextField();
		JLabel pesquisaLabel = new JLabel("Pesquisa: ");
		pesquisaPanel.add(pesquisaLabel, BorderLayout.WEST);
		pesquisaPanel.add(pesquisa, BorderLayout.CENTER);

		getContentPane().add(pesquisaPanel, BorderLayout.NORTH);

		filtrosPanel = new JPanel();
		pesquisaPanel.add(filtrosPanel, BorderLayout.EAST);

		checkID = new JCheckBox("ID");
		checkID.setSelected(true);
		filtrosPanel.add(checkID);

		checkTitulo = new JCheckBox("T\u00EDtulo");
		checkTitulo.setSelected(true);
		filtrosPanel.add(checkTitulo);

		checkAutor = new JCheckBox("Autor");
		checkAutor.setSelected(true);
		filtrosPanel.add(checkAutor);

		checkEditora = new JCheckBox("Editora");
		checkEditora.setSelected(true);
		filtrosPanel.add(checkEditora);

		checkClassificacao = new JCheckBox("Classifica\u00E7\u00E3o");
		checkClassificacao.setSelected(true);
		filtrosPanel.add(checkClassificacao);
		
		checkLocal = new JCheckBox("Localiza\u00E7\u00E3o");
		checkLocal.setSelected(true);
		filtrosPanel.add(checkLocal);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Livros", LivroPanel.getInstance());

		media = new JTable();
		tabbedPane.addTab("Multimédia", null, media, null);

		outros = new JTable();
		tabbedPane.addTab("Outros", null, outros, null);

		emprestimos = new JTable();
		tabbedPane.addTab("Empréstimos", null, emprestimos, null);

		users = new JTable();
		tabbedPane.addTab("Usuários", null, users, null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnArquivo = new JMenu("Arquivo");
		menuBar.add(mnArquivo);

		menuEstatisticas = new JMenuItem("Estat\u00EDsticas");
		mnArquivo.add(menuEstatisticas);

		menuImportar = new JMenuItem("Importar base de dados (.mdb)");
		mnArquivo.add(menuImportar);

		menuBackup = new JMenuItem("Cópia de segurança");
		mnArquivo.add(menuBackup);

		menuVoltar = new JMenuItem("Voltar");
		menuVoltar.addActionListener(new VoltarAction());
		mnArquivo.add(menuVoltar);

		menuConfig = new JMenuItem("Configura\u00E7\u00F5es");
		mnArquivo.add(menuConfig);

		mnEditar = new JMenu("Editar");
		menuBar.add(mnEditar);

		menuAnular = new JMenuItem("Anular (Ctrl+Z) - ()");
		menuAnular.setEnabled(false);
		mnEditar.add(menuAnular);

		menuRefazer = new JMenuItem("Refazer (Ctrl+Y) - ()");
		menuRefazer.setEnabled(false);
		mnEditar.add(menuRefazer);

		menuOrdenar = new JMenuItem("Ordenar livros (A-Z)");
		mnEditar.add(menuOrdenar);

		mnAjuda = new JMenu("Ajuda");
		menuBar.add(mnAjuda);

		menuSobre = new JMenuItem("Sobre");
		mnAjuda.add(menuSobre);

		getRootPane().setDefaultButton(LivroPanel.getInstance().getbAdd());

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());

		menuAnular.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				anular();
			}
		});

		menuRefazer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refazer();
			}
		});

		menuOrdenar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ordenar();
			}
		});

		pesquisa.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				filter(pesquisa.getText().toLowerCase());
			}

		});

		TableModelLivro.getInstance().addListeners();

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				visibleBoxes();
			}
		});

	}

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					anular();
				} else if ((e.getKeyCode() == KeyEvent.VK_Y) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					refazer();
				}

			}
			return false;
		}
	}

	public JMenuItem getMenuAnular() {
		return menuAnular;
	}

	public JMenuItem getMenuRefazer() {
		return menuRefazer;
	}

	public void open() {
		setVisible(true);
	}

	public void visibleBoxes() {
		if (tabbedPane.getSelectedIndex() == 0) {
			checkTitulo.setText("Título");
			checkAutor.setText("Autor");
			checkEditora.setVisible(true);
			checkClassificacao.setVisible(true);
		} else if (tabbedPane.getSelectedIndex() == 1) {
			checkTitulo.setText("Título");
			checkAutor.setText("Artista");
			checkEditora.setVisible(false);
			checkClassificacao.setVisible(true);
		}
		// TODO
		filtrosPanel.repaint();
	}

	public void anular() {
		if (tabbedPane.getSelectedIndex() == 0)
			TableModelLivro.getInstance().getUndoManager().undo();
		// else if (tabbedPane.getSelectedIndex() == 1)
		// TODO
	}

	public void refazer() {
		if (tabbedPane.getSelectedIndex() == 0)
			TableModelLivro.getInstance().getUndoManager().redo();
		// else if (tabbedPane.getSelectedIndex() == 1)
		// TODO
	}

	public void ordenar() {
		if (tabbedPane.getSelectedIndex() == 0)
			TableModelLivro.getInstance().ordenar();
		// else if (tabbedPane.getSelectedIndex() == 1)
		// TODO
	}

	private int num_checkboxEnabled() {
		int count = 0;
		if (checkID.isSelected())
			count++;
		if (checkTitulo.isSelected())
			count++;
		if (checkAutor.isSelected())
			count++;
		if (checkEditora.isSelected())
			count++;
		if (checkClassificacao.isSelected())
			count++;
		if(checkLocal.isSelected())
			count++;
		return count;
	}

	public int[] checkBoxEnabled() {
		int count = 0;
		int[] columns = new int[num_checkboxEnabled()+3];
		if (checkID.isSelected())
			columns[count++] = 0;
		if (checkTitulo.isSelected())
			columns[count++] = 1;
		if (checkAutor.isSelected())
			columns[count++] = 2;
		if (checkEditora.isSelected())
			columns[count++] = 3;
		if (checkClassificacao.isSelected())
			columns[count++] = 4;
		columns[count++] = 5;
		columns[count++] = 6;
		columns[count++] = 7;
		if (checkLocal.isSelected())
			columns[count++] = 8;
		return columns;
	}

	public static DataGui getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DataGui();
		return INSTANCE;
	}

	public void filter(String filtro) {
		if (tabbedPane.getSelectedIndex() == 0) {
			TableRowSorter<TableModelLivro> sorter = new TableRowSorter<TableModelLivro>(TableModelLivro.getInstance());
			LivroPanel.getInstance().getLivros().setRowSorter(sorter);
			RowFilter<TableModelLivro, Object> filter;
			if (filtro.trim().equals("")) {
				sorter.setRowFilter(null);
			} else {
				if (num_checkboxEnabled() == 6 || num_checkboxEnabled() == 0) {
					filter = RowFilter
							.regexFilter((Pattern.compile("(?i)" + filtro, Pattern.CASE_INSENSITIVE).toString()));
					LivroPanel.getInstance().getLivros().setDefaultRenderer(Object.class, new CellRenderer());
				} else
					filter = RowFilter.regexFilter(
							(Pattern.compile("(?i)" + filtro, Pattern.CASE_INSENSITIVE).toString()), checkBoxEnabled());
				sorter.setRowFilter(filter);
				setRenderers();
			}
		}
	}

	public void setRenderers() {
		TableColumnModel tcl = LivroPanel.getInstance().getLivros().getColumnModel();
		if (checkID.isSelected())
			tcl.getColumn(0).setCellRenderer(new CellRendererInt());
		else
			tcl.getColumn(0).setCellRenderer(new DefaultCellRenderer());
		if (checkTitulo.isSelected())
			tcl.getColumn(1).setCellRenderer(new CellRenderer());
		else
			tcl.getColumn(1).setCellRenderer(new DefaultCellRenderer());
		if (checkAutor.isSelected())
			tcl.getColumn(2).setCellRenderer(new CellRenderer());
		else
			tcl.getColumn(2).setCellRenderer(new DefaultCellRenderer());
		if (checkEditora.isSelected())
			tcl.getColumn(3).setCellRenderer(new CellRenderer());
		else
			tcl.getColumn(3).setCellRenderer(new DefaultCellRenderer());
		if (checkClassificacao.isSelected())
			tcl.getColumn(4).setCellRenderer(new CellRenderer());
		else
			tcl.getColumn(4).setCellRenderer(new DefaultCellRenderer());
		if (checkLocal.isSelected())
			tcl.getColumn(8).setCellRenderer(new CellRenderer());
		else
			tcl.getColumn(8).setCellRenderer(new DefaultCellRenderer());


	}

	private class VoltarAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			Inicial.getInstance().open();
		}
	}

	public JTextField getPesquisa() {
		return pesquisa;
	}

	// public TableCellRenderer getRenderer() {
	// return new DefaultTableCellRenderer() {
	// JTextField f = new JTextField();
	//
	// // @Override
	// // public Component getTableCellRendererComponent(JTable arg0,
	// // Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
	// // if(arg1 != null){
	// // f.setText(arg1.toString());
	// // String string = arg1.toString();
	// // if(string.contains(pesquisa.getText().toLowerCase())){
	// // int indexOf = string.indexOf(pesquisa.getText().toLowerCase());
	// // try {
	// //
	// f.getHighlighter().addHighlight(indexOf,indexOf+pesquisa.getText().toLowerCase().length(),new
	// //
	// javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Color.RED));
	// // } catch (BadLocationException e) {
	// // e.printStackTrace();
	// // }
	// // }
	// // } else {
	// // f.setText("");
	// // f.getHighlighter().removeAllHighlights();
	// // }
	// // return f;
	// // }
	// @Override
	// public Component getTableCellRendererComponent(JTable table, Object
	// value, boolean selected,
	// boolean hasFocus, int row, int column) {
	// DefaultTableCellRenderer d = new DefaultTableCellRenderer();
	// Component c = this.getTableCellRendererComponent(table, value, selected,
	// hasFocus, row, column);
	// JLabel original = (JLabel) c;
	// LabelHighlighted label = new LabelHighlighted();
	// label.setFont(original.getFont());
	// label.setText(original.getText());
	// label.setBackground(original.getBackground());
	// label.setForeground(original.getForeground());
	// label.setHorizontalTextPosition(original.getHorizontalTextPosition());
	// label.highlightText(pesquisa.getText().toLowerCase());
	// return label;
	// }
	// };
	// }
}
