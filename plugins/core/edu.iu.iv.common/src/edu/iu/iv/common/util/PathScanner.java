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
 * which may be jar files or folders which contain jars, or .class files
 * or folders which contain .class files.  The classes can
 * then be scanned to enumerate the subtypes of a given class.
 *
 * @author Josh Bonner
 */
public class PathScanner {

	private static FileFilter jarOrFolderFilter = new FileFilter() {
			public boolean accept(File file) {			    
				boolean passed = file.isDirectory() || file.getName().endsWith(".jar");				
				return passed;
			}
	};
	
	private static FileFilter classOrFolderFilter = new FileFilter() {
		public boolean accept(File file) {			    
			boolean passed = file.isDirectory() || file.getName().endsWith(".class");				
			return passed;
		}
	};

	private ClassLoader classLoader;
	private ClassLoader parentClassLoader;
	private List classList;
	
	//maps from className (String) to the Path (File) of the jar 
	//or .class file it was loaded out of.
	private Map classToPathMap;

	/**
	 * Creates a JarScanner with no paths initially set.
	 */
	public PathScanner() {
		// set up an empty class list
		classList = new ArrayList();		
		classToPathMap = new HashMap();
	}

	/**
	 * Creates a PathScanner and uses it to scan the given paths.
	 *
	 * @param paths the paths to scan
	 *
	 * @throws IOException if the scanner encounters an IO problem
	 * while processing one of the given paths
	 */
	public PathScanner(String[] paths) throws IOException {
		setScanPaths(paths);
	}
	
	public void setParentClassLoader(ClassLoader parentClassLoader){
	    this.parentClassLoader = parentClassLoader;
	}

	/**
	 * Loads the classes from the jar files or .class files 
	 * located at the given set of paths.  The old class loader 
	 * and set of loaded classes are discarded.
	 *
	 * @param paths the paths to scan for jar files and .class files
	 *
	 * @throws IOException if an IO problem is encountered while processing
	 * one of the given paths
	 */
	public void setScanPaths(String[] paths) throws IOException {
		try {
			List jarFiles = new ArrayList();
			List classFiles = new ArrayList();
			classList = new ArrayList();
									
			if (parentClassLoader == null){
			    parentClassLoader = this.getClass().getClassLoader();
			}
			
			File path;
			classLoader = parentClassLoader;
			//look for regular classes
			for (int i = 0; i < paths.length; i++){
			    path = new File(paths[i]);
			    if(path.exists()){
			        classFiles.clear();
			        classFiles.addAll(getClassesInPath(new File(paths[i])));
			        loadClasses(classFiles, paths[i]);
			    }
			}			
			
			//scan jars
			for (int i = 0; i < paths.length; i++){
			    path = new File(paths[i]);
			    if(path.exists()){
			        jarFiles.addAll(getJarsInPath(new File(paths[i])));
			    }
			}		
					
			Iterator jarIterator = jarFiles.iterator();
			int index = 0;
			URL[] jarURLs = new URL[jarFiles.size()];
			
			while (jarIterator.hasNext()) {
				jarURLs[index] = ((File) jarIterator.next()).toURL();
				index++;
			}			
			
			classLoader = new URLClassLoader(jarURLs, parentClassLoader);
			loadJarClasses(jarFiles);
					
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
			    resultList.addAll(getJarsInPath(files[i]));
			}
			else
			{
				resultList.add(files[i]);
			}
		}

		return resultList;
	}

	/*
	 * finds all of the .class files in the path
	 */
	private List getClassesInPath(File path) {
		List resultList = new ArrayList();

		if (classOrFolderFilter.accept(path) && !path.isDirectory()) {
			resultList.add(path);
			return resultList;
		}

		File[] files = path.listFiles(classOrFolderFilter);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {			    			           
			    resultList.addAll(getClassesInPath(files[i]));
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
	private void loadJarClasses(List jarFiles) throws IOException {
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
						Class potentialClass = classLoader.loadClass(toClassName(entry.getName()));
						
						potentialClassList.add(potentialClass);
						
						//associated the class to the path to its jar's directory
						classToPathMap.put(potentialClass,new File(path));
					}
					 catch (ClassNotFoundException e) {
						e.printStackTrace();
						jarValid = false;
					}
					 catch (NoClassDefFoundError e) {}
				}
			}

			//only add in the classes from the jar if the jar was valid.
			if (jarValid) {
				classList.addAll(potentialClassList);
			}
		}
	}
	
	/*
	 * loads the classes in the list, which are all found inside the given path's
	 * directory structure
	 */
	private void loadClasses(List classFiles, String path) throws IOException {
		Iterator classIterator = classFiles.iterator();

		while (classIterator.hasNext()) {
			File classFile = (File) classIterator.next();
			String name = toFullClassName(classFile, path);
			boolean retry = true;
			while(retry) {
				try {
					Class potentialClass =  classLoader.loadClass(name);					
					classList.add(potentialClass);					
					classToPathMap.put(potentialClass,classFile);
					retry = false;
				} catch (Exception e) {
					retry = true;
				}
				if(retry){
				    int index = name.indexOf('.');
				    if(index == -1){
				        retry = false;
				    }
				    else{
				        name = name.substring(index + 1);
				    }
				}
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
	
	/**
	 * returns the dotted form of the class name, fully qualified with package, starting
	 * at the point after the prefix of path in the file path. This will result in
	 * incorrect packages if bin or build or something like that is before the start of the
	 * package directory structure, so this should be checked for and iterated through until the
	 * correct name is found
	 * 
	 * @param classFile the File where the .class file resides
	 * @param path the path that was used to find this file (prefix or classFile's path)
	 * @return dotted form of class name w/ package prefix (and possibly directory prefix in front of that)
	 */
	private static String toFullClassName(File classFile, String path){
	    String filePath = classFile.getPath();
	    String partialPath = filePath.substring(path.length() - 1);
	    partialPath = partialPath.substring(0, partialPath.lastIndexOf(".class"));	    	    	    	   
	    return partialPath.replace(File.separatorChar, '.');
	}
}
