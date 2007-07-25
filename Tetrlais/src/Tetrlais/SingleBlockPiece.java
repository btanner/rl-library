package Tetrlais;

public class SingleBlockPiece {
	private int[] block;
	private int rows;
	private int columns;
	
	public SingleBlockPiece(){
		this.block = new int[4];
		this.rows = 2;
		this.columns = 2;
	}
	
	public SingleBlockPiece(int[] in, int x, int y){	
		this.block = in; 
		this.rows = y;
		this.columns = x;
	}
	
	public SingleBlockPiece(int tl, int tr, int bl, int br){
		this.block = new int[4];
		this.block[0] = tl;
		this.block[1] = tr;
		this.block[2] = bl;
		this.block[3] = br;
		this.rows = 2;
		this.columns = 2;
	}
	
	public int getBrick(int i){
		return this.block[i];
	}
	public int getRows(){
		return this.rows;
	}
	public int getColumns(){
		return this.columns;
	}
	
}
