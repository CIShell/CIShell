/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Nov 7, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.gui.builder.SwtGUIBuilder;

/**
 * 
 * @author Bruce Herr
 */
public class BuilderTester extends AbstractUIPlugin {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Display display = new Display();
        final Shell shell = new Shell(display, SWT.CLOSE | SWT.RESIZE | SWT.SHELL_TRIM);
        shell.setText("ParameterMap Builder");
      
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        shell.setLayout(gridLayout);
        
        final ParameterMapBuilder builder = new ParameterMapBuilder(shell);
        
        Button button = new Button(shell, SWT.NONE);
        button.setText("Close");
        button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                ParameterMap pmap = builder.getParameterMap();
                
                launch(pmap);
                
                shell.dispose();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }});
        
        shell.pack();
        shell.setVisible(true);
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
    
    private static void launch(ParameterMap pmap) {
        final Shell shell = new Shell();
        Display display = Display.getCurrent();
        
        final SwtGUIBuilder builder = new SwtGUIBuilder(shell, "Input Editor", "");
        builder.addAllToGUI(pmap);
        
        builder.addSelectionListener(new edu.iu.iv.common.guibuilder.SelectionListener() {
            public void widgetSelected() {
                builder.close();
                shell.dispose();
            }});
        
        builder.open();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        
        ParameterMapConverter converter = ParameterMapConverter.getInstance();
        System.out.println(converter.convertToJavaCode(pmap));
        System.out.println(converter.getImportsNeeded(pmap, ""));
    }

}
