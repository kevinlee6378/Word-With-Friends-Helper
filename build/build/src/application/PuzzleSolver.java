package application;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class PuzzleSolver {
	private static int[] tileScores = {1,4,4,2,1,4,3,3,1,10,5,2,4,2,1,4,10,1,1,1,2,5,4,8,3,10};
	//private static ExecutorService executor = Executors.newCachedThreadPool();
	private DictionaryTree dt;
	private String[][] codes;
	public PuzzleSolver(DictionaryTree dt, String[][] codes) {
		this.dt = dt;
		this.codes = codes;
	}

	public BoardScore solvePuzzle(WWFBoard board, LinkedList<Character> letters) {
		//		int maxScore = 0;
		//		WWFBoard maxBoard = new WWFBoard(board);
		//		boolean hasSomething = false;
		//		for (int i = 0; i < board.board.length; i++) {
		//			for (int j = 0; j < board.board[0].length; j++) {
		//				if (board.board[i][j] != null) {
		//					hasSomething = true;
		//
		//					BoardScore a = solvePuzzleKernel(board, letters, dt, false, j, j , i);
		//					BoardScore b = solvePuzzleKernel(board, letters, dt, true, i, i, j);
		//
		//					if (a.score > maxScore) {
		//						maxScore = a.score;
		//						maxBoard = a.board;
		//					}
		//					if (b.score > maxScore) {
		//						maxScore = b.score;
		//						maxBoard = b.board;
		//					}
		//
		//				}
		//			}
		//		}
		int maxScore[] = new int[2];
		WWFBoard[] maxBoard = new WWFBoard[2];
		for(int i = 0; i < maxBoard.length; i++) {
			maxBoard[i] = new WWFBoard(board);
		}
		boolean hasSomething[] = new boolean[2];
		Thread A = new Thread(() -> {
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board[0].length; j++) {
					if (board.board[i][j] != null) {
						hasSomething[0] = true;

						BoardScore a = solvePuzzleKernel(board, letters, false, j, j , i);

						if (a.score > maxScore[0]) {
							maxScore[0] = a.score;
							maxBoard[0] = a.board;
						}
					}
				}
			}
		});
		//Future<?> futureA = executor.submit(() ->{

		//});
		A.start();
		Thread B = new Thread(() -> {
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board[0].length; j++) {
					if (board.board[i][j] != null) {
						hasSomething[1] = true;

						BoardScore b = solvePuzzleKernel(board, letters, true, i, i, j);

						if (b.score > maxScore[1]) {
							maxScore[1] = b.score;
							maxBoard[1] = b.board;
						}

					}
				}
			}
		});
		B.start();

		try {
			A.join();
			B.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!hasSomething[0] && !hasSomething[1]) {
			char c = letters.removeLast();
			board.addTile(new Tile(c,true), 5, 5);
			BoardScore a = solvePuzzleKernel(board, letters, false, 5, 5 , 5);
			BoardScore b = solvePuzzleKernel(board, letters, true, 5, 5, 5);
			if (a.score > maxScore[0]) {
				maxScore[0] = a.score;
				maxBoard[0] = a.board;
			}
			if (b.score > maxScore[1]) {
				maxScore[1] = b.score;
				maxBoard[1] = b.board;
			}

		}

		return maxScore[0] > maxScore[1] ? new BoardScore(maxBoard[0], maxScore[0]) : new BoardScore(maxBoard[1],maxScore[1]);

	}

	public BoardScore solvePuzzleKernel(WWFBoard board, LinkedList<Character> letters, boolean isVertical,int start, int end, int index) {
		int maxScore = 0;
		WWFBoard maxBoard = new WWFBoard(board);
		for(char t : letters) {

			if (t == '?') {
				LinkedList<Character> newLetters = new LinkedList<>();
				boolean hasRemoved = false;
				for (char i : letters){
					if (i != t) {
						newLetters.add(i);
					}
					else {
						if (hasRemoved) {
							newLetters.add(i);
						}
						else {
							hasRemoved = true;
						}
					}

				}
				for (char c = 'a'; c <= 'z'; c++) {

					//put to the left (!isVertical) or up (isVertical)
					WWFBoard newBoard = new WWFBoard(board);
					int newRow = isVertical? nextBefore(board,isVertical,start, index) : index;
					int newColumn = isVertical? index : nextBefore(board,isVertical,start,index);
					boolean needToContinue = newRow != -1 && newColumn != -1;
					if (needToContinue) {
						boolean withinBounds = newRow < 11 && newColumn < 11 && newRow > 0 && newColumn > 0;
						boolean checkAround = checkSides(board, isVertical, newRow, newColumn);
						if (withinBounds && checkAround) {
							newBoard.addTile(new Tile(c,false),newRow, newColumn);
							int newStartPlaced = isVertical? newRow : newColumn;
							int newStartOfWord = startOfWord(newBoard, isVertical, newStartPlaced, index);
							int endOfWord = endOfWord(newBoard, isVertical, newStartPlaced, index);
							String newWord = getWord(newBoard, isVertical, newStartOfWord, endOfWord, index);
							if (dt.isValidWord(newWord)){
								int score = getWordScore(newBoard, isVertical, newStartOfWord, end, index);
								if (score > maxScore) {
									maxScore = score;
									maxBoard = newBoard;
								}
							}
							BoardScore recurse = solvePuzzleKernel(newBoard, newLetters, isVertical, newStartOfWord, endOfWord, index);
							//newBoard.printBoard();
							if (recurse.score > maxScore) {
								maxScore = recurse.score;
								maxBoard = recurse.board;
							}

						}
					}
					//put to the right or below
					WWFBoard newBoard2 = new WWFBoard(board);
					int newRow2 = isVertical? nextAfter(board,isVertical,start, index) : index;
					int newColumn2 = isVertical? index : nextAfter(board,isVertical,start, index);
					boolean needToContinue2 = newRow2 != -1 && newColumn2 != -1;
					if (needToContinue2) {
						boolean withinBounds2 = newRow2 < 11 && newColumn2 < 11 && newRow2 > 0 && newColumn2 > 0;
						boolean checkAround2 = checkSides(board, isVertical, newRow2, newColumn2);
						if (withinBounds2 && checkAround2) {
							newBoard2.addTile(new Tile(c,false),newRow2, newColumn2);
							int newEndPlaced = isVertical? newRow2 : newColumn2;
							int newEndOfWord = endOfWord(newBoard2, isVertical, newEndPlaced, index);
							int startOfWord2 = startOfWord(newBoard2, isVertical, newEndPlaced, index);
							String newWord2 = getWord(newBoard2, isVertical, startOfWord2, newEndOfWord, index);
							//System.out.println(newWord2);
							//System.out.println(dt.isValidWord(newWord2));
							if (dt.isValidWord(newWord2)){
								int score = getWordScore(newBoard2, isVertical, startOfWord2, newEndOfWord, index);
								if (score > maxScore) {
									maxScore = score;
									maxBoard = newBoard2;
								}
							}
							//newBoard2.printBoard();
							BoardScore recurse2 = solvePuzzleKernel(newBoard2, newLetters, isVertical, start, newEndOfWord, index);
							//newBoard.printBoard();
							if (recurse2.score > maxScore) {
								maxScore = recurse2.score;
								maxBoard = recurse2.board;
							}

						}
					}
				}
			}
			else {
				LinkedList<Character> newLetters = new LinkedList<>();
				boolean hasRemoved = false;
				for (char i : letters){
					if (i != t) {
						newLetters.add(i);
					}
					else {
						if (hasRemoved) {
							newLetters.add(i);
						}
						else {
							hasRemoved = true;
						}
					}

				}
				//put to the left (!isVertical) or up (isVertical)
				WWFBoard newBoard = new WWFBoard(board);
				int newRow = isVertical? nextBefore(board,isVertical,start, index) : index;
				int newColumn = isVertical? index : nextBefore(board,isVertical,start,index);
				boolean needToContinue = newRow != -1 && newColumn != -1;
				if (needToContinue) {
					boolean withinBounds = newRow < 11 && newColumn < 11 && newRow > 0 && newColumn > 0;
					boolean checkAround = checkSides(board, isVertical, newRow, newColumn);
					if (withinBounds && checkAround) {
						newBoard.addTile(new Tile(t,true),newRow, newColumn);
						int newStartPlaced = isVertical? newRow : newColumn;
						int newStartOfWord = startOfWord(newBoard, isVertical, newStartPlaced, index);
						int endOfWord = endOfWord(newBoard, isVertical, newStartPlaced, index);
						String newWord = getWord(newBoard, isVertical, newStartOfWord, endOfWord, index);
						if (dt.isValidWord(newWord)){
							int score = getWordScore(newBoard, isVertical, newStartOfWord, end, index);
							if (score > maxScore) {
								maxScore = score;
								maxBoard = newBoard;
							}
						}
						BoardScore recurse = solvePuzzleKernel(newBoard, newLetters, isVertical, newStartOfWord, endOfWord, index);
						//newBoard.printBoard();
						if (recurse.score > maxScore) {
							maxScore = recurse.score;
							maxBoard = recurse.board;
						}

					}
				}
				//put to the right or below
				WWFBoard newBoard2 = new WWFBoard(board);
				int newRow2 = isVertical? nextAfter(board,isVertical,start, index) : index;
				int newColumn2 = isVertical? index : nextAfter(board,isVertical,start, index);
				boolean needToContinue2 = newRow2 != -1 && newColumn2 != -1;
				if (needToContinue2) {
					boolean withinBounds2 = newRow2 < 11 && newColumn2 < 11 && newRow2 > 0 && newColumn2 > 0;
					boolean checkAround2 = checkSides(board, isVertical, newRow2, newColumn2);
					if (withinBounds2 && checkAround2) {
						newBoard2.addTile(new Tile(t,true),newRow2, newColumn2);
						int newEndPlaced = isVertical? newRow2 : newColumn2;
						int newEndOfWord = endOfWord(newBoard2, isVertical, newEndPlaced, index);
						int startOfWord2 = startOfWord(newBoard2, isVertical, newEndPlaced, index);
						String newWord2 = getWord(newBoard2, isVertical, startOfWord2, newEndOfWord, index);
						//System.out.println(newWord2);
						//System.out.println(dt.isValidWord(newWord2));
						if (dt.isValidWord(newWord2)){
							int score = getWordScore(newBoard2, isVertical, startOfWord2, newEndOfWord, index);
							if (score > maxScore) {
								maxScore = score;
								maxBoard = newBoard2;
							}
						}
						//newBoard2.printBoard();
						BoardScore recurse2 = solvePuzzleKernel(newBoard2, newLetters, isVertical, start, newEndOfWord, index);
						//newBoard.printBoard();
						if (recurse2.score > maxScore) {
							maxScore = recurse2.score;
							maxBoard = recurse2.board;
						}

					}
				}
			}
		}
		return new BoardScore(maxBoard,maxScore);
	}

	public static int nextBefore(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current - 1;
		while (i >= 0) {
			boolean isBeforeNull = isVertical? board.board[i][index] == null : board.board[index][i] == null;
			if (isBeforeNull) {
				return i;
			}
			i--;
		}
		return -1;
	}
	public static int nextAfter(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current + 1;
		while (i < 11) {
			boolean isAfterNull = isVertical? board.board[i][index] == null : board.board[index][i] == null;
			if (isAfterNull) {
				return i;
			}
			i++;
		}
		return -1;
	}
	public static boolean checkSides(WWFBoard board, boolean isVertical, int i, int j) {
		int top = i-1;
		int bottom = i+1;
		int right = j+1;
		int left = j-1;
		boolean canCheckRight = (right) < board.board[0].length;
		boolean canCheckLeft = (left) >= 0;
		boolean canCheckTop = (top) > 0;
		boolean canCheckBottom = (bottom) < board.board.length;

		if (isVertical) {
			if (canCheckRight) {
				if (board.board[i][right] != null){
					return false;
				}
			}
			if (canCheckLeft) {
				if (board.board[i][left] != null) {
					return false;
				}
			}
		}
		else {
			if (canCheckTop) {
				if (board.board[top][j] != null) {
					return false;
				}
			}
			if (canCheckBottom) {
				if (board.board[bottom][j] != null) {
					return false;
				}

			}
		}

		return true;
	}
	public boolean checkSidesAdvanced(WWFBoard board, boolean isVertical, int i, int j, char c) {
		int top = i-1;
		int bottom = i+1;
		int right = j+1;
		int left = j-1;
		boolean canCheckRight = (right) < board.board[0].length;
		boolean canCheckLeft = (left) >= 0;
		boolean canCheckTop = (top) > 0;
		boolean canCheckBottom = (bottom) < board.board.length;

		if (isVertical) {
			int rightEndIndex = j;
			int leftStartIndex = j;
			if (canCheckRight) {
				if (board.board[i][right] != null){
					rightEndIndex = endOfWord(board, isVertical, j, i);
				}
			}
			if (canCheckLeft) {
				if (board.board[i][left] != null) {
					leftStartIndex = startOfWord(board, isVertical, j, i);
				}
			}
			if (rightEndIndex == leftStartIndex) {
				return true;
			}
			else {
				String leftWord = leftStartIndex == j ? "" : getWord(board, isVertical, leftStartIndex, j - 1, i);
				String rightWord = rightEndIndex == j ? "" : getWord(board, isVertical, j + 1, rightEndIndex, i);
				String potentialWord = leftWord + Character.toString(c) + rightWord;
				if (dt.isValidWord(potentialWord)){
					return true;
				}
				else {
					return false;
				}
			}
		}
		else {
			int topStartIndex = i;
			int bottomEndIndex = i;
			if (canCheckTop) {
				if (board.board[top][j] != null) {
					topStartIndex = startOfWord(board, isVertical, i, j);
				}
			}
			if (canCheckBottom) {
				if (board.board[bottom][j] != null) {
					bottomEndIndex = endOfWord(board, isVertical, i, j);
				}

			}
			if (topStartIndex == bottomEndIndex) {
				return true;
			}
			else {
				String topWord = topStartIndex == i ? "" : getWord(board, isVertical, topStartIndex, i - 1, j);
				String bottomWord = bottomEndIndex == j ? "" : getWord(board, isVertical, i + 1, bottomEndIndex, j);
				String potentialWord = topWord + Character.toString(c) + bottomWord;
				if (dt.isValidWord(potentialWord)){
					return true;
				}
				else {
					return false;
				}
			}
		}
	}
	public static int startOfWord(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current - 1;
		while (i >= 0) {
			boolean isBeforeNull = isVertical? board.board[i][index] == null : board.board[index][i] == null;
			if (isBeforeNull) {
				return i + 1;
			}
			i--;
		}
		return i + 1;
	}
	public static int endOfWord(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current + 1;
		while ( i < 11) {
			boolean isAfterNull = isVertical? board.board[i][index] == null : board.board[index][i] == null;
			if (isAfterNull) {
				return i-1;
			}
			i++;
		}
		return i-1;
	}
	public static String getWord(WWFBoard board, boolean isVertical, int min, int max, int index) {
		String ret = "";
		for (int i = min; i <= max; i++) {
			ret += isVertical? board.board[i][index].letter : board.board[index][i].letter;
		}
		return ret;
	}
	public int getWordScore(WWFBoard board, boolean isVertical, int min, int max, int index) {
		int score = 0;
		boolean doubleWord = false;
		boolean tripleWord = false;
		for (int i = min; i <= max; i++){
			char letter = isVertical? board.board[i][index].letter : board.board[index][i].letter;
			boolean hasScore = isVertical? board.board[i][index].hasScore : board.board[index][i].hasScore;
			int position = getPosition(letter);
			int letterScore = tileScores[position];
			String code = isVertical? codes[i][index] : codes[index][i];
			if (code.equals("dw")) {
				doubleWord = true;
			}
			else if(code.equals("tw")) {
				tripleWord = true;
			}
			else if(code.equals("dl")) {
				letterScore *= 2;
			}
			else if(code.equals("tl")) {
				letterScore *= 3;
			}
			else {

			}
			if (hasScore) {
				score += letterScore;
			}
		}
		if (tripleWord) {
			score *= 3;
		}
		else if (doubleWord) {
			score *= 2;
		}
		else {
			score *= 1;
		}
		return score;
	}	
	public static int getPosition(char c) {
		return c - 'a';
	}
}

