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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.Timer;

import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Desktop;

import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import dad.biblioteca.Livro;
import dad.biblioteca.User;
import dad.biblioteca.table.EmprestimoPanel;
import dad.biblioteca.table.LivroPanel;
import dad.biblioteca.table.TableModelEmprestimo;
import dad.biblioteca.table.TableModelLivro;
import dad.biblioteca.table.TableModelUser;
import dad.biblioteca.table.UserPanel;
import dad.recursos.CellRenderer;
import dad.recursos.CellRendererNoImage;
import dad.recursos.DefaultCellRenderer;
import dad.recursos.Log;
import dad.recursos.ZipCompress;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

public class DataGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5748160687318648477L;
	private static final int DELAY = 2200;
	private static DataGui INSTANCE;
	private JTabbedPane tabbedPane;
	private JTable media, outros;
	private JMenu mnArquivo, mnAjuda, mnEditar;
	private JMenuItem menuSobre, menuEstatisticas, menuSair, menuAnular, menuRefazer, menuImportar, menuBackup,
			menuOrdenar, menuAtualizar, menuConfig;
	private JTextField pesquisa;
	private JPanel filtrosPanel;
	private JCheckBox checkID, checkTitulo, checkAutor, checkEditora, checkClassificacao, checkLocal, checkIDItem,
			checkCliente, checkFuncionario, checkDataEmp, checkDataDevol, checkNome, checkData_Nasc, checkCpf;
	private JMenuItem mntmRelatarErro;
	private JMenuItem mnLimpar;

	private DataGui() {
		INSTANCE = this;
		setTitle("Biblioteca - Dádiva de Deus");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("/DAD.jpg"))));
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

		checkIDItem = new JCheckBox("ID do Item");
		checkIDItem.setSelected(true);
		filtrosPanel.add(checkIDItem);
		checkIDItem.setVisible(false);

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

		checkDataEmp = new JCheckBox("Data do Empréstimo");
		checkDataEmp.setSelected(true);
		filtrosPanel.add(checkDataEmp);
		checkDataEmp.setVisible(false);

		checkDataDevol = new JCheckBox("Data de Devolução");
		checkDataDevol.setSelected(true);
		filtrosPanel.add(checkDataDevol);
		checkDataDevol.setVisible(false);

		checkCliente = new JCheckBox("Cliente");
		checkCliente.setSelected(true);
		filtrosPanel.add(checkCliente);
		checkCliente.setVisible(false);

		checkFuncionario = new JCheckBox("Funcionário");
		checkFuncionario.setSelected(true);
		filtrosPanel.add(checkFuncionario);
		checkFuncionario.setVisible(false);

		checkCpf = new JCheckBox("CPF");
		checkCpf.setSelected(true);
		filtrosPanel.add(checkCpf);
		checkCpf.setVisible(false);

		checkNome = new JCheckBox("Nome");
		checkNome.setSelected(true);
		filtrosPanel.add(checkNome);
		checkNome.setVisible(false);

		checkData_Nasc = new JCheckBox("Data de Nascimento");
		checkData_Nasc.setSelected(true);
		filtrosPanel.add(checkData_Nasc);
		checkData_Nasc.setVisible(false);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Livros", LivroPanel.getInstance());

		media = new JTable();
		tabbedPane.addTab("Multimédia", null, media, null);

		outros = new JTable();
		tabbedPane.addTab("Outros", null, outros, null);

		tabbedPane.addTab("Empréstimos", EmprestimoPanel.getInstance());

		tabbedPane.addTab("Clientes", null, UserPanel.getInstance(), null);

		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);
		tabbedPane.setToolTipTextAt(1, "Recurso em desenvolvimento... Brevemente disponível na versão 2.0");
		tabbedPane.setToolTipTextAt(2, "Recurso em desenvolvimento... Brevemente disponível na versão 2.0");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnArquivo = new JMenu("Arquivo");
		menuBar.add(mnArquivo);

		menuEstatisticas = new JMenuItem("Estat\u00EDsticas");
		menuEstatisticas.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Estatisticas().open();
			}
		});
		mnArquivo.add(menuEstatisticas);

		menuBackup = new JMenuItem("Cópia de segurança");
		menuBackup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				backup();
			}
		});
		mnArquivo.add(menuBackup);

		menuImportar = new JMenuItem("Restaurar Cópia de Segurança");
		mnArquivo.add(menuImportar);
		menuImportar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Restauro().open();

			}
		});

		menuConfig = new JMenuItem("Configura\u00E7\u00F5es");
		menuConfig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Config().open();
			}
		});
		mnArquivo.add(menuConfig);

		menuAtualizar = new JMenuItem("Atualizar Tabelas");
		mnArquivo.add(menuAtualizar);

		mnLimpar = new JMenuItem("Limpar espa\u00E7o");
		mnLimpar.setToolTipText("Apaga os arquivos de logs antigos, que são desnecessários");
		mnLimpar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int ok = JOptionPane.showConfirmDialog(null,
						"Os arquivos de logs antigos serão apagados. Isso não influencia o funcionamento do programa.\n"
								+ "Tem a certeza que quer limpar?",
						"Limpar espaço", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				if (ok == JOptionPane.YES_OPTION)
					limpar();
			}
		});
		mnArquivo.add(mnLimpar);

		menuSair = new JMenuItem("Sair");
		menuSair.addActionListener(new SairAction());
		mnArquivo.add(menuSair);

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

		mntmRelatarErro = new JMenuItem("Relatar erro");
		mntmRelatarErro.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new BugReport().open();				
			}
		});
		mnAjuda.add(mntmRelatarErro);

		menuSobre = new JMenuItem("Sobre");
		menuSobre.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new About().open();
				
			}
		});
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

		menuAtualizar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TableModelLivro.getInstance().fireTableDataChanged();
				TableModelEmprestimo.getInstance().fireTableDataChanged();
				TableModelEmprestimo.getInstance().atualizarMultas();
				TableModelUser.getInstance().fireTableDataChanged();
			}
		});

		pesquisa.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				filter(pesquisa.getText().toLowerCase());
			}

		});

		TableModelLivro.getInstance().addListeners();
		TableModelUser.getInstance().addListeners();

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				visibleBoxes();
				filter("");
				updateItems();
			}
		});

	}

	public void limpar() {
		String logPath = Main.DATA_DIR + "Logs/";
		String month_year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMyyyy")).toUpperCase();
		File logs = new File(logPath);
		File logMonth = new File(logPath + month_year + "/");
		File logMonthTmp = new File(Main.DATA_DIR + month_year + "/");

		try {
			FileUtils.copyDirectory(logMonth, logMonthTmp);
			FileUtils.deleteDirectory(logs);
		} catch (IOException e) {
			try {
				FileUtils.copyDirectory(logMonthTmp, logMonth);
				FileUtils.deleteDirectory(logMonthTmp);
				JOptionPane.showMessageDialog(null, "Limpeza feita!",
						"Limpar espaço - Sucesso", JOptionPane.OK_OPTION,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			} catch (IOException e1) {
				Log.getInstance().printLog("Erro ao limpar o espaço! - " + e.getMessage());
				JOptionPane.showMessageDialog(null, "Erro ao fazer a limpeza! - " + e.getMessage(),
						"Limpar espaço - Erro", JOptionPane.OK_OPTION,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				e1.printStackTrace();
			}
		}

	}

	public void backup() {
		String message = "Deseja criar uma cópia de segurança de todas as bases de dados do programa?"
				+ "\nObs: A cópia irá incluir as configurações, livros, empréstimos, clientes, funcionários e imagens, que serão salvos em um único ficheiro.\n"
				+ "Não modifique esse ficheiro!\n"
				+ "Você deve copiá-lo para um lugar seguro (por exemplo, uma pen-drive) para mais tarde ser possível restaurar,\n"
				+ "caso o computador seja formatado ou você pretenda usar o programa em outro computador.";
		int ok = JOptionPane.showConfirmDialog(null, message, "Cópia de Segurança", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
		if (ok == JOptionPane.OK_OPTION) {
			String name = "BibliotecaDAD-Backup-" + new SimpleDateFormat("ddMMMyyyy-HH'h'mm").format(new Date()) + ".dadb";
			ZipCompress.compress(Main.DATABASE_DIR, name, Main.BACKUP_DIR);
			JOptionPane.showMessageDialog(null, "Cópia de segurança salva com sucesso na pasta:\n" + Main.BACKUP_DIR,
					"Cópia de Segurança - Sucesso", JOptionPane.OK_OPTION,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			try {
				Desktop.getDesktop().open(new File(Main.BACKUP_DIR));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateItems() {
		if (tabbedPane.getSelectedIndex() == 0) {
			TableModelLivro.getInstance().updateItems();
		} else if (tabbedPane.getSelectedIndex() == 4) {
			TableModelUser.getInstance().updateItems();
		} else {
			DataGui.getInstance().getMenuAnular().setEnabled(false);
			DataGui.getInstance().getMenuRefazer().setEnabled(false);
		}

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
		JOptionPane pane = new JOptionPane("Bem vindo " + Login.NOME + "!", JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, new ImageIcon(getClass().getResource("/DAD_SS.jpg")), new Object[] {},
				null);
		final JDialog dialog = pane.createDialog("Boas vindas");
		dialog.setModal(true);
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("/DAD.jpg"))));
		Timer timer = new Timer(DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		timer.setRepeats(false);
		timer.start();
		dialog.setVisible(true);
	}

	public void visibleBoxes() {
		if (tabbedPane.getSelectedIndex() == 0) {
			menuOrdenar.setEnabled(true);
			checkTitulo.setText("Título");
			checkAutor.setText("Autor");
			checkID.setVisible(true);
			checkTitulo.setVisible(true);
			checkAutor.setVisible(true);
			checkEditora.setVisible(true);
			checkClassificacao.setVisible(true);
			checkLocal.setVisible(true);
			checkIDItem.setVisible(false);
			checkDataEmp.setVisible(false);
			checkDataDevol.setVisible(false);
			checkCliente.setVisible(false);
			checkFuncionario.setVisible(false);
			checkCpf.setVisible(false);
			checkData_Nasc.setVisible(false);
			checkNome.setVisible(false);
		} else if (tabbedPane.getSelectedIndex() == 1) {
			menuOrdenar.setEnabled(true);
			checkTitulo.setText("Título");
			checkAutor.setText("Artista");
			checkID.setVisible(true);
			checkTitulo.setVisible(true);
			checkAutor.setVisible(true);
			checkEditora.setVisible(false);
			checkClassificacao.setVisible(true);
			checkLocal.setVisible(true);
			checkIDItem.setVisible(false);
			checkDataEmp.setVisible(false);
			checkDataDevol.setVisible(false);
			checkCliente.setVisible(false);
			checkFuncionario.setVisible(false);
			checkCpf.setVisible(false);
			checkData_Nasc.setVisible(false);
			checkNome.setVisible(false);
		} else if (tabbedPane.getSelectedIndex() == 3) {
			menuOrdenar.setEnabled(false);
			checkID.setVisible(true);
			checkTitulo.setVisible(true);
			checkAutor.setVisible(false);
			checkEditora.setVisible(false);
			checkClassificacao.setVisible(false);
			checkLocal.setVisible(false);
			checkCpf.setVisible(false);
			checkData_Nasc.setVisible(false);
			checkNome.setVisible(false);
			checkIDItem.setVisible(true);
			checkDataEmp.setVisible(true);
			checkDataDevol.setVisible(true);
			checkCliente.setVisible(true);
			checkFuncionario.setVisible(true);
		} else if (tabbedPane.getSelectedIndex() == 4) {
			menuOrdenar.setEnabled(false);
			checkID.setVisible(false);
			checkTitulo.setVisible(false);
			checkAutor.setVisible(false);
			checkEditora.setVisible(false);
			checkClassificacao.setVisible(false);
			checkLocal.setVisible(false);
			checkIDItem.setVisible(false);
			checkDataEmp.setVisible(false);
			checkDataDevol.setVisible(false);
			checkCliente.setVisible(false);
			checkFuncionario.setVisible(false);
			checkCpf.setVisible(true);
			checkData_Nasc.setVisible(true);
			checkNome.setVisible(true);
		}
		// TODO
		filtrosPanel.repaint();
	}

	public void anular() {
		if (tabbedPane.getSelectedIndex() == 0)
			TableModelLivro.getInstance().getUndoManager().undo();
		else if (tabbedPane.getSelectedIndex() == 4)
			TableModelUser.getInstance().getUndoManager().undo();
		// TODO
	}

	public void refazer() {
		if (tabbedPane.getSelectedIndex() == 0)
			TableModelLivro.getInstance().getUndoManager().redo();
		else if (tabbedPane.getSelectedIndex() == 4)
			TableModelUser.getInstance().getUndoManager().redo();
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
		if (tabbedPane.getSelectedIndex() == 0) {
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
			if (checkLocal.isSelected())
				count++;
		} else if (tabbedPane.getSelectedIndex() == 3) {
			if (checkID.isSelected())
				count++;
			if (checkIDItem.isSelected())
				count++;
			if (checkTitulo.isSelected())
				count++;
			if (checkDataEmp.isSelected())
				count++;
			if (checkDataDevol.isSelected())
				count++;
			if (checkCliente.isSelected())
				count++;
			if (checkFuncionario.isSelected())
				count++;
		} else if (tabbedPane.getSelectedIndex() == 4) {
			if (checkCpf.isSelected())
				count++;
			if (checkNome.isSelected())
				count++;
			if (checkData_Nasc.isSelected())
				count++;
		}
		return count;
	}

	public int[] checkBoxEnabled() {
		int count = 0;
		int[] columns = new int[num_checkboxEnabled() + 3];
		if (tabbedPane.getSelectedIndex() == 0) {
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
		} else if (tabbedPane.getSelectedIndex() == 3) {
			columns = new int[num_checkboxEnabled() + 2];
			if (checkID.isSelected())
				columns[count++] = 0;
			if (checkIDItem.isSelected())
				columns[count++] = 1;
			if (checkTitulo.isSelected())
				columns[count++] = 2;
			if (checkDataEmp.isSelected())
				columns[count++] = 3;
			if (checkDataDevol.isSelected())
				columns[count++] = 4;
			if (checkCliente.isSelected())
				columns[count++] = 5;
			if (checkFuncionario.isSelected())
				columns[count++] = 6;
			columns[count++] = 7;
			columns[count++] = 8;
		} else if (tabbedPane.getSelectedIndex() == 4) {
			columns = new int[num_checkboxEnabled() + 1];
			if (checkCpf.isSelected())
				columns[count++] = 0;
			if (checkNome.isSelected())
				columns[count++] = 1;
			if (checkData_Nasc.isSelected())
				columns[count++] = 2;
			columns[count++] = 3;
		}
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
		} else if (tabbedPane.getSelectedIndex() == 3) {
			filtrarEmprestimos(filtro);
		} else if (tabbedPane.getSelectedIndex() == 4) {
			filtrarUsers(filtro);
		}
	}

	public void filtrarEmprestimos(String filtro) {
		TableRowSorter<TableModelEmprestimo> sorter = new TableRowSorter<TableModelEmprestimo>(
				TableModelEmprestimo.getInstance());
		EmprestimoPanel.getInstance().getEmprestimos().setRowSorter(sorter);
		RowFilter<TableModelEmprestimo, Object> filter;
		if (filtro.trim().equals("")) {
			sorter.setRowFilter(null);
		} else {
			if (num_checkboxEnabled() == 7 || num_checkboxEnabled() == 0) {
				filter = RowFilter.regexFilter((Pattern.compile("(?i)" + filtro, Pattern.CASE_INSENSITIVE).toString()));
				EmprestimoPanel.getInstance().getEmprestimos().setDefaultRenderer(Object.class, new CellRenderer());
			} else
				filter = RowFilter.regexFilter((Pattern.compile("(?i)" + filtro, Pattern.CASE_INSENSITIVE).toString()),
						checkBoxEnabled());
			sorter.setRowFilter(filter);
			setRenderers();
		}
	}

	public void filtrarUsers(String filtro) {
		TableRowSorter<TableModelUser> sorter = new TableRowSorter<TableModelUser>(TableModelUser.getInstance());
		UserPanel.getInstance().getUsers().setRowSorter(sorter);
		RowFilter<TableModelUser, Object> filter;
		if (filtro.trim().equals("")) {
			sorter.setRowFilter(null);
		} else {
			if (num_checkboxEnabled() == 7 || num_checkboxEnabled() == 0) {
				filter = RowFilter.regexFilter((Pattern.compile("(?i)" + filtro, Pattern.CASE_INSENSITIVE).toString()));
				UserPanel.getInstance().getUsers().setDefaultRenderer(Object.class, new CellRenderer());
			} else
				filter = RowFilter.regexFilter((Pattern.compile("(?i)" + filtro, Pattern.CASE_INSENSITIVE).toString()),
						checkBoxEnabled());
			sorter.setRowFilter(filter);
			setRenderers();
		}
	}

	public void filtrarEmprestimos(Livro l, JTable table) {
		String filtro = String.valueOf(l.getId());
		TableRowSorter<TableModelEmprestimo> sorter = new TableRowSorter<TableModelEmprestimo>(
				TableModelEmprestimo.getInstance());
		table.setRowSorter(sorter);
		RowFilter<TableModelEmprestimo, Object> filter;
		filter = RowFilter.regexFilter(Pattern.compile("\\b" + filtro + "\\b").toString(), 1);
		sorter.setRowFilter(filter);
		setRenderers();
	}

	public void filtrarEmprestimos(User user, JTable table) {
		String filtro = String.valueOf(user.getCpf());
		TableRowSorter<TableModelEmprestimo> sorter = new TableRowSorter<TableModelEmprestimo>(
				TableModelEmprestimo.getInstance());
		table.setRowSorter(sorter);
		RowFilter<TableModelEmprestimo, Object> filter;
		filter = RowFilter.regexFilter(Pattern.compile("\\b" + filtro + "\\b").toString(), 5);
		sorter.setRowFilter(filter);
		setRenderers();
	}

	public void setRenderers() {
		if (tabbedPane.getSelectedIndex() == 0) {
			TableColumnModel tcl = LivroPanel.getInstance().getLivros().getColumnModel();
			if (checkID.isSelected())
				tcl.getColumn(0).setCellRenderer(new CellRendererNoImage());
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

		} else if (tabbedPane.getSelectedIndex() == 3) {
			TableColumnModel tcl = EmprestimoPanel.getInstance().getEmprestimos().getColumnModel();
			if (checkID.isSelected())
				tcl.getColumn(0).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(0).setCellRenderer(new DefaultCellRenderer());

			if (checkIDItem.isSelected())
				tcl.getColumn(1).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(1).setCellRenderer(new DefaultCellRenderer());

			if (checkTitulo.isSelected())
				tcl.getColumn(2).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(2).setCellRenderer(new DefaultCellRenderer());

			if (checkDataEmp.isSelected())
				tcl.getColumn(3).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(3).setCellRenderer(new DefaultCellRenderer());

			if (checkDataDevol.isSelected())
				tcl.getColumn(4).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(4).setCellRenderer(new DefaultCellRenderer());

			if (checkCliente.isSelected())
				tcl.getColumn(5).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(5).setCellRenderer(new DefaultCellRenderer());

			if (checkFuncionario.isSelected())
				tcl.getColumn(6).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(6).setCellRenderer(new DefaultCellRenderer());
		} else if (tabbedPane.getSelectedIndex() == 4) {
			TableColumnModel tcl = UserPanel.getInstance().getUsers().getColumnModel();

			if (checkCpf.isSelected())
				tcl.getColumn(0).setCellRenderer(new CellRendererNoImage());
			else
				tcl.getColumn(0).setCellRenderer(new DefaultCellRenderer());

			if (checkNome.isSelected())
				tcl.getColumn(1).setCellRenderer(new CellRenderer());
			else
				tcl.getColumn(1).setCellRenderer(new DefaultCellRenderer());

			if (checkData_Nasc.isSelected())
				tcl.getColumn(2).setCellRenderer(new CellRenderer());
			else
				tcl.getColumn(2).setCellRenderer(new DefaultCellRenderer());
		}

	}

	private class SairAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			long time = System.currentTimeMillis() - Login.inicialTime;
			Log.getInstance().printLog("Usuário " + Login.NOME + " saiu!\nTempo de Uso: "
					+ DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's"));
			Login.getInstance().open();
		}
	}

	public JTextField getPesquisa() {
		return pesquisa;
	}

}
