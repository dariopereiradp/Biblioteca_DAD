package dad.biblioteca;

/**
 * Classe que representa outros itens, que n�o s�o livros nem multimedia, como por exemplo: revistas, jornais e outros.
 * @author D�rio Pereira
 *
 */
public class Outros extends Item {

	/**
	 * Informa��o relevante sobre o item.
	 */
	private String outrasInf;

	public Outros(String nome, String tipo) {
		super(nome, tipo);
		this.setOutrasInf("-");
	}

	public Outros(String nome, String autor, String tipo, String classificacao, String outrasInf, String local) {
		super(nome, autor, classificacao, local, null, tipo);
		if (!outrasInf.trim().equals(""))
			this.setOutrasInf(outrasInf);
		else
			this.setOutrasInf("-");

	}

	public String getOutrasInf() {
		return outrasInf;
	}

	public void setOutrasInf(String outrasInf) {
		this.outrasInf = outrasInf;
	}

}
