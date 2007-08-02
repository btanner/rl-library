package Tetrlais;

public class TetrlaisPiece {
	int thePiece[][][]=new int[4][7][7];
	int currentOrientation=0;

	public void setRow(int theRow, int[] theCols){
		thePiece[0][theRow]=theCols;
	}

	public static TetrlaisPiece makeSquare(){
		TetrlaisPiece newPiece = new TetrlaisPiece();

		int[] row0={0,0,0,0,0,0,0};
		int[] row1={0,0,0,1,0,0,0};
		int[] row2={0,0,0,1,0,0,0};
		int[] row3={0,0,0,1,0,0,0};
		int[] row4={0,0,0,1,0,0,0};
		int[] row5={0,0,0,0,0,0,0};
		int[] row6={0,0,0,0,0,0,0};


		newPiece.setRow(0, row0);
		newPiece.setRow(1, row1);
		newPiece.setRow(2, row2);
		newPiece.setRow(3, row3);
		newPiece.setRow(4, row4);
		newPiece.setRow(5, row5);
		newPiece.setRow(6, row6);

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
	public void makeRotations(){
		for(int nextDirection=1;nextDirection<4;nextDirection++){
			for(int i=0;i<thePiece[currentOrientation].length;i++){
				for(int j=0;j<thePiece[currentOrientation][i].length;j++){
					thePiece[nextDirection][i][j]=thePiece[nextDirection-1][6-j][i];
				}
			}
		}
	}

	public static void main(String []args){
		TetrlaisPiece thePiece=makeSquare();
		thePiece.makeRotations();
		System.out.println(thePiece);
		thePiece.currentOrientation=1;
		System.out.println(thePiece);
		thePiece.currentOrientation=2;
		System.out.println(thePiece);
		thePiece.currentOrientation=3;
		System.out.println(thePiece);
	}


}
