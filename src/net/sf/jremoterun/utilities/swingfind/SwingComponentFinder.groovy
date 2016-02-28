package net.sf.jremoterun.utilities.swingfind;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.Collection;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrUtilities;

/**
 * Used walk on each component and find needed component
 */
@CompileStatic
public class SwingComponentFinder {

	public static Component findComponentG(Closure<Boolean> accepter){
		return findComponent(new AccepterProxy(accepter))
	}
	
	public static Component findComponent(Accepter accepter){		
		Collection<Window> windows = JrrUtilities.findVisibleAwtWindows();
		for (Window window : windows) {
			Component component = findComponent(window, accepter);
			if (component != null) {
				return component;
			}
		}
		return null;
	}

	public static Component findComponentG(Container container, Closure<Boolean> accepter) {
		return findComponent(container, new AccepterProxy(accepter))
	}
	
	public static Component findComponent(Container container, Accepter accepter) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			if (accepter.accept(component)) {
				return component;
			}
			if (component instanceof JTable) {
				JTable table = (JTable) component;
				for (int row = 0; row < table.getRowCount(); row++) {
					for (int column = 0; column < table.getColumnCount(); column++) {
						Object value = table.getValueAt(row, column);
						table.requestFocus();
						table.editCellAt(row, column);
						TableCellRenderer renderer = table.getCellRenderer(row,
								column);

						Component component2 = renderer
								.getTableCellRendererComponent(table, value,
										true, true, row, column);
						if (accepter.accept(component2)) {
							return component2;
						}
					}
				}
			} else if (component instanceof Container) {
				Container new_name = (Container) component;
				Component aa = findComponent(new_name, accepter);
				if (aa != null) {
					return aa;
				}
			}
		}
		return null;
	}

}
