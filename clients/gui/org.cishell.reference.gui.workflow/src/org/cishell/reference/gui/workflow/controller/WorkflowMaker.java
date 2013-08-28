package org.cishell.reference.gui.workflow.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.cishell.reference.gui.workflow.model.AlgorithmWorkflowItem;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.WorkflowItem;
import org.cishell.reference.gui.workflow.views.WorkflowView;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class WorkflowMaker {
	public WorkflowMaker(){
		
	}

	public void save(Workflow wf){		
	  write(wf);
	}
	
	public synchronized void load(){
		new Thread(
				new Runnable(){
					public void run(){

						JFileChooser fileChooser = new JFileChooser();
						File currentDirectory = null;
						fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fileChooser.setCurrentDirectory(currentDirectory);
						fileChooser.showOpenDialog(null);
						File file = fileChooser.getSelectedFile();
						FileReader freader;
						BufferedReader in;
						XStream reader;
						
						if( file != null){
							try {
								freader = new FileReader(file);
								in = new BufferedReader(freader);
								reader = new XStream(new StaxDriver());
								reader.setClassLoader(WorkflowSaver.class.getClassLoader());
								WorkflowSaver saver =(WorkflowSaver)  reader.fromXML(in) ;
								//print all the VALUES
								WorkflowManager mgr =WorkflowManager.getInstance();
								List<Workflow> list = new ArrayList<Workflow>();
							
								 Workflow wf =  saver.getCurrentWorkflow();

								 list.add(wf);
								 for(Map.Entry<Long, WorkflowItem> item:((NormalWorkflow)wf).getMap().entrySet()) {										
										 ((AlgorithmWorkflowItem)item.getValue()).setWorkflow(wf);
								 }
								 Long id = mgr.getUniqueInternalId();
								 wf.setInternalId(id);
								 mgr.addWorkflow(id,wf);								
								 
								 final List<Workflow> wfList = new ArrayList<Workflow>(list);
								 Display.getDefault().asyncExec(new Runnable() {
									    public void run() {
									    	for(Workflow wf :wfList)
									    	{
											 WorkflowView.getDefault().addWorflowtoUI(wf);
									    	}
									    }
									});
								return ;
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		
						}
		
						return;
					}
					
				}
				).start();
	}
	
	
	public void write(final Workflow wf) {
		new Thread(
				new Runnable(){
						public void run() {
		XStream writer = new XStream(new StaxDriver());
		writer.autodetectAnnotations(true);

		//writer.alias("workflowmaker", WorkflowMaker.class );
		String xml = writer.toXML(new WorkflowSaver(wf));
		File currentDirectory = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.showSaveDialog(null);
		File file = fileChooser.getSelectedFile();
		String filePath = file.getPath();
		if(!filePath.toLowerCase().endsWith(".xml"))
		{
		    file = new File(filePath + ".xml");
		}
		
		//File file = new File(state.getFilename());
		if (file != null) {
			try {
				FileWriter fstream;
				fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(xml);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}
	}).start();
}

	
}

