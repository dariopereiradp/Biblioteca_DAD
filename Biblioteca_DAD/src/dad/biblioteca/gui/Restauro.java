package dad.biblioteca.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import dad.recursos.Log;
import dad.recursos.ZipCompress;
import net.lingala.zip4j.ZipFile;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

/**
 * Classe que permite escolher o que se deseja restaurar de uma cópia de
 * segurança.
 * 
 * @author Dário Pereira
 *
 */
public class Restauro extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7863423688679744400L;
	private final JPanel contentPanel = new JPanel();
	private File backupFile;
	private JCheckBox conf, base_dados;

	/**
	 * Create the dialog.
	 */
	public Restauro() {
		setBounds(100, 100, 350, 200);
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage((getClass().getResource("/DAD.jpg"))));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel title = new JLabel("RESTAURAR C\u00D3PIA DE SEGURAN\u00C7A");
			title.setBounds(0, 10, 334, 23);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setFont(new Font("Dialog", Font.PLAIN, 17));
			title.setBackground(new Color(60, 179, 113));
			contentPanel.add(title);
		}

		conf = new JCheckBox("Configura\u00E7\u00F5es");
		conf.setSelected(true);
		conf.setFont(new Font("Dialog", Font.PLAIN, 14));
		conf.setBounds(6, 56, 142, 25);
		contentPanel.add(conf);

		base_dados = new JCheckBox("Base de Dados");
		base_dados.setSelected(true);
		base_dados.setFont(new Font("Dialog", Font.PLAIN, 14));
		base_dados.setBounds(6, 91, 142, 25);
		contentPanel.add(base_dados);


		JLabel lInfo = new JLabel("Selecione quais itens deseja restaurar");
		lInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lInfo.setBounds(0, 35, 334, 14);
		contentPanel.add(lInfo);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int ok = JOptionPane.showConfirmDialog(null,
								"Tem certeza que quer restaurar a cópia de segurança selecionada?\n"
										+ "Tenha atenção que os dados selecionados serão perdidos e substituídos pelos dados da cópia!\n"
										+ "Se clicar em 'Yes', o programa vai ser fechado. Quando você abrir outra vez os dados da cópia estarão restaurados.\n"
										+ "Obs: Por segurança, vai ser realizada uma cópia de segurança dos dados atuais. Essa cópia pode ser restaurada mais tarde.",
								"Restaurar Cópia de Segurança", JOptionPane.YES_NO_OPTION,
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/DAD_SS.jpg")));
						if (ok == JOptionPane.YES_OPTION) {
							String name = "BibliotecaDAD-Backup-"
									+ new SimpleDateFormat("ddMMMyyyy-HH'h'mm").format(new Date()) + ".dadb";
							ZipCompress.compress(Main.DATABASE_DIR, name, Main.BACKUP_DIR);
							restaurar();
						}
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancelar");
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
	 * Extrai os arquivos da cópia de segurança para uma pasta temporária,
	 * mantendo apenas aqueles que foram selecionados nas check-box e depois
	 * encerra o programa.
	 */
	public void restaurar() {
		String tempDir = Main.DATA_DIR + "temp/";
		File temp = new File(tempDir);
		temp.mkdirs();
		try {
			new ZipFile(backupFile).extractAll(temp.getPath());
			if (!conf.isSelected()) {
				File confFile = new File(tempDir + "conf.dad");
				confFile.delete();
			}
			if (!base_dados.isSelected()) {
				File funcFile = new File(tempDir + "logins.mdb");
				funcFile.delete();
				File livrosFile = new File(tempDir + "livros.mdb");
				livrosFile.delete();
				File images = new File(tempDir + "Imagens/");
				FileUtils.deleteDirectory(images);
				File empFile = new File(tempDir + "emprestimos.mdb");
				empFile.delete();
				File userFile = new File(tempDir + "users.mdb");
				userFile.delete();
			}
			long time = System.currentTimeMillis() - Main.inicialTime;
			Log.getInstance().printLog("Tempo de Uso: " + DurationFormatUtils.formatDuration(time, "HH'h'mm'm'ss's")
					+ "\nO programa terminou, para restaurar a cópia de segurança!");
			System.exit(0);
		} catch (Exception e) {
			Log.getInstance()
					.printLog("Ocorreram alguns erros ao restaurar a cópia de segurança... - " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Abre um JFileChooser para escolher a base de dados que se deseja
	 * restaurar, depois abre os diálogos.
	 */
	public void open() {
		JFileChooser jfc = new JFileChooser(Main.BACKUP_DIR);
		jfc.setDialogTitle("Selecione o arquivo da cópia de segurança que deseja restaurar");
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new FileNameExtensionFilter("Arquivo de backup DAD (*.dadb)", "dadb"));
		jfc.setAcceptAllFileFilterUsed(false);
		if (jfc.showOpenDialog(DataGui.getInstance()) == JFileChooser.APPROVE_OPTION) {
			backupFile = jfc.getSelectedFile();
			setVisible(true);
		} else {
			dispose();
		}
	}
}
