package dad.biblioteca;

public class Multimedia extends Item {

	// t�tulo (nome), artista (autor), classifica��o

	public Multimedia(String nome, String tipo) {
		super(nome, tipo);
		// TODO Auto-generated constructor stub
	}

	public Multimedia(String nome, String tipo, String classificacao, String local) {
		super(nome, classificacao, local, null, tipo);

	}

}
