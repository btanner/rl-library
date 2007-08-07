package rlVizLib.utilities;

import java.util.StringTokenizer;

public class TaskSpecObject
{
    public double version;			
    public char episodic;			
    public int obs_dim;	
	public int num_discrete_obs_dims;
	public int num_continuous_obs_dims;		
    public char [] obs_types;	    
    public double [] obs_mins;           
    public double [] obs_maxs;			
    public int action_dim;
	public int num_discrete_action_dims;
	public int num_continuous_action_dims;			
    public char [] action_types;		
    public double [] action_mins;		
    public double [] action_maxs;	
    
    //Test program
    public static void main(String [] args) throws Exception
	{
		double minPosition = -1.2;
		double maxPosition =  0.6;
		double minVelocity = -0.07;    
		double maxVelocity =  0.07;
	    
		String taskSpec = "1:e:2_[f,f]_["+minPosition+","+maxPosition+"]_["+minVelocity+","+maxVelocity+"]:1_[i]_[0,2]";

		System.err.println(taskSpec);
		
		TaskSpecObject taskObject = new TaskSpecObject(taskSpec);
		
		System.err.println(taskObject);
	}
    
    //As we discussed, the TaskSpecObject should parse in its constructor
    public TaskSpecObject(String taskSpecString){
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

		version = Double.parseDouble(versionString);
		episodic = taskStyle.charAt(0);

		try {
			parseObservations(observationString);
			parseActions(actionString);
		} catch (Exception e) {
			System.err.println("Error parsing the Task Spec");
			System.err.println("Task Spec was: "+taskSpecString);
			System.err.println("Exception was: "+e);
			e.printStackTrace();
		}
    }
    
	protected  void parseObservationTypesAndDimensions(String obsTypesString) throws Exception
	{
		// Discard the [ ] around the types string
		obsTypesString = obsTypesString.substring(1, obsTypesString.length() - 1);
				
		// Split up the observation types
		StringTokenizer obsTypesTokenizer = new StringTokenizer(obsTypesString, ",");
		
		/* Parse the data out of obsTypesString.
		 * Allocate and fill the obs_types array, and set the number 
		 * of discrete and continuous observation dimensions.
		 */
		this.obs_types = new char[obsTypesTokenizer.countTokens()];
		this.num_discrete_obs_dims   = 0;
		this.num_continuous_obs_dims = 0;
		
		/* We get the observation type from the tokenizer, 
		 * add it to the obs_types array, and update the discrete and continuous dimensions
		 */
		int currentObservationTypeIndex = 0;
		while (obsTypesTokenizer.hasMoreTokens())
		{
			char obsType = obsTypesTokenizer.nextToken().charAt(0);
			this.obs_types[currentObservationTypeIndex] = obsType;
			switch(obsType)
			{
			case 'i':
				this.num_discrete_obs_dims += 1;
				break;
				
			case 'f':
				this.num_continuous_obs_dims += 1;
				break;
				
			default: 
				throw new Exception ("Unknown Observation Type: " + obsType);
			}
			currentObservationTypeIndex += 1;
		}
	}
	
