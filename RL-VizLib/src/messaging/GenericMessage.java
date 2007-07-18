package messaging;

import java.util.StringTokenizer;

public class GenericMessage {
	private int theMessageType;
	private MessageUser from;
	private MessageUser to;
	private MessageValueType payLoadType;
	private String payLoad;
	
	public GenericMessage(String theMessage){
		StringTokenizer theTokenizer=new StringTokenizer(theMessage, " ");
		String toString=theTokenizer.nextToken();
		String fromString=theTokenizer.nextToken();
		String typeString=theTokenizer.nextToken();
		String valueString=theTokenizer.nextToken();
		String payLoadString=theTokenizer.nextToken();

		
		from=GenericMessageParser.parseUser(toString);
		to=GenericMessageParser.parseUser(fromString);
		theMessageType=GenericMessageParser.parseInt(typeString);
		payLoadType=GenericMessageParser.parseValueType(typeString);
		payLoad=GenericMessageParser.parsePayLoad(payLoadString);

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
