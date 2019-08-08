package dad.biblioteca.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import dad.biblioteca.User;
import dad.recursos.Command;
import dad.recursos.ConexaoUser;
import dad.recursos.CriptografiaAES;
import dad.recursos.Log;

public class AtualizaUser implements Command {

	private Connection con;
	private PreparedStatement pst;
	private User user;
	private String coluna;
	private Object valor;
	private Object old;
	private AbstractTableModel table;

	public AtualizaUser(AbstractTableModel table, String coluna, User user, Object valor) {
		this.table = table;
		this.coluna = coluna;
		this.user = user;
		this.valor = valor;
		con = ConexaoUser.getConnection();
		switch (coluna) {
		case "Nome":
			old = user.getNome();
			break;
		case "Data_Nascimento":
			old = user.getData_nascimento();
			break;
		case "Telefone":
			old = user.getTelefone();
			break;
		default:
			break;
		}
	}

	@Override
	public void execute() {
		try {
			CriptografiaAES.setKey(User.key);
			CriptografiaAES.encrypt(user.getCpf());
			pst = con.prepareStatement("update usuarios set " + coluna + "=? where CPF=?");
			switch (coluna) {
			case "Nome":
				pst.setString(1, (String) valor);
				user.setNome((String) valor);
				break;
			case "Data_Nascimento":
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date data_nasc = dateFormat.parse((String) valor);
				user.setData_nascimento(data_nasc);
				String data = new SimpleDateFormat("yyyy-M-d").format(data_nasc);
				pst.setDate(1, java.sql.Date.valueOf(data));
				break;
			case "Telefone":
				String telefone = ((String) valor).replace("-", "").replace("(", "").replace(")", "").replace(" ", "");
				if(telefone.length()!=11)
					telefone = "00000000000";
				pst.setString(1, telefone);
				user.setTelefone(telefone);
			default:
				break;
			}
			pst.setString(2, CriptografiaAES.getEncryptedString());
			pst.execute();
			table.fireTableDataChanged();
		} catch (Exception e) {
			Log.getInstance().printLog("Erro ao atualizar " + coluna);
			e.printStackTrace();
		}

	}

	@Override
	public void undo() {
		try {
			CriptografiaAES.setKey(User.key);
			CriptografiaAES.encrypt(user.getCpf());
			pst = con.prepareStatement("update usuarios set " + coluna + "=? where CPF=?");
			switch (coluna) {
			case "Nome":
				pst.setString(1, (String) old);
				user.setNome((String) old);
				break;
			case "Data_Nascimento":
				String data = new SimpleDateFormat("yyyy-M-d").format((Date) old);
				pst.setDate(1, java.sql.Date.valueOf(data));
				user.setData_nascimento((Date) old);
				break;
			case "Telefone":
				pst.setString(1, (String) old);
				user.setTelefone((String) old);
				break;
			default:
				break;
			}
			pst.setString(2, CriptografiaAES.getEncryptedString());
			pst.execute();
			table.fireTableDataChanged();
		} catch (Exception e) {
			Log.getInstance().printLog("Erro ao anular a ação!\n" + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public String getName() {
		return "Editar " + coluna;
	}
}
