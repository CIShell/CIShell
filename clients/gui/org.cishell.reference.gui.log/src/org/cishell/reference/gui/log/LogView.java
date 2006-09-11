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

//standard java
import java.util.HashMap;
import java.util.Map;

//osgi
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

//eclipse
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * @author Weixia Huang (huangb@indiana.edu)
 *         Bruce Herr (bh2@bh2.net)
 */
public class LogView extends ViewPart implements LogListener{
    private static LogReaderService reader;
    
	private static LogView defaultView; 
	private static Composite parent;	
	private static StyledText text;
	
	private static int defaultLevel;
	private static int currentLevel;
	
	private static Map colorMapping;
	private static Color URL_COLOR;
	private static Color LOG_ERROR_COLOR;
	private static Color LOG_WARNING_COLOR;
	//FOR ALGORITHM INFO
	private static Color LOG_INFO_COLOR;
	//FOR ACTIVITY INFO
	private static Color LOG_DEBUG_COLOR;
	   
	private static URLClickedListener urlListener;
    private static URLMouseCursorListener urlCursorListener;
    
    static {
    	Display.getDefault().syncExec(new Runnable(){
        	public void run(){
            	LOG_ERROR_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
            	LOG_WARNING_COLOR = new Color(Display.getDefault(), 255, 127, 0); //orange
            	LOG_INFO_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
                LOG_DEBUG_COLOR = new Color(Display.getDefault(), 150, 150, 150); //gray
                               
                URL_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
            }
        });
    }

    /**
     * Constructor
     */
    public LogView() {
    	defaultView = this;
    	defaultLevel = LogService.LOG_INFO;
    	
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
        colorMapping = new HashMap();
        colorMapping.put(""+LogService.LOG_DEBUG, LOG_DEBUG_COLOR);
        colorMapping.put(""+LogService.LOG_INFO, LOG_INFO_COLOR);
        colorMapping.put(""+LogService.LOG_WARNING, LOG_WARNING_COLOR);
        colorMapping.put(""+LogService.LOG_ERROR, LOG_ERROR_COLOR);

    }

    public static LogView getDefault() {
    	return defaultView;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
   	
    	LogView.parent = parent;
        text = new StyledText(parent,
                SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
        text.setEditable(false);
        text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        text.getCaret().setVisible(false);
        
        //handle url
        urlListener = new URLClickedListener();
        text.addMouseListener(urlListener);
        urlCursorListener = new URLMouseCursorListener();
        text.addMouseMoveListener(urlCursorListener);
        
        //add copy context menu when hover a block of text and right click the mouse
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
 
         //Get LogReaderService through BundleContext
         //Add itself to the LogReaderService as a LogListener
         BundleContext context = Activator.getContext();
         ServiceReference ref = context.getServiceReference(LogReaderService.class.getName());
         LogReaderService reader = (LogReaderService) context.getService(ref);
         if (reader != null) {
        	 reader.addLogListener(this);               
         }
         else
        	 System.out.println("reader is null");
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
    	text.setFocus();
    }
    
    public void logged(final LogEntry entry) {
        
    	PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				String message = entry.getMessage();
				try {
					if (goodMessage(message)) {
                        //not all messages end w/ a new line, but they
                        //need to to print properly
                        message += "\n";
                        
						appendString(message, (Color) colorMapping.get(""+entry.getLevel()));						
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
    
    /*
     * append the given string to the console with the given color,
     * this will do the job of checking for URLs within the string and
     * registering the proper listeners on them as well.
     */
    private void appendString(String message, Color color) {
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


}
