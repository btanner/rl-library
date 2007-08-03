package Tetrlais;

import java.util.Random;
import java.util.Vector;

import rlglue.types.Action;
import rlglue.types.Observation;

public class GameState{

	/*Action values*/
	static final int LEFT =0; /*Action value for a move left*/
	static final int RIGHT = 1; /*Action value for a move right*/
	static final int CW =2; /*Action value for a clockwise rotation*/
	static final int CCW =3; /*Action value for a counter clockwise rotation*/
	static final int NONE =4; /*The no-action Action*/
	static final int FALL = 5; /* fall down */


	private int currentBlockId;/*which block we're using in the block table*/
	private int currentRotation=0;

	private int currentX;/* where the falling block is currently*/
	private int currentY;
	private int score;/* what is the current_score*/
	private boolean is_game_over;/*have we reached the end state yet*/
	private int worldWidth;/*how wide our board is*/
	private int worldHeight;/*how tall our board is*/
	private int[] worldState;/*what the world looks like without the current block*/
	private int[] worldObservation;/*what the world looks like with the current block*/
	private Random random = new Random();
	Vector<TetrlaisPiece> possibleBlocks=null;

	public GameState(int width, int height, Vector<TetrlaisPiece> possibleBlocks){
		worldWidth = width;
		worldHeight = height;
		worldState = new int[width*height];
		worldObservation = new int[width*height];
		this.possibleBlocks=possibleBlocks;
		reset();
	}

	public void reset(){
		currentX = worldWidth/2 -1;
		currentY = 0; 
		score =0;
		for(int i=0; i < worldState.length; i++){
			worldState[i] = 0;
			worldObservation[i]=0;
		}
		currentRotation=0;
		is_game_over = false;
	}

	public Observation get_observation(){
		for(int i =0; i<worldObservation.length; i++)
			worldObservation[i] = worldState[i];

		writeCurrentBlock(worldObservation);
		Observation o = new Observation(worldObservation.length,0);
		for(int i=0; i< worldObservation.length; i++)
			o.intArray[i] = worldObservation[i];

		return o;
	}

	private void writeCurrentBlock( int[] game_world){
		int[][] thisPiece=possibleBlocks.get(currentBlockId).getShape(currentRotation);

		for ( int y = 0; y < thisPiece.length; ++y ){
			for ( int x = 0; x < thisPiece[0].length; ++x ){
				if( thisPiece[x][y] != 0 ){	
					game_world[ (currentY + y) * worldWidth + (currentX + x) ] = 1;
				}	
			}
		}

	}


	public boolean gameOver(){
		return is_game_over;
	}

	public void take_action(Action action){
		if (gameOver()) return;

		int theAction=action.intArray[0];

		int nextRotation=currentRotation;
		int nextX=currentX;
		int nextY=currentY;

		switch (theAction) {
		case  GameState.CW:
			nextRotation=(currentRotation+1)%4;
			break;
		case  GameState.CCW:
			nextRotation=(currentRotation-1);
			if(nextRotation<0)nextRotation=3;
			break;
		case  GameState.LEFT:
			nextX=currentX-1;
			break;
		case  GameState.RIGHT:
			nextX=currentX+1;
			break;
		case GameState.FALL:
			nextY=currentY;
			
			boolean isInBounds=true;
			boolean isColliding=false;
			
			//Fall until you hit something then back up once
			while(isInBounds&&!isColliding){
				nextY++;
				isInBounds=inBounds(nextX,nextY,nextRotation);
				if(isInBounds)isColliding=colliding(nextX, nextY, nextRotation);
			}
			nextY--;
		default:
			break;
		}
		if(inBounds(nextX,nextY,nextRotation))
			if(!colliding(nextX,nextY,nextRotation)){
				currentRotation=nextRotation;
				currentX=nextX;
				currentY=nextY;
			}

	}

	private boolean colliding(int checkX, int checkY, int checkOrientation) {
		int[][] thePiece=possibleBlocks.get(currentBlockId).getShape(checkOrientation);
		int collisions =0;
		for(int y =0; y<thePiece.length;++y)
			for(int x=0; x<thePiece[0].length;++x)
				if(thePiece[x][y]!=0)
					collisions += worldState[(checkY+y)*worldWidth+(checkX+x)];
		return collisions != 0;
	}

	private boolean inBounds(int checkX, int checkY, int checkOrientation)  {
		int[][] thePiece=possibleBlocks.get(currentBlockId).getShape(checkOrientation);

		for(int y =0; y<thePiece.length;++y)
			for(int x=0; x<thePiece[0].length;++x)
				if(thePiece[x][y]!=0)
					if(!(checkX+x>=0 && checkX+x < worldWidth && checkY+y >= 0 && checkY+y < worldHeight))
						return false;

		return true;
	}

	private boolean nextInBounds(){
		return inBounds(currentX, currentY+1,currentRotation);
	}
	private boolean nextColliding(){
		return colliding(currentX, currentY+1,currentRotation);	
	}

	public void update() {
		if (gameOver()) return;

		// Sanity check.  The game piece should always be in bounds.
		if (!inBounds(currentX,currentY,currentRotation))System.out.println("In GameState.Java the Current Position of the board is Out Of Bounds... Consistency Check Failed" );

		//Need to be careful here because can't check nextColliding if not in bounds
		boolean doneFalling=false;
		if(!nextInBounds())doneFalling=true;
		if(!doneFalling)if(nextColliding())doneFalling=true;

		if (doneFalling){
			writeCurrentBlock(worldState);
			checkIfRowAndScore();
			spawn_block();
		}else{
			//fall
			currentY += 1;
		}
	}

	private void spawn_block() {
		currentBlockId = random.nextInt(possibleBlocks.size());
		currentRotation=0;
		currentX = (int)(worldWidth /2)-2; 
		currentY =0;
		is_game_over = colliding(currentX,currentY,currentRotation);
	}

	private void printMatrix(){
		for(int y = worldHeight-1; y>=0; --y){
			for( int x = 0; x < worldWidth; ++x){
				System.out.print(worldState[y*worldWidth+x]);	
			}
			System.out.println();

		}
	}

	private void checkIfRowAndScore() {
		for(int y = worldHeight-1; y>=0; --y){
			if(isRow(y))
			{
				removeRow(y);
				score +=1;	
				y +=1;
			}			
		}
	}

	private boolean isRow(int y) {
		int line_count = 0;
		for( int x = 0; x < worldWidth; ++x)
			line_count += worldState[y*worldWidth+x];	
		return line_count == worldWidth;
	}

	private void removeRow(int y) {
		if(!isRow(y)){
			System.out.println("In GameState.java remove_row you have tried to remove a row which is not complete. Failed to remove row");
			return;
		}
		for ( int x = 0; x < worldWidth; ++x )
		{
			worldState[ y*worldWidth + x ] = 0;
		}

		for ( int ty = y; ty > 0; --ty )
		{
			for ( int x = 0; x < worldWidth; ++x )
			{
				worldState[ ty*worldWidth + x ] = worldState[ (ty-1)*worldWidth + x ];
			}
		}

	}

	public int get_score() {
		return score;
	}

	public int getWidth(){
		return worldWidth;
	}
	public int getHeight(){
		return worldHeight;
	}

	public int [] getWorldState(){
		return worldObservation;
	}


}
