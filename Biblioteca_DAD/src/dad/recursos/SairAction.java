package dad.recursos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.lang.time.DurationFormatUtils;

import dad.biblioteca.gui.DataGui;
import dad.biblioteca.gui.Login;

/**
 * Classe que representa um ActionListener para fazer Logout do funcionário.
 * @author Dário Pereira
 *
 */
public class SairAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		DataGui.getInstance().dispose();
		long time = System.currentTimeMillis() - Login.inicialTime;
		Log.getInstance().printLog("Usuário " + Login.NOME + " saiu!\nTempo de Uso: "
				+ DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's"));
		Login.getInstance().open();

	}

}
