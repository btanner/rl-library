import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import rlglue.Environment;


public class EnvLoadingHelper {
	Vector<File> theFiles=new Vector<File>();

	public void loadEnvFiles(){
		String curDir = System.getProperty("user.dir");
		File d= new File(curDir);
		String workSpaceDir=d.getParent();


		String envJarDirString=workSpaceDir+"/envJars/";
		

		File envJarDir=new File(envJarDirString);
		File [] theFileList=envJarDir.listFiles();
		for (File thisFile : theFileList) {
			if(thisFile.getName().endsWith(".jar"))
				theFiles.add(thisFile);
		}
	}

	public Vector<String> getEnvNames() {
		Vector<String> resultList=new Vector<String>();

		for (File theFile : theFiles) {
			//Chop off the trailing .jar
			resultList.add(theFile.getName().substring(0, theFile.getName().length()-4));
		}

		return resultList;
	}
	
	public Environment loadEnvironment(String envName) {
		//Get the file from the list
		for (File theFile : theFiles) {
			if(theFile.getName().equals(envName+".jar")){
				//this is the right one load it
				return loadEnvironmentFromFile(theFile,envName);
			}
		}
		return null;
	}

	private Environment loadEnvironmentFromFile(File theFile,String envName){
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theEnvironment;
	}

}
