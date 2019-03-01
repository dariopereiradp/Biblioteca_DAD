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
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.time.DurationFormatUtils;

import dad.biblioteca.table.LivroPanel;
import dad.biblioteca.table.TableModelLivro;
import dad.recursos.Log;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class DataGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5748160687318648477L;
	private static DataGui INSTANCE;
	private JTabbedPane tabbedPane;
	private JTable dvds, revistas, cds, jornais;
	private JMenu mnArquivo;
	private JTable emprestimos;
	private JMenu mnAjuda;
	private JMenuItem menuSobre, menuEstatisticas, menuVoltar, menuAnular, menuRefazer, menuImportar, menuBackup,
			menuOrdenar;
	private JMenu mnEditar;
	private JTextField pesquisa;

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

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Livros", LivroPanel.getInstance());

		cds = new JTable();
		tabbedPane.addTab("CDs", null, cds, null);

		dvds = new JTable();
		tabbedPane.addTab("DVDs", null, dvds, null);

		jornais = new JTable();
		tabbedPane.addTab("Jornais", null, jornais, null);

		revistas = new JTable();
		tabbedPane.addTab("Revistas", null, revistas, null);

		emprestimos = new JTable();
		tabbedPane.addTab("Empréstimos", null, emprestimos, null);

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

	public void anular() {
		if (tabbedPane.isEnabledAt(0))
			TableModelLivro.getInstance().getUndoManager().undo();
		// else if(tabbedPane.isEnabledAt(1))
		// TODO
	}

	public void refazer() {
		if (tabbedPane.isEnabledAt(0))
			TableModelLivro.getInstance().getUndoManager().redo();
		// else if(tabbedPane.isEnabledAt(1))
		// TODO
	}

	public void ordenar() {
		if (tabbedPane.isEnabledAt(0))
			TableModelLivro.getInstance().ordenar();
		// else if(tabbedPane.isEnabledAt(1))
		// TODO
	}

	public static DataGui getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DataGui();
		return INSTANCE;
	}

	public void filter(String filtro) {
		if (tabbedPane.isEnabledAt(0)) {
			TableRowSorter<TableModelLivro> sorter = new TableRowSorter<TableModelLivro>(TableModelLivro.getInstance());
			LivroPanel.getInstance().getLivros().setRowSorter(sorter);
			RowFilter<TableModelLivro, Object> filter = RowFilter
					.regexFilter((Pattern.compile("(?i)" + filtro, Pattern.CASE_INSENSITIVE).toString()));
			sorter.setRowFilter(filter);
		}
	}

	private class VoltarAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			Inicial.getInstance().open();
		}
	}

}
