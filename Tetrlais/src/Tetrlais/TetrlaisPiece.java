package Tetrlais;

public class TetrlaisPiece {
	int thePiece[][][]=new int[4][5][5];
	int currentOrientation=0;

	public void setShape(int Direction, int row0[],int row1[],int row2[],int row3[],int row4[]){
		thePiece[Direction][0]=row0;
		thePiece[Direction][1]=row1;
		thePiece[Direction][2]=row2;
		thePiece[Direction][3]=row3;
		thePiece[Direction][4]=row4;
	}
	
	public int[][] getShape(int whichOrientation){
		return thePiece[whichOrientation];
	}

	public static TetrlaisPiece makeSquare(){
		TetrlaisPiece newPiece = new TetrlaisPiece();

		{
			//Orientation 0,1,2,3
		int[] row0={0,0,0,0,0};
		int[] row1={0,0,1,1,0};
		int[] row2={0,0,1,1,0};
		int[] row3={0,0,0,0,0};
		int[] row4={0,0,0,0,0};
		newPiece.setShape(0, row0, row1, row2, row3, row4);
		newPiece.setShape(1, row0, row1, row2, row3, row4);
		newPiece.setShape(2, row0, row1, row2, row3, row4);
		newPiece.setShape(3, row0, row1, row2, row3, row4);
		}
		
		return newPiece;
	}
	
	public static TetrlaisPiece makeTri(){
		TetrlaisPiece newPiece = new TetrlaisPiece();

		{
			//Orientation 0
		int[] row0={0,0,0,0,0};
		int[] row1={0,0,1,0,0};
		int[] row2={0,1,1,1,0};
		int[] row3={0,0,0,0,0};
		int[] row4={0,0,0,0,0};
		newPiece.setShape(0, row0, row1, row2, row3, row4);
		}
		{
			//Orientation 1
		int[] row0={0,0,0,0,0};
		int[] row1={0,0,1,0,0};
		int[] row2={0,0,1,1,0};
		int[] row3={0,0,1,0,0};
		int[] row4={0,0,0,0,0};
		newPiece.setShape(1, row0, row1, row2, row3, row4);
		}

		{
			//Orientation 2
		int[] row0={0,0,0,0,0};
		int[] row1={0,0,0,0,0};
		int[] row2={0,1,1,1,0};
		int[] row3={0,0,1,0,0};
		int[] row4={0,0,0,0,0};
		newPiece.setShape(2, row0, row1, row2, row3, row4);
		}
		{
			//Orientation 3
		int[] row0={0,0,0,0,0};
		int[] row1={0,0,1,0,0};
		int[] row2={0,1,1,0,0};
		int[] row3={0,0,1,0,0};
		int[] row4={0,0,0,0,0};
		newPiece.setShape(3, row0, row1, row2, row3, row4);
		}

		
		return newPiece;
	}

		public static TetrlaisPiece makeLine(){
		TetrlaisPiece newPiece = new TetrlaisPiece();

		{
			//Orientation 0+2
		int[] row0={0,0,1,0,0};
		int[] row1={0,0,1,0,0};
		int[] row2={0,0,1,0,0};
		int[] row3={0,0,1,0,0};
		int[] row4={0,0,0,0,0};
		newPiece.setShape(0, row0, row1, row2, row3, row4);
		newPiece.setShape(2, row0, row1, row2, row3, row4);
		}

		{
			//Orientation 1+3
		int[] row0={0,0,0,0,0};
		int[] row1={0,0,0,0,0};
		int[] row2={0,1,1,1,1};
		int[] row3={0,0,0,0,0};
		int[] row4={0,0,0,0,0};
		newPiece.setShape(1, row0, row1, row2, row3, row4);
		newPiece.setShape(3, row0, row1, row2, row3, row4);
		}
		return newPiece;

	}
	public String toString(){
		StringBuffer shapeBuffer=new StringBuffer();
		for(int i=0;i<thePiece[currentOrientation].length;i++){
			for(int j=0;j<thePiece[currentOrientation][i].length;j++){
				shapeBuffer.append(" "+thePiece[currentOrientation][i][j]);
			}
			shapeBuffer.append("\n");
		}
		return shapeBuffer.toString();

	}

	public static void main(String []args){
		TetrlaisPiece thePiece=makeTri();
		System.out.println(thePiece);
		thePiece.currentOrientation=1;
		System.out.println(thePiece);
		thePiece.currentOrientation=2;
		System.out.println(thePiece);
		thePiece.currentOrientation=3;
		System.out.println(thePiece);
	}


}
