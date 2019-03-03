package dad.biblioteca;

public class Outros extends Item {

	private String tipo;
	private String outrasInf;

	public Outros(String nome) {
		super(nome);
		this.setTipo("-");
		this.setOutrasInf("-");
	}

	public Outros(String nome, String tipo, String classificacao, String outrasInf, String local) {
		super(nome, classificacao, local, null);
		if (!tipo.trim().equals(""))
			this.setTipo(tipo);
		else
			this.setTipo("-");
		if (!outrasInf.trim().equals(""))
			this.setOutrasInf(outrasInf);
		else
			this.setOutrasInf("-");

	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getOutrasInf() {
		return outrasInf;
	}

	public void setOutrasInf(String outrasInf) {
		this.outrasInf = outrasInf;
	}

}
