/* Tetris Domain
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package org.rlcommunity.tetris;

public class TetrisPiece {
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

	public static TetrisPiece makeSquare(){
		TetrisPiece newPiece = new TetrisPiece();

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
	
	public static TetrisPiece makeTri(){
		TetrisPiece newPiece = new TetrisPiece();

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

		public static TetrisPiece makeLine(){
		TetrisPiece newPiece = new TetrisPiece();

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
		
		public static TetrisPiece makeSShape(){
			TetrisPiece newPiece = new TetrisPiece();

			{
				//Orientation 0+2
			int[] row0={0,0,0,0,0};
			int[] row1={0,1,0,0,0};
			int[] row2={0,1,1,0,0};
			int[] row3={0,0,1,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(0, row0, row1, row2, row3, row4);
			newPiece.setShape(2, row0, row1, row2, row3, row4);
			}

			{
				//Orientation 1+3
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,1,1,0};
			int[] row2={0,1,1,0,0};
			int[] row3={0,0,0,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(1, row0, row1, row2, row3, row4);
			newPiece.setShape(3, row0, row1, row2, row3, row4);
			}
			return newPiece;

		}
		
		public static TetrisPiece makeZShape(){
			TetrisPiece newPiece = new TetrisPiece();

			{
				//Orientation 0+2
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,1,0,0};
			int[] row2={0,1,1,0,0};
			int[] row3={0,1,0,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(0, row0, row1, row2, row3, row4);
			newPiece.setShape(2, row0, row1, row2, row3, row4);
			}

			{
				//Orientation 1+3
			int[] row0={0,0,0,0,0};
			int[] row1={0,1,1,0,0};
			int[] row2={0,0,1,1,0};
			int[] row3={0,0,0,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(1, row0, row1, row2, row3, row4);
			newPiece.setShape(3, row0, row1, row2, row3, row4);
			}
			return newPiece;

		}
		
		public static TetrisPiece makeLShape(){
			TetrisPiece newPiece = new TetrisPiece();

			{
				//Orientation 0
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,1,0,0};
			int[] row2={0,0,1,0,0};
			int[] row3={0,0,1,1,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(0, row0, row1, row2, row3, row4);
			}
                        {
				//Orientation 1
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,0,0,0};
			int[] row2={0,1,1,1,0};
			int[] row3={0,1,0,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(1, row0, row1, row2, row3, row4);
			}
		
			{
				//Orientation 2
			int[] row0={0,0,0,0,0};
			int[] row1={0,1,1,0,0};
			int[] row2={0,0,1,0,0};
			int[] row3={0,0,1,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(2, row0, row1, row2, row3, row4);
			}
				{
				//Orientation 3
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,0,1,0};
			int[] row2={0,1,1,1,0};
			int[] row3={0,0,0,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(3, row0, row1, row2, row3, row4);
			}


			
			return newPiece;
		}
		
		public static TetrisPiece makeJShape(){
			TetrisPiece newPiece = new TetrisPiece();

			{
				//Orientation 0
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,1,0,0};
			int[] row2={0,0,1,0,0};
			int[] row3={0,1,1,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(0, row0, row1, row2, row3, row4);
			}
			{
				//Orientation 1
			int[] row0={0,0,0,0,0};
			int[] row1={0,1,0,0,0};
			int[] row2={0,1,1,1,0};
			int[] row3={0,0,0,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(1, row0, row1, row2, row3, row4);
			}

			{
				//Orientation 2
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,1,1,0};
			int[] row2={0,0,1,0,0};
			int[] row3={0,0,1,0,0};
			int[] row4={0,0,0,0,0};
			newPiece.setShape(2, row0, row1, row2, row3, row4);
			}
			{
				//Orientation 3
			int[] row0={0,0,0,0,0};
			int[] row1={0,0,0,0,0};
			int[] row2={0,1,1,1,0};
			int[] row3={0,0,0,1,0};
			int[] row4={0,0,0,0,0};
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
		TetrisPiece thePiece=makeTri();
		System.out.println(thePiece);
		thePiece.currentOrientation=1;
		System.out.println(thePiece);
		thePiece.currentOrientation=2;
		System.out.println(thePiece);
		thePiece.currentOrientation=3;
		System.out.println(thePiece);
	}


}
