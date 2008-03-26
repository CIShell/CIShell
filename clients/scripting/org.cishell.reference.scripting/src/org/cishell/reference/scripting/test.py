from java.util import Hashtable
from org.cishell.framework.algorithm import AlgorithmFactory

def findAlgorithms(filter=None):
    if not filter:
        filter = "(objectClass=%s)" % AlgorithmFactory
    
    refs = bContext.getServiceReferences(str(AlgorithmFactory), filter)
    return refs

def getAlgorithm(ref):
    if ref:
        return bContext.getService(ref)

def getService(service):
    return bContext.getService(bContext.getServiceReference(str(service)))

from org.cishell.framework.data import BasicData

refs = findAlgorithms()
factory = getAlgorithm(refs[2])
alg = factory.createAlgorithm([BasicData(None)],Hashtable(),ciContext)
dm1 = alg.execute()

dm2 = [BasicDataModel("100"),]
factory = getAlgorithm(refs[0])
alg = factory.createAlgorithm(dm2,Hashtable(),ciContext)
dm3 = alg.execute()

factory = getAlgorithm(refs[1])
alg = factory.createAlgorithm(dm3,Hashtable(),ciContext)
dm4 = alg.execute()


# possible other commands
from org.osgi.service.log import LogService
log = getService(LogService)


#other commands
for k in refs[0].getPropertyKeys(): print k, "=", refs[0].getProperty(k)

for i in refs: 
	for k in i.getPropertyKeys(): 
		print k, "=", i.getProperty(k)
	print

