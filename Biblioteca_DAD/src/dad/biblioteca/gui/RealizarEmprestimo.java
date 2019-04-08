package dad.biblioteca.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import com.qoppa.pdfWriter.PDFDocument;
import com.toedter.calendar.JDateChooser;

import dad.biblioteca.Emprestimo;
import dad.biblioteca.Item;
import dad.biblioteca.User;
import dad.biblioteca.table.TableModelLivro;
import dad.recursos.CpfValidator;
import dad.recursos.Log;
import dad.recursos.PDFGenerator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RealizarEmprestimo {

	public static final String EMPRESTIMOS_PATH = System.getProperty("user.home") + System.getProperty("file.separator")
			+ "Documents/BibliotecaDAD/Comprovantes/";
	private String dirPath;
	private JDialog dial;
	private JTextField id;
	private JTextField tipo;
	private JTextField titulo;
	private JFormattedTextField cpf;
	private JTextField idEmp;
	private JTextField nome;
	private JTextField dias;
	private MaskFormatter mascaraCpf;
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;

	public RealizarEmprestimo(Item item) {
		String month_year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMyyyy")).toUpperCase();
		dirPath = RealizarEmprestimo.EMPRESTIMOS_PATH + month_year + "/";
		File docDir = new File(dirPath);
		if (!docDir.exists())
			docDir.mkdirs();
		dial = new JDialog(DataGui.getInstance(), "Realizar Empréstimo");
		dial.getContentPane().setFont(new Font("Roboto", Font.PLAIN, 12));
		dial.setMinimumSize(new Dimension(500, 400));
		dial.setResizable(false);
		dial.getContentPane().setLayout(null);

		JLabel lID = new JLabel("ID do Item:");
		lID.setFont(new Font("Roboto", Font.PLAIN, 12));
		lID.setBounds(24, 43, 68, 14);
		dial.getContentPane().add(lID);

		JLabel lTipo = new JLabel("Tipo:");
		lTipo.setFont(new Font("Roboto", Font.PLAIN, 12));
		lTipo.setBounds(24, 66, 46, 14);
		dial.getContentPane().add(lTipo);

		JLabel lTitulo = new JLabel("T\u00EDtulo:");
		lTitulo.setFont(new Font("Roboto", Font.PLAIN, 12));
		lTitulo.setBounds(24, 90, 46, 14);
		dial.getContentPane().add(lTitulo);

		JLabel lCPF = new JLabel("CPF:");
		lCPF.setFont(new Font("Roboto", Font.PLAIN, 12));
		lCPF.setBounds(24, 164, 46, 14);
		dial.getContentPane().add(lCPF);

		JLabel lDataLimite = new JLabel("Data limite para devolu\u00E7\u00E3o:");
		lDataLimite.setFont(new Font("Roboto", Font.PLAIN, 12));
		lDataLimite.setBounds(24, 239, 152, 14);
		dial.getContentPane().add(lDataLimite);

		JLabel lIDEmp = new JLabel("ID do Empr\u00E9stimo:");
		lIDEmp.setFont(new Font("Roboto", Font.PLAIN, 12));
		lIDEmp.setBounds(24, 285, 103, 14);
		dial.getContentPane().add(lIDEmp);

		JLabel lDataEmp = new JLabel("Data do Empr\u00E9stimo:");
		lDataEmp.setFont(new Font("Roboto", Font.PLAIN, 12));
		lDataEmp.setBounds(24, 310, 119, 14);
		dial.getContentPane().add(lDataEmp);

		JLabel lDias = new JLabel("N\u00FAmero de Dias:");
		lDias.setFont(new Font("Roboto", Font.PLAIN, 12));
		lDias.setBounds(24, 335, 103, 14);
		dial.getContentPane().add(lDias);

		JLabel image = new JLabel();
		image.setFont(new Font("Roboto", Font.PLAIN, 12));
		image.setBounds(380, 11, 177 / 2, 236 / 2);
		image.setHorizontalAlignment(JLabel.CENTER);
		image.setVerticalAlignment(JLabel.CENTER);
		if (item.getImg() != null)
			image.setIcon(
					new ImageIcon(item.getImg().getImage().getScaledInstance(177 / 2, 236 / 2, Image.SCALE_DEFAULT)));
		else
			image.setText("Sem Imagem");
		image.setBorder(new LineBorder(Color.BLACK, 3));
		dial.getContentPane().add(image);

		id = new JTextField();
		id.setFont(new Font("Roboto", Font.PLAIN, 12));
		id.setEditable(false);
		id.setBounds(90, 40, 86, 20);
		id.setText(String.valueOf(item.getId()));
		dial.getContentPane().add(id);
		id.setColumns(10);

		tipo = new JTextField();
		tipo.setEditable(false);
		tipo.setFont(new Font("Roboto", Font.PLAIN, 12));
		tipo.setBounds(90, 64, 86, 20);
		dial.getContentPane().add(tipo);
		tipo.setText(item.getTipo());
		tipo.setColumns(10);

		JButton bConf = new JButton("Confirmar");
		bConf.setEnabled(false);
		bConf.setFont(new Font("Roboto", Font.PLAIN, 12));
		bConf.setBounds(365, 332, 103, 23);
		dial.getContentPane().add(bConf);

		titulo = new JTextField();
		titulo.setEditable(false);
		titulo.setFont(new Font("Roboto", Font.PLAIN, 12));
		titulo.setBounds(90, 88, 280, 20);
		dial.getContentPane().add(titulo);
		titulo.setText(item.getNome());
		titulo.setColumns(10);

		try {
			mascaraCpf = new MaskFormatter("###.###.###-##");
			mascaraCpf.setCommitsOnValidEdit(true);
			cpf = new JFormattedTextField(mascaraCpf);
		} catch (ParseException e1) {
			cpf = new JFormattedTextField();
			e1.printStackTrace();
		}

		cpf.setFont(new Font("Roboto", Font.PLAIN, 13));
		cpf.setBounds(90, 162, 181, 20);
		dial.getContentPane().add(cpf);
		cpf.setColumns(10);

		JButton bValidar = new JButton("Validar");
		bValidar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (validar()) {
					// Procurar o nome na base de dados e atualizar o campo Nome
					bConf.setEnabled(true);
					cpf.setEditable(false);
					bValidar.setEnabled(false);
				}
			}
		});
		bValidar.setFont(new Font("Roboto", Font.PLAIN, 12));
		bValidar.setBounds(281, 160, 89, 23);
		dial.getContentPane().add(bValidar);

		idEmp = new JTextField();
		idEmp.setEditable(false);
		idEmp.setFont(new Font("Roboto", Font.PLAIN, 12));
		idEmp.setBounds(153, 282, 139, 20);
		dial.getContentPane().add(idEmp);
		idEmp.setText(String.valueOf(Emprestimo.getCountId() + 1));
		idEmp.setColumns(10);

		JLabel lNome = new JLabel("Nome:");
		lNome.setFont(new Font("Roboto", Font.PLAIN, 12));
		lNome.setBounds(24, 195, 46, 14);
		dial.getContentPane().add(lNome);

		nome = new JTextField();
		nome.setEditable(false);
		nome.setFont(new Font("Roboto", Font.PLAIN, 12));
		nome.setBounds(90, 193, 280, 20);
		dial.getContentPane().add(nome);
		nome.setColumns(10);

		dias = new JTextField();
		dias.setEditable(false);
		dias.setFont(new Font("Roboto", Font.PLAIN, 12));
		dias.setBounds(153, 333, 139, 20);
		dial.getContentPane().add(dias);
		dias.setColumns(10);

		JDateChooser date_emp = new JDateChooser();
		date_emp.setLocale(new Locale("pt", "BR"));
		date_emp.setDateFormatString("dd/MMM/yyyy");
		date_emp.setMaxSelectableDate(new Date());
		date_emp.setDate(new Date());
		date_emp.setBounds(153, 308, 139, 20);
		dial.getContentPane().add(date_emp);

		JDateChooser date_entrega = new JDateChooser();
		date_entrega.setLocale(new Locale("PT", "BR"));
		date_entrega.setDateFormatString("dd/MMM/yyyy");
		date_entrega.setMinSelectableDate(new Date());
		date_entrega.setDate(new Date());
		date_entrega.setBounds(186, 239, 139, 20);
		dial.getContentPane().add(date_entrega);

		date_entrega.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				dias.setText(String.valueOf(
						ChronoUnit.DAYS.between(date_emp.getDate().toInstant(), date_entrega.getDate().toInstant())
								+ 1));
			}
		});

		bConf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Emprestimo emprestimo = new Emprestimo(User.getUser(cpf.getText().replace(".", "").replace("-", "")), item,
						date_emp.getDate(), date_entrega.getDate());
				// TODO salvar na base de dados
				emprestimo.getItem().inc_exemp_emprestados(); //salvar na base de dados
				TableModelLivro.getInstance().fireTableDataChanged();
				save(emprestimo);
			}
		});

	}

	public boolean validar() {
		String cpfString;
		cpfString = cpf.getText();
		cpfString = cpfString.replace(".", "").replace("-", "");
		if (CpfValidator.isCPF(cpfString)) {
			// TODO procurar na base de dados
			return true;
		} else {
			JOptionPane.showMessageDialog(dial, "Número de CPF inválido!", "Erro", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(getClass().getResource("DAD_SS.jpg")));
			return false;
		}
	}

	private void save(Emprestimo emprestimo) {
		PDFDocument pdf = new PDFGenerator(emprestimo).generatePDF();
		try {
			pdf.saveDocument(dirPath + emprestimo.toString() + ".pdf");
			String message = "O empréstimo com ID=" + emprestimo.getId()
					+ " foi criado com sucesso!\nFoi salvo um recibo (que pode ser impresso) na pasta:\n" + dirPath
					+ "\nVocê quer abrir o recibo agora?";
			int ok = JOptionPane.showConfirmDialog(dial, message, "Criado com sucesso", JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("DAD_SS.jpg")));
			Log.getInstance().printLog(message);
			if (ok == JOptionPane.YES_OPTION) {
				Desktop.getDesktop().open(new File(dirPath));
				Desktop.getDesktop().open(new File(dirPath + emprestimo.toString() + ".pdf"));
			}
			dial.dispose();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void open() {
		dial.setVisible(true);
	}
}
