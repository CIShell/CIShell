# To see the output printed to the console in Network Workbench, use the 
# switch "-console" when opening Network Workbench from the command line.
from java import io

print "Here is the value you gave me when you ran the algorithm..."
print sample_attr

print "Here is what I got from CIShell (the graph you highlighted)"
print arg0
graph = arg0
edge_count = graph.getEdgeCount()

print "Here is the number of edges in the graph you provided..."
print edge_count

aFile = io.File("whatever.txt")
writer = io.BufferedWriter(io.FileWriter(aFile))
writer.write(str(edge_count))
writer.close()

print "I will now return the original graph, and the edge count in a file"

aFile = io.File("whatever.txt")
writer = io.BufferedWriter(io.FileWriter(aFile))
writer.write(str(edge_count))
writer.close()

result0 = arg0
result1 = aFile