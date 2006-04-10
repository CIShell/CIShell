/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 12, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;

/**
 * 
 * @author Bruce Herr
 */
public interface MenuSetupPageValues {
    public static final String KEY_ACTION_LABEL = "actionLabel";
    public static final String KEY_MENUBAR_PATH = "menubarPath";
    public static final String KEY_TOOLTIP = "tooltip";

    public static final String ACTION_LABEL_LABEL = "Plug-in Menu Item Label:";
    public static final String MENUBAR_PATH_LABEL = "Menubar Path:\n(i.e. visualization/additions)";
    public static final String TOOLTIP_LABEL = "Menu Item Tooltip:";   
   
    public static final String MENU_PAGE_DESCRIPTION = "New Plug-in's Menu Item Settings.  The " +
                "Menubar Path should be seperated by '/' characters, " +
                "specifying the path to the new Menu Item in the IVC Menubar.  " +
                "This is typically the menu name, then any group name " +
                "that is being added to.";
}
