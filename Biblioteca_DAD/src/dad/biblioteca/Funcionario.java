package dad.biblioteca;

import java.util.Date;

/**
 * 
 * @author Dário Pereira
 *
 */
public class Funcionario {

	private String nome;
	private int num_acessos;
	private Date data_ultimo_acesso;
	private Date data_criacao;

	public Funcionario(String nome, int num_acessos, Date data_ultimo_acesso, Date data_criacao) {
		super();
		this.nome = nome;
		this.num_acessos = num_acessos;
		this.data_ultimo_acesso = data_ultimo_acesso;
		this.data_criacao = data_criacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getNum_acessos() {
		return num_acessos;
	}

	public void setNum_acessos(int num_acessos) {
		this.num_acessos = num_acessos;
	}

	public void novoAcesso() {
		num_acessos++;
		data_ultimo_acesso = new Date();
	}

	public Date getData_ultimo_acesso() {
		return data_ultimo_acesso;
	}

	public void setData_ultimo_acesso(Date data_ultimo_acesso) {
		this.data_ultimo_acesso = data_ultimo_acesso;
	}

	public Date getData_criacao() {
		return data_criacao;
	}

	public void setData_criacao(Date data_criacao) {
		this.data_criacao = data_criacao;
	}

}
