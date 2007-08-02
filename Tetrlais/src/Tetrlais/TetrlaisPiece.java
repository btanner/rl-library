package Tetrlais;

public class TetrlaisPiece {
	boolean thePiece[][]=new boolean[3][3];
	
	public void setRow(int theRow, boolean Col0, boolean Col1, boolean Col2){
		thePiece[theRow][0]=Col0;
		thePiece[theRow][1]=Col1;
		thePiece[theRow][2]=Col2;
	}
	
	

}
