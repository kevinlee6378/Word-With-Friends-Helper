package application;

public class Tile {
	private char letter;
	private boolean hasScore;
	private boolean isNew;
	
	public Tile(char l, boolean hasScore, boolean isNew){
		this.letter = l;
		this.hasScore = hasScore;
		this.isNew = isNew;
	}
	
	public char getLetter() {
		return this.letter;
	}
	
	public boolean hasScore() {
		return this.hasScore;
	}
	
	public boolean isNew() {
		return this.isNew;
	}
}
