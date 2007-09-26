package org.cishell.reference.gui.persistence.load;

//standard java
import java.util.Dictionary;

//osgi
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;

//cishell
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;

/* 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 */
public class FileLoadFactory implements AlgorithmFactory {
    private BundleContext bcontext;

    protected void activate(ComponentContext ctxt) {
        bcontext = ctxt.getBundleContext();
    }
    protected void deactivate(ComponentContext ctxt) {}

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new FileLoad(context, bcontext);
    }
    public MetaTypeProvider createParameters(Data[] data) {
        return null;
    }
    
}