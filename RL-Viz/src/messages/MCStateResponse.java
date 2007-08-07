package messages;


import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;


public class MCStateResponse extends AbstractResponse{
	double position;
	double velocity;
	double height;
	double deltaheight;

	int episodeNumber;
	int currentStep;
	int totalSteps;

	public MCStateResponse(double position, double velocity, double height,double deltaheight, int episodeNumber, int currentStep, int totalSteps) {
		this.position=position;
		this.velocity=velocity;
		this.height=height;
		this.deltaheight=deltaheight;
		
		this.episodeNumber=episodeNumber;
		this.currentStep=currentStep;
		this.totalSteps=totalSteps;
	}

	public MCStateResponse(String responseMessage) throws NotAnRLVizMessageException {

		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");

		position=Double.parseDouble(stateTokenizer.nextToken());
		velocity=Double.parseDouble(stateTokenizer.nextToken());
		height=Double.parseDouble(stateTokenizer.nextToken());
		deltaheight=Double.parseDouble(stateTokenizer.nextToken());
		episodeNumber=Integer.parseInt(stateTokenizer.nextToken());
		currentStep=Integer.parseInt(stateTokenizer.nextToken());
		totalSteps=Integer.parseInt(stateTokenizer.nextToken());
	}

	@Override
	public String toString() {
		String theResponse="MCStateResponse: not implemented ";
		return theResponse;
	}


	@Override
	public String makeStringResponse() {
		StringBuffer theResponseBuffer= new StringBuffer();
		theResponseBuffer.append("TO=");
		theResponseBuffer.append(MessageUser.kBenchmark.id());
		theResponseBuffer.append(" FROM=");
		theResponseBuffer.append(MessageUser.kEnv.id());
		theResponseBuffer.append(" CMD=");
		theResponseBuffer.append(EnvMessageType.kEnvResponse.id());
		theResponseBuffer.append(" VALTYPE=");
		theResponseBuffer.append(MessageValueType.kStringList.id());
		theResponseBuffer.append(" VALS=");

		theResponseBuffer.append(position);
		theResponseBuffer.append(":");
		theResponseBuffer.append(velocity);
		theResponseBuffer.append(":");
		theResponseBuffer.append(height);
		theResponseBuffer.append(":");
		theResponseBuffer.append(deltaheight);
		theResponseBuffer.append(":");
		theResponseBuffer.append(episodeNumber);
		theResponseBuffer.append(":");
		theResponseBuffer.append(currentStep);
		theResponseBuffer.append(":");
		theResponseBuffer.append(totalSteps);
		theResponseBuffer.append(":");


		return theResponseBuffer.toString();
	}

	public double getPosition() {
		return position;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getHeight() {
		return height;
	}

	public double getDeltaheight() {
		return deltaheight;
	}

	public int getEpisodeNumber() {
		return episodeNumber;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public int getTotalSteps() {
		return totalSteps;
	}

};