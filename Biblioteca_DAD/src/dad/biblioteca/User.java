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

public class User {

	public static final String key = "dad";
	private static Connection con;
	private static PreparedStatement pst;
	private static ResultSet rs;
	private String nome;
	private Date data_nascimento;
	private String cpf;
	private int n_emprestimos;

	private User(String nome, Date data_nascimento, String cpf, int n_emprestimos, boolean adicionar) {
		con = ConexaoUser.getConnection();
		nome = WordUtils.capitalize(nome);
		this.setNome(nome);
		this.setData_nascimento(data_nascimento);
		this.setCpf(cpf);
		this.n_emprestimos = n_emprestimos;
		if (adicionar) {
			adicionarNaBaseDeDados();
			Log.getInstance().printLog("Utilizador admin criado com sucesso!");
			// TODO: salvar na base de dados
		}
	}

	public void adicionarNaBaseDeDados() {
		try {
			CriptografiaAES.setKey(key);
			CriptografiaAES.encrypt(cpf);
			pst = con.prepareStatement("insert into usuarios(CPF,Nome,Data_Nascimento,N_Emprestimos) values (?,?,?,?)");
			pst.setString(1, CriptografiaAES.getEncryptedString());
			pst.setString(2, getNome());
			String data = new SimpleDateFormat("yyyy-M-d").format(data_nascimento);
			pst.setDate(3, java.sql.Date.valueOf(data));
			pst.setInt(4, getN_emprestimos());
			pst.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
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
		} catch (Exception e){
			e.printStackTrace();
		}
		
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

	public static User getUser(String cpf) {
		String nome = "";
		Date data_nascimento = new Date();
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
			// data_nascimento =
			// DateFormat.getDateInstance().parse(rs.getString(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new User(nome, data_nascimento, cpf, n_emprestimos, false);
	}

	public static User newUser(String nome, Date data_nascimento, String cpf, int n_emprestimos) {
		if (existe(cpf))
			return getUser(cpf);
		else
			return new User(nome, data_nascimento, cpf, n_emprestimos, true);
	}

	@Override
	public String toString() {
		return nome;
	}

	public String toText() {
		return nome + " | " + cpf + " | " + new SimpleDateFormat("dd/MM/yyyy").format(data_nascimento)
				+ " | Nº de empréstimos: " + n_emprestimos;
	}

}
