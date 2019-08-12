package dad.biblioteca;

/**
 *  Classe que representa itens de multim�dia: cds, dvds e outros.
 * @author D�rio Pereira
 *
 */
public class Multimedia extends Item {

	// t�tulo (nome), artista (autor), classifica��o

	public Multimedia(String nome, String tipo) {
		super(nome, tipo);
		// TODO Auto-generated constructor stub
	}

	public Multimedia(String nome, String artista, String tipo, String classificacao, String local) {
		super(nome, artista, classificacao, local, null, tipo);

	}

}
