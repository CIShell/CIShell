package org.cishell.utility.swt;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SWTUtilities {
	public static final String URL_START_TAG = "[url]";
	public static final String URL_END_TAG = "[/url]";

	/** Do not instantiate utilities. */
	protected SWTUtilities() {		
		throw new UnsupportedOperationException();
	}
	
	/**
     * Style text within URL_START_TAG and URL_END_TAG like URLs and make them clickable links.
     */
    public static Collection<StyleRange> urlifyUrls(
    		StyledText textField,
    		URLClickedListener urlListener,
    		URLMouseCursorListener urlCursorListener,
    		String message,
    		Color normalColor,
    		Color urlColor) {
    	int startTagIndex = message.indexOf(URL_START_TAG);
    	int endTagIndex = message.indexOf(URL_END_TAG);
    	
    	boolean urlDetected = startTagIndex >= 0 && endTagIndex >= 0;
        if (urlDetected) {
        	String urlWithTags =
        		message.substring(startTagIndex, endTagIndex + URL_END_TAG.length());
    		String url =
    			urlWithTags.substring(
    					URL_START_TAG.length(),
    					urlWithTags.length() - URL_END_TAG.length());
        	
    		String messageWithFirstUrlDetagged =
    			message.replaceFirst(Pattern.quote(urlWithTags), url);
    		
    		String messageBeforeUrl = messageWithFirstUrlDetagged.substring(0, startTagIndex);
	        StyleRange preURLStyle = syncedStyledPrint(
	        		textField, messageBeforeUrl, normalColor, SWT.NORMAL);
	        urlListener.addURL(textField.getText().length(), url);
	        urlCursorListener.addURL(textField.getText().length(), url);
	        StyleRange urlStyle =
	        	syncedStyledPrint(textField, url, urlColor, SWT.BOLD);
	        String messageBeyondFirstUrl =
	        	messageWithFirstUrlDetagged.substring(startTagIndex + url.length());
	        Collection<StyleRange> postURLStyles = urlifyUrls(
	        	textField,
	        	urlListener,
	        	urlCursorListener,
	        	messageBeyondFirstUrl,
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
	    	StyleRange style = syncedStyledPrint(textField, message, normalColor, SWT.NORMAL);
	
	        if (style != null) {
	        	Collection<StyleRange> finalStyles = new HashSet<StyleRange>();
	        	finalStyles.add(style);
	
	        	return finalStyles;
	        } else {
	        	return new HashSet<StyleRange>();
	        }
	    }
    }

    /**
     * Format the string with a style range and append it to the StyledText control.
     */
    public static StyleRange syncedStyledPrint(
    		final StyledText textField,
    		final String message,
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