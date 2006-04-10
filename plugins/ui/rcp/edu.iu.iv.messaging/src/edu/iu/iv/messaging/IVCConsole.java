package edu.iu.iv.messaging;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.part.ViewPart;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.messaging.ConsoleHandler;
import edu.iu.iv.core.messaging.ConsoleLevel;


/**
 * IVCConsole controls the console window that provides information to the user.  
 * It can be print to in various ways, using the static methods in this class.
 *
 * @author Team IVC
 */
public class IVCConsole extends ViewPart implements ConsoleHandler, IStartup {
    public static final String ID_VIEW = "edu.iu.iv.messaging.IVCConsole";
    
    private static IVCConsole defaultView;
    
    private static final String WELCOME_TEXT =
        "Welcome to the Information Visualization Cyberinfrastructure!\n\n" +
        "Please acknowledge this effort by citing:\n" +
        "Information Visualization CyberInfrastructure, Information Visualization Lab at " +
        "Indiana University, http://iv.slis.indiana.edu\n\n";  
    
    private static Color IVC_ACTIVITY_INFORMATION_COLOR;
    private static Color ALGORITHM_INFORMATION_COLOR;
    private static Color IVC_WARNING_COLOR;
    private static Color IVC_ERROR_COLOR;
    private static Color URL_COLOR;
    
    static {
        Display.getDefault().syncExec(new Runnable(){
            public void run(){
                IVC_ACTIVITY_INFORMATION_COLOR = new Color(Display.getDefault(), 150, 150, 150); //gray
                ALGORITHM_INFORMATION_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
                IVC_WARNING_COLOR = new Color(Display.getDefault(), 255, 127, 0); //orange
                IVC_ERROR_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
                URL_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
            }
        });
    }
    
    
    private static StyledText text;
    private static Composite parent;
    private static URLClickedListener urlListener;
    private static URLMouseCursorListener urlCursorListener;
    private static ConsoleLevel defaultLevel;
    private static ConsoleLevel currentLevel;
    private static Map colorMapping;    

    /**
     * Creates a new IVCConsole object.
     */
    public IVCConsole() {
        defaultView = this;
        defaultLevel = ConsoleLevel.ALGORITHM_INFORMATION;
        
        //set the defaults based on preferences
        Configuration cfg = IVC.getInstance().getConfiguration();
        boolean showAll = cfg.getBoolean(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE);
        boolean showCritical = cfg.getBoolean(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE);
        if(showAll || showCritical){
            currentLevel = ConsoleLevel.SYSTEM_ERROR;
        }
        else{
            currentLevel = ConsoleLevel.ALGORITHM_INFORMATION;
        }

        colorMapping = new HashMap();
        colorMapping.put(ConsoleLevel.USER_ACTIVITY, IVC_ACTIVITY_INFORMATION_COLOR);
        colorMapping.put(ConsoleLevel.SYSTEM_INFORMATION, IVC_ACTIVITY_INFORMATION_COLOR);
        colorMapping.put(ConsoleLevel.ALGORITHM_INFORMATION, ALGORITHM_INFORMATION_COLOR);
        colorMapping.put(ConsoleLevel.SYSTEM_WARNING, IVC_WARNING_COLOR);
        colorMapping.put(ConsoleLevel.SYSTEM_ERROR, IVC_ERROR_COLOR);
    }
    
    public static IVCConsole getDefault(){     
        return defaultView;
    }
    
    /**
     * Asks this view part to take focus within the workbench.
     */
    public void setFocus() {
        text.setFocus();
    }

