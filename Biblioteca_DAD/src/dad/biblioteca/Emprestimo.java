package dad.biblioteca;

import com.qoppa.pdfWriter.PDFDocument;
import dad.recursos.PDFGenerator;

public class Emprestimo {
	
	public static double MULTA = 0.5;
	private static int countId = 0;
	private int id;
	private User user;
	private Item item;
	private long data;
	private int num_dias;
	private boolean entregue;
	
	public Emprestimo(User user, Item item, long data, int num_dias) {
		id = ++countId;
		this.user = user;
		this.item = item;
		this.data = data;
		this.num_dias = num_dias;
		entregue = false;
	}
	
	public static void setMULTA(double multa) {
		MULTA = multa;
	}
	
	public void entregar(){
		entregue = true;
	}

	public int getId() {
		return id;
	}
	
	public void setId (int id){
		this.id = id;
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

	public long getData() {
		return data;
	}

	public void setData(long data) {
		this.data = data;
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
	
	public PDFDocument toPdf(){
		return new PDFGenerator(this).generatePDF();
	}
	
	
	
	

}
