from SimpleXMLRPCServer import SimpleXMLRPCServer
from SimpleXMLRPCServer import SimpleXMLRPCRequestHandler
import random

# Restrict to a particular path.
#class RequestHandler(SimpleXMLRPCRequestHandler):
#   rpc_paths = ('/',)

# Create server
server = SimpleXMLRPCServer(("0.0.0.0", 8000))
 #                           requestHandler=RequestHandler)
#server.register_introspection_functions()


class sensorDataManager:
	data = None
	def __init__(self):
		data = dict()	
	def addData(self, clientId, X, Y, Z, timestamp):
		if clientId in data:
			data[clientId].append((timestamp, X, Y, Z))
		else:
			data[clientId] = [(timestamp, X, Y, Z)]
	def getDatabyId(self, clientId):
		return data[clientId]
	def getIds(self):
		return data.keys()
	
class dataAnalysis:
	mydataManager = None 
	def __init__(self, dataManager):
		self.mydataManager = dataManager		
	def makeJudgement(self):
		return random.choice(mydataManager.getIds)
			


class Game:
	user1 = None 
	user2 = None
	sensordataMan = sensorDataManager()
	dataAna = dataAnalysis(sensordataMan)
	readyClients = 0
	#connectingClients = set()
	fightStarted = False
	#sensorData = {}
	def sendReady(self, clientId):
		if self.fightStarted:
			return True
		print "sendReady called"
		if not self.user1:
			self.user1 = clientId
		elif not clientId == self.user1:
			self.user2 = clientId
			self.fightStarted = True
			return True
		return False


		#self.connectingClients.add(clientId)
		#if len(self.connectingClients()) > 1:
		#	self.fightStarted = True
		#	return True
		#else:
		#	return False

	def sendSensorData(self, clientId, X, Y, Z, timestamp):
		if self.fightStarted: 
			self.sensordataMan.addData(clientId, X, Y, Z, timestamp)
			return True
		else:
			return False			

	def getResult(self, clientId):
		return self.dataAna.makeJudgement()
			


server.register_instance(Game())

# Run the server's main loop
server.serve_forever()