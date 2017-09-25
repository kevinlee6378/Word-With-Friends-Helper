package application;

public class WWFBoard {

	public Tile[][] board;
	public WWFBoard() {
		this.board = new Tile[11][11];
	}
	
	public WWFBoard(WWFBoard prevBoard) {
		this.board = prevBoard.board;
	}
	
	public void addTile(Tile t, int x, int y) {
		this.board[x][y] = t;
	}
	
	public void printBoard() {
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				Tile t = this.board[i][j];
				if (t != null) {
					System.out.print(t.letter);
				}
				else {
					System.out.print(" ");
				}
				if (j == 10) {
					System.out.print("\n");
				}
				else {
					System.out.print("|");
				}
			}
		}
	}
}
