package rlVizLib.general;

import java.util.StringTokenizer;

public class RLVizVersion implements Comparable{
	int majorRevision;
	int minorRevision;
	
	public static final RLVizVersion NOVERSION=new RLVizVersion(0,0);
	public static final RLVizVersion CURRENTVERSION=new RLVizVersion(1,0);
	
	public RLVizVersion(int majorRevision, int minorRevision){
		this.majorRevision=majorRevision;
		this.minorRevision=minorRevision;
	}
	
	public RLVizVersion(String serialized){
		StringTokenizer theTokenizer=new StringTokenizer(serialized,"_");
		majorRevision=Integer.parseInt(theTokenizer.nextToken());
		minorRevision=Integer.parseInt(theTokenizer.nextToken());
	}

	public int getMajorRevision() {
		return majorRevision;
	}

	public int getMinorRevision() {
		return minorRevision;
	}
	
	public String serialize(){
		String theString=majorRevision+"_"+minorRevision;
		return theString;
	}

	public int compareTo(Object in) {
		RLVizVersion otherVersion=(RLVizVersion)in;
		
		if(otherVersion.getMajorRevision()<getMajorRevision())
			return 1;
		if(otherVersion.getMajorRevision()>getMajorRevision())
			return -1;
		if(otherVersion.getMinorRevision()<getMinorRevision())
			return 1;
		if(otherVersion.getMinorRevision()>getMinorRevision())
			return -1;
		
		return 0;

	}

}
