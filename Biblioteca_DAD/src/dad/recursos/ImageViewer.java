package dad.recursos;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Image;

public class ImageViewer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7707938657446823751L;

	public ImageViewer(ImageIcon img) {
		setSize(177*3, 263*2);
		JLabel imageView = new JLabel("");
		imageView.setSize(177*3, 263*2);
		imageView.setIcon(new ImageIcon(img.getImage().getScaledInstance(177*3, 236*3, Image.SCALE_SMOOTH)));
		getContentPane().add(imageView, BorderLayout.CENTER);
	}
	
}
