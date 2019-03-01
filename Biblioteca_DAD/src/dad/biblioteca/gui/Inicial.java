package dad.biblioteca.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import dad.recursos.BackgroundPanel;
import dad.recursos.Log;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;

import org.apache.commons.lang.time.DurationFormatUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class Inicial {

	private static Inicial INSTANCE;
	private JFrame frame;
	// private Object[] options = { "Sim", "Não", "Cancelar" };
	private Object[] itemOptions = { "Livro", "CD", "DVD", "Jornal", "Revista" };

	private Inicial() {
		INSTANCE = this;
		frame = new JFrame("Biblioteca - Dádiva de Deus");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("DAD.jpg"))));
		frame.setBounds(50, 50, 800, 600);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				long time = System.currentTimeMillis() - Main.inicialTime;
				Log.getInstance().printLog("Tempo de Uso: " + DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's")
						+ "\nPrograma Terminou");
				System.exit(0);
			}
		});

		BufferedImage img = null;
		try {
			img = ImageIO.read(getClass().getResource("background.jpg"));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Erro ao carregar a imagem de background!", "ERRO",
					JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource("DAD_S.jpg")));
			e.printStackTrace();
		}
		BackgroundPanel panel = new BackgroundPanel(img, BackgroundPanel.SCALED, 0.0f, 0.0f);
		frame.setContentPane(panel);

		JLabel titulo = new JLabel("BIBLIOTECA D\u00C1DIVA DE DEUS");
		titulo.setIcon(new ImageIcon(Inicial.class.getResource("DAD_T.png")));
		titulo.setHorizontalAlignment(SwingConstants.CENTER);
		titulo.setBackground(Color.LIGHT_GRAY);
		titulo.setForeground(Color.WHITE);
		titulo.setFont(new Font("Roboto Black", Font.BOLD, 40));
		panel.add(titulo, BorderLayout.NORTH);

		JPanel painelBotoes = new JPanel();
		panel.add(painelBotoes, BorderLayout.CENTER);
		painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));

		JButton addItem = new JButton("   ADICIONAR ITEM    ");
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opcao = JOptionPane.showOptionDialog(frame, "O que você quer adicionar?", "Adicionar Item",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
						new ImageIcon(getClass().getResource("DAD_S.jpg")), itemOptions, itemOptions[0]);
				switch (opcao) {
				case 0:
					System.out.println("Livro");
					break;
				case 1:
					System.out.println("CD");
					break;
				case 2:
					System.out.println("DVD");
					break;
				case 3:
					System.out.println("Jornal");
					break;
				case 4:
					System.out.println("Revista");
					break;
				default:
					System.out.println("Fechou");
					break;

				}
			}
		});
		personalizarBotao(addItem, painelBotoes);

		JButton verLista = new JButton("   LISTA DE LIVROS   ");
		verLista.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				DataGui.getInstance().open();
			}
		});
		personalizarBotao(verLista, painelBotoes);

		JButton verEmprestimos = new JButton(" VER EMPRÉSTIMOS "); // Aqui se
																	// 'remove'
																	// um
																	// empréstimo
																	// ou marca
																	// como
																	// entregue.
		personalizarBotao(verEmprestimos, painelBotoes);

		JButton fazerEmprestimo = new JButton("FAZER EMPRÉSTIMO");
		personalizarBotao(fazerEmprestimo, painelBotoes);

	}

	public void personalizarBotao(JButton jb, JPanel jp) {
		jb.setForeground(MaterialColors.LIGHT_BLUE_400);
		jb.setFont(new Font("Roboto Black", Font.PLAIN, 20));
		jb.setBackground(new Color(247, 247, 255));
		MaterialUIMovement.add(jb, MaterialColors.GRAY_300, 5, 1000 / 30);
		jp.add(jb);
	}

	public void open() {
		frame.setVisible(true);
	}

	public JFrame getFrame() {
		return frame;
	}

	public static Inicial getInstance() {
		if (INSTANCE == null) {
			new Inicial();
		}
		return INSTANCE;
	}
}
