package edu.iu.iv.gui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
	
public class IVCPerspective implements IPerspectiveFactory {
    public static final String ID_PERSPECTIVE = "edu.iu.iv.gui.IVCPerspective";     
    
    public IVCPerspective() {
    }  
    
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);        
    }
    
    
}