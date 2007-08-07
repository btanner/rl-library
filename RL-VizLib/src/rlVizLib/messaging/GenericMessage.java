package rlVizLib.messaging;

import java.util.StringTokenizer;

public class GenericMessage {
	private int theMessageType;
	protected MessageUser from;
	protected MessageUser to;
	protected MessageValueType payLoadType;
	protected String payLoad;

	public GenericMessage(String theMessage) throws NotAnRLVizMessageException{
		try {
			StringTokenizer theTokenizer=new StringTokenizer(theMessage, " ");


			String toString=theTokenizer.nextToken();
			String fromString=theTokenizer.nextToken();
			String typeString=theTokenizer.nextToken();
			String valueString=theTokenizer.nextToken();
			String payLoadString=theTokenizer.nextToken("\f");

			from=GenericMessageParser.parseUser(fromString);
			to=GenericMessageParser.parseUser(toString);

			theMessageType=GenericMessageParser.parseInt(typeString);
			payLoadType=GenericMessageParser.parseValueType(valueString);
			payLoad=GenericMessageParser.parsePayLoad(payLoadString);
		} catch (Exception e) {
			//The message was NOT what we expected!
			throw new NotAnRLVizMessageException();
		}		

	}

	/**
	 * @return the theMessageType
	 */
	public int getTheMessageType() {
		return theMessageType;
	}

	/**
	 * @return the from
	 */
	public MessageUser getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public MessageUser getTo() {
		return to;
	}

	/**
	 * @return the payLoadType
	 */
	public MessageValueType getPayLoadType() {
		return payLoadType;
	}

	/**
	 * @return the payLoad
	 */
	public String getPayLoad() {
		return payLoad;
	}

}