	protected void parseObservationRanges(StringTokenizer observationTokenizer)
	{
		// Now we can allocate our obs mins and obs maxs arrays
		this.obs_mins = new double[this.obs_types.length];
		this.obs_maxs = new double[this.obs_types.length];
		int currentRange = 0;
		while(observationTokenizer.hasMoreTokens())
		{
			String observationRange = observationTokenizer.nextToken();
			observationRange = observationRange.substring(1, observationRange.length() - 1);
			StringTokenizer rangeTokenizer = new StringTokenizer(observationRange, ",");	
			
			this.obs_mins[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			this.obs_maxs[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			currentRange += 1;
		}		
	}

	protected void parseActionTypesAndDimensions(String actionTypesString) throws Exception
	{
		// Discard the [ ] around the types string
		actionTypesString = actionTypesString.substring(1, actionTypesString.length() - 1);
		
		// Split up the observation types
		StringTokenizer actionTypesTokenizer = new StringTokenizer(actionTypesString, ",");
		
		/* Parse the data out of obsTypesString.
		 * Allocate and fill the obs_types array, and set the number 
		 * of discrete and continuous observation dimensions.
		 */
		this.action_types = new char[actionTypesTokenizer.countTokens()];
		this.num_discrete_action_dims = 0;
		this.num_continuous_action_dims = 0;
		
		/* We get the observation type from the tokenizer, 
		 * add it to the obs_types array, and update the discrete and continuous dimensions
		 */
		int currentActionTypeIndex = 0;
		while (actionTypesTokenizer.hasMoreTokens())
		{
			char actionType = actionTypesTokenizer.nextToken().charAt(0);
			this.action_types[currentActionTypeIndex] = actionType;
			switch(actionType)
			{
			case 'i':
				this.num_discrete_action_dims += 1;
				break;
				
			case 'f':
				this.num_continuous_action_dims += 1;
				break;
				
			default: 
				throw new Exception ("Unknown Action Type: " + actionType);
			}
			currentActionTypeIndex += 1;
		}
	}
	
	protected void parseActionRanges(StringTokenizer actionTokenizer)
	{
		// Now we can allocate our obs mins and obs maxs arrays
		this.action_mins = new double[this.action_types.length];
		this.action_maxs = new double[this.action_types.length];
		int currentRange = 0;
		while(actionTokenizer.hasMoreTokens())
		{
			String actionRange = actionTokenizer.nextToken();
			actionRange = actionRange.substring(1, actionRange.length() - 1);
			StringTokenizer rangeTokenizer = new StringTokenizer(actionRange, ",");	
			
			this.action_mins[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			this.action_maxs[currentRange] = Double.parseDouble(rangeTokenizer.nextToken());
			currentRange += 1;
		}		
	}
	
	protected void parseObservations(String observationString) throws Exception
	{

		/* Break the observation into its three component parts
		 * The number of dimensions to the observation
		 * The types of the observation
		 * The ranges of the observations
		 */
		StringTokenizer observationTokenizer = new StringTokenizer(observationString, "_");
		String obsDimensionString = observationTokenizer.nextToken();
		String obsTypesString = observationTokenizer.nextToken();
		
		this.obs_dim = Integer.parseInt(obsDimensionString);
		parseObservationTypesAndDimensions(obsTypesString);
		parseObservationRanges(observationTokenizer);
	}
	
	protected void parseActions(String actionString) throws Exception
	{
		StringTokenizer actionTokenizer = new StringTokenizer(actionString, "_");
		String actionDimensionString = actionTokenizer.nextToken();
		String actionTypesString = actionTokenizer.nextToken();	
		
		this.action_dim = Integer.parseInt(actionDimensionString);
		parseActionTypesAndDimensions(actionTypesString);
		parseActionRanges(actionTokenizer);
	}


    
    public String toString()
    {
    	String obs_types_string = "";
    	for (int i = 0; i < obs_types.length; ++i)
    		obs_types_string += obs_types[i] + " ";
    	
    	String obs_mins_string = "";
    	for (int i = 0; i < obs_mins.length; ++i)
    		obs_mins_string += obs_mins[i] + " ";
    	
    	String obs_maxs_string = "";
    	for (int i = 0; i < obs_maxs.length; ++i)
    		obs_maxs_string += obs_maxs[i] + " ";
    	
       	String action_types_string = "";
    	for (int i = 0; i < action_types.length; ++i)
    		action_types_string += action_types[i] + " ";
    	
    	String action_mins_string = "";
    	for (int i = 0; i < action_mins.length; ++i)
    		action_mins_string += action_mins[i] + " ";
    	
    	String action_maxs_string = "";
    	for (int i = 0; i < action_maxs.length; ++i)
    		action_maxs_string += action_maxs[i] + " ";
 
    	
    	String taskSpecObject = "version: " + version + "\n" +
    		   "episodic: " + episodic + "\n" + 
    		   "obs_dim: " + obs_dim + "\n" + 
    		   "num_discrete_obs_dims: " + num_discrete_obs_dims + "\n" +
    		   "num_continuous_obs_dims: " + num_continuous_obs_dims + "\n" +
    		   "obs_types: " + obs_types_string + "\n" +
    		   "obs_mins: " + obs_mins_string + "\n" +
    		   "obs_maxs: " + obs_maxs_string + "\n" +
    		   "action_dim: " + action_dim + "\n" + 
    		   "num_discrete_action_dims: " + num_discrete_action_dims + "\n" +
    		   "num_continuous_action_dims: " + num_continuous_action_dims + "\n" +
    		   "action_types: " + action_types_string + "\n" +
    		   "action_mins: " + action_mins_string + "\n" +
    		   "action_maxs: " + action_maxs_string + "\n";
    	
    	return taskSpecObject;
    }
}
