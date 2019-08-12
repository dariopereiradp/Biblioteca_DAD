package dad.biblioteca.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

import dad.biblioteca.Livro;
import dad.recursos.Command;
import dad.recursos.ConexaoLivros;
import dad.recursos.Log;

/**
 * Classe que representa um comando para atualizar as informações de um livro,
 * implementando as funções de undo e redo e guardando na base de dados.
 * 
 * @author Dário Pereira
 *
 */
public class AtualizaLivro implements Command {

	private Connection con;
	private PreparedStatement pst;
	private Livro livro;
	private String coluna;
	private Object valor;
	private String old;
	private AbstractTableModel table;

	public AtualizaLivro(AbstractTableModel table, String coluna, Livro livro, Object valor) {
		this.table = table;
		this.coluna = coluna;
		this.livro = livro;
		this.valor = valor;
		con = ConexaoLivros.getConnection();
		switch (coluna) {
		case "Título":
			old = livro.getNome();
			break;
		case "Autor":
			old = livro.getAutor();
			break;
		case "Editora":
			old = livro.getEditora();
			break;
		case "Classificação":
			old = livro.getClassificacao();
			break;
		case "Local":
			old = livro.getLocal();
		default:
			break;
		}
	}

	@Override
	public void execute() {
		switch (coluna) {
		case "Título":
			livro.setNome((String) valor);
			break;
		case "Autor":
			livro.setAutor((String) valor);
			break;
		case "Editora":
			livro.setEditora((String) valor);
			break;
		case "Classificação":
			livro.setClassificacao((String) valor);
			break;
		case "Local":
			livro.setLocal((String) valor);
			break;
		default:
			break;
		}
		try {
			pst = con.prepareStatement("update livros set " + coluna + "=? where ID=" + livro.getId());
			pst.setString(1, (String) valor);
			pst.execute();
		} catch (SQLException e) {
			Log.getInstance().printLog("Erro ao atualizar " + coluna);
			e.printStackTrace();
		}

	}

	@Override
	public void undo() {
		try {
			pst = con.prepareStatement("update livros set " + coluna + "=? where ID=" + livro.getId());
			pst.setString(1, old);
			pst.execute();
			switch (coluna) {
			case "Título":
				livro.setNome(old);
				break;
			case "Autor":
				livro.setAutor(old);
				break;
			case "Editora":
				livro.setEditora(old);
				break;
			case "Classificação":
				livro.setClassificacao(old);
				break;
			case "Local":
				livro.setLocal(old);
				break;
			default:
				break;
			}
			table.fireTableDataChanged();
		} catch (SQLException e) {
			Log.getInstance().printLog("Erro ao anular a ação!\n" + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void redo() {
		execute();
		table.fireTableDataChanged();
	}

	@Override
	public String getName() {
		return "Editar " + coluna;
	}
}
