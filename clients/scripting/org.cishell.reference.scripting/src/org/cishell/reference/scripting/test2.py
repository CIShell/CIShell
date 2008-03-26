from java.util import Hashtable
from org.cishell.framework.algorithm import AlgorithmFactory

def findAlgorithms(filter=None):    
    return bContext.getServiceReferences(str(AlgorithmFactory), filter)

def getAlgorithm(ref):
    if ref:
        return bContext.getService(ref)

def getService(service):
    return bContext.getService(bContext.getServiceReference(str(service)))

from org.cishell.framework.data import BasicData


refs = findAlgorithms("(service.pid=org.cishell.tests.conversion1.algA)")
factory = getAlgorithm(refs[0])
ht = Hashtable()
ht["org.cishell.tests.conversion1.AlgA.myInput"] = "100"
alg = factory.createAlgorithm([],ht,ciContext)
dm1 = alg.execute()


refs = findAlgorithms("(service.pid=org.cishell.tests.conversion1.algB)")
factory = getAlgorithm(refs[0])
alg = factory.createAlgorithm(dm1,Hashtable(),ciContext)
dm2 = alg.execute()


refs = findAlgorithms("(service.pid=org.cishell.tests.conversion1.algC)")
factory = getAlgorithm(refs[0])
alg = factory.createAlgorithm(dm2,Hashtable(),ciContext)
dm3 = alg.execute()


# other commands
from org.osgi.service.log import LogService
log = getService(LogService)
log.log(LogService.LOG_INFO,"I'm logging from Jython!")

for k in refs[0].getPropertyKeys(): print k, "=", refs[0].getProperty(k)

for i in refs: 
	for k in i.getPropertyKeys(): 
		print k, "=", i.getProperty(k)
	print

