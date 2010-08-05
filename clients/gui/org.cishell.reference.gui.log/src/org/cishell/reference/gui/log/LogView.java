/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 21, 2006 at Indiana University.
 * 
 * Contributors:
 * 	   Weixia(Bonnie) Huang, Bruce Herr
 *     School of Library and Information Science, Indiana University 
 * ***************************************************************************/
package org.cishell.reference.gui.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.cishell.utility.swt.SWTUtilities;
import org.cishell.utility.swt.URLClickedListener;
import org.cishell.utility.swt.URLMouseCursorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

public class LogView extends ViewPart implements LogListener {
	public static final String CONFIGURATION_DIRECTORY = "configuration";
	public static final String WELCOME_TEXT_FILE_NAME = "Welcome.properties";
	public static final String GREETING_PROPERTY = "greeting";

	public static final Color URL_COLOR = getSystemColor(SWT.COLOR_BLUE);
	public static final Color LOG_ERROR_COLOR = getSystemColor(SWT.COLOR_RED);
	public static final Color LOG_WARNING_COLOR = new Color(Display.getDefault(), 255, 127, 0);
	public static final Color LOG_INFO_COLOR = getSystemColor(SWT.COLOR_BLACK);
	public static final Color LOG_DEBUG_COLOR = new Color(Display.getDefault(), 150, 150, 150);

	private static Color getSystemColor(final int swtColor) {
		final Color[] color = new Color[1];

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				color[0] = Display.getDefault().getSystemColor(swtColor);
			}
		});

		return color[0];
	}
	
	public static final Map<String, Color> COLOR_MAPPING = getColorMapping();

	private static Map<String, Color> getColorMapping() {
		Map<String, Color> colorMapping = new HashMap<String, Color>();
        colorMapping.put("" + LogService.LOG_DEBUG, LOG_DEBUG_COLOR);
        colorMapping.put("" + LogService.LOG_INFO, LOG_INFO_COLOR);
        colorMapping.put("" + LogService.LOG_WARNING, LOG_WARNING_COLOR);
        colorMapping.put("" + LogService.LOG_ERROR, LOG_ERROR_COLOR);

        return Collections.unmodifiableMap(colorMapping);
	}

	private Composite parent;	
	private StyledText textField;
	private URLClickedListener urlListener;
    private URLMouseCursorListener urlCursorListener;
    
    public LogView() {
        //TODO: Need to set the log level based on preferences service
 /*       Configuration cfg = IVC.getInstance().getConfiguration();
        boolean showAll = cfg.getBoolean(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE);
        boolean showCritical = cfg.getBoolean(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE);
        if(showAll || showCritical){
            currentLevel = LogService.LOG_DEBUG;
        }
        else{
            currentLevel = LogService.LOG_INFO;
        }
*/
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    // TODO: Refactor this.  Do we even need the member variables?
    @SuppressWarnings("unchecked")
    public void createPartControl(Composite parent) {
    	this.parent = parent;
        this.textField = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
        this.textField.setEditable(false);
        this.textField.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        this.textField.getCaret().setVisible(false);
        
        // Handle URL.
        this.urlListener = new URLClickedListener(textField);
        this.textField.addMouseListener(this.urlListener);
        this.urlCursorListener = new URLMouseCursorListener(this.parent, this.textField);
        this.textField.addMouseMoveListener(this.urlCursorListener);
        
        // Add copy context menu when hover a block of textField and right click the mouse.
        Display display = Display.getDefault();
        final Clipboard clipboard = new Clipboard(display);
        final Menu menu = new Menu(textField);
        menu.setVisible(false);

        MenuItem actionItem = new MenuItem(menu, SWT.PUSH);
        actionItem.setText("Copy");
        actionItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                String textData = LogView.this.textField.getSelectionText();
                TextTransfer textTransfer = TextTransfer.getInstance();
                clipboard.setContents(new Object[] { textData }, new Transfer[] { textTransfer });
            }
        });

         textField.addSelectionListener(new SelectionAdapter() {
        	 public void widgetSelected(SelectionEvent event) {
        		 String selection = ((StyledText) event.widget).getSelectionText();

                 if (selection.equals("")) {
                	 textField.setMenu(null);
                 } else {
                	 textField.setMenu(menu);
                 }
        	 }
          });
 
         // Get LogReaderService through BundleContext.
         // Add itself to the LogReaderService as a LogListener.
         BundleContext context = Activator.getContext();
         ServiceReference logReaderServiceReference =
         	context.getServiceReference(LogReaderService.class.getName());
         LogReaderService logReaderService =
         	(LogReaderService) context.getService(logReaderServiceReference);
         
         if (logReaderService != null) {
        	 logReaderService.addLogListener(this);   
        	 
        	 Enumeration backLogEntries = logReaderService.getLog();
        	 
        	 while (backLogEntries.hasMoreElements()) {
        	 	LogEntry logEntry = (LogEntry)backLogEntries.nextElement();
        	 	this.logged(logEntry);
        	 }
         }
         else {
        	 System.out.println("reader is null");
         }
         
         ServiceReference logServiceReference =
         	context.getServiceReference(LogService.class.getName());
         LogService logService = (LogService) context.getService(logServiceReference);
         
         if (logService != null) {
         	try {
         		URL welcomeTextFileURL = new URL(new URL(
         			System.getProperty("osgi.configuration.area")), WELCOME_TEXT_FILE_NAME);
         		Properties properties = new Properties();
         		properties.load(welcomeTextFileURL.openStream());
         		String greetingText = properties.getProperty(GREETING_PROPERTY, null);
         		logService.log(LogService.LOG_INFO, greetingText);
         	} catch (IOException e) {
         		System.err.println("Error reading Welcome properties file: " + e.getMessage());
         	}
         } else {
         	try {
         		FileWriter fstream = new FileWriter("WelcomeTextError.txt", true);
         		BufferedWriter out = new BufferedWriter(fstream);
         		out.write("The Log Service cannot be found.\r\n");
         		out.close();
         	} catch (Exception e) {
         		System.err.println("Error writing to file: " + e.getMessage());
         	}
         }
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
    	textField.setFocus();
    }
    
    public void logged(final LogEntry entry) {
    	PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					String message = entry.getMessage();
					if (goodMessage(message)) {
                        // Not all messages end w/ a new line, but they need to to print properly.
						if (!message.endsWith("\n")) {
							message += "\n";
						}
                        
						SWTUtilities.appendStringWithURL(
							LogView.this.textField,
							LogView.this.urlListener,
							LogView.this.urlCursorListener,
							message,
							COLOR_MAPPING.get("" + entry.getLevel()),
							URL_COLOR);						
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
    	});
    }
    
    private boolean goodMessage(String msg) {
        if (msg == null || 
                msg.startsWith("ServiceEvent ") || 
                msg.startsWith("BundleEvent ") || 
                msg.startsWith("FrameworkEvent ")) {
            return false;
        } else {
            return true;   
        }
    }
}
