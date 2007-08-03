package rlVizLib.utilities;

import java.util.StringTokenizer;

/* This shouldn't all be static, and TaskSpecObject and TaskSpecParser should be merged.
 * The parsing should be started by the TaskSpecParser constructor.
 */

public class TaskSpecParser
{	
	protected static void parseObservationTypesAndDimensions(String obsTypesString, TaskSpecObject specObject) throws Exception
	{
		// Discard the [ ] around the types string
		obsTypesString = obsTypesString.substring(1, obsTypesString.length() - 1);
				
		// Split up the observation types
		StringTokenizer obsTypesTokenizer = new StringTokenizer(obsTypesString, ",");
		
		/* Parse the data out of obsTypesString.
		 * Allocate and fill the obs_types array, and set the number 
		 * of discrete and continuous observation dimensions.
		 */
		specObject.obs_types = new char[obsTypesTokenizer.countTokens()];
		specObject.num_discrete_obs_dims   = 0;
		specObject.num_continuous_obs_dims = 0;
		
		/* We get the observation type from the tokenizer, 
		 * add it to the obs_types array, and update the discrete and continuous dimensions
		 */
		int currentObservationTypeIndex = 0;
		while (obsTypesTokenizer.hasMoreTokens())
		{
			char obsType = obsTypesTokenizer.nextToken().charAt(0);
			specObject.obs_types[currentObservationTypeIndex] = obsType;
			switch(obsType)
			{
			case 'i':
				specObject.num_discrete_obs_dims += 1;
				break;
				
			case 'f':
				specObject.num_continuous_obs_dims += 1;
				break;
				
			default: 
				throw new Exception ("Unknown Observation Type: " + obsType);
			}
			currentObservationTypeIndex += 1;
		}
	}
	
	protected static void parseObservationRanges(StringTokenizer observationTokenizer, TaskSpecObject specObject)
	{
		// Now we can allocate our obs mins and obs maxs arrays
		specObject.obs_mins = new double[specObject.obs_types.length];
		specObject.obs_maxs = new double[specObject.obs_types.length];
		int currentRange = 0;
		while(observationTokenizer.hasMoreTokens())
		{
			String observationRange = observationTokenizer.nextToken();
			observationRange = observationRange.substring(1, observationRange.length() - 1);
			StringTokenizer rangeTokenizer = new StringTokenizer(observationRange, ",");	
			
			specObject.obs_mins[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			specObject.obs_maxs[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			currentRange += 1;
		}		
	}

	protected static void parseActionTypesAndDimensions(String actionTypesString, TaskSpecObject specObject) throws Exception
	{
		// Discard the [ ] around the types string
		actionTypesString = actionTypesString.substring(1, actionTypesString.length() - 1);
		
		// Split up the observation types
		StringTokenizer actionTypesTokenizer = new StringTokenizer(actionTypesString, ",");
		
		/* Parse the data out of obsTypesString.
		 * Allocate and fill the obs_types array, and set the number 
		 * of discrete and continuous observation dimensions.
		 */
		specObject.action_types = new char[actionTypesTokenizer.countTokens()];
		specObject.num_discrete_action_dims = 0;
		specObject.num_continuous_action_dims = 0;
		
		/* We get the observation type from the tokenizer, 
		 * add it to the obs_types array, and update the discrete and continuous dimensions
		 */
		int currentActionTypeIndex = 0;
		while (actionTypesTokenizer.hasMoreTokens())
		{
			char actionType = actionTypesTokenizer.nextToken().charAt(0);
			specObject.action_types[currentActionTypeIndex] = actionType;
			switch(actionType)
			{
			case 'i':
				specObject.num_discrete_action_dims += 1;
				break;
				
			case 'f':
				specObject.num_continuous_action_dims += 1;
				break;
				
			default: 
				throw new Exception ("Unknown Action Type: " + actionType);
			}
			currentActionTypeIndex += 1;
		}
	}
	
	protected static void parseActionRanges(StringTokenizer actionTokenizer, TaskSpecObject specObject)
	{
		// Now we can allocate our obs mins and obs maxs arrays
		specObject.action_mins = new double[specObject.action_types.length];
		specObject.action_maxs = new double[specObject.action_types.length];
		int currentRange = 0;
		while(actionTokenizer.hasMoreTokens())
		{
			String actionRange = actionTokenizer.nextToken();
			actionRange = actionRange.substring(1, actionRange.length() - 1);
			StringTokenizer rangeTokenizer = new StringTokenizer(actionRange, ",");	
			
			specObject.action_mins[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			specObject.action_maxs[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			currentRange += 1;
		}		
	}
	
	protected static void parseObservations(String observationString, TaskSpecObject specObject) throws Exception
	{

		/* Break the observation into its three component parts
		 * The number of dimensions to the observation
		 * The types of the observation
		 * The ranges of the observations
		 */
		StringTokenizer observationTokenizer = new StringTokenizer(observationString, "_");
		String obsDimensionString = observationTokenizer.nextToken();
		String obsTypesString = observationTokenizer.nextToken();
		
		specObject.obs_dim = Integer.parseInt(obsDimensionString);
		parseObservationTypesAndDimensions(obsTypesString, specObject);
		parseObservationRanges(observationTokenizer, specObject);
	}
	
	protected static void parseActions(String actionString, TaskSpecObject specObject) throws Exception
	{
		StringTokenizer actionTokenizer = new StringTokenizer(actionString, "_");
		String actionDimensionString = actionTokenizer.nextToken();
		String actionTypesString = actionTokenizer.nextToken();	
		
		specObject.action_dim = Integer.parseInt(actionDimensionString);
		parseActionTypesAndDimensions(actionTypesString, specObject);
		parseActionRanges(actionTokenizer, specObject);
	}


	public static TaskSpecObject parse(String taskSpecString) throws Exception
	{
		TaskSpecObject specObject = new TaskSpecObject();
		
		/* Break the task spec into its four component parts
		 * The version number
		 * The task style (episodic/continuous)
		 * The observation data
		 * The action data 
		 */
		
		StringTokenizer tokenizer = new StringTokenizer(taskSpecString, ":");		

		String versionString = tokenizer.nextToken();
		String taskStyle = tokenizer.nextToken();
		String observationString = tokenizer.nextToken();
		String actionString = tokenizer.nextToken();	

		specObject.version = Double.parseDouble(versionString);
		specObject.episodic = taskStyle.charAt(0);

		parseObservations(observationString, specObject);
		parseActions(actionString, specObject);
		
		return specObject;
	}
	
	public static void main(String [] args) throws Exception
	{
		double minPosition = -1.2;
		double maxPosition =  0.6;
		double minVelocity = -0.07;    
		double maxVelocity =  0.07;
	    
		String taskSpec = "1:e:2_[f,f]_["+minPosition+","+maxPosition+"]_["+minVelocity+","+maxVelocity+"]:1_[i]_[0,2]";

		System.err.println(taskSpec);
		
		TaskSpecObject taskObject = TaskSpecParser.parse(taskSpec);
		
		System.err.println(taskObject);
	}
}

