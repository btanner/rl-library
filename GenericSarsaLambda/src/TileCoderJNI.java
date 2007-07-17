public class TileCoderJNI{
	public static native void tiles(int theTiles[], int numTilings, int memSize, double doubleVars[], int doubleCount, int intVars[], int intCount);
//	tiles(&F[a*NUM_TILINGS],NUM_TILINGS,MEMORY_SIZE,double_vars,doubleCount,int_vars, intCount+1);

}