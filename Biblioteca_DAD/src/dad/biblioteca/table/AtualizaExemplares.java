package dad.biblioteca.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dad.biblioteca.Livro;
import dad.recursos.Command;
import dad.recursos.ConexaoLivros;
import dad.recursos.Log;

public class AtualizaExemplares implements Command {

	private Connection con;
	private PreparedStatement pst;
	private boolean disponivel;
	private Livro livro;
	private Object valor;
	private int oldExemp;
	private int oldDisp;

	public AtualizaExemplares(boolean disponivel, Livro livro, Object valor) {
		this.disponivel = disponivel;
		this.livro = livro;
		this.valor = valor;
		con = ConexaoLivros.getConnection();
		oldExemp = livro.getNumero_exemplares();
		oldDisp = livro.getN_exemp_disponiveis();
	}

	@Override
	public void execute() {
		try {
			livro.setNumero_exemplares((Integer) valor);
			pst = con.prepareStatement("update livros set Exemplares=? where ID=" + livro.getId());
			pst.setString(1, String.valueOf((Integer) valor));
			pst.execute();
			pst = con.prepareStatement("update livros set Disponíveis=? where ID=" + livro.getId());
			pst.setString(1, String.valueOf(livro.getN_exemp_disponiveis()));
			pst.execute();
			if (livro.isDisponivel() != disponivel) {
				pst = con.prepareStatement("update livros set Disponível=? where ID=" + livro.getId());
				if (livro.isDisponivel())
					pst.setString(1, "Sim");
				else
					pst.setString(1, "Não");
				pst.execute();
			}
			TableModelLivro.getInstance().fireTableDataChanged();
		} catch (SQLException e) {
			Log.getInstance().printLog("Erro ao atualizar número de exemplares");
		}

	}

	@Override
	public void undo() {
		try {
			livro.setNumero_exemplares(oldExemp);
			livro.setN_exemp_disponiveis(oldDisp);
			pst = con.prepareStatement("update livros set Exemplares=? where ID=" + livro.getId());
			pst.setString(1, String.valueOf(oldExemp));
			pst.execute();
			pst = con.prepareStatement("update livros set Disponíveis=? where ID=" + livro.getId());
			pst.setString(1, String.valueOf(oldDisp));
			pst.execute();
			if (livro.isDisponivel() != disponivel) {
				pst = con.prepareStatement("update livros set Disponível=? where ID=" + livro.getId());
				if (livro.isDisponivel())
					pst.setString(1, "Sim");
				else
					pst.setString(1, "Não");
				pst.execute();
			}
			TableModelLivro.getInstance().fireTableDataChanged();
		} catch (SQLException e) {
			Log.getInstance().printLog("Erro ao atualizar número de exemplares");
		}

	}

	@Override
	public void redo() {
		execute();

	}

	@Override
	public String getName() {
		return "Atualizar Exemplares";
	}

}