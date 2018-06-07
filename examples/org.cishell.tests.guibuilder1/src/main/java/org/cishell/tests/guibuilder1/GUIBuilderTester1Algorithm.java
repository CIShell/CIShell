package org.cishell.tests.guibuilder1;

import java.util.Dictionary;
import java.util.Enumeration;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.service.log.LogService;

public class GUIBuilderTester1Algorithm implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    
    public GUIBuilderTester1Algorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
    }

    public Data[] execute() {
        if (parameters != null) {
            LogService log = (LogService) context.getService(LogService.class.getName());
            log.log(LogService.LOG_INFO, "Parameters Entered:");
            for (Enumeration i = parameters.keys(); i.hasMoreElements(); ) {
                String key = (String) i.nextElement();
                Object value = parameters.get(key);
                
                log.log(LogService.LOG_INFO, key + " -> " + value + " (" + value.getClass().getName() + ")");
            }
            
            GUIBuilderService guiBuilder = (GUIBuilderService)
                context.getService(GUIBuilderService.class.getName());
            
            boolean confirm = guiBuilder.showConfirm("showConfirm()", "showConfirm Test", "showConfirm Details");
            log.log(LogService.LOG_INFO, "Confirmed? " + confirm);
            guiBuilder.showError("showError()", "showError Test", "showError Details");
            
            try {
                Integer.parseInt("Not an integer...");
            } catch (NumberFormatException e) {
                guiBuilder.showError("showError()", "showError w/ Throwable Test", e);
            }
            
            guiBuilder.showInformation("showInformation()", "showInformation Test", "showInformation Details");
            confirm = guiBuilder.showQuestion("showQuestion()", "showQuestion Test", "showQuestion Details");
            log.log(LogService.LOG_INFO, "Yes? " + (confirm ? "Yes" : "No"));
            guiBuilder.showWarning("showWarning()", "showWarning Test", "showWarning Details");
        }
        
        return null;
    }
}