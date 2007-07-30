package rlVizLib.general;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

public class ParameterHolder {

	public static final int intParam=0;
	public static final int doubleParam=1;
	public static final int boolParam=2;
	public static final int stringParam=3;

	//This a straight port of my bt-glue C++ Code, might simplify in Java

	Map<String, Integer> intParams=new TreeMap<String, Integer>();
	Map<String, Double> doubleParams=new TreeMap<String, Double>();
	Map<String, String> stringParams=new TreeMap<String, String>();
	Map<String, Boolean> boolParams=new TreeMap<String, Boolean>();

	Map<String, Integer> allParams=new TreeMap<String, Integer>();
	Map<String, String> aliases=new TreeMap<String, String>();


	Vector<Integer> allParamTypes=new Vector<Integer>();
	Vector<String> allParamNames=new Vector<String>();
	Vector<String> allAliases=new Vector<String>();


	public ParameterHolder(){

	}

	public boolean isNull(){
		return (allParams.size()==0);
	}

	public ParameterHolder(final String theString){
		this();

		StringTokenizer iss=new StringTokenizer(theString,"_");

		int numParams;
		String thisParamName;

		int thisParamType;

		//Make sure the first bit of this isn't NULL!
		if(iss.nextToken().equals("NULL"))
			return;

		
		numParams=Integer.parseInt(iss.nextToken());

		for(int i=0;i<numParams;i++){
			thisParamName=iss.nextToken();
			thisParamType=Integer.parseInt(iss.nextToken());

			if(thisParamType==intParam){
				int thisParamValue=Integer.parseInt(iss.nextToken());
				addIntParam(thisParamName,thisParamValue);
			}
			if(thisParamType==doubleParam){
				double thisParamValue=Double.parseDouble(iss.nextToken());
				addDoubleParam(thisParamName,thisParamValue);
			}
			if(thisParamType==boolParam){
				boolean thisParamValue=Boolean.parseBoolean(iss.nextToken());
				addBooleanParam(thisParamName,thisParamValue);
			}
			if(thisParamType==stringParam){
				addStringParam(thisParamName,iss.nextToken());
			}
		}

		//Alias time
		int numAliases;
		numAliases=Integer.parseInt(iss.nextToken());


		for(int i=0;i<numAliases;i++){
			String thisAlias=iss.nextToken();
			String thisTarget=iss.nextToken();
			setAlias(thisAlias,thisTarget);
		}


	}



	public void setAlias(String thisAlias, String thisTarget) {
		if(thisAlias.contains(" ")||thisAlias.contains(":")||thisAlias.contains("_")){
			System.out.println("The name or alias of a parameter cannot contain a space or : or _");
			Thread.dumpStack();
			System.exit(1);
		}
		
		if(allParams.get(thisTarget)==null){
			System.out.println("Careful, you are setting an alias of: "+thisAlias+" to original: "+thisTarget+" but the original isn't in the parameter set!");
			Thread.dumpStack();
			System.exit(1);
		}
		aliases.put(thisAlias,thisTarget);
		allAliases.add(thisAlias);
	}


	public void addStringParam(String thisParamName, String thisParamValue) {
		addStringParam(thisParamName);
		setStringParam(thisParamName,thisParamValue);
	}



	public void setStringParam(String thisParamAlias, String thisParamValue) {
		String name=getAlias(thisParamAlias);
		if(!allParams.containsKey(name)){
			System.err.println("Careful, you are setting the value of parameter: "+name+" but the parameter hasn't been added...");
		}
		stringParams.put(name, thisParamValue);}



	private void genericNewParam(String thisParamName){
		allParamNames.add(thisParamName);
		setAlias(thisParamName,thisParamName);
	}
	public void addStringParam(String thisParamName) {
		allParams.put(thisParamName,stringParam);
		allParamTypes.add(stringParam);
		genericNewParam(thisParamName);
	}

	public void addIntParam(String thisParamName) {
		allParams.put(thisParamName,intParam);
		allParamTypes.add(intParam);
		genericNewParam(thisParamName);
	}

	public void addDoubleParam(String thisParamName) {
		allParams.put(thisParamName,doubleParam);
		allParamTypes.add(doubleParam);
		genericNewParam(thisParamName);
	}


	public void addDoubleParam(String thisParamName, double thisParamValue) {
		addDoubleParam(thisParamName);
		setDoubleParam(thisParamName,thisParamValue);
	}


	public void setDoubleParam(String thisParamAlias, double thisParamValue) {
		String name=getAlias(thisParamAlias);
		if(!allParams.containsKey(name)){
			System.err.println("Careful, you are setting the value of parameter: "+name+" but the parameter hasn't been added...");
		}
		doubleParams.put(name, thisParamValue);
	}


	public void addBooleanParam(String thisParamName) {
		allParams.put(thisParamName,boolParam);
		allParamTypes.add(boolParam);
		genericNewParam(thisParamName);
	}


	public void addBooleanParam(String thisParamName, boolean thisParamValue) {
		addBooleanParam(thisParamName);
		setBooleanParam(thisParamName,thisParamValue);
	}


	public void setBooleanParam(String thisParamAlias, boolean thisParamValue) {
		String name=getAlias(thisParamAlias);
		if(!allParams.containsKey(name)){
			System.err.println("Careful, you are setting the value of parameter: "+name+" but the parameter hasn't been added...");
		}
		boolParams.put(name, thisParamValue);
	}
	
	public void addIntParam(String thisParamName, int thisParamValue) {
		addIntParam(thisParamName);
		setIntParam(thisParamName,thisParamValue);
	}



