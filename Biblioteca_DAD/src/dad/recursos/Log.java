package dad.recursos;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Log {

	private static Log INSTANCE;
	private Logger logger;
	private FileHandler fh;
	private String name;
	
	private Log() {
		INSTANCE = this;
		String logFormatName = "log_"
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMMyyyy-HH'h'mm'm'ss's'"));
		String month_year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMyyyy")).toUpperCase();
		logger = Logger.getLogger(logFormatName);
		try {
			File dir = new File(System.getenv("APPDATA") + "/BibliotecaDAD/Logs/" + month_year + "/");
			if (!dir.exists())
				dir.mkdirs();
			name = dir.getAbsolutePath() + "/" + logFormatName + ".log";
			open();
			logger.addHandler(fh);
			logger.setUseParentHandlers(true);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {
			String message = "Ocorreu um erro ao criar o log...\n" + e.getMessage() + "\n" + this.getClass();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_S.jpg")));
			e.printStackTrace();
		}

	}

	public void open() {
		try {
			fh = new FileHandler(name);
			logger.addHandler(fh);
		} catch (SecurityException e) {
			String message = "Ocorreu um erro ao criar o log...\n" + e.getMessage() + "\n" + this.getClass();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_S.jpg")));
			e.printStackTrace();
		} catch (IOException e) {
			String message = "Ocorreu um erro ao criar o log...\n" + e.getMessage() + "\n" + this.getClass();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_S.jpg")));
			e.printStackTrace();
		}
	}

	public static Log getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Log();
		return INSTANCE;
	}

	public void printLog(String message) {
		logger.info(message + "\n");
	}
	
	public void close(){
		fh.close();
	}

}
