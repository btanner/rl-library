package rlVizLib.functionapproximation;

import java.util.Vector;

public class LeakyCache {
	Vector<DataPoint> theData=new Vector<DataPoint>();
	int maxSize;
	int currentIndex;
	int actualSize;
	
	public LeakyCache(int maxSize){
		this.maxSize=maxSize;
		theData.setSize(maxSize);
		currentIndex=0;
		actualSize=0;
	}
	
	public void add(DataPoint d){
		if(currentIndex<theData.size())
			theData.set(currentIndex,d);
		else{
			currentIndex=0;
			theData.set(currentIndex,d);
		}
		currentIndex++;
		currentIndex%=theData.size();
		actualSize++;
		if(actualSize>maxSize)
			actualSize=maxSize;
	}
	
	public DataPoint sample(){
		int randIndex=(int)(Math.random()*actualSize);
		return theData.get(randIndex);
	}

}


