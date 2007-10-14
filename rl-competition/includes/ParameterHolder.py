# Parameter Holder for Passing Parameters Around
# Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

INT_PARAM=0
DOUBLE_PARAM=1
BOOL_PARAM=2
STRING_PARAM=3

class ParameterHolder:
	intParams = {}
	doubleParams = {}
	boolParams = {}
	stringParams = {}
	
	allParams = {}
	#we'll let everything be an alias to itself, and then we'll always just look up aliases
	aliases = {}

	allParamNames = []
	allParamTypes = []
	allAliases = []
	
	def __init__(self,stringRep=None):
		if stringRep == None:
			return
		arrayRep = stringRep.split('_')
		
		numParams = arrayRep.pop(0)
		numParams = int(numParams)
		for i in range(numParams):
			thisParamName = arrayRep.pop(0)
			thisParamType = int(arrayRep.pop(0))
			if thisParamType == INT_PARAM:
				iParamValue = int(arrayRep.pop(0))
				addIntegerParam(thisParamName,iParamValue)
			elif thisParamType == DOUBLE_PARAM:
				fParamValue = float(arrayRep.pop(0))
				addDoubleParam(thisParamName,fParamValue)
			elif thisParamType == BOOL_PARAM:
				bParamValue = arrayRep.pop(0)
				if bParamValue == "true":
					addBoolParam(thisParamName,True)
				else:
					addBoolParam(thisParamName,False)
			elif thisParamType == STRING_PARAM:
				sParamValue = arrayRep.pop(0)
				addStringParam(thisParamName,sParamValue)
		
		numAliases = arrayRep.pop(0)
		numAliases = int(numAliases)
		for i in range(numAliases):
			thisAlias = arrayRep.pop(0)
			thisTarget = arrayRep.pop(0)
			setAlias(thisAlias,thisTarget)


	def stringSerialize(self): #return string
		outString = "PARAMHOLDER_%d_" % (len(allParamNames))
		for i in range(len(allParamNames)):
			paramType = allParamTypes[i]
			outString = outString + "%s_%d_" % (allParamNames[i],paramType)
			if paramType == INT_PARAM:
				outString = outString + "%d_" % (getIntegerParam(allParamNames[i]))
			elif paramType == DOUBLE_PARAM:
				outString = outString + "%f_" % (getDoubleParam(allParamNames[i]))
			elif paramType == BOOL_PARAM:
				if (getBoolParam(allParamNames[i])):
					outString = outString + "true_"
				else
					outString = outString + "false_"
			else
				outString = outString + getStringParam(allParamNames[i])
			
		outString = outString + "%d_" % (len(allAliases))
		for i in range(len(allAliases)):
			outString = outString + "%s_%s_" % (allAliases[i],getAlias(allAliases[i]))
		
		return outString


	def getAlias(self,alias): #return string
		if not aliases.has_key(alias):
			sys.stderr.write("You wanted to look up original for alias: %s, but that alias hasn't been set\n" % (alias))
			sys.exit(-1)
		return aliases[alias]
	
	def supportsParam(self,alias): #return bool
		return aliases.has_key(alias)

	def setAlias(self,alias,original): #void
		if not allParams.has_key(original):
			sys.stderr.write("C++ Parameter Holder::Careful, you are setting an alias of:%s to: %s but: %s isn't in the parameter set\n" % (alias,original,original))
			sys.exit(1)
		aliases[alias] = original
		allAliases.append(alias)
		

	def setIntegerParam(self,alias,value): #void
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are setting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
		intParams[name] = value
	
	def setDoubleParam(self,alias,value): #void
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are setting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
		doubleParams[name] = value
	
	def setBoolParam(self,alias,value): #void
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are setting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
		boolParams[name] = value
	
	def setStringParam(self,alias,value): #void
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are setting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
		boolParams[name] = value

	def addIntegerParam(self,alias): #void
		allParams[name] = INT_PARAM
		allParamNames.append(name)
		allParamTypes.append(intParam)
		setAlias(name,name)
	
	def addDoubleParam(self,alias): #void
		allParams[name] = DOUBLE_PARAM
		allParamNames.append(name)
		allParamTypes.append(doubleParam)
		setAlias(name,name)
	
	def addBoolParam(self,alias): #void
		allParams[name] = BOOL_PARAM
		allParamNames.append(name)
		allParamTypes.append(boolParam)
		setAlias(name,name)
	
	def addStringParam(self,alias): #void
		allParams[name] = STRING_PARAM
		allParamNames.append(name)
		allParamTypes.append(stringParam)
		setAlias(name,name)

	#Should have done this a while ago
	def addIntegerParam(self,alias,defaultValue): #void
		addIntegerParam(name)
		setIntegerParam(name, defaultValue)
	
	def addDoubleParam(self,alias,defaultValue): #void
		addDoubleParam(name)
		setDoubleParam(name, defaultValue)
	
	def addBoolParam(self,alias,defaultValue): #void
		addBoolParam(name)
		setBoolParam(name, defaultValue)
	
	def addStringParam(self,alias,defaultValue): #void
		addStringParam(name)
		setStringParam(name, defaultValue)

	def getIntegerParam(self,alias): #int
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
			sys.exit(1)
		if not intParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter isn't an int parameter...\n" % (name))
			sys.exit(1)
		return intParams[name]
	
	def getDoubleParam(self,alias): #double
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
			sys.exit(1)
		if not doubleParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter isn't a double parameter...\n" % (name))
			sys.exit(1)
		return doubleParams[name]
	
	def getBoolParam(self,alias): #bool
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
			sys.exit(1)
		if not boolParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter isn't a boolean parameter...\n" % (name))
			sys.exit(1)
		return boolParams[name]
	
	def getStringParam(self,alias): #string
		name = getAlias(alias)
		if not allParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter hasn't been added...\n" % (name))
			sys.exit(1)
		if not stringParams.has_key(name):
			sys.stderr.write("Careful, you are getting the value of parameter: %s but the parameter isn't a string parameter...\n" % (name))
			sys.exit(1)
		return stringParams[name]

	def getParamCount(self): #int
		return len(allParams)
	
	def getParamName(self,which): #string
		return allParamNames[which]
	
	def getParamType(self,which): #PHTypes
		return allParamTypes[which]



