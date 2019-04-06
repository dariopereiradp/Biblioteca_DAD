package dad.biblioteca;

public class Multimedia extends Item {

	// título (nome), artista (autor), classificação

	public Multimedia(String nome, String tipo) {
		super(nome, tipo);
		// TODO Auto-generated constructor stub
	}

	public Multimedia(String nome, String tipo, String classificacao, String local) {
		super(nome, classificacao, local, null, tipo);

	}

}
