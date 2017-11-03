package application;

public class BoardScore {
	private int score;
	private WWFBoard board;
	
	public BoardScore(WWFBoard board, int score) {
		this.score = score;
		this.board = board;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public WWFBoard getBoard() {
		return this.board;
	}
}
