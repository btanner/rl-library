package Tetrlais;

import java.util.Random;


import rlglue.*;

public class GameState{
	
	/*Action values*/
	static final int LEFT =0; /*Action value for a move left*/
	static final int RIGHT = 1; /*Action value for a move right*/
	static final int CW =2; /*Action value for a clockwise rotation*/
	static final int CCW =3; /*Action value for a counter clockwise rotation*/
	static final int NONE =4; /*The no-action Action*/
	
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
	private Block[] blockTable = {new Block(sbpO, sbp0rotate, sbp0rotate, 0), new Block(sbpI, sbpIrotate, sbpIrotate, 0), new Block(sbpL, sbpLcw, sbpLccw, 0)};

	private int current_block;/*which block we're using in the block table*/
	
	private int current_x;/* where the falling block is currently*/
	private int current_y;
	private int current_score;/* what is the current_score*/
	private boolean is_game_over;/*have we reached the end state yet*/
	private int world_width;/*how wide our board is*/
	private int world_height;/*how tall our board is*/
	private int[] world_state;/*what the world looks like without the current block*/
	private int[] world_observation;/*what the world looks like with the current block*/
	private Random random = new Random();
	
	public GameState(int width, int height){
		this.world_width = width;
		this.world_height = height;
		this.world_state = new int[width*height];
		this.world_observation = new int[width*height];
		this.reset();
	}

	public void reset(){
		// TODO Fill in Method
		this.current_x = this.world_width/2 -1;
		this.current_y = 0; 
		this.current_score =0;
		for(int i=0; i < world_state.length; i++){
			this.world_state[i] = 0;
			this.world_observation[i]=0;
		}

		this.is_game_over = false;
	}

	public Observation get_observation(){
		// TODO Fill in Method
		
		for(int i =0; i<this.world_observation.length; i++)
			this.world_observation[i] = this.world_state[i];
		write_block(this.current_x, this.current_y, (this.blockTable[this.current_block]).getCurrentPiece(), this.world_observation);
		Observation o = new Observation(this.world_observation.length,0);
		for(int i=0; i< this.world_observation.length; i++)
			o.intArray[i] = this.world_observation[i];					
		return o;
	}

	private void write_block(int current_x2, int current_y2, SingleBlockPiece block, int[] game_world) {
		// TODO Auto-generated method stub
		for ( int y = 0; y < block.getRows(); ++y )
		{
			for ( int x = 0; x < block.getColumns(); ++x )
		    {
				if( block.getBrick( y*block.getColumns() + x ) != 0 )
				{	
					game_world[ (current_y2 + y) * this.world_width + (current_x2 + x) ] = 1;
				}	
		    }
		  }
	}

	public int game_over(){
		// TODO Auto-generated method stub
		if(this.is_game_over)
			return 1;
		return  0;
	}

	public void take_action(Action action){
		// TODO Auto-generated method stub
		if ( this.game_over() ==1 ) return;

		  if ( action.intArray[0] == GameState.CW )
		  {
		    if (this.in_bounds( this.current_x, this.current_y, this.blockTable[this.current_block].get_rotated_block(Block.CLOCKWISE)) &&
		    	!this.is_colliding( this.current_x, this.current_y, this.blockTable[ this.current_block].get_rotated_block(Block.CLOCKWISE)))
		    {
		    	this.blockTable[ this.current_block].rotate(Block.CLOCKWISE);
		    }
		  }
		  else if ( action.intArray[0] == GameState.CCW )
		  {
		    if (this.in_bounds( this.current_x, this.current_y, this.blockTable[this.current_block].get_rotated_block(Block.COUNTERCLOCKWISE)) &&
		    	!this.is_colliding( this.current_x, this.current_y, this.blockTable[this.current_block].get_rotated_block(Block.COUNTERCLOCKWISE)))
		    {
		      this.blockTable[this.current_block].rotate(Block.COUNTERCLOCKWISE);
		    }
		  }
		  else if ( action.intArray[0] == GameState.LEFT)
		  {
		    if (
			this.in_bounds( this.current_x-1, this.current_y, this.blockTable[this.current_block].getCurrentPiece()) &&
			!this.is_colliding( this.current_x-1, this.current_y, this.blockTable[this.current_block].getCurrentPiece() ) 
		       )
		    {
		      this.current_x -= 1;
		    }
		  }
		  else if ( action.intArray[0] == GameState.RIGHT )
		  {
		    if ( 
			this.in_bounds(this.current_x+1, this.current_y, this.blockTable[this.current_block].getCurrentPiece()) &&
			!this.is_colliding( this.current_x+1, this.current_y, this.blockTable[this.current_block].getCurrentPiece() ) 
		       )
		    {
		      this.current_x += 1;
		    }
		  } 
		  else if ( action.intArray[0] == GameState.NONE )
		  {
		  }
		  else 
		  {
		    System.out.println("in GameState.java take_action(Action) you have provided an invalid Action type. No action has been taken.");
		  }

	}

