package dad.recursos;

public interface Command {

	void execute();

	void undo();

	void redo();

	String getName();

}
