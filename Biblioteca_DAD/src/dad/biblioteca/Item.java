package dad.biblioteca;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import dad.biblioteca.gui.DataGui;
import dad.biblioteca.table.TableModelLivro;
import dad.recursos.ImageCompression;
import dad.recursos.Log;

/**
 * 
 * @author Dário Pereira Classe geral que representa um item da biblioteca:
 *         livro, multimédia ou outros.
 */
public class Item {

	/**
	 * Caminho para a pasta das imagens dos livros.
	 */
	public static final String imgPath = System.getenv("APPDATA") + "/BibliotecaDAD/Databases/Imagens/";
	/**
	 * Variável usada para controle do próximo id a ser atribuído a um novo
	 * item.
	 */
	public static int countID = 0;
	private int id;
	private String nome;
	/**
	 * Localização física do item na biblioteca.
	 */
	private String local;
	private String classificacao;
	private String autor;
	/**
	 * Indica se o item está disponível para empréstimo ou não.
	 */
	private boolean disponivel;
	private int numero_exemplares;
	private int n_exemp_disponiveis;
	private ImageIcon img;
	/**
	 * Indica o tipo do item: livro, cd, dvd, revista, jornal, etc...
	 */
	private String tipo;

	public Item(String nome, String tipo) {
		this.nome = nome;
		this.tipo = tipo;
		local = "-";
		autor = "-";
		setClassificacao("-");
		disponivel = true;
		id = ++countID;
		numero_exemplares = 1;
		setN_exemp_disponiveis(1);
	}

	public Item(String nome, String autor, String classificacao, String local, ImageIcon img, String tipo) {
		this.nome = nome;
		if (!tipo.trim().equals(""))
			this.tipo = tipo;
		else
			this.tipo = "-";

		if (!autor.trim().equals(""))
			this.autor = autor;
		else
			this.autor = "-";
		if (!local.trim().equals(""))
			this.local = local;
		else
			this.local = "-";
		if (!classificacao.trim().equals(""))
			this.classificacao = classificacao;
		else
			this.classificacao = "-";
		disponivel = true;
		id = ++countID;
		numero_exemplares = 1;
		setN_exemp_disponiveis(1);
		this.img = img;
	}

	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public boolean isDisponivel() {
		if (n_exemp_disponiveis > 0)
			disponivel = true;
		else
			disponivel = false;
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}

	/**
	 * Diz se está disponível ou não de acordo com o valor salvo na base de dados: 'Sim' ou 'Não'
	 * @param s 'Sim' se está disponível. 'Não' se não está disponível. Se for introduzida outra string o método não faz nada.
	 */
	public void setDisponivel(String s) {
		if (s.equals("Sim"))
			disponivel = true;
		else if (s.equals("Não"))
			disponivel = false;
	}

	public void incrementar_exemplares() {
		numero_exemplares++;
		n_exemp_disponiveis++;
	}

	public void decrementar_exemplares() {
		numero_exemplares--;
		n_exemp_disponiveis--;
	}
	
	public int getNumero_exemplares() {
		return numero_exemplares;
	}

	public void setNumero_exemplares(int numero_exemplares) {
		if (numero_exemplares > 0) {
			if (getN_exemp_emprestados() > 0) {
				int n_exemp_disp = numero_exemplares - getN_exemp_emprestados();
				this.numero_exemplares = numero_exemplares;
				setN_exemp_disponiveis(n_exemp_disp);

			} else {
				this.numero_exemplares = numero_exemplares;
				setN_exemp_disponiveis(numero_exemplares);
			}
		}
	}

	public int getN_exemp_disponiveis() {
		return n_exemp_disponiveis;
	}

	public void setN_exemp_disponiveis(int n_exemp_disponiveis) {
		if (n_exemp_disponiveis >= 0) {
			if (n_exemp_disponiveis <= numero_exemplares)
				this.n_exemp_disponiveis = n_exemp_disponiveis;
			if (n_exemp_disponiveis == 0)
				disponivel = false;
		}
	}

	public int getN_exemp_emprestados() {
		return getNumero_exemplares() - getN_exemp_disponiveis();
	}

	public void inc_exemp_emprestados() {
		n_exemp_disponiveis--;
		if (this instanceof Livro) {
			TableModelLivro.getInstance().atualizaExemplaresDisponiveis((Livro) this);
		}
	}

	public void dec_exemp_emprestados() {
		n_exemp_disponiveis++;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public ImageIcon getImg() {
		return img;
	}

	public void setImg(ImageIcon img) {
		this.img = img;
	}

	/**
	 * Abre um diálogo para escolher uma imagem para ficar associada ao item.
	 */
	public void addImg() {
		FileDialog fd = new FileDialog(DataGui.getInstance(), "Escolher uma imagem", FileDialog.LOAD);
		fd.setDirectory(System.getProperty("user.home") + System.getProperty("file.separator") + "Pictures");
		fd.setFile("*.jpg");
		fd.setVisible(true);
		String filename = fd.getFile();
		if (filename != null)
			try {
				ImageCompression.compress(new File(fd.getDirectory() + filename), this);
			} catch (IOException e) {
				e.printStackTrace();
				Log.getInstance().printLog("Item - addImg: Erro ao copiar a imagem!");
			}
	}

	/**
	 *  É dado um 'id' e o programa devolve o item que tem esse 'id', se existir.
	 * @param id - 'id' do item pretendido.
	 * @return o item com o 'id' introduzido. Se o 'id' não estiver associado a nenhum item, retorna 'null'
	 */
	public static Item getItemById(int id) {
		if (TableModelLivro.getInstance().getLivroById(id) != null)
			return TableModelLivro.getInstance().getLivroById(id);
		// else: verificar na multimedia e outros

		// se não existe em lado nenhum
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		Item other = (Item) obj;
		if (id != other.id)
			return false;
		if (nome.toLowerCase() == null) {
			if (other.nome.toLowerCase() != null)
				return false;
		} else if (!nome.toLowerCase().equals(other.nome.toLowerCase()))
			return false;
		return true;
	}

}
