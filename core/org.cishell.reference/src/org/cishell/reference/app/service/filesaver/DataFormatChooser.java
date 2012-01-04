package org.cishell.reference.app.service.filesaver;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.reference.app.service.persistence.AbstractDialog;
import org.cishell.service.conversion.Converter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.ServiceReference;

/* TODO Kill dead methods */
public class DataFormatChooser extends AbstractDialog implements AlgorithmProperty {
//	public static final Image QUESTION_ICON =
//    	Display.getCurrent().getSystemImage(SWT.ICON_QUESTION);

    protected Data data;
    protected Converter[] converters;
    private List converterListComponent;
    private StyledText detailPane;
    private Converter chosenConverter;

    public DataFormatChooser(Data data, Shell parent, Converter[] converters, String title) {
    	super(parent, title, AbstractDialog.QUESTION);
        this.data = data;        
        this.converters = alphabetizeConverters(filterConverters(converters));
    }

    public Converter getChosenConverter() {
    	return this.chosenConverter;
    }

    private Composite initializeGUI(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        content.setLayout(layout);

        Group converterGroup = new Group(content, SWT.NONE);
        converterGroup.setText("Pick the Output Data Type");
        converterGroup.setLayout(new FillLayout());
        GridData persisterData = new GridData(GridData.FILL_BOTH);
        persisterData.widthHint = 200;
        converterGroup.setLayoutData(persisterData);

        converterListComponent =
        	new List(converterGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
        initializeConverterListComponent();
        converterListComponent.addMouseListener(new MouseAdapter() {
        	public void mouseDoubleClick(MouseEvent mouseEvent) {
        		List list = (List)mouseEvent.getSource();
        		int selection = list.getSelectionIndex();
        		
        		if (selection != -1) {
        			selectionMade(selection);
        		}
        	}
        });
        converterListComponent.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent selectionEvent) {
        		List list = (List)selectionEvent.getSource();
        		int selection = list.getSelectionIndex();

        		if (selection != -1) {
        			updateDetailPane(converters[selection]);
        		}
        	}
        });

        Group detailsGroup = new Group(content, SWT.NONE);
        detailsGroup.setText("Details");
        detailsGroup.setLayout(new FillLayout());        
        GridData detailsData = new GridData(GridData.FILL_BOTH);
        detailsData.widthHint = 200;
        detailsGroup.setLayoutData(detailsData);

        detailPane = initializeDetailPane(detailsGroup);

        // Select the first item by default.
        converterListComponent.setSelection(0);
        updateDetailPane(converters[0]);

        return content;
    }

    /**
     * Initialize the Listbox of Persisters using the stored Persister array
     */
    private void initializeConverterListComponent() {
        for (int ii = 0; ii < converters.length; ii++) {
			if (converters[ii] != null) {
				Dictionary converterProperties = converters[ii].getProperties();

				// Get the name of the persister from the property map.
				String outData = null;
                
                ServiceReference[] serviceReferences = converters[ii].getConverterChain();

                if ((serviceReferences != null) && (serviceReferences.length > 0)) {
                    outData = (String)serviceReferences[serviceReferences.length - 1].getProperty(
                    	AlgorithmProperty.LABEL);
                }

                if (outData == null) {
                    outData = (String)converterProperties.get(AlgorithmProperty.LABEL);
                }

				/*
				 * If someone was sloppy enough to not provide a name, then use the name of the
				 *  class instead.
				 */
				if ((outData == null) || (outData.length() == 0)) {
					outData = converters[ii].getClass().getName();
				}

				converterListComponent.add(outData);
			}
		}
    }

    private StyledText initializeDetailPane(Group detailsGroup) {
        StyledText detailPane = new StyledText(detailsGroup, SWT.H_SCROLL | SWT.V_SCROLL);
        detailPane.setEditable(false);
        detailPane.getCaret().setVisible(false);

        return detailPane;
    }

    private void updateDetailPane(Converter converter) {
        Dictionary converterProperties = converter.getProperties();
        Enumeration converterPropertiesKeys = converterProperties.keys();

        detailPane.setText("");

        while (converterPropertiesKeys.hasMoreElements()) {
            Object key = converterPropertiesKeys.nextElement();
            Object value = converterProperties.get(key);
	
            StyleRange styleRange = new StyleRange();
            styleRange.start = detailPane.getText().length();
            detailPane.append(key + ":\n");
            styleRange.length = key.toString().length() + 1;
            styleRange.fontStyle = SWT.BOLD;
            detailPane.setStyleRange(styleRange);
	
            detailPane.append(value + "\n");
        }
    }

    private Converter[] filterConverters(Converter[] allConverters) {
    	Map<String,Converter> lastInDataToConverter = new HashMap<String, Converter>();
    	
    	for (int ii = 0; ii < allConverters.length; ii++) {
    		Converter converter = allConverters[ii];
    		String lastInputData = getLastConverterInData(converter);

    		if (lastInDataToConverter.containsKey(lastInputData)) {
    			Converter alreadyStoredConverter = lastInDataToConverter.get(lastInputData);
    			Converter chosenConverter =
    				returnPreferredConverter(converter, alreadyStoredConverter);
    			lastInDataToConverter.put(lastInputData, chosenConverter);
    		} else {
    			lastInDataToConverter.put(lastInputData, converter);
    		}
    	}
    	
    	return (Converter[]) lastInDataToConverter.values().toArray(new Converter[0]);
    }
    
    private String getLastConverterInData(Converter converter) {
    	ServiceReference[] convChain = converter.getConverterChain();

    	if (convChain.length >= 1) {
    		ServiceReference lastConverter = convChain[convChain.length - 1];
    		String lastInData = (String) lastConverter.getProperty("in_data");

    		return lastInData;
    	} else {
    		return "";
    	}
    }

    private Converter returnPreferredConverter(Converter converter1, Converter converter2) {
    	Dictionary converter1Properties = converter1.getProperties();
    	String converter1Lossiness = (String)converter1Properties.get(CONVERSION);
    	int converter1Quality = determineQuality(converter1Lossiness);

    	Dictionary converter2Properties = converter2.getProperties();
    	String converter2Lossiness = (String)converter2Properties.get(CONVERSION);
    	int converter2Quality = determineQuality(converter2Lossiness);
    	
    	if (converter1Quality > converter2Quality) {
    		return converter1;
    	} else if (converter2Quality > converter1Quality) {
    		return converter2;
    	} else {
    		// They are tied. Look at chosenConverter chain length.
    		
    		int converter1Length = converter1.getConverterChain().length;
    		int converter2Length = converter2.getConverterChain().length;

    		if (converter1Length > converter2Length) {
    			return converter2;
    		} else if (converter2Length > converter1Length) {
    			return converter1;
    		} else {
    			/*
    			 * Both have the same lossiness and same length.
    			 * Arbitrary pick the first.
    			 */
    			return converter1;
    		}
    	}
    }

    private int determineQuality(String lossiness) {
    	if (lossiness == LOSSY) {
    		return 0;
    	} else if (lossiness == null) {
    		return 1;
    	} else { 
    		return 2;
    	}
    }

    private Converter[] alphabetizeConverters(Converter[] converters) {
    	Arrays.sort(converters, new CompareAlphabetically());

    	return converters;
    }

    protected void selectionMade(int selectedIndex) {
    	try {
	        getShell().setVisible(false);
	        this.chosenConverter = converters[selectedIndex];
//	        final SaveAsController saver = new SaveAsController(getShell(), ciShellContext);
	        close(true);
//	        close(saver.save(chosenConverter, data));
    	} catch (Exception exception) {
        	throw new RuntimeException(exception);
        }
    }

    public void createDialogButtons(Composite parent) {
        Button select = new Button(parent, SWT.PUSH);
        select.setText("Select");
        select.addSelectionListener(
        	new SelectionAdapter() {
                public void widgetSelected(SelectionEvent selectionEvent) {
                    int index = converterListComponent.getSelectionIndex();

                    if (index != -1) {
                        selectionMade(index);
                    }
                }
            }
        );
        select.setFocus();

        Button cancel = new Button(parent, SWT.NONE);
        cancel.setText("Cancel");
        cancel.addSelectionListener(
        	new SelectionAdapter() {
                public void widgetSelected(SelectionEvent selectionEvent) {
                    close(false);
                }
            }
        );
    }

    public Composite createContent(Composite parent) {
    	if (converters.length == 1) {
    		close(true);
//            final SaveAsController saver = new SaveAsController((Shell) parent, ciShellContext);
//            close(saver.save(converters[0], data));

            return parent;
    	} else {
    		return initializeGUI(parent);
    	}
    }
    
    private class CompareAlphabetically implements Comparator<Converter> {
    	public int compare(Converter converter1, Converter converter2) {
			String converter1Label = getLabel(converter1);
			String converter2Label = getLabel(converter2);
			
			if ((converter1Label != null) && (converter2Label != null)) {
				return converter1Label.compareTo(converter2Label);
			} else if (converter1Label == null) {
				return 1;
			} else if (converter2Label == null) {
				return -1;
			} else {
				return 0;
			}
    	}

		private String getLabel(Converter converter) {
			String label = "";
            ServiceReference[] serviceReferences = converter.getConverterChain();

            if ((serviceReferences != null) && (serviceReferences.length > 0)) {
                label = (String)serviceReferences[serviceReferences.length - 1].getProperty(
                	AlgorithmProperty.LABEL);
            }
            
            return label;
		}
    }
}
