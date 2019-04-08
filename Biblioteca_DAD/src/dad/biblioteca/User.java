package dad.biblioteca;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.WordUtils;

public class User {

	private String nome;
	private Date data_nascimento;
	private long cpf;
	private ArrayList<Emprestimo> emprestimos = new ArrayList<>();

	private User(String nome, Date data_nascimento, long cpf, boolean adicionar) {
		nome = WordUtils.capitalize(nome);
		this.setNome(nome);
		this.setData_nascimento(data_nascimento);
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

	public Date getData_nascimento() {
		return data_nascimento;
	}

	public void setData_nascimento(Date data_nascimento) {
		this.data_nascimento = data_nascimento;
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
		return false;
	}

	public static User getUser(long cpf) {
		// Ligação à base de dados
		String nome = "-";
		Date data_nascimento = new Date();
		return new User(nome, data_nascimento, cpf, false);
	}

	public static User newUser(String nome, Date data_nascimento, long cpf) {
		if (existe(cpf))
			return getUser(cpf);
		else
			return new User(nome, data_nascimento, cpf, true);
	}
	
	@Override
	public String toString(){
		return nome;
	}

}
