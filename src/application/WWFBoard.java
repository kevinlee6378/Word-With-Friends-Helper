package application;

public class WWFBoard {
	private static int boardLength = 11;
	private static int boardHeight = 11;
	private Tile[][] board;
	
	public WWFBoard() {
		this.board = new Tile[boardHeight][boardLength];
	}

	public WWFBoard(WWFBoard prevBoard) {
		this.board = new Tile[boardHeight][boardLength];
		for(int i = 0; i < boardHeight; i++) {
			for (int j = 0; j < boardLength; j++){
				if (prevBoard.board[i][j] != null){
					char letter = prevBoard.board[i][j].getLetter();
					boolean hasScore = prevBoard.board[i][j].hasScore();
					boolean isNew = prevBoard.board[i][j].isNew();
					this.board[i][j] = new Tile(letter,hasScore, isNew);
				}
			}
		}

	}
	public Tile[][] getBoard() {
		return this.board;
	}

	public void addTile(Tile t, int i, int j) {
		this.board[i][j] = t;
	}

	public void printBoard() {
		for (int i = 0; i < boardHeight; i++) {
			for (int j = 0; j < boardLength; j++) {
				Tile t = this.board[i][j];
				if (t != null) {
					System.out.print(t.getLetter());
				}
				else {
					System.out.print(" ");
				}
				if (j == boardLength - 1) {
					System.out.print("|\n");
				}
				else {
					System.out.print("|");
				}
			}
		}
	}
}
