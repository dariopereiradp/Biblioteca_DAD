package dad.biblioteca;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import dad.biblioteca.gui.DataGui;
import dad.recursos.ImageCompression;
import dad.recursos.Log;

public class Item {

	public static final String imgPath = System.getenv("APPDATA") + "/BibliotecaDAD/Databases/Imagens/";
	public static int countID = 0;
	private int id;
	private String nome;
	private String local;
	private String classificacao;
	private String autor;
	private boolean disponivel;
	private int numero_exemplares;
	private int n_exemp_disponiveis;
	private int n_exemp_emprestados;
	private ImageIcon img;
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
		n_exemp_emprestados = 0;
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
		n_exemp_emprestados = 0;
		this.img = img;
	}

	public String getNome() {
		return nome;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public void setDisponivel(String s) {
		if (s.equals("Sim"))
			disponivel = true;
		else if (s.equals("Não"))
			disponivel = false;
	}

	public int getNumero_exemplares() {
		return numero_exemplares;
	}

	public void setNumero_exemplares(int numero_exemplares) {
		if (numero_exemplares > 0) {
			this.numero_exemplares = numero_exemplares;
			setN_exemp_disponiveis(numero_exemplares - n_exemp_emprestados);
		}
	}

	public void incrementar_exemplares() {
		numero_exemplares++;
		n_exemp_disponiveis++;
	}

	public void decrementar_exemplares() {
		numero_exemplares--;
		n_exemp_disponiveis--;
	}

	public int getId() {
		return id;
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
		return n_exemp_emprestados;
	}

	public void setN_exemp_emprestados() {
		n_exemp_emprestados = numero_exemplares - n_exemp_disponiveis;
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

}
