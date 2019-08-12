package dad.biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.WordUtils;

import dad.recursos.ConexaoUser;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;

/**
 * Classe que representa um cliente (ou User): alguém que pode pedir um
 * empréstimo de um item.
 * 
 * @author Dário Pereira
 *
 */
public class User {

	/**
	 * Senha de criptografia para o CPF. Segurança fraca!
	 */
	public static final String key = "dad";
	/**
	 * Conexão para a base de dados dos usuarios.
	 */
	private static Connection con;
	private static PreparedStatement pst;
	private static ResultSet rs;
	private String nome;
	private Date data_nascimento;
	private String cpf;
	private String telefone;
	private int n_emprestimos;

	/**
	 * @param adicionar
	 *            - 'true' se deseja adicionar o novo User na base de dados,
	 *            'false' caso contrário.
	 */
	public User(String nome, Date data_nascimento, String cpf, String telefone, int n_emprestimos, boolean adicionar) {
		con = ConexaoUser.getConnection();
		nome = WordUtils.capitalize(nome);
		this.setNome(nome);
		this.setData_nascimento(data_nascimento);
		this.setCpf(cpf);
		if (telefone.length() == 11)
			this.setTelefone(telefone);
		else
			this.setTelefone("00000000000");
		this.n_emprestimos = n_emprestimos;
		if (adicionar) {
			adicionarNaBaseDeDados();
		}
	}

	/**
	 * Adiciona o cliente na base de dados.
	 */
	public void adicionarNaBaseDeDados() {
		try {
			CriptografiaAES.setKey(key);
			CriptografiaAES.encrypt(cpf);
			pst = con.prepareStatement("insert into usuarios(CPF,Nome,Data_Nascimento,Telefone,N_Emprestimos) values (?,?,?,?,?)");
			pst.setString(1, CriptografiaAES.getEncryptedString());
			pst.setString(2, getNome());
			String data = new SimpleDateFormat("yyyy-M-d").format(data_nascimento);
			pst.setDate(3, java.sql.Date.valueOf(data));
			pst.setString(4, getTelefone());
			pst.setInt(5, getN_emprestimos());
			pst.execute();
		} catch (Exception e) {
			Log.getInstance().printLog("Erro ao adicionar o cliente na base de dados! - " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Remove o cliente da base de dados.
	 */
	public void removerBaseDeDados() {
		try {
			CriptografiaAES.setKey(key);
			CriptografiaAES.encrypt(cpf);
			pst = con.prepareStatement("delete from usuarios where CPF=?");
			pst.setString(1, CriptografiaAES.getEncryptedString());
			pst.execute();
		} catch (Exception e) {
			Log.getInstance().printLog("Erro ao remover o cliente na base de dados! - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getData_nascimento() {
		return data_nascimento;
	}

	public void setData_nascimento(Date data_nascimento) {
		this.data_nascimento = data_nascimento;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public int getN_emprestimos() {
		return n_emprestimos;
	}

	public void setN_emprestimos(int n_emprestimos) {
		this.n_emprestimos = n_emprestimos;
	}

	public void incrementar_emprestimos() {
		n_emprestimos++;
		atualizarEmprestimos();
	}

	public void decrementar_emprestimos() {
		n_emprestimos--;
		atualizarEmprestimos();
	}

	/**
	 * Atualiza na base de dados o número de empréstimos realizados.
	 */
	public void atualizarEmprestimos() {
		try {
			String cpf = this.cpf;
			pst = con.prepareStatement("update usuarios set N_Emprestimos=? where cpf=?");
			CriptografiaAES.setKey(key);
			CriptografiaAES.encrypt(cpf);
			cpf = CriptografiaAES.getEncryptedString();
			pst.setInt(1, getN_emprestimos());
			pst.setString(2, cpf);
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Atualiza na base de dados os dados pessoais do cliente.
	 */
	public void atualizarDados() {
		try {
			String cpf = this.cpf;
			pst = con.prepareStatement("update usuarios set nome=?,Data_Nascimento=? where cpf=?");
			CriptografiaAES.setKey(key);
			CriptografiaAES.encrypt(cpf);
			cpf = CriptografiaAES.getEncryptedString();
			pst.setString(1, getNome());
			String data = new SimpleDateFormat("yyyy-M-d").format(data_nascimento);
			pst.setDate(2, java.sql.Date.valueOf(data));
			pst.setString(3, cpf);
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Verifica se o cliente com o dado cpf existe na base de dados.
	 * 
	 * @param cpf
	 *            - cpf do cliente a consultar.
	 * @return 'true' se o cliente existe. 'false' caso contrário.
	 */
	public static boolean existe(String cpf) {
		try {
			CriptografiaAES.setKey(key);
			CriptografiaAES.encrypt(cpf);
			cpf = CriptografiaAES.getEncryptedString();
			con = ConexaoUser.getConnection();
			pst = con.prepareStatement("select * from usuarios where cpf = ?");
			pst.setString(1, cpf);
			rs = pst.executeQuery();
			if (rs.next())
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Cria uma nova instância de um cliente que existe na base de dados com o
	 * cpf dado.
	 * 
	 * @param cpf
	 *            - número de cpf a consultar na base de dados.
	 * @return uma nova instância em memória do cliente que tem o dado cpf na
	 *         base de dados.
	 */
	public static User getUser(String cpf) {
		String nome = "";
		Date data_nascimento = new Date();
		String telefone = "";
		int n_emprestimos = 0;
		try {
			CriptografiaAES.setKey(key);
			CriptografiaAES.encrypt(cpf);
			con = ConexaoUser.getConnection();
			pst = con.prepareStatement("select * from usuarios where cpf=?");
			pst.setString(1, CriptografiaAES.getEncryptedString());
			rs = pst.executeQuery();
			rs.next();
			nome = rs.getString(2);
			data_nascimento = rs.getDate(3);
			n_emprestimos = rs.getInt(4);
			telefone = rs.getString(5);
			if (telefone == null)
				telefone = "-";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new User(nome, data_nascimento, cpf, telefone, n_emprestimos, false);
	}

	/**
	 * Se o cliente com o dado cpf existe na base de dados, retorna uma
	 * instância desse cliente. Se não existe, cria um novo cliente e adiciona
	 * na base de dados.
	 * 
	 * @param nome
	 * @param data_nascimento
	 * @param cpf
	 * @param telefone
	 * @param n_emprestimos
	 * @return
	 */
	public static User newUser(String nome, Date data_nascimento, String cpf, String telefone, int n_emprestimos) {
		if (existe(cpf))
			return getUser(cpf);
		else
			return new User(nome, data_nascimento, cpf, telefone, n_emprestimos, true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
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
		if (cpf == null) {
			if (other.cpf != null)
				return false;
		} else if (!cpf.equals(other.cpf))
			return false;
		return true;
	}
	
	/**
	 * Devolve uma representação textual mais detalhada do cliente.
	 * @return
	 */
	public String toText() {
		return nome + " | " + cpf + " | " + new SimpleDateFormat("dd/MM/yyyy").format(data_nascimento)
				+ " | Nº de empréstimos: " + n_emprestimos;
	}
	
	@Override
	public String toString() {
		return nome;
	}

}
