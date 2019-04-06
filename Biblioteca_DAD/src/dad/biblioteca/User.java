package dad.biblioteca;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;

public class User {

	private String nome;
	private int idade;
	private long cpf;
	private ArrayList<Emprestimo> emprestimos = new ArrayList<>();

	private User(String nome, int idade, long cpf, boolean adicionar) {
		nome = WordUtils.capitalize(nome);
		this.setNome(nome);
		this.setIdade(idade);
		this.setCpf(cpf);
		if (adicionar) {
			// TODO: salvar na base de dados
		}
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public long getCpf() {
		return cpf;
	}

	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public ArrayList<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(ArrayList<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cpf ^ (cpf >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (cpf != other.cpf)
			return false;
		return true;
	}

	public static boolean existe(long cpf) {
		// Ligação à base de dados
		return true;
	}

	public static User getUser(long cpf) {
		// Ligação à base de dados
		String nome = "-";
		int idade = 0;
		return new User(nome, idade, cpf, false);
	}

	public static User newUser(String nome, int idade, long cpf) {
		if (existe(cpf))
			return getUser(cpf);
		else
			return new User(nome, idade, cpf, true);
	}
	
	@Override
	public String toString(){
		return nome;
	}

}
