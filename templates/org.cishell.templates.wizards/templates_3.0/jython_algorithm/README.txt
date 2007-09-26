Here is the minimum you need to know to integrate a jython script with 
CIShell, working from what is already provided by default in this project.

Arguments passed in from the Network Workbench Data Manager are passed 
into your script by default as variables with the prefix "arg" and a 
numeric suffix. The first argument is arg0, the next is arg1, and so on. 
You may specify how many, and what type of data you want these to be by 
editing the in_data property in the algorithm.properties file.

Arguments passed in from the user at the time the algorithm is run (The 
kind where a dialog box pops up and the user fills things in) can have 
any valid python variable name.  You can define what data you need, the 
type of data, and the variable names of that data by editing the 
METADATA.XML file. The id provided will be the name of the variable in 
the python environment.

To return results, assign values to variables with the prefix "result".
The first result is result0, the second is result1, and so on. You 
must specify the format of the data you are returning by editing the 
out_data property in the algorithm.properties file. You must also 
define values for the metadata of each result you return. Look at the
algorithm.properties file to see how this is done. Each result must 
specify a label and type, but parent part is optional. Label can be 
any text, type must be one of the pre-define types ("Network", "Text",
"Matrix", or "Other" currently), and parent must be the variable 
name of one of the provided data arguments (arg0, arg1, etc...).

Change the menu path to something sensible too.
 
NOTE: There are other things that should probably be changed if you 
are doing anything more than playing around with the jython algorithm
capabilities in CIShell. The plugin name, symbolic-name, all references
to the package, the service.pid, the algorithm description, and 
probably a few other things should be unique for each project. This
template is currently in a rough state. It should probably have
a wizard that asks the user to specify all the stuff that
should change between projects. This should do for now though,
especially for internal use.
 
 