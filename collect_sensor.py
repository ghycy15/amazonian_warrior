from SimpleXMLRPCServer import SimpleXMLRPCServer
from SimpleXMLRPCServer import SimpleXMLRPCRequestHandler
import random
import math

# Restrict to a particular path.
#class RequestHandler(SimpleXMLRPCRequestHandler):
#   rpc_paths = ('/',)

# Create server
server = SimpleXMLRPCServer(("0.0.0.0", 8000))
 #                           requestHandler=RequestHandler)
#server.register_introspection_functions()



class dataBase:
	def __init__(self):
		pass

class dataType1(dataBase):
	a = 1
	b = 1
	c = 1
	startTimestamp = None 
	endTimestamp = None
	interval = None
	x = None
	y = None
	z = None
	def __init__(self, startTimestamp, endTimestamp, x, y, z):
		self.startTimestamp = startTimestamp
		self.endTimestamp = endTimestamp
		self.interval = endTimestamp - startTimestamp
		self.x = x
		self.y = y
		self.z = z
		self.distance = self.getDistance()
	def getDistance(self):
		return math.sqrt(pow(self.x, 2) + pow(self.y, 2) + pow(self.z, 2))

	def getScore(self, defenderTrack):
		if self.endTimestamp > defenderTrack.startTimestamp \
			and self.endTimestamp < defenderTrack.endTimestamp \
				and self.startTimestamp < defenderTrack.startTimestamp:
			return self.a * (defenderTrack.startTimestamp - self.startTimestamp) * self.b * (self.distance/self.interval) \
					+ self.c * (self.distance/self.interval - defenderTrack.distance/defenderTrack.interval)
		return 0
			


class sensorDataManager:
	data = None
	def __init__(self):
		self.data = dict()	
	def addData(self, clientId, rawDataEntry):
		if clientId in self.data:
			self.data[clientId].append(rawDataEntry)
		else:
			self.data[clientId] = [rawDataEntry]
	def getDatabyId(self, clientId):
		return self.data[clientId]
	def getIds(self):
		return self.data.keys()

class dataAnalysis:
	mydataManager = None 
	def __init__(self, dataManager):
		self.mydataManager = dataManager		
	def makeJudgement(self, aID, dID):
		score = 0
		for dataEntryA in self.mydataManager.getDatabyId(aID):
			for dataEntryD in self.mydataManager.getDatabyId(dID):
				score += dataEntryA.getScore(dataEntryD)
		print score
		return score

class Game:
	user1 = None 
	user2 = None
	user1Ready = False
	user2Ready = False
	roundIndex = 0
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
	
	def sendSensorData(self, clientId, startTimestamp, endTimestamp, x, y, z):
		print clientId, startTimestamp, endTimestamp, x, y, z
		#if self.fightStarted: 
		if True:
			self.sensordataMan.addData(clientId, dataType1(startTimestamp, endTimestamp, x, y, z))
			return True
		else:
			return False			
	def getAttackerId(self):
		return 1
	def getDefenderId(self):
		return 2
	def getResult(self, clientId):
		return self.dataAna.makeJudgement(self.getAttackerId(), self.getDefenderId())
			


server.register_instance(Game())

# Run the server's main loop
server.serve_forever()