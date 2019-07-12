package dad.recursos;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DefaultCellRenderer extends DefaultTableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 870200575796284069L;
	private final ImageIcon editIcon = new ImageIcon(getClass().getResource("/edit.png"));

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
		this.setIcon(table.isCellEditable(row, column) ? editIcon : null);
		return this;
	}
}
