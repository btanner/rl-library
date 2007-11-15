-injars oldEnv.jar
-outjars newEnv.jar
-libraryjars ../../../rl-competition/system/libraries/RLVizLib.jar:/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar
-renamesourcefileattribute SourceFile
-keepattributes Signature, *Annotation*

-dontshrink
-dontoptimize

-keep public class * {
    public <init>(***);
    public static *;
}

-keepclassmembers class *{
    public static *;
}

-keepnames class * extends rlVizLib.Environments.EnvironmentBase{
    public *** env_init(***);
    public *** env_start(***);
    public *** env_step(***);
    public *** getDefaultParameters();
    public *** env_message(***);
    public *** env_cleanup(***);
    public *** env_get_random_seed(***);
    public *** env_get_stat(***);
    public *** env_set_random_seed(***);
    public *** env_set_state(***);
    public *** getVizualizer(***);
}
