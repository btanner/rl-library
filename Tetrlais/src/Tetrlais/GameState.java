package Tetrlais;

import java.util.Random;


import rlglue.*;
import rlglue.types.Action;
import rlglue.types.Observation;

public class GameState{

	/*Action values*/
	static final int LEFT =0; /*Action value for a move left*/
	static final int RIGHT = 1; /*Action value for a move right*/
	static final int CW =2; /*Action value for a clockwise rotation*/
	static final int CCW =3; /*Action value for a counter clockwise rotation*/
	static final int NONE =4; /*The no-action Action*/
	static final int FALL = 5; /* fall down *

	/*creating bricks*/
	SingleBlockPiece sbpO1 = new SingleBlockPiece(1,1,1,1);// the "O brick" all four squares 
	SingleBlockPiece[] sbpO = {sbpO1}; 
	int[] sbp0rotate = {0};// rotation matrix for the O brick
	SingleBlockPiece sbpI1 = new SingleBlockPiece(1,1,0,0); //The I brick. the i brick on it's side (a 2X1 brick)
	SingleBlockPiece sbpI2 = new SingleBlockPiece(1,0,1,0); // the I brick standing up (a 1X2 brick)
	SingleBlockPiece[] sbpI = {sbpI1, sbpI2};
	int[] sbpIrotate = {1,0};//rotation for the I brick
	SingleBlockPiece sbpL1 = new SingleBlockPiece(1,0,1,1); //the L brick. top right square missing
	SingleBlockPiece sbpL2 = new SingleBlockPiece(1,1,1,0); //L brick  bottom right square missing
	SingleBlockPiece sbpL3 = new SingleBlockPiece(1,1,0,1); // the L Brick,bottom left square missing
	SingleBlockPiece sbpL4 = new SingleBlockPiece(0,1,1,1); // the L brick, top left square missing
	SingleBlockPiece[] sbpL = {sbpL1, sbpL2,sbpL3,sbpL4};
	int[] sbpLcw = {1,2,3,0}; // clock wise rotation matrix for L brick
	int[] sbpLccw = {3,0,1,2}; //counter clock wise rotation matrix for L brick

	/*Hold all the possible bricks that can fall*/
//	The right thing
	private Block[] blockTable = {new Block(sbpO, sbp0rotate, sbp0rotate, 0), new Block(sbpI, sbpIrotate, sbpIrotate, 0), new Block(sbpL, sbpLcw, sbpLccw, 0)};
//	private Block[] blockTable = {new Block(sbpO, sbp0rotate, sbp0rotate, 0),new Block(sbpI, sbpIrotate, sbpIrotate, 0)};

	private int current_block;/*which block we're using in the block table*/

	private int current_x;/* where the falling block is currently*/
	private int current_y;
	private int current_score;/* what is the current_score*/
	private boolean is_game_over;/*have we reached the end state yet*/
	private int worldWidth;/*how wide our board is*/
	private int world_height;/*how tall our board is*/
	private int[] worldState;/*what the world looks like without the current block*/
	private int[] world_observation;/*what the world looks like with the current block*/
	private Random random = new Random();

	public GameState(int width, int height){
		worldWidth = width;
		world_height = height;
		worldState = new int[width*height];
		world_observation = new int[width*height];
		reset();
	}

	public void reset(){
		current_x = worldWidth/2 -1;
		current_y = 0; 
		current_score =0;
		for(int i=0; i < worldState.length; i++){
			worldState[i] = 0;
			world_observation[i]=0;
		}

		is_game_over = false;
	}

	public Observation get_observation(){
		for(int i =0; i<world_observation.length; i++)
			world_observation[i] = worldState[i];

//		write_block(current_x, current_y, (blockTable[current_block]).getCurrentPiece(), world_observation);
		writeCurrentBlock(world_observation);
		Observation o = new Observation(world_observation.length,0);
		for(int i=0; i< world_observation.length; i++)
			o.intArray[i] = world_observation[i];

		return o;
	}

	private void writeCurrentBlock( int[] game_world){
//		write_block(current_x, current_y, (blockTable[current_block]).getCurrentPiece(), world_observation);
		SingleBlockPiece thisBlock=blockTable[current_block].getCurrentPiece();
		for ( int y = 0; y < thisBlock.getRows(); ++y ){
			for ( int x = 0; x < thisBlock.getColumns(); ++x ){
				if( thisBlock.getBrick( y*thisBlock.getColumns() + x ) != 0 ){	
					game_world[ (current_y + y) * worldWidth + (current_x + x) ] = 1;
				}	
			}
		}

	}


	public boolean gameOver(){
		return is_game_over;
	}

