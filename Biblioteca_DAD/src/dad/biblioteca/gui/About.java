package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;


/**
 * 
 * @author Dário Pereira
 *
 */
public class About extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8083357425196226363L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public About() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 350, 300);
		setTitle("Sobre");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTextPane sobre = new JTextPane();
			sobre.setEditable(false);
			sobre.setFont(new Font("Tahoma", Font.PLAIN, 12));
			sobre.setContentType("text/html");
			// JTextArea sobre = new JTextArea();
			sobre.setText(
					"\u00A9 DPSoft 2019 <br>Feito por D\u00E1rio Pereira\r\n<br>Email de Suporte: <a href=\"#\">" + Main.EMAIL_SUPORTE + "</a>"
							+ "<br><br>Código Fonte (GitHub): <a href=\"https://github.com/dariopereiradp/Biblioteca_DAD\">Biblioteca DAD</a><br><br>"
							+ "Compat\u00EDvel com <a href=\"https://www.java.com/download\">Java 8</a><br><br>"
							+ "Bibliotecas usadas:<br><br><a href=\"https://github.com/atarw/material-ui-swing\">Material UI Swing 0.9.6.1</a><br>"
							+ "<a href=\"https://sourceforge.net/projects/ucanaccess\">UCanAccess 4.0.4</a><br>"
							+ "<a href=\"https://github.com/srikanth-lingala/zip4j\">Zip4j 2.1.1</a><br>"
							+ "<a href=\"http://hsqldb.org\">HyperSQL</a><br>"
							+ "<a href=\"https://sourceforge.net/projects/jackcess\">Jackcess 2.1.11</a><br>"
							+ "<a href=\"https://toedter.com/jcalendar\">JCalendar 1.4</a><br>"
							+ "<a href=\"https://www.qoppa.com/pdfwriter\">jPDFWriter v2016R1.04</a><br>"
							+ "<a href=\"https://git.eclipse.org/r/plugins/gitiles/windowbuilder/org.eclipse.windowbuilder/+/9be47c5cd5decde80f45e7febe7af8698e02d498/org.eclipse.wb.swing.MigLayout.lib/miglayout15-swing.jar?autodive=0%2F\">MigLayout15</a><br>"
							+ "<a href=\"https://mvnrepository.com/artifact/commons-codec/commons-codec/1.11\">Apache Commons Codec 1.11</a><br>"
							+ "<a href=\"https://mvnrepository.com/artifact/commons-io/commons-io/2.6\">Apache Commons IO 2.6</a><br>"
							+ "<a href=\"https://mvnrepository.com/artifact/commons-lang/commons-lang/2.6\">Commons Lang 2.6</a><br>"
							+ "<a href=\"https://mvnrepository.com/artifact/commons-logging/commons-logging/1.1.3\">Apache Commons Logging 1.1.3</a><br>"
							+ "<br><br>Soli Deo Gloria - A Deus toda a glória!<br>");

			sobre.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						if (Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().browse(e.getURL().toURI());
							} catch (IOException | URISyntaxException e1) {
								e1.printStackTrace();
							} catch (NullPointerException e2) {

							}
						}
					}
				}
			});
			JScrollPane jsp = new JScrollPane(sobre);
			contentPanel.add(jsp, BorderLayout.CENTER);
		}
		{
			JLabel lblBibliotecaDdivaDe = new JLabel(Main.TITLE);
			lblBibliotecaDdivaDe.setHorizontalAlignment(SwingConstants.CENTER);
			lblBibliotecaDdivaDe.setFont(new Font("Dialog", Font.PLAIN, 17));
			contentPanel.add(lblBibliotecaDdivaDe, BorderLayout.NORTH);
		}
		{
			JLabel lblVerso = new JLabel("Vers\u00E3o " + Main.VERSION + " - " + Main.DATA_PUBLICACAO);
			lblVerso.setFont(new Font("Dialog", Font.PLAIN, 13));
			lblVerso.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblVerso, BorderLayout.SOUTH);
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
						dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	public void open() {
		setVisible(true);
	}

}
