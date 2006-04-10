/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Nov 8, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.builder;

import org.eclipse.swt.widgets.TableItem;

/**
 * 
 * @author Bruce Herr
 */
public interface BuilderDelegate {
    
    public String[] createItem(String action);
    
    public void edit(TableItem item);
    
    public String[] getColumns();
}