	public void setIntParam(String thisParamAlias,int thisParamValue) {
		String name=getAlias(thisParamAlias);
		if(!allParams.containsKey(name)){
			System.err.println("Careful, you are setting the value of parameter: "+name+" but the parameter hasn't been added...");
		}
		intParams.put(name, thisParamValue);
	}



	private String getAlias(String thisParamAlias) {
		if(!aliases.containsKey(thisParamAlias)){
			System.err.println("You wanted to look up original for alias: "+thisParamAlias+", but that alias hasn't been set");
			Thread.dumpStack();
			System.exit(-1);
		}
		return  aliases.get(thisParamAlias);

	}






	public	String stringSerialize() {
		StringBuffer outs=new StringBuffer();

		//Do this here instead of externally later when we're ready
		outs.append("PARAMHOLDER_");
		//First, write the number of param names
		outs.append(allParamNames.size());
		outs.append("_");

		for(int i=0;i<allParamNames.size();i++){
			outs.append(allParamNames.get(i));
			outs.append("_");

			int paramType=allParamTypes.get(i);

			outs.append(paramType);
			outs.append("_");

			if(paramType==intParam)outs.append(getIntParam(allParamNames.get(i)));
			if(paramType==doubleParam)outs.append(getDoubleParam(allParamNames.get(i)));
			if(paramType==boolParam)outs.append(getBooleanParam(allParamNames.get(i)));
			if(paramType==stringParam)outs.append(getStringParam(allParamNames.get(i)));
			outs.append("_");
		}

		//Now write all of the aliases
		outs.append(allAliases.size());
		outs.append("_");

		for(int i=0;i<allAliases.size();i++){
			outs.append(allAliases.get(i));
			outs.append("_");
			outs.append(getAlias(allAliases.get(i)));
			outs.append("_");
		}

		return outs.toString();
	}



	public String getStringParam(String theAlias) {
		//Convert from an alias to the real name
		String name=getAlias(theAlias);

		if(!allParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter hasn't been added...");System.exit(1);}
		if(!stringParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter isn't a String parameter...");System.exit(1);}

		return stringParams.get(name);
	}



	public double getDoubleParam(String theAlias) {
		//Convert from an alias to the real name
		String name=getAlias(theAlias);

		if(!allParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter hasn't been added...");System.exit(1);}
		if(!doubleParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter isn't a double parameter...");System.exit(1);}

		return doubleParams.get(name);
	}

	public boolean getBooleanParam(String theAlias) {
		//Convert from an alias to the real name
		String name=getAlias(theAlias);

		if(!allParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter hasn't been added...");System.exit(1);}
		if(!boolParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter isn't a bool parameter...");System.exit(1);}

		return boolParams.get(name);
	}


	public int getIntParam(String theAlias) {
		//Convert from an alias to the real name
		String name=getAlias(theAlias);

		if(!allParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter hasn't been added...");System.exit(1);}
		if(!intParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter isn't an int parameter...");System.exit(1);}

		return intParams.get(name);
	}



//	int ParameterHolder::getParamCount(){
//	return allParamNames.size();
//	}
//	String ParameterHolder::getParamName(int which){
//	return allParamNames[which];
//	}
//	PHTypes ParameterHolder::getParamType(int which){
//	return allParamTypes[which];
//	}

//	bool ParameterHolder::supportsParam(String alias){
//	return (aliases.count(alias)!=0);
//	}

	public static ParameterHolder makeTestParameterHolder(){
		ParameterHolder p= new ParameterHolder();
		
		p.addDoubleParam("Alpha",.1);
		p.addDoubleParam("epsilon",.03);

		p.addIntParam("StepCount",5);
		p.addIntParam("Tiles",16);

		p.addStringParam("AgentName","Dave");
		p.addStringParam("AgentOccupation","Winner");

		p.addBooleanParam("ISCool",false);
		p.addBooleanParam("ISFast",true);

		return p;
	}
	public static void main(String []args){

		ParameterHolder p=makeTestParameterHolder();
		String serializedVersion = p.stringSerialize();
		System.out.println("serialized: "+serializedVersion);
		
		ParameterHolder unpackedP=new ParameterHolder(serializedVersion);

		System.out.println(p.getDoubleParam("Alpha"));
		System.out.println(p.getDoubleParam("epsilon"));
		System.out.println(p.getIntParam("StepCount"));
		System.out.println(p.getIntParam("Tiles"));
		System.out.println(p.getStringParam("AgentName"));
		System.out.println(p.getStringParam("AgentOccupation"));
		System.out.println(p.getBooleanParam("ISCool"));
		System.out.println(p.getBooleanParam("ISFast"));
		
		System.out.println("---");
		
		System.out.println(unpackedP.getDoubleParam("Alpha"));
		System.out.println(unpackedP.getDoubleParam("epsilon"));
		System.out.println(unpackedP.getIntParam("StepCount"));
		System.out.println(unpackedP.getIntParam("Tiles"));
		System.out.println(unpackedP.getStringParam("AgentName"));
		System.out.println(unpackedP.getStringParam("AgentOccupation"));
		System.out.println(unpackedP.getBooleanParam("ISCool"));
		System.out.println(unpackedP.getBooleanParam("ISFast"));

}

	public int getParamCount() {
		return allParams.size();
	}

	public int getParamType(int i) {
		return allParamTypes.get(i);
	}

	public String getParamName(int i) {
		return allParamNames.get(i);
	}


}
