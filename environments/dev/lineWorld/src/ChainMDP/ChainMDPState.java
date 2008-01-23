package ChainMDP;

import java.util.Random;

public class ChainMDPState {
//	Current State Information
    public int index;

//Some of these are fixed
    public int length = 10;
    public int defaultInitState = 0;
    public int numActions = 3;
    public double[][][] T;
    public double rewardPerStep = 0.0d;
    public double rewardAtGoal = 1.0d;

//These are configurable
    public boolean randomStarts = false;
    private Random randomGenerator;

    ChainMDPState(Random randomGenerator) {
        this.randomGenerator = randomGenerator;


    }

    public ChainMDPState(ChainMDPState stateToCopy) {
        this.numActions = stateToCopy.numActions;
        this.index = stateToCopy.index;
        this.length = stateToCopy.length;
        this.defaultInitState = stateToCopy.defaultInitState;
        this.rewardPerStep = stateToCopy.rewardPerStep;
        this.rewardAtGoal = stateToCopy.rewardAtGoal;
        this.randomStarts = stateToCopy.randomStarts;

//These are pointers but that's ok
        this.randomGenerator = stateToCopy.randomGenerator;
    }

    //	Stopping condition
    public boolean inGoalRegion() {
        return index == length - 1;
    }

    void initState(int length, int numActions) {
        this.length = length;
        this.numActions = numActions;


        T = new double[length][length][numActions];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < numActions; k++) {
                    T[i][j][k] = 0.0;
                }
            }
        }

//                for(int j=0;j<length-1;j++)
//                    T[j][j+1][0] = 1.0;
//                
//                for (int i = 0; i < length; i++) {
//                    for (int k = 1; k < numActions; k++) {
//                        int ind;
//                        if(i==0)ind = 0;
//                        else ind = randomGenerator.nextInt(i);
//                        T[i][ind][k] = 1.0;   
//                    }
//                }

        for (int j = 0; j < length - 1; j++) {
            int ind = randomGenerator.nextInt(numActions);
            T[j][j + 1][ind] = 1.0;
        }


        for (int i = 0; i < length - 1; i++) {
            for (int k = 0; k < numActions; k++) {
                if (T[i][i + 1][k] != 1.0) {
                    int ind;
                    if (i == 0) {
                        ind = 0;
                    } else {
                        ind = randomGenerator.nextInt(i);
                    }
                    T[i][ind][k] = 1.0;
                }
            }
        }

//                 for (int i = 0; i < numActions; i++) {
//                    System.out.println("\nAction matrix "+i);
//                     for (int j = 0; j < length; j++) {
//                        for (int k = 0; k < length; k++) {
//                            System.out.print("   "+T[j][k][i]);
//                        }
//                    System.out.println();
//                    }                 
//                 }
//                System.exit(0);

    }

    void update(int a) {

        if (index == length - 1) {
            System.out.println("asking about = " + index);
        }
        for (int i = 0; i < length; i++) {
            if (T[index][i][a] == 1.0) {
                index = i;
                break;
            }
        }

//                switch(index){
//                    case 0:
//                        if(a == 0) index++;
//                        if(a == 1) index = index;
//                        if(a == 2) index = index;
//                        if(a == 3) index = index;
//                    break;
//                        
//                    case 1:
//                        if(a == 0) index++;
//                        if(a == 1) index = index;
//                        if(a == 2) index--;
//                        if(a == 3) index--;
//                    break;
//                
//                    case 2:
//			if(a == 0) index++;
//                        if(a == 1) index -= 2;
//			if(a == 2) index--;
//			if(a == 3) index = index;                    
//                    break;
//                    
//                    case 3:
//			if(a == 0) index++;
//			if(a == 1) index -= 2;
//			if(a == 2) index -= 3;
//			if(a == 3) index--;
//                   break;     
//                }





    }

    public double getReward() {
        if (inGoalRegion()) {
            return rewardAtGoal;
        } else {
            return rewardPerStep;
        }
    }
}
