package dad.biblioteca;

public class Multimedia extends Item {
	
	private String tipo;

	//título (nome), artista (autor), classificação

	public Multimedia(String nome, String tipo) {
		super(nome);
		this.setTipo(tipo);
		// TODO Auto-generated constructor stub
	}
	
	public Multimedia(String nome, String tipo, String classificacao, String local){
		super(nome, classificacao, local, null);
		if (!tipo.trim().equals(""))
			this.setTipo(tipo);
		else
			this.setTipo("-");
		
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
