package dad.recursos;

/**
 * Interface para representar um comando, com as funções de execute, undo e redo.
 * @author Dário Pereira
 *
 */
public interface Command {

	void execute();

	void undo();

	void redo();

	String getName();

}
