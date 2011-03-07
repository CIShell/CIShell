package org.cishell.utility.swt;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** TODO: The URL (http://-prefixed) parsing utilities in this class need to be updated to handle
 * https as well.
 */
public class SWTUtilities {
	public static final Color DEFAULT_BACKGROUND_COLOR =
		new Color(Display.getDefault(), 255, 255, 255);
	/*
     * Append the given string to the console with the given color, this will do the job of
     *  checking for URLs within the string and registering the proper listeners on them as well.
     */
	public static Collection<StyleRange> appendStringWithURL(
    		StyledText textField,
    		URLClickedListener urlListener,
    		URLMouseCursorListener urlCursorListener,
    		String message,
    		Color normalColor,
    		Color urlColor) {
		return appendStringWithURL(
			textField,
			urlListener,
			urlCursorListener,
			message,
			DEFAULT_BACKGROUND_COLOR,
			normalColor,
			urlColor);
	}

    public static Collection<StyleRange> appendStringWithURL(
    		StyledText textField,
    		URLClickedListener urlListener,
    		URLMouseCursorListener urlCursorListener,
    		String message,
    		Color backgroundColor,
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
               

            StyleRange preURLStyle = syncedStyledPrint(
            	textField, message.substring(0, index), backgroundColor, normalColor, SWT.NORMAL);
            urlListener.addURL(textField.getText().length(), url);
            urlCursorListener.addURL(textField.getText().length(), url);
            StyleRange urlStyle =
            	syncedStyledPrint(textField, url, backgroundColor, urlColor, SWT.BOLD);
            Collection<StyleRange> postURLStyles = appendStringWithURL(
            	textField,
            	urlListener,
            	urlCursorListener,
            	message.substring(index + url.length()),
            	backgroundColor,
            	normalColor,
            	urlColor);

            Collection<StyleRange> finalStyles = new HashSet<StyleRange>();

            if (preURLStyle != null) {
            	finalStyles.add(preURLStyle);
            }

            if (urlStyle != null) {
            	finalStyles.add(urlStyle);
            }

            finalStyles.addAll(postURLStyles);

            return finalStyles;
        } else {
            StyleRange style = syncedStyledPrint(
            	textField, message, backgroundColor, normalColor, SWT.NORMAL);

            if (style != null) {
            	Collection<StyleRange> finalStyles = new HashSet<StyleRange>();
            	finalStyles.add(style);

            	return finalStyles;
            } else {
            	return new HashSet<StyleRange>();
            }
        }
    }

	/*
     * Helper to actually format the string with a style range and
     * append it to the StyledText control.
     */

    public static StyleRange syncedStyledPrint(
    		StyledText textField, String message, Color color, int style) {
    	return syncedStyledPrint(textField, message, DEFAULT_BACKGROUND_COLOR, color, style);
    }

    public static StyleRange syncedStyledPrint(
    		final StyledText textField,
    		final String message,
    		final Color backgroundColor,
    		final Color color,
    		final int style) {
    	final StyleRange[] styleRange = new StyleRange[1];
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
		        styleRange[0] = styledPrint(textField, message, color, style);
            }
        });

        return styleRange[0];
    }

    public static StyleRange styledPrint(
    		StyledText textField, String message, Color color, int style) {
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

            return styleRange;
        } else {
        	return null;
        }
    }

    public static StyleRange printURL(
    		Composite parent,
    		StyledText textField,
    		String url,
    		String displayURL,
    		Color color,
    		int style) {
    	URLClickedListener urlClickedListener = new URLClickedListener(textField);
		URLMouseCursorListener urlCursorListener = new URLMouseCursorListener(parent, textField);
		textField.addMouseListener(urlClickedListener);
		textField.addMouseMoveListener(urlCursorListener);

		urlClickedListener.addURL(textField.getText().length(), url, displayURL);
        urlCursorListener.addURL(textField.getText().length(), url, displayURL);

        return styledPrint(textField, displayURL, color, style);
    }
}