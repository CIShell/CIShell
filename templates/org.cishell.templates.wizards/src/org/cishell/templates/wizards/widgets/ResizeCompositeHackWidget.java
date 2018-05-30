package org.cishell.templates.wizards.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/*
 * This "widget" ensures that both it and its parent component get resized
 *  appropriate when its size is set.
 * It handles the case where its parent component is a ScrolledComposite, so
 *  scrolling happens properly.
 */
public class ResizeCompositeHackWidget extends Composite {
	public ResizeCompositeHackWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		Composite parent = getParent();
		
		if (parent != null) {
			/*
			 * TODO: This is totally a hack.  Figure out how to trigger resize
			 * events manually!
			 */
			if (parent instanceof ScrolledComposite) {
				ScrolledComposite scrolledParent = (ScrolledComposite)parent;
				scrolledParent.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			} else {
				parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		}
	}
	
	public void setSize(Point size) {
		setSize(size.x, size.y);
	}
}