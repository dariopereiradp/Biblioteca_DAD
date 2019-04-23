package dad.biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.WordUtils;

import dad.recursos.ConexaoUser;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;

public class User {

	private static final String key = "dad";
	private static Connection con;
	private static PreparedStatement pst;
	private static ResultSet rs;
	private String nome;
	private Date data_nascimento;
	private String cpf;
	private ArrayList<Emprestimo> emprestimos = new ArrayList<>();

	private User(String nome, Date data_nascimento, String cpf, boolean adicionar) {
		con = ConexaoUser.getConnection();
		nome = WordUtils.capitalize(nome);
		this.setNome(nome);
		this.setData_nascimento(data_nascimento);
		this.setCpf(cpf);
		if (adicionar) {

			try {
				CriptografiaAES.setKey(key);
				CriptografiaAES.encrypt(cpf);
				pst = con.prepareStatement(
						"insert into usuarios(CPF,Nome,Data_Nascimento,N_Emprestimos) values (?,?,?,?)");
				pst.setString(1, CriptografiaAES.getEncryptedString());
				pst.setString(2, getNome());
				String data = new SimpleDateFormat("yyyy-M-d").format(data_nascimento);
				pst.setDate(3, java.sql.Date.valueOf(data));
				pst.setInt(4, 0);
				pst.execute();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.getInstance().printLog("Utilizador admin criado com sucesso!");
			// TODO: salvar na base de dados
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
//			data_nascimento = DateFormat.getDateInstance().parse(rs.getString(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new User(nome, data_nascimento, cpf, false);
	}

	public static User newUser(String nome, Date data_nascimento, String cpf) {
		if (existe(cpf))
			return getUser(cpf);
		else
			return new User(nome, data_nascimento, cpf, true);
	}

	@Override
	public String toString() {
		return nome;
	}

	public String toText() {
		return nome + " | " + cpf + " | " + new SimpleDateFormat("dd/MM/yyyy").format(data_nascimento)
				+ " | N� de empr�stimos: " + emprestimos.size();
	}

}
