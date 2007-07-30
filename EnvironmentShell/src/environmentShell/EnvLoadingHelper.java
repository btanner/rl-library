package environmentShell;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlglue.Environment;


public class EnvLoadingHelper {
	Vector<File> theFiles=null;
	Vector<String> theEnvNames=null;
	Vector<ParameterHolder> theParamHolders=null;

	public void loadEnvFiles(){
		theFiles=new Vector<File>();
		theEnvNames=new Vector<String>();
		theParamHolders=new Vector<ParameterHolder>();
		
		String curDir = System.getProperty("user.dir");
		File d= new File(curDir);
		String workSpaceDir=d.getParent();


		String envJarDirString=workSpaceDir+"/envJars/";
		

		File envJarDir=new File(envJarDirString);
		File [] theFileList=envJarDir.listFiles();
		for (File thisFile : theFileList) {
			if(thisFile.getName().endsWith(".jar")){
				theFiles.add(thisFile);
				String thisEnvName=thisFile.getName().substring(0, thisFile.getName().length()-4);
				theEnvNames.add(thisEnvName);
				theParamHolders.add(loadParameterHolderFromFile(thisFile,thisEnvName));
			}
		}
	}

	public Vector<String> getEnvNames() {
		if(theEnvNames==null)
			loadEnvFiles();
		
		return theEnvNames;
	}
	
	public Vector<ParameterHolder> getParamHolders() {
		return theParamHolders;
	}

	
	public Environment loadEnvironment(String envName, ParameterHolder theParams) {
		if(theFiles==null)loadEnvFiles();
		
		//Get the file from the list
		for (File theFile : theFiles) {
			if(theFile.getName().equals(envName+".jar")){
				//this is the right one load it
				return loadEnvironmentFromFile(theFile,envName,theParams);
			}
		}
		return null;
	}

	private ParameterHolder loadParameterHolderFromFile(File theFile,String envName){
		ParameterHolder theParamHolder=null;

		String theFileName=theFile.getAbsolutePath();
		URLClassLoader urlLoader = null;

		try {
			URL theURL=new URL("file",null, theFileName);

			boolean loadRemote=false;
			if(loadRemote)theURL=new URL("http://rl-library.googlecode.com/svn/trunk/envJars/MountainCar.jar");


			urlLoader = new URLClassLoader(new URL[]{theURL});

			//If an environment is in jar called myEnv.jar, we'll expect there to be a class at myEnv/myEnv.class
			String className=envName+"."+envName;

			Class theEnvClass=urlLoader.loadClass(className);
			
			Class[] reflectParamArray=new Class[0];
			Method paramMakerMethod = theEnvClass.getDeclaredMethod("getDefaultParameters",reflectParamArray);
			
			if(paramMakerMethod!=null){
				System.out.println("We found a param maker method in file: "+theFile);
				System.out.println("The method is: "+paramMakerMethod);
				theParamHolder=(ParameterHolder) paramMakerMethod.invoke(null,null);
				}else{
				System.out.println("NO param maker method in file: "+theFile);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theParamHolder;
	}



	private Environment loadEnvironmentFromFile(File theFile,String envName, ParameterHolder theParams){
		Environment theEnvironment=null;
		String theFileName=theFile.getAbsolutePath();

		URLClassLoader urlLoader = null;



		try {
			URL theURL=new URL("file",null, theFileName);

			boolean loadRemote=false;
			if(loadRemote)theURL=new URL("http://rl-library.googlecode.com/svn/trunk/envJars/MountainCar.jar");


			urlLoader = new URLClassLoader(new URL[]{theURL});

			//If an environment is in jar called myEnv.jar, we'll expect there to be a class at myEnv/myEnv.class
			String className=envName+"."+envName;

			Class theEnvClass=urlLoader.loadClass(className);
			theEnvironment=(Environment)theEnvClass.newInstance();
			
			Constructor paramBasedConstructor = theEnvClass.getConstructor(ParameterHolder.class);

			theEnvironment=(Environment)paramBasedConstructor.newInstance(theParams);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theEnvironment;
	}

	public Vector<ParameterHolder> getTheParamHolders() {
		return theParamHolders;
	}

}