	private boolean is_colliding(int X, int Y,SingleBlockPiece block) {
		// TODO Auto-generated method stub
		int collisions =0;
		for(int y =0; y< block.getRows(); ++y)
			for(int x =0; x< block.getColumns(); ++x)
				if(block.getBrick(y*block.getColumns()+x) != 0)
					collisions += this.world_state[(Y+y)*this.world_width+(X+x)];
		return collisions != 0;
	}

	private boolean in_bounds(int X, int Y,	SingleBlockPiece block) {
		// TODO Auto-generated method stub
		for(int y =0; y<block.getRows();++y)
			for(int x=0; x<block.getColumns();++x)
				if(block.getBrick(y*block.getColumns()+x) !=0)
					if(!(X+x>=0 && X+x < this.world_width && Y+y >= 0 && Y+y < this.world_height))
						return false;

		return true;
	}

	public boolean update() {
		// TODO Auto-generated method stub
		  if ( this.game_over() ==1 ) return false;

		  // Sanity check.  The game piece should always be in bounds.
		  if ( !this.in_bounds(  this.current_x, 
					  this.current_y, 
					  this.blockTable[ this.current_block ].getCurrentPiece() ) )
		  {
		    System.out.println("In GameState.Java the Current Position of the board is Out Of Bounds... Consistency Check Failed" );
		  }

		  if ( !this.in_bounds(  this.current_x, this.current_y+1, this.blockTable[ this.current_block ].getCurrentPiece() ) ||
		      this.is_colliding( this.current_x, this.current_y+1, this.blockTable[ this.current_block ].getCurrentPiece() ) 
		     )
		  { 
		    this.write_block( this.current_x,
				       this.current_y,
				       this.blockTable[ this.current_block ].getCurrentPiece(),
				       this.world_state );
		    this.score();
		    return this.spawn_block();
		  }

		  this.current_y += 1;
		  return true;
	}

	private boolean spawn_block() {
		// TODO Auto-generated method stub
		this.current_block = random.nextInt(this.blockTable.length);
		this.blockTable[this.current_block ].setCurrentBlockPiece(0);
		this.current_x = (int)(this.world_width /2) -1; 
		this.current_y =0;
		this.is_game_over = this.is_colliding( this.current_x, this.current_y, (this.blockTable[ this.current_block ]).getCurrentPiece() );
		return !(this.game_over()==1);
	}

	private void score() {
		// TODO Auto-generated method stub
		for(int y = this.world_height-1; y>=0; --y){
			if(this.is_row(y))
			{
				this.remove_row(y);
				this.current_score +=1;	
				y +=1;
			}			
		}
	}

	private boolean is_row(int y) {
		// TODO Auto-generated method stub
		int line_count = 0;
		for( int x = 0; x < this.world_width; ++x)
			line_count += this.world_state[y*this.world_width+x];	
		return line_count == this.world_width;
	}

	private void remove_row(int y) {
		// TODO Auto-generated method stub
		  if(!this.is_row(y)){
			  System.out.println("In GameState.java remove_row you have tried to remove a row which is not complete. Failed to remove row");
			  return;
		  }
		  for ( int x = 0; x < this.world_width; ++x )
		  {
		    this.world_state[ y*this.world_width + x ] = 0;
		  }

		  for ( int ty = y; ty > 0; --ty )
		  {
		    for ( int x = 0; x < this.world_width; ++x )
		    {
		      this.world_state[ ty*this.world_width + x ] = this.world_state[ (ty-1)*this.world_width + x ];
		    }
		  }
		
	}

	public int get_score() {
		// TODO Auto-generated method stub remember return -100 if terminal
			return this.current_score;
	}
	
	public int getWidth(){
		return this.world_width;
	}
	public int getHeight(){
		return this.world_height;
	}
	
	public int [] getWorldState(){
		return this.world_observation;
	}


}
