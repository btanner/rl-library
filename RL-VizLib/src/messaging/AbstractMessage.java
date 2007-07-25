package messaging;

import java.util.StringTokenizer;

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
	public boolean canHandleAutomatically() {
		return false;
	}


}
