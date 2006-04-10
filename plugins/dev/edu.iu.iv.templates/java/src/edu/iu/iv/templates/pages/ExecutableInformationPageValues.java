/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 12, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;

import org.eclipse.core.runtime.Platform;

/**
 * 
 * @author Bruce Herr
 */
public interface ExecutableInformationPageValues {
    public static final String KEY_SUBDIRECTORY = "subdirectory";
    public static final String KEY_BASE_EXECUTABLE = "baseexecutable";
    public static final String KEY_BASE_FILES = "basefiles";
    public static final String KEY_SUPPORTED_PLATFORMS = "supportedPlatforms";
    public static final String KEY_PLATFORM_SPECIFIC_DIRECTORIES = "platformSpecificDirectories";
    
    public static final String SUBDIRECTORY_LABEL = "Subdirectory:";
    public static final String BASE_EXECUTABLE_LABEL = "Base executable name:";
    public static final String BASE_FILES_LABEL = "Base files directory:";
    public static final String PLATFORM_DIRECTORIES_LABEL = "Supported Platform(s):";
    
    public static final String[] SUPPORTED_PLATFORMS = new String[] 
      { Platform.OS_WIN32, 
        Platform.OS_LINUX + "." + Platform.ARCH_X86,
        Platform.OS_LINUX + "." + Platform.ARCH_X86_64,
        Platform.OS_LINUX + "." + Platform.ARCH_PPC,
        Platform.OS_MACOSX + "." + Platform.ARCH_PPC,
        Platform.OS_SOLARIS + "." + Platform.ARCH_SPARC,
        Platform.OS_HPUX + "." + Platform.ARCH_PA_RISC,
        Platform.OS_AIX + "." + Platform.ARCH_PPC
      };
                                                              
    public static final String[] SUPPORTED_PLATFORM_LABELS = new String[]
      { "Windows",
        "Linux/x86",
        "Linux/x86 (64-bit)",
        "Linux/PowerPC",
        "Mac OSX/PowerPC",
        "Solaris/PowerPC",
        "HPUX/PA_RISC",
        "AIX/PowerPC"
      };
                                                              
    public static final String EXECUTABLE_INFORMATION_PAGE_DESCRIPTION = "Please enter the executable information";
}
