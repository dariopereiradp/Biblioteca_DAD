package dad.biblioteca;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.qoppa.pdfWriter.PDFDocument;
import dad.recursos.PDFGenerator;

public class Emprestimo {

	public static double MULTA = 0.5;
	public static int countId = 0;
	private int id;
	private User user;
	private Item item;
	private Date data_emprestimo;
	private Date data_entrega;
	private int num_dias;
	private boolean entregue;

	public Emprestimo(User user, Item item, Date data_emprestimo, Date data_entrega) {
		id = ++countId;
		this.user = user;
		this.item = item;
		this.data_emprestimo = data_emprestimo;
		this.data_entrega = data_entrega;
		this.num_dias = Math.toIntExact(ChronoUnit.DAYS.between(data_emprestimo.toInstant(), data_entrega.toInstant())) + 1;
		entregue = false;
	}

	public static void setMULTA(double multa) {
		MULTA = multa;
	}

	public void entregar() {
		entregue = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getData_emprestimo() {
		return data_emprestimo;
	}

	public void setData_emprestimo(Date data_emprestimo) {
		this.data_emprestimo = data_emprestimo;
	}

	public Date getData_entrega() {
		return data_entrega;
	}

	public void setData_entrega(Date data_entrega) {
		this.data_entrega = data_entrega;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getNum_dias() {
		return num_dias;
	}

	public void setNum_dias(int num_dias) {
		this.num_dias = num_dias;
	}

	public boolean isEntregue() {
		return entregue;
	}

	public void setEntregue(boolean entregue) {
		this.entregue = entregue;
	}

	public static int getCountId() {
		return countId;
	}

	public PDFDocument toPdf() {
		return new PDFGenerator(this).generatePDF();
	}

	@Override
	public String toString() {
		return id + "-" + item.getNome() + "-" + user.getNome() + "-" + new SimpleDateFormat("dd_MMM_yyyy").format(data_emprestimo);
	}
	
	

}
