package dad.biblioteca.gui;

import java.awt.Dimension;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.Font;

/**
 * Classe que representa a SplashScreen de abertura do programa.
 * @author Dário Pereira
 *
 */
public class Splash extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1303656694735837681L;
	private JProgressBar progressBar;

	/**
	 * Create the frame.
	 */
	public Splash() {
		setMinimumSize(new Dimension(450, 300));
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(50, 238, 350, 35);
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar);
		
		JLabel lblNewLabel = new JLabel(Main.TITLE);
		lblNewLabel.setFont(new Font("Roboto Medium", Font.PLAIN, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(50, 172, 350, 35);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setIcon(new ImageIcon(Splash.class.getResource("/splash_gif.gif")));
		lblNewLabel_1.setBounds(0, 0, 450, 181);
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblVerso = new JLabel("Vers\u00E3o " + Main.VERSION);
		lblVerso.setHorizontalAlignment(SwingConstants.CENTER);
		lblVerso.setFont(new Font("Roboto Medium", Font.PLAIN, 13));
		lblVerso.setBounds(180, 213, 90, 14);
		getContentPane().add(lblVerso);

	}
	
	/**
	 * Incrementa a porcentagem para o valor definido em i.
	 * @param i - valor que se pretende ficar na barra de progresso.
	 */
	public void incrementar(int i){
		progressBar.setValue(i);
	}
}
