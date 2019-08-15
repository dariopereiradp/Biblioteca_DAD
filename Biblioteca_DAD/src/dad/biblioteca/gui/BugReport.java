package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import dad.recursos.Log;
import dad.recursos.ZipCompress;
import net.lingala.zip4j.ZipFile;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import java.awt.Desktop;

/**
 * Classe que representa um Diálogo para reportar bugs no programa.
 * 
 * @author Dário Pereira
 *
 */
public class BugReport extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5804659379003860184L;
	private final JPanel contentPanel = new JPanel();
	private JTextArea text;

	/**
	 * Create the dialog.
	 */
	public BugReport() {
		super(DataGui.getInstance(), ModalityType.DOCUMENT_MODAL);
		setTitle("Bug Report");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 350);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblReportarErro = new JLabel("REPORTAR ERRO");
			lblReportarErro.setBackground(new Color(255, 51, 51));
			lblReportarErro.setFont(new Font("Dialog", Font.PLAIN, 17));
			lblReportarErro.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblReportarErro, BorderLayout.NORTH);
		}
		JPanel panel = new JPanel(new BorderLayout());
		contentPanel.add(panel, BorderLayout.CENTER);
		{
			text = new JTextArea();
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			JScrollPane jsp = new JScrollPane(text);
			panel.add(jsp, BorderLayout.CENTER);
		}
		{
			JLabel lblEscrevaEmDetalhes = new JLabel("Escreva em detalhes o problema e clique em 'OK'");
			lblEscrevaEmDetalhes.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(lblEscrevaEmDetalhes, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						criarRelatorio();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Cria um ficheiro '.zip' com os ficheiros log do mês atual e com um
	 * ficheiro '.txt' contendo a descrição do problema.
	 */
	public void criarRelatorio() {
		String data_hora = new SimpleDateFormat("dd/MM/yyyy 'às' HH'h'mm'm'ss").format(new Date());
		String data = new SimpleDateFormat("ddMMMyyyy_HH'h'mm").format(new Date());
		String info = Main.TITLE + " - " + Main.VERSION + "\n" + data_hora + "\n\nRelatório criado por: " + Login.NOME
				+ "\n\n" + text.getText();
		String path_temp = Main.DATA_DIR + "BibliotecaDAD_Relatorio_v." + Main.VERSION + "_" + data + ".txt";
		File file_text = new File(path_temp);
		String month_year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMyyyy")).toUpperCase();
		String name = "BibliotecaDAD_Relatorio_v." + Main.VERSION + "_" + data + ".zip";
		try (FileWriter fw = new FileWriter(file_text, false);) {
			fw.write(info);

			Log.getInstance().close();

			ZipCompress.compress(Main.DATA_DIR + "Logs/" + month_year + "/", name, Main.BUG_REPORTS_DIR);

			Log.getInstance().open();

		} catch (NullPointerException | IOException e) {
			String message = "Erro ao criar o relatório de erros! - " + e.getMessage();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			Log.getInstance().printLog(message);
		}
		try {
			ZipFile zip = new ZipFile(Main.BUG_REPORTS_DIR + name);
			zip.addFile(file_text);

			String bName = DataGui.getInstance().backupDirect();
			
			zip.addFile(new File(bName));

			String message = "Relatório criado com sucesso em: " + data_hora + "\nEnvie o arquivo de relatório '" + name
					+ "' para '" + Main.EMAIL_SUPORTE + "' "
					+ "\npara que possamos resolver o problema o mais rápido possível! Pedimos desculpa pelos incovenientes causados!";
			JOptionPane.showMessageDialog(null, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			Desktop.getDesktop().open(new File(Main.BUG_REPORTS_DIR));
			Log.getInstance().printLog(message);
			file_text.delete();
			dispose();
		} catch (Exception e) {
			String message = "Erro ao criar o relatório de erros! - " + e.getMessage();
			JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
			Log.getInstance().printLog(message);
		}

	}

	/**
	 * Torna o diálogo visível.
	 */
	public void open() {
		setVisible(true);
	}

}