    /**
     * Creates the GUI controls for this View. For IVCConsole this consists
     * of creating the StyledText control for the console.
     *
     * @param parent the parent of this View
     */
    public void createPartControl(Composite parent) {
        IVCConsole.parent = parent;
        text = new StyledText(parent,
                SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
        text.setEditable(false);
        text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        text.getCaret().setVisible(false);
        urlListener = new URLClickedListener();
        text.addMouseListener(urlListener);
        urlCursorListener = new URLMouseCursorListener();
        text.addMouseMoveListener(urlCursorListener);
        
        
        //add copy context menu
        Display display = Display.getDefault();
        final Clipboard cb = new Clipboard(display);
        final Menu menu = new Menu(text);
        menu.setVisible(false);

        MenuItem actionItem = new MenuItem(menu, SWT.PUSH);
        actionItem.setText("Copy");
        actionItem.addListener(SWT.Selection,
            new Listener() {
                public void handleEvent(Event event) {
                    String textData = text.getSelectionText();
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    cb.setContents(new Object[] { textData },
                        new Transfer[] { textTransfer });
                }
            });

        text.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    String selection = ((StyledText) e.widget).getSelectionText();

                    if (selection.equals("")) {
                        text.setMenu(null);
                    } else {
                        text.setMenu(menu);
                    }
                }
            });

        print(WELCOME_TEXT, ConsoleLevel.SYSTEM_INFORMATION);
    }
        

    /**
     * Sets the default ConsoleLevel for the IVCConsole. This determines what
     * ConsoleLevel is used when none is specified. By Default this is
     * ALGORITHM_INFORMATION.
     *
     * @param level the ConsoleLevel to use when none is specified.
     */
    public void setDefault(ConsoleLevel level) {
        defaultLevel = level;
    }

    /**
     * Returns the default level of this IVCConsole. This is the level
     * which is used for printing when no level is specified.  By default this
     * is ALGORITHM_INFORMATION.
     * 
     * @return the default level of this IVCConsole
     */
    public ConsoleLevel getDefaultLevel(){
        return defaultLevel;
    }
    
    /**
     * Sets the maximum ConsoleLevel for the IVCConsole. This determines whether
     * a given message will be allowed to be displayed on the console.  By default,
     * this is ALGORITHM_INFORMATION, meaning things like SYSTEM_WARNING and 
     * SYSTEM_ERROR do not show up on the console.
     *
     * @param level the ConsoleLevel of messages to allow to be displayed.
     */
    public void setMaximumLevel(ConsoleLevel level) {
        currentLevel = level;
    }
    
    /**
     * Returns the maximum ConsoleLevel for the IVCConsole.  This is the
     * highest level at which a message will be allowed to be displayed
     * on the console.
     * 
     * @return the maximum ConsoleLevel for the IVCConsole
     */
    public ConsoleLevel getMaximumLevel(){
        return currentLevel;
    }

    /**
     * Prints the given message to the IVCConsole at the default level
     * (ALGORITHM_INFORMATION by default).
     * 
     * @param message the message to print at the default level.
     */
    public void print(String message) {
        appendString(message, (Color) colorMapping.get(defaultLevel));            
    }

    /**
     * Prints the given message at the given ConsoleLevel, if that level
     * is not of greater priority than the maximum level of the IVCConsole. For
     * example, if the IVCConsole's maximum level is ALGORITHM_INFORMATION, then
     * SYSTEM_ERROR messages will not be displayed.
     *
     * @param message the message to display in the IVCConsole
     * @param level the level of the given message
     */
    public void print(String message, ConsoleLevel level) {
        appendString(message, (Color) colorMapping.get(level));
    }

    /**
     * Prints the given message at the USER_ACTIVITY ConsoleLevel.
     *
     * @param message the message to print at the USER_ACTIVITY ConsoleLevel.
     */
    public void printUserActivity(String message) {
        appendString(message, IVC_ACTIVITY_INFORMATION_COLOR);
    }

    /**
     * Prints the given message at the SYSTEM_INFORMATION ConsoleLevel.
     *
     * @param message the message to print at the SYSTEM_INFORMATION ConsoleLevel.
     */
    public void printSystemInformation(String message) {
        appendString(message, IVC_ACTIVITY_INFORMATION_COLOR);        
    }

    /**
     * Prints the given message at the SYSTEM_WARNING ConsoleLevel.
     *
     * @param message the message to print at the SYSTEM_WARNING ConsoleLevel.
     */
    public void printSystemWarning(String message) {
        appendString(message, IVC_WARNING_COLOR);
    }

    /**
     * Prints the given message at the SYSTEM_ERROR ConsoleLevel.
     *
     * @param message the message to print at the SYSTEM_ERROR ConsoleLevel.
     */
    public void printSystemError(String message) {
        appendString(message, IVC_ERROR_COLOR);
       
    }

    /**
     * Prints the given message at the ALGORITHM_INFORMATION ConsoleLevel.
     *
     * @param message the message to print at the ALGORITHM_INFORMATION ConsoleLevel.
     */
    public void printAlgorithmInformation(String message) {
        appendString(message, ALGORITHM_INFORMATION_COLOR);
    }


    /*
     * append the given string to the console with the given color,
     * this will do the job of checking for URLs within the string and
     * registering the proper listeners on them as well.
     */
    private static void appendString(String message, Color color) {
        int index = message.indexOf("http://");

        if (index == -1) {
            index = message.indexOf("www.");
        }

        if (index > -1) {
            String url = message.substring(index);

            if (url.indexOf(" ") > -1) {
                url = url.substring(0, url.indexOf(" "));
            }

            if (url.indexOf("\n") > -1) {
                url = url.substring(0, url.indexOf("\n"));
            }

            if (url.indexOf("\t") > -1) {
                url = url.substring(0, url.indexOf("\n"));
            }

            printHelper(message.substring(0, index), color, SWT.NORMAL);
            urlListener.addUrl(text.getText().length(), url);
            urlCursorListener.addUrl(text.getText().length(), url);
            printHelper(url, URL_COLOR, SWT.BOLD);
            appendString(message.substring(index + url.length()), color);
        } else {
            printHelper(message, color, SWT.NORMAL);
        }
    }

    /*
     * helper to actually format the string with a style range and
     * append it to the StyledText control
     */
    private static void printHelper(final String inText, final Color color, final int style) {        
        Display.getDefault().syncExec(new Runnable(){
            public void run(){
		        if (!text.isDisposed()) {
		            text.append(inText);
		
		            StyleRange sr = new StyleRange();
		            sr.start = text.getText().length() - inText.length();
		            sr.length = inText.length();
		            sr.foreground = color;
		            sr.fontStyle = style;
		            text.setStyleRange(sr);
		
		            //autoscroll
		            text.setTopIndex(text.getLineCount());
		        }
            }
        });
    }

    /*
     * class that monitors the mouse and changes the cursor when it is
     * over a URL
     */
    private class URLMouseCursorListener implements MouseMoveListener {
        Map offsetToUrlMap = new HashMap();

        public void addUrl(int offset, String url) {
            offsetToUrlMap.put(new Integer(offset), url);
        }

        public void mouseMove(MouseEvent e) {
            int position = -1;

            try {
                position = text.getOffsetAtLocation(new Point(e.x, e.y));
            } catch (IllegalArgumentException ex) {
                Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);
                text.setCursor(cursor);
            }

            if (position < 0) {
                return;
            }

            Integer[] offsets = new Integer[1];
            offsets = (Integer[]) offsetToUrlMap.keySet().toArray(offsets);

            boolean overURL = false;

            for (int i = 0; i < offsets.length; i++) {
                Integer offset = offsets[i];
                String url = (String) offsetToUrlMap.get(offset);

                if ((position >= offset.intValue()) &&
                        (position <= (offset.intValue() + url.length()))) {
                    overURL = true;

                    break;
                }
            }
            
            if (overURL) {
                Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
                text.setCursor(cursor);
            } else {
                Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);
                text.setCursor(cursor);
            }
        }
    }

    /*
     * class that listens for clicks on urls and launches a browser appropriatly
     */
    private class URLClickedListener extends MouseAdapter {
        Map offsetToUrlMap = new HashMap();

        public void addUrl(int offset, String url) {           
            offsetToUrlMap.put(new Integer(offset), url);
        }

        public void mouseDown(MouseEvent e) {
            if (e.button != 1) {
                return;
            }

            int clicked = -1;

            try {
                clicked = text.getOffsetAtLocation(new Point(e.x, e.y));
            } catch (IllegalArgumentException ex) {
            }

            if (clicked < 0) {
                return;
            }

            Integer[] offsets = new Integer[1];
            offsets = (Integer[]) offsetToUrlMap.keySet().toArray(offsets);

            for (int i = 0; i < offsets.length; i++) {
                Integer offset = offsets[i];
                String url = (String) offsetToUrlMap.get(offset);

                if ((clicked >= offset.intValue()) &&
                        (clicked <= (offset.intValue() + url.length()))) {
                    try {
                        Program.launch(url);
                    } catch (Exception e1) {
                    }
                }
            }
        }
    }

    public void earlyStartup() {
		IVC.getInstance().getConsole().add(IVCConsole.getDefault());
    }

}
