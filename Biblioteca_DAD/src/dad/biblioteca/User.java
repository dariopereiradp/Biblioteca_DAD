package dad.biblioteca;

import java.util.ArrayList;

public class User {

	private String nome;
	private int idade;
	private long cpf;
	private ArrayList<Emprestimo> emprestimos = new ArrayList<>();

	public User(String nome, int idade, long cpf) {
		this.nome = nome;
		this.idade = idade;
		this.cpf = cpf;
	}

}
