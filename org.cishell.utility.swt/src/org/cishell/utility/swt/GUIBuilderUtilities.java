package org.cishell.utility.swt;

import org.cishell.utility.datastructure.ObjectContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class GUIBuilderUtilities {
	public static Display createDisplay() {
		return new Display();
	}

	public static Shell createShell(
			Display display,
			String windowTitle,
			int windowWidth,
			int windowHeight,
			int columnCount,
			boolean clearSpacing) {
		Shell shell = new Shell(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shell.setText(windowTitle);
    	shell.setSize(windowWidth, windowHeight);
    	shell.setLayout(createShellLayout(columnCount, clearSpacing));

    	return shell;
	}

	public static GridLayout createShellLayout(int columnCount, boolean clearSpacing) {
		GridLayout layout = new GridLayout(columnCount, true);

		if (clearSpacing) {
			clearSpacing(layout);
		}

		return layout;
	}

	public static void openShell(
			Shell shell, int windowHeight, boolean useWindowHeightToSizeShell) {
//		if (useWindowHeightToSizeShell) {
//			/* (So far, we've created the shell at the maximum possible size we'll allow
//			 *  (according to windowHeight).  This line shrinks the shell to be a more fitting size
//			 *  if the actual contents (i.e. our (number of) columns) are smaller than the maximum
//			 *  size we set.)
//    	 	 */
//    		Point shellSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//    		shell.setMinimumSize(shellSize.x, Math.min(windowHeight, shellSize.y));
//		}

		shell.pack();
		shell.open();

		if (useWindowHeightToSizeShell) {
			Point shellSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    		shell.setSize(shell.getSize().x, Math.min(windowHeight, shellSize.y));
		}
	}

	public static void swtLoop(Display display, Shell shell) {
		while (!shell.isDisposed()) {
    		if (!display.readAndDispatch()) {
    			display.sleep();
    		}
    	}

    	display.dispose();
	}

	public static void clearMargins(GridLayout layout) {
		layout.marginTop = layout.marginBottom = layout.marginHeight = 0;
		layout.marginLeft = layout.marginRight = layout.marginWidth = 0;
	}

	public static void clearSpacing(GridLayout layout) {
		layout.horizontalSpacing = layout.verticalSpacing = 0;
	}

	public static void setCancelable(
			final Shell shell, final ObjectContainer<GUICanceledException> exceptionThrown) {
		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					shell.close();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;

//					if (exceptionThrown != null) {
//						String exceptionMessage = "Canceled by user.";
//						exceptionThrown.object = new GUICanceledException(exceptionMessage);
//					}

					break;
				}
			}
		});
		shell.addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent event) {
			}

			public void shellClosed(ShellEvent event) {
				if (exceptionThrown != null) {
					String exceptionMessage = "Canceled by user.";
					exceptionThrown.object = new GUICanceledException(exceptionMessage);
				}
			}

			public void shellDeactivated(ShellEvent event) {
			}

			public void shellDeiconified(ShellEvent event) {
			}

			public void shellIconified(ShellEvent event) {
			}
		});
	}
}