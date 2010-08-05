package org.cishell.utility.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/*
 * Monitors the mouse and changes the cursor when it is over a URL.
 */
public class URLMouseCursorListener implements MouseMoveListener {
    private Map<Integer, String> offsetsToURLs = new HashMap<Integer, String>();
    private Map<String, String> urlsToDisplayURLs = new HashMap<String, String>();
    private Composite parent;
    private StyledText textField;

    public URLMouseCursorListener(Composite parent, StyledText textField) {
    	this.parent = parent;
    	this.textField = textField;
    }

    public void addURL(int offset, String url) {
        addURL(offset, url, url);
    }

    public void addURL(int offset, String url, String displayURL) {
    	this.offsetsToURLs.put(new Integer(offset), url);
    	this.urlsToDisplayURLs.put(url, displayURL);
    }

    public void mouseMove(MouseEvent event) {
        int urlOffsetOfMousePosition = determineURLOffsetOfMousePosition(event);
        Integer[] urlOffsets = this.offsetsToURLs.keySet().toArray(new Integer[0]);
        boolean mouseIsOverURL = determineIfMouseIsHoveringOverURL(urlOffsetOfMousePosition, urlOffsets);
        Cursor cursor = new Cursor(parent.getDisplay(), determineMouseCursor(mouseIsOverURL));
        textField.setCursor(cursor);
    }

    private int determineURLOffsetOfMousePosition(MouseEvent event) {
        try {
            return textField.getOffsetAtLocation(new Point(event.x, event.y));
        } catch (IllegalArgumentException e) {
            Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);
            textField.setCursor(cursor);
        }

        return -1;
    }

    private boolean determineIfMouseIsHoveringOverURL(
    		int urlOffsetOfMousePosition, Integer[] urlOffsets) {
    	for (Integer urlOffset : urlOffsets) {
            String url = this.offsetsToURLs.get(urlOffset);

            if ((urlOffset != null) &&
            		(url != null) &&
            		(urlOffsetOfMousePosition >= urlOffset.intValue()) &&
                    (urlOffsetOfMousePosition <= (urlOffset.intValue() + url.length()))) {
                return true;
            }
        }

    	return false;
    }

    private int determineMouseCursor(boolean mouseIsOverURL) {
    	if (mouseIsOverURL) {
    		return SWT.CURSOR_HAND;
    	} else {
    		return SWT.CURSOR_ARROW;
    	}
    }
}