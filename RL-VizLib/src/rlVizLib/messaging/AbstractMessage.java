package rlVizLib.messaging;



public class AbstractMessage {
	GenericMessage theRealMessageObject=null;
	
	public AbstractMessage(GenericMessage theMessageObject){
		this.theRealMessageObject=theMessageObject;
	}

	/**
	 * @return the theMessageType
	 */
	public int getTheMessageType() {
		return theRealMessageObject.getTheMessageType();
	}

	/**
	 * @return the from
	 */
	public MessageUser getFrom() {
		return theRealMessageObject.getFrom();
	}

	/**
	 * @return the to
	 */
	public MessageUser getTo() {
		return theRealMessageObject.getTo();
	}

	/**
	 * @return the payLoadType
	 */
	public MessageValueType getPayLoadType() {
		return theRealMessageObject.getPayLoadType();
	}

	/**
	 * @return the payLoad
	 */
	public String getPayLoad() {
		return theRealMessageObject.getPayLoad();
	}
	
	/*
	 * Override this if you can handle automatically given a queryable environment or agent
	 */
	public boolean canHandleAutomatically(Object theReceiver) {
		return false;
	}
	

	public static String makeMessage(int TO, int FROM, int CMD, int VALTYPE,String PAYLOAD) {
		StringBuffer theRequestBuffer=new StringBuffer();
		theRequestBuffer.append("TO=");
		theRequestBuffer.append(TO);
		theRequestBuffer.append(" FROM=");
		theRequestBuffer.append(FROM);
		theRequestBuffer.append(" CMD=");
		theRequestBuffer.append(CMD);
		theRequestBuffer.append(" VALTYPE=");
		theRequestBuffer.append(VALTYPE);
		theRequestBuffer.append(" VALS=");
		theRequestBuffer.append(PAYLOAD);
		
		return theRequestBuffer.toString();
	}


}
