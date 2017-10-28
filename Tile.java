package application;

public class Tile {
	public char letter;
	public int score;
	public boolean hasScore;
	public Tile(char l, boolean b) {
		this.letter = l;
		this.hasScore = b;
		switch(l) {
			case 'A':
				this.score = 1;
				break;
			case 'B':
				this.score = 4;
				break;
			case 'C':
				this.score = 4;
				break;
			case 'D':
				this.score = 2;
				break;
			case 'E':
				this.score = 1;
				break;
			case 'F':
				this.score = 4;
				break;
			case 'G':
				this.score = 3;
				break;
			case 'H':
				this.score = 3;
				break;
			case 'I':
				this.score = 1;
				break;
			case 'J':
				this.score = 10;
				break;
			case 'K':
				this.score = 5;
				break;
			case 'L':
				this.score = 2;
				break;
			case 'M':
				this.score = 4;
				break;
			case 'N':
				this.score = 2;
				break;
			case 'O':
				this.score = 1;
				break;
			case 'P':
				this.score = 4;
				break;
			case 'Q':
				this.score = 10;
				break;
			case 'R':
				this.score = 1;
				break;
			case 'S':
				this.score = 1;
				break;
			case 'T':
				this.score = 1;
				break;
			case 'U':
				this.score = 2;
				break;
			case 'V':
				this.score = 5;
				break;
			case 'W':
				this.score = 4;
				break;
			case 'X':
				this.score = 8;
				break;
			case 'Y':
				this.score = 3;
				break;
			case 'Z':
				this.score = 10;
				break;
			case '?':
				this.score = 0;
				break;
		}
	}
}