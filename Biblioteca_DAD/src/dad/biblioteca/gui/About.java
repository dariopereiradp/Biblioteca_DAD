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
					"\u00A9 DPSoft 2019 <br>\r\nFeito por D\u00E1rio Pereira\r\n<br>\r\nEmail de Suporte: <a href=\"#\">" + Main.EMAIL_SUPORTE + "</a>\r\n"
							+ "<br><br>\r\nCompat\u00EDvel com Java 8\r\n<br><br>\r\n"
							+ "Bibliotecas usadas:\r\n<a href=\"https://github.com/srikanth-lingala/zip4j\">Zip4j</a>");

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
