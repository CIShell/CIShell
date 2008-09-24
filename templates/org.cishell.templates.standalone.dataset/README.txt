Dataset Integration Template README:

To integrate a static executable, follow the myData.txt example in the data 
directory. Below are the steps you should take to integrate a dataset:

1. Place your dataset file in the data directory.
2. Create a .properties file with name 'yourDataFileName'.properties
3. Fill in the properties (normal algorithm service properties) to describe your dataset.
4. Repeat for multiple datasets.
5. Update the manifest.properties for bundle-wide properties.
6. Run 'ant' to compile a jar file that will be available in the build directory.
7. Copy the jar file to a CIShell/OSGi installation to use.
