/**
 * 
 */
package dad.recursos;

import java.awt.Font;

import javax.swing.JButton;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

/**
 * @author dariopereiradp
 *
 */
public class Utils {
	
	/**
	 * Personaliza o aspecto dos botões.
	 * 
	 * @param jb - botão a ser personalizado.
	 */
	public static void personalizarBotao(JButton jb) {
		jb.setFont(new Font("Roboto", Font.PLAIN, 15));
		MaterialUIMovement.getMovement(jb, MaterialColors.GRAY_300, 5, 1000 / 30);
	}

}
