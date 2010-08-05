package org.cishell.utility.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;

/*
 * Listens for clicks on urls and launches a browser.
 */
public class URLClickedListener extends MouseAdapter {
    private Map<Integer, String> offsetsToURLs = new HashMap<Integer, String>();
    private Map<String, String> urlsToDisplayURLs = new HashMap<String, String>();
    private StyledText textField;

    public URLClickedListener(StyledText textField) {
    	super();
    	this.textField = textField;
    }

    public void addURL(int offset, String url) {           
        addURL(offset, url, url);
    }

    public void addURL(int offset, String url, String displayURL) {
    	this.offsetsToURLs.put(offset, url);
    	this.urlsToDisplayURLs.put(url, displayURL);
    }

    public void mouseDown(MouseEvent event) {
        if (event.button != 1) {
            return;
        }

        int clickedPosition = determineClickedPosition(event);
		
        if (clickedPosition < 0) {
            return;
        }

        for (Integer offset : this.offsetsToURLs.keySet().toArray(new Integer[0])) {
        	String url = this.offsetsToURLs.get(offset);
            String displayURL = this.urlsToDisplayURLs.get(url);

            if ((displayURL != null) &&
            		(clickedPosition >= offset.intValue()) &&
                    (clickedPosition <= (offset.intValue() + displayURL.length()))) {
                try {
                    Program.launch(url);
                } catch (Exception e) {
                }
            }
        }
    }

    private int determineClickedPosition(MouseEvent event) {
    	int clickedPosition = -1;

        try {
            clickedPosition = this.textField.getOffsetAtLocation(new Point(event.x, event.y));
        } catch (IllegalArgumentException ex) {
        }

        return clickedPosition;
    }
}