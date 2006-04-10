/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Nov 7, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;

/**
 * 
 * @author Bruce Herr
 */
public class ParameterMapBuilder {
    private PallettedListBuilder builder;
    private ParameterMapBuilderDelegate delegate;

    public ParameterMapBuilder(Composite parent) {
        this(parent, SWT.NONE);
    }
    
    public ParameterMapBuilder(Composite parent, int style) {
        delegate = new ParameterMapBuilderDelegate();
        builder = new PallettedListBuilder(parent, style, delegate);
    }
    
    public Composite getComposite() {
        return builder.getComposite();
    }
    
    public ParameterMap getParameterMap() {
        ParameterMap pmap = new ParameterMap();
        
        String[][] itemTable = builder.getItemTable();
        
        for (int i=0; i < itemTable.length; i++) {
            Parameter param = delegate.getParameter(itemTable[i][0]);
            
            if (param != null) {
                pmap.put(itemTable[i][0], param);
            }
        }
        
        return pmap;
    }
}
