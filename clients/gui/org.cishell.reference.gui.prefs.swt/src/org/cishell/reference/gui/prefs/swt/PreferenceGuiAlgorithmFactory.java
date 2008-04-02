package org.cishell.reference.gui.prefs.swt;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.reference.prefs.admin.PrefAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;


public class PreferenceGuiAlgorithmFactory implements AlgorithmFactory {
	
	private LogService log;
	private MetaTypeService mts;
	private PrefAdmin prefAdmin;
	
    protected void activate(ComponentContext ctxt) {
    	this.log = (LogService) ctxt.locateService("LOG");
        this.mts = (MetaTypeService)ctxt.locateService("MTS");
        this.prefAdmin = (PrefAdmin) ctxt.locateService("PREF_ADMIN");
    }
    
    protected void deactivate(ComponentContext ctxt) {
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new PreferenceGuiAlgorithm(data, parameters, context, prefAdmin, this.log);
    }
    
    public MetaTypeProvider createParameters(Data[] data) {
    	return null;
    }
}