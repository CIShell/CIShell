/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on May 19, 2004 at Indiana University.
 */
package edu.iu.iv.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * A utility class that can dynamically load classes from a set of paths,
 * which may be jar files or folders which contain jars.  The classes can
 * then be scanned to enumerate the subtypes of a given class.
 *
 * @author Josh Bonner
 */
public class JarScanner {

	private static FileFilter jarOrFolderFilter = new FileFilter() {
			public boolean accept(File file) {			    
				boolean passed = file.isDirectory() || file.getName().endsWith(".jar");				
				return passed;
			}
		};

	private ClassLoader classLoader;
	private List classList;
	
	//maps from className (String) to the Path (File) of the jar it was loaded out of.
	private Map classToPathMap;

	/**
	 * Creates a JarScanner with no paths initially set.
	 */
	public JarScanner() {
		// set up an empty class list
		classList = new ArrayList();		
		classToPathMap = new HashMap();
	}

	/**
	 * Creates a JarScanner and uses it to scan the given paths.
	 *
	 * @param jarPaths the paths to scan
	 *
	 * @throws IOException if the scanner encounters an IO problem
	 * while processing one of the given paths
	 */
	public JarScanner(String[] jarPaths) throws IOException {
		setScanPaths(jarPaths);
	}

	/**
	 * Loads the classes from the jar files located at the given set of
	 * paths.  The old class loader and set of loaded classes are discarded.
	 *
	 * @param jarPaths the paths to scan for jar files
	 *
	 * @throws IOException if an IO problem is encountered while processing
	 * one of the given paths
	 */
	public void setScanPaths(String[] jarPaths) throws IOException {
		try {
			List jarFiles = new ArrayList();		
			File path;
			for (int i = 0; i < jarPaths.length; i++){
			    path = new File(jarPaths[i]);
			    if(path.exists()){
			        jarFiles.addAll(getJarsInPath(new File(jarPaths[i])));
			    }
			}
			
			Iterator jarIterator = jarFiles.iterator();
			int index = 0;
			URL[] jarURLs = new URL[jarFiles.size()];
			
			while (jarIterator.hasNext()) {
				jarURLs[index] = ((File) jarIterator.next()).toURL();
				index++;
			}
			
			classLoader = new URLClassLoader(jarURLs,
				    this.getClass().getClassLoader());
			loadClasses(jarFiles);
		}
		 catch (MalformedURLException e) {
			// cannot happen unless the filename was bad,
			// in which case we're throwing a FileNotFoundException anyway
		}
	}

	/**
	 * Returns a list of the classes loaded by this scanner that are
	 * instantiable subtypes of the given supertype.
	 *
	 * @param supertype the class whose subtypes are desired
	 *
	 * @return a list of subtype Classes
	 */
	public List getInstantiableSubtypes(Class supertype) {
		List implementors = new ArrayList();

		Iterator classIterator = classList.iterator();

		while (classIterator.hasNext()) {
			Class candidate = (Class) classIterator.next();
			int modifiers = candidate.getModifiers();

			// interfaces and nonpublic or abstract class are useless
			// since we can't instantiate them
			if (Modifier.isPublic(modifiers) &&
				    !Modifier.isInterface(modifiers) &&
				    !Modifier.isAbstract(modifiers) &&
				    supertype.isAssignableFrom(candidate)) {
				// now figure out if the candidate has a no-arg constructor
				Constructor[] constructors = candidate.getConstructors();

				for (int i = 0; i < constructors.length; i++) {
					if (constructors[i].getParameterTypes().length == 0) {
						implementors.add(candidate);

						break;
					}
				}
			}
		}

		return implementors;
	}

	/**
	 * @param path a path to scan for jar files
	 *
	 * @return a list of jar Files available at the given path
	 * (and any of its subfolders, if it is a folder)
	 */
	private List getJarsInPath(File path) {
		List resultList = new ArrayList();

		if (jarOrFolderFilter.accept(path) && !path.isDirectory()) {
			resultList.add(path);

			return resultList;
		}

		File[] files = path.listFiles(jarOrFolderFilter);

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
			    if(!files[i].getName().equals("lib") && !files[i].getName().startsWith("org.eclipse.")){			           
			        resultList.addAll(getJarsInPath(files[i]));
			    }
			}
			else
			{
				resultList.add(files[i]);
			}
		}

		return resultList;
	}

	/**
	 * Loads the classes from the given list of jars into the class list.
	 *
	 * @param jarFiles a list of jar files to load classes from
	 *
	 * @throws IOException if one of the given Files is not a jar
	 */
	private void loadClasses(List jarFiles) throws IOException {
		classList = new ArrayList();

		Iterator jarIterator = jarFiles.iterator();

		while (jarIterator.hasNext()) {
			File jarFile = (File) jarIterator.next();

			//find the directory where the jar is at
			String path = jarFile.getAbsolutePath(); 
			int lastSlash = path.lastIndexOf(System.getProperty("file.separator"));
			path = path.substring(0,lastSlash);
			
			JarFile jar = new JarFile(jarFile);
			Enumeration entries = jar.entries();

			//states if this jar is valid, only becomes invalid when a class can't be 
			//loaded by the classloader here..
			boolean jarValid = true;
			List potentialClassList = new ArrayList();

			while (jarValid && entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();

				if (isClass(entry.getName())) {
					try {
						Class potentialClass = classLoader.loadClass(
						        toClassName(entry.getName()));
						
						potentialClassList.add(potentialClass);
						
						//associated the class to the path to its jar's directory
						classToPathMap.put(potentialClass,new File(path));
					}
					 catch (ClassNotFoundException e) {
						e.printStackTrace();
						//System.err.println("Error loading class: " +
						//    toClassName(entry.getName()));

						jarValid = false;
					}
					 catch (NoClassDefFoundError e) {
						//System.err.println("Error loading class: " +
						//    toClassName(entry.getName()));
						//System.err.println("Jar: " + jar.getName() +
						//    " is invalid. Dependent library is probably not in the lib directory.");

						//jarValid = false;
					}
				}
			}

			//only add in the classes from the jar if the jar was valid.
			if (jarValid) {
				classList.addAll(potentialClassList);
			}
		}
	}
	
	/**
	 * returns the Class -> Path (File) map generated by the jar scanner
	 * 
	 * @return the class to path map
	 */
	public Map getClassToPathMap() {
		return classToPathMap;
	}

	/**
	 * @param name a resource name from a jar file
	 *
	 * @return true if the name corresponds to a class
	 */
	private static boolean isClass(String name) {
		return name.endsWith(".class");
	}

	/**
	 * @param resourcePath the resource name of a class from a jar file
	 *
	 * @return the fully qualified class name of the given class
	 */
	private static String toClassName(String resourcePath) {
		String className = resourcePath.replace('/', '.');
		className = className.substring(0, className.lastIndexOf(".class"));

		return className;
	}
}
