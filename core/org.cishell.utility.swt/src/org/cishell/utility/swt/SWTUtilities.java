package org.cishell.utility.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SWTUtilities {
	/*
     * Append the given string to the console with the given color, this will do the job of
     *  checking for URLs within the string and registering the proper listeners on them as well.
     */
    public static void appendStringWithURL(
    		StyledText textField,
    		URLClickedListener urlListener,
    		URLMouseCursorListener urlCursorListener,
    		String message,
    		Color normalColor,
    		Color urlColor) {
    
    	//find a URL in the message
    
        int index = message.indexOf("http://");
        if (index == -1) {
            index = message.indexOf("https://");
        }
        if (index == -1) {
            index = message.indexOf("www.");
        }

        if (index > -1) {
            String url = message.substring(index);
            if (url.indexOf(") ") > -1) {
                url = url.substring(0, url.indexOf(") "));
            }
            else if (url.indexOf(" ") > -1) {
                url = url.substring(0, url.indexOf(" "));
                if (url.trim().endsWith(".") ){
                	url=url.substring(0, url.length()-1);
                }
            }
            if (url.endsWith(".\n") || url.endsWith(".\t")){
            	url=url.substring(0, url.length()-2);
            }
            if (url.indexOf("\n") > -1) {
                url = url.substring(0, url.indexOf("\n"));
            }
            if (url.indexOf("\t") > -1) {
                url = url.substring(0, url.indexOf("\n"));
            }
               

            syncedStyledPrint(textField, message.substring(0, index), normalColor, SWT.NORMAL);
            urlListener.addURL(textField.getText().length(), url);
            urlCursorListener.addURL(textField.getText().length(), url);
            syncedStyledPrint(textField, url, urlColor, SWT.BOLD);
            appendStringWithURL(
            	textField,
            	urlListener,
            	urlCursorListener,
            	message.substring(index + url.length()),
            	normalColor,urlColor);
        } else {
            syncedStyledPrint(textField, message, normalColor, SWT.NORMAL);
        }
    }

	/*
     * Helper to actually format the string with a style range and
     * append it to the StyledText control.
     */

    public static void syncedStyledPrint(
    		final StyledText textField, final String message, final Color color, final int style) {        
        Display.getDefault().syncExec(new Runnable() {
            public void run(){
		        styledPrint(textField, message, color, style);
            }
        });
    }

    public static void styledPrint(StyledText textField, String message, Color color, int style) {
    	if (!textField.isDisposed()) {
            textField.append(message);

            StyleRange styleRange = new StyleRange();
            styleRange.start = textField.getText().length() - message.length();
            styleRange.length = message.length();
            styleRange.foreground = color;
            styleRange.fontStyle = style;
            textField.setStyleRange(styleRange);

            // This makes it autoscroll.
            textField.setTopIndex(textField.getLineCount());
        }
    }

    public static void printURL(
    		Composite parent,
    		StyledText textField,
    		String url,
    		String displayURL,
    		Color color,
    		int style) {
    	URLClickedListener urlClickedListener = new URLClickedListener(textField);
		URLMouseCursorListener urlCursorListener =
			new URLMouseCursorListener(parent, textField);
		textField.addMouseListener(urlClickedListener);
		textField.addMouseMoveListener(urlCursorListener);

		urlClickedListener.addURL(
        	textField.getText().length(), url, displayURL);
        urlCursorListener.addURL(
        	textField.getText().length(), url, displayURL);
        SWTUtilities.styledPrint(
        	textField,
        	displayURL,
        	color,
        	style);
    }
}