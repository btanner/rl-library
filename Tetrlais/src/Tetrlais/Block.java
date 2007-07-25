package Tetrlais;

public class Block {
	private SingleBlockPiece[] matrix;
	private int[] rotate_cw;
	private int[] rotate_ccw;
	private int currentBlockPiece;
	static final int CLOCKWISE = 0;
	static final int COUNTERCLOCKWISE = 1;
	
	public Block(SingleBlockPiece[] matrix, int[] rcw, int[] rccw, int current){
			this.matrix = matrix;
			this.rotate_cw = rcw;
			this.rotate_ccw = rccw;
			this.currentBlockPiece = current;
	}
	
	public Block(){
		this.matrix = new SingleBlockPiece[4];
		this.rotate_cw = new int[4];
		this.rotate_ccw = new int[4];
		this.currentBlockPiece = 0;	
	}
	
	public SingleBlockPiece getBlockPiece(int i){
		if((i>0)&&(i< this.matrix.length))
		return this.matrix[i];
		else{
			System.out.println("In Block.java getBlockPiece(int i) you have chosen an out of bounds value for i. There is no piece at index i");
			return null;
		}
	}
	public void setBlockPiece(int i, SingleBlockPiece sbp)
	{	if((i >0) && (i< this.matrix.length))
		this.matrix[i] = sbp;
		else	
			System.out.println("In Block.java setBlockPiece(int i, singleblockpiece sbp) you have chosen an out of bounds value for i. There is no index for i");
	}
	public SingleBlockPiece getCurrentPiece(){
		return this.matrix[this.currentBlockPiece];
	}
	        
	public void rotate(int i){
		if(i == Block.CLOCKWISE)
			this.currentBlockPiece = this.rotate_cw[this.currentBlockPiece];
		else if(i ==Block.COUNTERCLOCKWISE)
			this.currentBlockPiece = this.rotate_ccw[this.currentBlockPiece];
		else
			System.out.println("in Block.java rotate(int i) you have chosen an invalid value. Try using Block.CLOCKWISE or Block.COUNTERCLOCKWISE.");
	}
	
	public SingleBlockPiece get_rotated_block(int i){
		if(i == this.CLOCKWISE)
			return this.matrix[this.rotate_cw[this.currentBlockPiece]];
		else if(i == this.COUNTERCLOCKWISE)
			return this.matrix[this.rotate_ccw[this.currentBlockPiece]];
		else{
			System.out.println("in Block.java get_rotated_block(int i) you have chosen an invalid value. Try using Block.CLOCKWISE or Block.COUNTERCLOCKWISE.");
			return null;
		}
	}
	public void setCurrentBlockPiece(int i){
		this.currentBlockPiece = i;
	}
	
	
}
