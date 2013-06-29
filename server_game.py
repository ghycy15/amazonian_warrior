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
server.register_introspection_functions()



class trackData:
	a = 0.001
	b = 0.01
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
	def match(self, defenderTrack):
		if self.endTimestamp > defenderTrack.startTimestamp \
			and self.endTimestamp < defenderTrack.endTimestamp \
				and self.startTimestamp < defenderTrack.startTimestamp:
			return True
		else:
			return False

	def getScore(self, defenderTrack):
		if not defenderTrack:
			return self.a * self.distance
		else:
			return self.a * (defenderTrack.startTimestamp - self.startTimestamp) * (self.distance/self.interval) \
					+ self.b * (self.distance/self.interval - defenderTrack.distance/defenderTrack.interval)
		return 0
			


class trackDataManager:
	data = None
	def __init__(self):
		self.data = dict()	
	def addData(self, clientId, rawDataEntry):
		if clientId in self.data:
			self.data[clientId].append(rawDataEntry)
		else:
			self.data[clientId] = [rawDataEntry]
	def getDatabyId(self, clientId):
		if clientId in self.data.keys():
			return self.data[clientId]
		else:
			return []
	def getIds(self):
		return self.data.keys()

class trackDataAnalysis:
	mydataManager = None 
	def __init__(self, dataManager):
		self.mydataManager = dataManager
	def findMatchedDefendTrack(self, attackTrack, defendTracks):
		for defendTrack in defendTracks:
			if attackTrack.match(defendTrack):
				return defendTrack
		return None

	def makeJudgement(self, aID, dID):
		ids = self.mydataManager.getIds()
		if aID in ids:
			score = 0
			defendTracks = self.mydataManager.getDatabyId(dID)			
			for dataEntryA in self.mydataManager.getDatabyId(aID):
				score += dataEntryA.getScore(self.findMatchedDefendTrack(dataEntryA, defendTracks))
			#print "attack", self.mydataManager.getDatabyId(aID)[0].startTimestamp, self.mydataManager.getDatabyId(aID)[0].endTimestamp
			#print "defender", self.mydataManager.getDatabyId(dID)[0].startTimestamp, self.mydataManager.getDatabyId(dID)[0].endTimestamp
			print "score: ", score
			return score
		else:
			return 0




class Round:
	roundIndex = None
	dataManager = None
	dataAnalysis = None
	attacker = None
	defender = None
	result = None

	def __init__(self, roundIndex, attacker, defender):
		self.roundIndex = roundIndex
		self.attacker = attacker
		self.defender = defender		
		self.dataManager = trackDataManager()
		self.dataAnalysis = trackDataAnalysis(self.dataManager)
	def appendData(self, clientId, dataEntry):
		self.dataManager.addData(clientId, dataEntry)
	def getResult(self):
		print "round: %d, attacker: %d, defender: %d" % (self.roundIndex, self.attacker, self.defender)
		if self.result:
			return self.result
		else:
			self.result = self.dataAnalysis.makeJudgement(self.attacker, self.defender)
			return self.result

class game:
	user1 = None
	user2 = None
	sessionId = None
	roundIndex = None
	readyStatus = None
	HPs = None
	rounds = {}
	end = False
	currentRoundResult = -1
	endStatus = None
	def __init__(self, sessionId, user1, user2):
		self.user1 = user1
		self.user2 = user2
		self.sessionId = sessionId
		self.readyStatus = {user1: False, user2: False}
		self.endStatus = {user1: False, user2: False}
		self.HPs = {user1: 100, user2: 100}
		self.roundIndex = 0
	def setReady(self, user):
		self.readyStatus[user] = True
		if not self.end:
			return True
		else:
			return False
	def readyToStart(self, clientId):
		if self.readyStatus[self.user1] and self.readyStatus[self.user2]:
			if self.roundIndex not in self.rounds:
				self.rounds[self.roundIndex] = Round(self.roundIndex, self.getAttacker(), self.getDefender())
			if self.getAttacker() == clientId:
				return 0
			else:
				return 1
		else:
			return -1

	def appendData(self, clientId, startTimestamp, endTimestamp, x, y, z):
		self.rounds[self.roundIndex].appendData(clientId, trackData(startTimestamp, endTimestamp, x, y, z))
		return True

	def getRoundResult(self, cliId):
		if not (self.endStatus[self.user1] and self.endStatus[self.user2]):
			self.endStatus[cliId] = True
			return -100
		
		if self.currentRoundResult < 0:
			print "get result, current round index", self.roundIndex
			self.currentRoundResult = self.rounds[self.roundIndex].getResult()
			#print "2"
			self.HPs[self.getDefender()] -= self.currentRoundResult
			#print "3"
			if self.HPs[self.getDefender()] < 0:
				self.end = True
				return -50
			#print "4"

			return self.currentRoundResult
		else:
			print "Attacker: %d, defender: %d, round index: %d" % (self.getAttacker(), self.getDefender(), self.roundIndex)
			self.readyStatus = {self.user1: False, self.user2: False}
			self.endStatus = {self.user1: False, self.user2: False}
			self.roundIndex += 1
			re = self.currentRoundResult
			self.currentRoundResult = -1
			print "Result Score: ", re
			if self.end:
				return -50
			return re

	def getAttacker(self):
		if self.roundIndex % 2 == 0:
			return self.user1
		else:
			return self.user2
	def getDefender(self):
		if self.roundIndex % 2 == 1:
			return self.user1
		else:
			return self.user2
		


class serverFuncs:
	sessionId = 100
	pairs = {}
	currentWaitingSession = 100
	games = {}
	def login(self, clientId):
		print "login %d" % clientId
		if self.currentWaitingSession in self.pairs:
			self.pairs[self.currentWaitingSession][1] = clientId
			self.games[self.sessionId] = game(self.sessionId, self.pairs[self.sessionId][0], self.pairs[self.sessionId][1])
			self.sessionId += 1
			self.currentWaitingSession += 1
			return self.sessionId - 1 
		else:
			self.pairs[self.currentWaitingSession] = {}
			self.pairs[self.currentWaitingSession][0] = clientId
			return self.sessionId

	def getMatch(self, sesId):
		print "getMatch %d" % sesId
		return self.sessionId > sesId

	def sendReady(self, sesId, cliId):
		print "sendReady %d, %d" % (sesId, cliId)
		self.games[sesId].setReady(cliId)
		return True

	def askForStart(self, sesId, cliId):
		print "askForStart %d, %d" % (sesId, cliId)
		return self.games[sesId].readyToStart(cliId)
	
	def sendTrackData(self, sesId, clientId, startTimestamp, endTimestamp, x, y, z):
		print sesId, clientId, startTimestamp, endTimestamp, x, y, z
		self.games[sesId].appendData(clientId, startTimestamp, endTimestamp, x, y, z)
		return True
	def getResult(self, sesId, cliId):
		#print sesId
		print "Result"
		re = self.games[sesId].getRoundResult(cliId)
		print "Result: ", re
		return int(re)
		
		







'''
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
			
'''

server.register_instance(serverFuncs())

# Run the server's main loop
server.serve_forever()