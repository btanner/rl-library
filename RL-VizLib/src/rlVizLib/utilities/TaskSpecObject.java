package rlVizLib.utilities;
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
