package dad.biblioteca;

public class Livro extends Item implements Comparable<Livro> {

	private String autor;
	private String editora;

	public Livro(String nome) {
		super(nome);
		autor = "-";
		editora = "-";
	}
	
	public Livro(String nome, String autor, String editora, String classificacao, String local) {
		super(nome, classificacao, local, null);
		if (!autor.trim().equals(""))
			this.autor = autor;
		else
			this.autor = "-";
		if (!editora.trim().equals(""))
			this.editora = editora;
		else
			this.editora = "-";
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	@Override
	public String toString() {
		return super.getNome() + " | " + autor + " | " + editora + " | " + getClassificacao() + " | " + isDisponivel()
				+ " | " + getNumero_exemplares();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((autor == null) ? 0 : autor.hashCode());
		result = prime * result + ((getClassificacao() == null) ? 0 : getClassificacao().hashCode());
		result = prime * result + ((editora == null) ? 0 : editora.hashCode());
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
		Livro other = (Livro) obj;
		if (other.getId() == getId())
			return true;
		else if (other.getNome().toLowerCase().equals(getNome().toLowerCase())
				&& other.getAutor().toLowerCase().equals(getAutor().toLowerCase())
				&& other.getEditora().toLowerCase().equals(getEditora().toLowerCase())
				&& other.getClassificacao().toLowerCase().equals(getClassificacao().toLowerCase()))
			return true;
		else
			return false;

	}

	@Override
	public int compareTo(Livro o) {
		return this.getNome().compareToIgnoreCase(o.getNome());
	}

}
