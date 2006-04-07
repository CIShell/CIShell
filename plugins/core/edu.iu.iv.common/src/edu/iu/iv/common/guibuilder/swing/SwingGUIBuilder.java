/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 23, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.iu.iv.common.guibuilder.GUIBuilder;
import edu.iu.iv.common.guibuilder.SelectionListener;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * A GUIBuilder that uses Swing to create its GUI
 * 
 * @author Bruce Herr
 */
public class SwingGUIBuilder extends GUIBuilder implements ChangeListener {
    private static final GUIBuilder INSTANCE = new SwingGUIBuilder();

    private JFrame gui;
    private SwingPanelBuilder userArea;
    private JButton okButton;
    
    /**
     * A constructor used only for setting GUIBuilder's default builder.
     */
    private SwingGUIBuilder() { }
    
    /**
     * creates a new blank SwingGUIBuilder given a title and message
     * 
     * @param title the title 
     * @param message the message to appear at the top of the GUI
     */
    public SwingGUIBuilder(String title, String message) {
        gui = new JFrame(title);
        Container content = gui.getContentPane();
        
        content.setLayout(new BorderLayout());
        
        if (message != null && !message.equals("")) {
            JPanel msgPanel = new JPanel();
            msgPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            JLabel msg = new JLabel(message);
            
            msgPanel.add(msg);
            content.add(msgPanel, BorderLayout.NORTH);
        }
                
        userArea = new SwingPanelBuilder();
        userArea.addChangeListener(this);
        
        content.add(userArea, BorderLayout.CENTER);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                close();
            }
        });
        
        okButton = new JButton("Ok");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        
        JPanel innerButtonPanel = new JPanel();
        buttonPanel.add(innerButtonPanel, BorderLayout.EAST);
        
        innerButtonPanel.add(cancelButton);
        innerButtonPanel.add(okButton);
        
        content.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * get the static instance of the SwingGUIBuilder for setting as the 
     * default GUIBuilder
     * 
     * @return the gui builder
     */
    public static GUIBuilder getGUIBuilder() {
        return INSTANCE;
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#createGUIBuilder(java.lang.String, java.lang.String)
     */
    protected GUIBuilder createGUIBuilder(String title, String message) {
        return new SwingGUIBuilder(title, message);
    }

    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#addSelectionListener(edu.iu.iv.common.guibuilder.SelectionListener)
     */
    public void addSelectionListener(SelectionListener listener) {
        okButton.addActionListener(new OkButtonListener(listener));
    }
    
    private class OkButtonListener implements ActionListener {
        private SelectionListener listener;
        
        public OkButtonListener(SelectionListener listener) {
            this.listener = listener;
        }
        
        public void actionPerformed(ActionEvent arg0) {
            listener.widgetSelected();
        }
    }

    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#open()
     */
    public void open() {
        gui.pack();
        gui.show();
    }

    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#close()
     */
    public void close() {
        gui.dispose();
    }

    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#addToGUI(edu.iu.iv.common.parameter.Parameter, edu.iu.iv.common.parameter.InputType)
     */
    protected void addToGUI(Parameter parameter, InputType type) {
        userArea.addToGUI(parameter, type);
    }
    
    /**
     * notifies the main shell that the userArea has changed and
     * then updates the OK Button.
     */
    public void changeOccured() {
        if (userArea.getValidity()) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }
}
