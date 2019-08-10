package dad.biblioteca;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.qoppa.pdfWriter.PDFDocument;

import dad.recursos.PDFGenerator;

/**
 * 
 * @author Dário Pereira
 * Classe que representa os empréstimos realizados. O empréstimo de um item (livro, multimedia ou outros) é realizado a um cliente (user).
 */
public class Emprestimo {
	
	/**
	 * Valor da multa a ser cobrado por cada dia de atraso.
	 */
	public static double MULTA = 0.5;
	/**
	 * Usado para ter um controle do ID a ser atribuído para o próximo empréstimo a ser criado.
	 */
	public static int countId = 0;
	private int id;
	private User cliente;
	private Item item;
	/**
	 * Data em que o empréstimo foi realizado.
	 */
	private Date data_emprestimo;
	/**
	 * Prazo máximo de entrega do empréstimo. Se for entregue depois desse dia será paga uma multa de acordo com o número de dias em atraso.
	 */
	private Date data_entrega;
	/**
	 * Número de dias em que o cliente ficará com o item emprestado.
	 */
	private int num_dias;
	private boolean entregue, pago;
	/**
	 * Funcionário que realizou o empréstimo.
	 */
	private String funcionario;

	public Emprestimo(User cliente, Item item, Date data_emprestimo, Date data_entrega, String funcionario) {
		id = ++countId;
		this.cliente = cliente;
		this.item = item;
		this.data_emprestimo = data_emprestimo;
		this.data_entrega = DateUtils.truncate(data_entrega, Calendar.DAY_OF_MONTH);
		this.num_dias = Math.toIntExact(ChronoUnit.DAYS.between(data_emprestimo.toInstant(), data_entrega.toInstant()))
				+ 1;
		entregue = false;
		this.funcionario = funcionario;
		this.pago = false;
	}
	

	public static void setMULTA(double multa) {
		MULTA = multa;
	}

	/**
	 * Informa que o item foi devolvido.
	 */
	public void entregar() {
		entregue = true;
		item.dec_exemp_emprestados();
	}
	
	/**
	 * Informa que a multa (caso exista) foi paga.
	 */
	public void pagar (){
		pago = true;
	}
	
	public boolean isPago() {
		return pago;
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

	public User getCliente() {
		return cliente;
	}

	public void setCliente(User cliente) {
		this.cliente = cliente;
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
	
	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	public static int getCountId() {
		return countId;
	}

	/**
	 * Cria um recibo PDF do empréstimo.
	 * @return
	 */
	public PDFDocument toPdf() {
		return new PDFGenerator(this).generatePDF();
	}

	public double getMulta() {
		Date hoje = new Date();
		long days = ChronoUnit.DAYS.between(data_entrega.toInstant(), hoje.toInstant());
		if (days >= 0)
			return MULTA * days;
		else return 0.0;
	}
	
	@Override
	public String toString() {
		int endIndex = Math.min(15, item.getNome().trim().length());
		return id + "-" + item.getNome().trim().substring(0, endIndex) + "-" + item.getId() + "-" + cliente.getNome().split(" ")[0] + "-"
				+ new SimpleDateFormat("dd_MMM_yyyy").format(data_emprestimo);
	}

}
