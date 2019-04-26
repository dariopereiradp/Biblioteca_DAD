package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import dad.biblioteca.Item;
import dad.biblioteca.Livro;
import dad.biblioteca.table.AtualizaExemplares;
import dad.biblioteca.table.AtualizaLivro;
import dad.biblioteca.table.CompositeCommand;
import dad.biblioteca.table.TableModelLivro;
import dad.recursos.ImageViewer;
import dad.recursos.RealizarEmprestimo;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;
import net.miginfocom.swing.MigLayout;

public class LivroDetail {

	private Livro l;
	private JTextField titulo, autor, editora, classificacao, local, exemp, exempDisp, disp, exempEmp;
	private JDialog dial;

	public LivroDetail(Livro l) {
		this.l = l;
		int oldExemplares = l.getNumero_exemplares();
		System.out.println(l);
		dial = new JDialog(DataGui.getInstance(), l.getNome());
		dial.setSize(new Dimension(750, 500));
		dial.setMinimumSize(new Dimension(750, 500));
		dial.getContentPane().setLayout(new BorderLayout());

		JPanel principal = new JPanel(new BorderLayout());
		JPanel botoesPrincipais = new JPanel();
		JTable emprestimos = new JTable();
		JPanel cimaPanel = new JPanel(new BorderLayout());
		JPanel infoPanelWithButtons = new JPanel(new BorderLayout());
		JPanel infoPanel = new JPanel(new GridLayout(10, 2));
		JPanel rightPanel = new JPanel(new BorderLayout());
		JPanel imagePanel = new JPanel(new MigLayout("al center center, wrap, gapy 15"));
		JPanel botoesSecund = new JPanel(new BorderLayout());
		botoesPrincipais
				.setLayout(new MigLayout("", "[79px][129px][45px][][][][][][][][][][150px][][][][][]", "[27px]"));
		botoesSecund.setLayout(new MigLayout("", "[79px][100px][][240px][][][][][]", "[27px]"));

		JButton apagar = new JButton("Apagar");
		botoesPrincipais.add(apagar, "cell 0 0,alignx left,aligny center");
		apagar.setBackground(MaterialColors.RED_400);
		personalizarBotao(apagar);

		JButton emprestar = new JButton("Realizar Empréstimo");
		emprestar.setBackground(MaterialColors.LIGHT_GREEN_500);
		personalizarBotao(emprestar);
		botoesPrincipais.add(emprestar, "cell 5 0,alignx left,aligny center");

		JButton ok = new JButton("Ok");
		ok.setBackground(MaterialColors.LIGHT_BLUE_200);
		personalizarBotao(ok);
		botoesPrincipais.add(ok, "cell 17 0,alignx left,aligny center");

		JButton editar = new JButton("Editar");
		editar.setBackground(MaterialColors.YELLOW_300);
		personalizarBotao(editar);
		botoesSecund.add(editar, "cell 0 0,alignx left,aligny center");

		JButton salvar = new JButton("Salvar");
		salvar.setBackground(MaterialColors.LIGHT_GREEN_300);
		personalizarBotao(salvar);
		botoesSecund.add(salvar, "cell 17 0,alignx left,aligny center");
		salvar.setEnabled(false);

		titulo = new JTextField(l.getNome());
		titulo.setEditable(false);
		autor = new JTextField(l.getAutor());
		autor.setEditable(false);
		editora = new JTextField(l.getEditora());
		editora.setEditable(false);
		classificacao = new JTextField(l.getClassificacao());
		classificacao.setEditable(false);
		local = new JTextField(l.getLocal());
		local.setEditable(false);
		exemp = new JTextField(String.valueOf(l.getNumero_exemplares()));
		exemp.setEditable(false);
		exempDisp = new JTextField(String.valueOf(l.getN_exemp_disponiveis()));
		exempDisp.setEditable(false);
		disp = new JTextField(l.isDisponivel() ? "Sim" : "Não");
		disp.setEditable(false);
		exempEmp = new JTextField(String.valueOf(l.getN_exemp_emprestados()));
		exempEmp.setEditable(false);

		infoPanel.add(new JLabel("Título: "));
		infoPanel.add(titulo);
		infoPanel.add(new JLabel("Autor: "));
		infoPanel.add(autor);
		infoPanel.add(new JLabel("Editora: "));
		infoPanel.add(editora);
		infoPanel.add(new JLabel("Classificação: "));
		infoPanel.add(classificacao);
		infoPanel.add(new JLabel("Localização: "));
		infoPanel.add(local);
		infoPanel.add(new JLabel("Número de Exemplares: "));
		infoPanel.add(exemp);
		infoPanel.add(new JLabel("Número de Exemplares Disponíveis: "));
		infoPanel.add(exempDisp);
		infoPanel.add(new JLabel("Disponível? "));
		infoPanel.add(disp);
		infoPanel.add(new JLabel("Número de Exemplares emprestados: "));
		infoPanel.add(exempEmp);

		infoPanelWithButtons.add(infoPanel, BorderLayout.CENTER);
		infoPanelWithButtons.add(botoesSecund, BorderLayout.SOUTH);

		JLabel image = new JLabel();
		image.setHorizontalAlignment(JLabel.CENTER);
		image.setVerticalAlignment(JLabel.CENTER);
		image.setMinimumSize(new Dimension(177, 236));
		if (l.getImg() != null)
			image.setIcon(new ImageIcon(l.getImg().getImage().getScaledInstance(177, 236, Image.SCALE_DEFAULT)));
		else
			image.setText("         Sem Imagem         ");
		image.setBorder(new LineBorder(Color.BLACK, 3));

		final class Add implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				l.addImg();
				if (l.getImg() != null) {
					image.setText(null);
					image.setIcon(
							new ImageIcon(l.getImg().getImage().getScaledInstance(177, 236, Image.SCALE_DEFAULT)));
				}

			}
		}

		final class Apagar implements ActionListener {

			public void apagar() {
				int ok = JOptionPane.showConfirmDialog(DataGui.getInstance(),
						"Tem certeza que quer apagar a imagem do livro?\n(Não é possível voltar atrás, a não ser adicionando uma nova imagem!)",
						"APAGAR", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
						new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
				if (ok == JOptionPane.OK_OPTION) {
					l.setImg(null);
					image.setIcon(null);
					File f = new File(Item.imgPath + l.getId() + ".jpg");
					f.delete();
				}
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				apagar();
			}
		}

		JPopupMenu menuApagar = new JPopupMenu();
		JMenuItem mAdd = new JMenuItem();
		if (l.getImg() == null) {
			mAdd.setText("Adicionar Imagem");
		} else
			mAdd.setText("Alterar Imagem");
		mAdd.addActionListener(new Add());
		menuApagar.add(mAdd);
		JMenuItem mApagar = new JMenuItem("Apagar");
		menuApagar.add(mApagar);
		mApagar.addActionListener(new Apagar());

		menuApagar.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (image.getIcon() == null) {
							mAdd.setText("Adicionar Imagem");
							mApagar.setEnabled(false);
						} else {
							mAdd.setText("Alterar Imagem");
							mApagar.setEnabled(true);
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

		image.setComponentPopupMenu(menuApagar);

		image.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				int count = evt.getClickCount();
				if (count == 2) {
					ImageViewer.show(l.getImg());
				}
			}

		});

		JButton addImage = new JButton();
		if (image.getIcon() == null)
			addImage.setText("Adicionar Imagem");
		else
			addImage.setText("Alterar imagem");
		addImage.setBackground(MaterialColors.BLUE_GRAY_500);
		personalizarBotao(addImage);
		imagePanel.add(image);
		imagePanel.add(addImage, "center");

		rightPanel.add(imagePanel, BorderLayout.CENTER);

		cimaPanel.add(infoPanelWithButtons, BorderLayout.CENTER);
		cimaPanel.add(rightPanel, BorderLayout.EAST);

		principal.add(cimaPanel, BorderLayout.CENTER);
		principal.add(emprestimos, BorderLayout.SOUTH);

		dial.getContentPane().add(principal, BorderLayout.CENTER);
		dial.getContentPane().add(botoesPrincipais, BorderLayout.SOUTH);

		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (image.getIcon() == null) {
					addImage.setText("Adicionar Imagem");
					image.setText("         Sem Imagem         ");
				} else {
					addImage.setText("Alterar Imagem");
					image.setText("");
				}

			}
		};

		image.addPropertyChangeListener(listener);

		editar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				titulo.setEditable(true);
				autor.setEditable(true);
				editora.setEditable(true);
				classificacao.setEditable(true);
				local.setEditable(true);
				exemp.setEditable(true);
				editar.setEnabled(false);
				salvar.setEnabled(true);

			}
		});

		final class Salvar implements ActionListener {

			private boolean close;

			public Salvar(boolean close) {
				this.close = close;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				editar.setEnabled(true);
				salvar.setEnabled(false);
				if (close && titulo.isEditable())
					save(oldExemplares, close);
				else if (close && !titulo.isEditable())
					dial.dispose();
				else
					save(oldExemplares, close);
			}
		}

		salvar.addActionListener(new Salvar(false));

		ok.addActionListener(new Salvar(true));

		dial.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (titulo.isEditable())
					save(oldExemplares, true);
				else
					dial.dispose();
			}

		});

		addImage.addActionListener(new Add());

		emprestar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (l.getN_exemp_disponiveis() > 0){
					new RealizarEmprestimo(l).open();
					dial.dispose();
				}
				
				else
					JOptionPane.showMessageDialog(DataGui.getInstance(),
							"Não há exemplares disponíveis para empréstimo...", "Realiza Empréstimo",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));

			}
		});

		// TODO Auto-generated method stub

	}

	public void save(int oldExemplares, boolean close) {
		titulo.setEditable(false);
		autor.setEditable(false);
		editora.setEditable(false);
		classificacao.setEditable(false);
		local.setEditable(false);
		try {
			int n = Integer.parseInt(exemp.getText());
			int d = Integer.parseInt(exempDisp.getText());
			if (n <= 0)
				exemp.setText(String.valueOf(oldExemplares));
			else {
				exempDisp.setText(String.valueOf(n - l.getN_exemp_emprestados()));
				if (d == 0) {
					disp.setText("Não");
				} else if (d > 0)
					disp.setText("Sim");
			}

		} catch (NumberFormatException e1) {
			exemp.setText(String.valueOf(oldExemplares));
		}
		exemp.setEditable(false);

		TableModelLivro.getInstance().getUndoManager()
				.execute(new CompositeCommand("Atualizar Livro",
						new AtualizaLivro(TableModelLivro.getInstance(), "Título", l, titulo.getText()),
						new AtualizaLivro(TableModelLivro.getInstance(), "Autor", l, autor.getText()),
						new AtualizaLivro(TableModelLivro.getInstance(), "Editora", l, editora.getText()),
						new AtualizaLivro(TableModelLivro.getInstance(), "Classificação", l, classificacao.getText()),
						new AtualizaLivro(TableModelLivro.getInstance(), "Local", l, local.getText()),
						new AtualizaExemplares(l.isDisponivel(), l, Integer.parseInt(exemp.getText()))));
		TableModelLivro.getInstance().fireTableDataChanged();

		if (close)
			dial.dispose();

	}

	public void personalizarBotao(JButton jb) {
		jb.setFont(new Font("Roboto", Font.PLAIN, 15));
		MaterialUIMovement.add(jb, MaterialColors.GRAY_300, 5, 1000 / 30);
	}

	public void open() {
		dial.setVisible(true);

	}

}
