package rlVizLib.general;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

public class ParameterHolder {

	private final int intParam=0;
	private final int doubleParam=1;
	private final int stringParam=2;



	//This a straight port of my bt-glue C++ Code, might simplify in Java

	Map<String, Integer> intParams=new TreeMap<String, Integer>();
	Map<String, Double> doubleParams=new TreeMap<String, Double>();
	Map<String, String> stringParams=new TreeMap<String, String>();

	Map<String, Integer> allParams=new TreeMap<String, Integer>();
	Map<String, String> aliases=new TreeMap<String, String>();


	Vector<Integer> allParamTypes=new Vector<Integer>();
	Vector<String> allParamNames=new Vector<String>();
	Vector<String> allAliases=new Vector<String>();


	public ParameterHolder(){

	}
	public ParameterHolder(final String theString){
		this();
		StringTokenizer iss=new StringTokenizer(theString,"_");

		int numParams;
		String thisParamName;

		double fParamValue;
		int iParamValue;
		String sParamValue;

		int thisParamType;

		//MAke sure the first bit of this isn't NULL!
		String pointerType;

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



	private void setAlias(String thisAlias, String thisTarget) {
		if(allParams.get(thisTarget)==null){
			System.out.println("Careful, you are setting an alias of: "+thisAlias+" to original: "+thisTarget+" but the original isn't in the parameter set!");
			Thread.dumpStack();
			System.exit(1);
		}
		aliases.put(thisAlias,thisTarget);
		allAliases.add(thisAlias);
	}


	private void addStringParam(String thisParamName, String thisParamValue) {
		addStringParam(thisParamName);
		setStringParam(thisParamName,thisParamValue);
	}



	private void setStringParam(String thisParamAlias, String thisParamValue) {
		String name=getAlias(thisParamAlias);
		if(!allParams.containsKey(name)){
			System.err.println("Careful, you are setting the value of parameter: "+name+" but the parameter hasn't been added...");
		}
		stringParams.put(name, thisParamValue);}



	private void genericNewParam(String thisParamName){
		allParamNames.add(thisParamName);
		setAlias(thisParamName,thisParamName);
	}
	private void addStringParam(String thisParamName) {
		allParams.put(thisParamName,stringParam);
		allParamTypes.add(stringParam);
		genericNewParam(thisParamName);
	}

	private void addIntParam(String thisParamName) {
		allParams.put(thisParamName,intParam);
		allParamTypes.add(intParam);
		genericNewParam(thisParamName);
	}

	private void addDoubleParam(String thisParamName) {
		allParams.put(thisParamName,doubleParam);
		allParamTypes.add(doubleParam);
		genericNewParam(thisParamName);
	}


	private void addDoubleParam(String thisParamName, double thisParamValue) {
		addDoubleParam(thisParamName);
		setDoubleParam(thisParamName,thisParamValue);
	}


	private void setDoubleParam(String thisParamAlias, double thisParamValue) {
		String name=getAlias(thisParamAlias);
		if(!allParams.containsKey(name)){
			System.err.println("Careful, you are setting the value of parameter: "+name+" but the parameter hasn't been added...");
		}
		doubleParams.put(name, thisParamValue);
	}



	private void addIntParam(String thisParamName, int thisParamValue) {
		addIntParam(thisParamName);
		setIntParam(thisParamName,thisParamValue);
	}



	private void setIntParam(String thisParamAlias,int thisParamValue) {
		String name=getAlias(thisParamAlias);
		if(!allParams.containsKey(name)){
			System.err.println("Careful, you are setting the value of parameter: "+name+" but the parameter hasn't been added...");
		}
		intParams.put(name, thisParamValue);
	}



	private String getAlias(String thisParamAlias) {
		if(!aliases.containsKey(thisParamAlias)){
			System.err.println("You wanted to look up original for alias: "+thisParamAlias+", but that alias hasn't been set");
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
			if(paramType==doubleParam)outs.append(getdoubleParam(allParamNames.get(i)));
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



	private Object getStringParam(String theAlias) {
		//Convert from an alias to the real name
		String name=getAlias(theAlias);

		if(!allParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter hasn't been added...");System.exit(1);}
		if(!stringParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter isn't a String parameter...");System.exit(1);}

		return stringParams.get(name);
	}



	private Object getdoubleParam(String theAlias) {
		//Convert from an alias to the real name
		String name=getAlias(theAlias);

		if(!allParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter hasn't been added...");System.exit(1);}
		if(!doubleParams.containsKey(name)){System.out.println("Careful, you are getting the value of parameter: "+name+" but the parameter isn't a double parameter...");System.exit(1);}

		return doubleParams.get(name);
	}



	private Object getIntParam(String theAlias) {
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

	public static void main(String []args){
		ParameterHolder p= new ParameterHolder();
		
		p.addDoubleParam("Alpha",.1);
		p.addDoubleParam("epsilon",.03);

		p.addIntParam("StepCount",5);
		p.addIntParam("Tiles",16);

		p.addStringParam("Agent Name","Dave");
		p.addStringParam("AgentOccupation","Winner");
		
		String serializedVersion = p.stringSerialize();
		System.out.println("serialized: "+serializedVersion);
		
		ParameterHolder unpackedP=new ParameterHolder(serializedVersion);

		System.out.println(p.getdoubleParam("Alpha"));
		System.out.println(p.getdoubleParam("epsilon"));
		System.out.println(p.getIntParam("StepCount"));
		System.out.println(p.getIntParam("Tiles"));
		System.out.println(p.getStringParam("Agent Name"));
		System.out.println(p.getStringParam("AgentOccupation"));
		
		System.out.println("---");
		
		System.out.println(unpackedP.getdoubleParam("Alpha"));
		System.out.println(unpackedP.getdoubleParam("epsilon"));
		System.out.println(unpackedP.getIntParam("StepCount"));
		System.out.println(unpackedP.getIntParam("Tiles"));
		System.out.println(unpackedP.getStringParam("Agent Name"));
		System.out.println(unpackedP.getStringParam("AgentOccupation"));
}


}
