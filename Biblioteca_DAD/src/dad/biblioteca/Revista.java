package dad.biblioteca;

public class Revista extends Item {
	
	private String editora;
	
	public Revista(String nome) {
		super(nome);
		this.setEditora("-");
	}
	
	public Revista(String nome, String editora) {
		super(nome);
		this.setEditora(editora);
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	public static Revista newRevista(String revista){
		//TODO - ver se já existe
		//se não retorna nova revista
		return new Revista(revista);
	}
}
