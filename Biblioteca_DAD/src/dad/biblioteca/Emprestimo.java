package dad.biblioteca;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class Emprestimo {
	
	private static final AtomicInteger count = new AtomicInteger(0); 
	private final int id;
	private String nome_da_pessoa;
	private Item item;
	private Calendar data;
	private int num_dias;
	private boolean entregue;
	
	public Emprestimo(String nome_da_pessoa, Item item, Calendar data, int num_dias) {
		id = count.incrementAndGet();
		this.nome_da_pessoa = nome_da_pessoa;
		this.item = item;
		this.data = data;
		this.num_dias = num_dias;
		entregue = false;
	}
	
	public void entregar(){
		entregue = true;
	}

	public int getId() {
		return id;
	}
	
	

}
