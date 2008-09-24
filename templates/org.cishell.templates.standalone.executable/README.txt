Static Executable Integration Template README:

To integrate a static executable, follow the sample_algorithm's example. Below
are the steps you should take to integrate an algorithm:

1. Create a directory a top level directory (in this directory) with a short name
of your algorithm (avoid spaces, please)
2. Create a default directory where you place platform independent code/files that
are needed by your algorithm.
3. Create directories for each platform/processor you support (examples are 'linux.x86', 
'win32', 'linux.x86_64', 'solaris.sparc', etc.) and place the proper executables and
platform specific files in them.
4. Create a config.properties file in your algorithm directory that specifies
a template of how the algorithm executable can be run and what it returns.
5. Create a service.properties file that contains the algorithm service properties
6. Add parameter information into the gui.xml file if it takes any user-provided
parameters.
7. Update the manifest.properties for bundle-wide properties.
8. Run 'ant' to compile a jar file that will be available in the build directory.
9. Copy the jar file to a CIShell/OSGi installation to use.