	public void take_action(Action action){
		if (gameOver()) return;

		if ( action.intArray[0] == GameState.CW )
		{
			if (checkInBounds( current_x, current_y, blockTable[current_block].get_rotated_block(Block.CLOCKWISE)) &&
					!checkCollision( current_x, current_y, blockTable[ current_block].get_rotated_block(Block.CLOCKWISE)))
			{
				blockTable[ current_block].rotate(Block.CLOCKWISE);
			}
		}
		else if ( action.intArray[0] == GameState.CCW )
		{
			if (checkInBounds( current_x, current_y, blockTable[current_block].get_rotated_block(Block.COUNTERCLOCKWISE)) &&
					!checkCollision( current_x, current_y, blockTable[current_block].get_rotated_block(Block.COUNTERCLOCKWISE)))
			{
				blockTable[current_block].rotate(Block.COUNTERCLOCKWISE);
			}
		}
		else if ( action.intArray[0] == GameState.LEFT)
		{
			if (
					checkInBounds( current_x-1, current_y, blockTable[current_block].getCurrentPiece()) &&
					!checkCollision( current_x-1, current_y, blockTable[current_block].getCurrentPiece() ) 
			)
			{
				current_x -= 1;
			}
		}
		else if ( action.intArray[0] == GameState.RIGHT )
		{
			if ( 
					checkInBounds(current_x+1, current_y, blockTable[current_block].getCurrentPiece()) &&
					!checkCollision( current_x+1, current_y, blockTable[current_block].getCurrentPiece() ) 
			)
			{
				current_x += 1;
			}
		} 
		else if ( action.intArray[0] == GameState.NONE )
		{
		}
		else if (action.intArray[0] == GameState.FALL){
			boolean changed=false;

			//remember we should only call nextColliding if nextInBounds!
			while(nextInBounds()&&!nextColliding()){
				current_y++;
				changed=true;
			}
			if(changed)	current_y--;
		}
		else 
		{
			System.out.println("in GameState.java take_action(Action) you have provided an invalid Action type. No action has been taken.");
		}

	}

	private boolean nextInBounds(){
		return checkInBounds(current_x, current_y+1, blockTable[ current_block ].getCurrentPiece());
	}
	private boolean nextColliding(){
		return checkCollision(current_x, current_y+1, blockTable[current_block ].getCurrentPiece());	
	}

	private boolean currentlyInBounds(){
		return checkInBounds(current_x, current_y, blockTable[ current_block ].getCurrentPiece());
	}
	private boolean currentlyColliding(){
		return checkCollision(current_x, current_y, blockTable[ current_block ].getCurrentPiece());
	}

	private boolean checkCollision(int X, int Y,SingleBlockPiece block) {
		int collisions =0;
		for(int y =0; y< block.getRows(); ++y)
			for(int x =0; x< block.getColumns(); ++x)
				if(block.getBrick(y*block.getColumns()+x) != 0)
					collisions += worldState[(Y+y)*worldWidth+(X+x)];
		return collisions != 0;
	}

	private boolean checkInBounds(int X, int Y,	SingleBlockPiece block) {
		for(int y =0; y<block.getRows();++y)
			for(int x=0; x<block.getColumns();++x)
				if(block.getBrick(y*block.getColumns()+x) !=0)
					if(!(X+x>=0 && X+x < worldWidth && Y+y >= 0 && Y+y < world_height))
						return false;

		return true;
	}
//	Not sure why update used to return a boolean? It seemed meaningless?

	public void update() {
		if (gameOver()) return;

		// Sanity check.  The game piece should always be in bounds.
		if (!currentlyInBounds())System.out.println("In GameState.Java the Current Position of the board is Out Of Bounds... Consistency Check Failed" );

		//The piece is done falling

		//Need to be careful here because can't check nextColliding if not in bounds
		boolean doneFalling=false;
		if(!nextInBounds())doneFalling=true;
		if(!doneFalling)
			if(nextColliding())doneFalling=true;

		if (doneFalling){
			writeCurrentBlock(worldState);
			checkIfRowAndScore();
			spawn_block();
		}else{
			//fall
			current_y += 1;
		}
	}

	//Returns whether the game should keep going or not?
	private void spawn_block() {
		// TODO Auto-generated method stub
		current_block = random.nextInt(blockTable.length);
		blockTable[current_block ].setCurrentBlockPiece(0);
		current_x = (int)(worldWidth /2) -1; 
		current_y =0;
		is_game_over = checkCollision( current_x, current_y, (blockTable[ current_block ]).getCurrentPiece() );
	}

	private void printMatrix(){
		for(int y = world_height-1; y>=0; --y){
			for( int x = 0; x < worldWidth; ++x){
				System.out.print(worldState[y*worldWidth+x]);	
			}
			System.out.println();

		}
	}

	private void checkIfRowAndScore() {
		for(int y = world_height-1; y>=0; --y){
			if(isRow(y))
			{
				removeRow(y);
				current_score +=1;	
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
		// TODO Auto-generated method stub remember return -100 if terminal
		return current_score;
	}

	public int getWidth(){
		return worldWidth;
	}
	public int getHeight(){
		return world_height;
	}

	public int [] getWorldState(){
		return world_observation;
	}


}
