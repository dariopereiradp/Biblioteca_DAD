package dad.biblioteca;

/**
 *  Classe que representa itens de multimédia: cds, dvds e outros.
 * @author Dário Pereira
 *
 */
public class Multimedia extends Item {

	// título (nome), artista (autor), classificação

	public Multimedia(String nome, String tipo) {
		super(nome, tipo);
		// TODO Auto-generated constructor stub
	}

	public Multimedia(String nome, String artista, String tipo, String classificacao, String local) {
		super(nome, artista, classificacao, local, null, tipo);

	}

}
