package application;

import java.util.LinkedList;



public class PuzzleSolver {
	private static int[] tileScores = {1,4,4,2,1,4,3,3,1,10,5,2,4,2,1,4,10,1,1,1,2,5,4,8,3,10};
	private DictionaryTree dt;
	private String[][] codes;
	
	public PuzzleSolver(DictionaryTree dt, String[][] codes) {
		this.dt = dt;
		this.codes = codes;
	}

	/* Will create two tasks one which runs all horizontal tasks on each tile in the board and the other 
	 * which runs all vertical tasks. */
	public BoardScore solvePuzzle(WWFBoard board, LinkedList<Character> letters) {
		int maxScore[] = new int[2];
		WWFBoard[] maxBoard = new WWFBoard[2];
		boolean hasSomething[] = new boolean[2];
		
		for(int i = 0; i < maxBoard.length; i++) {
			maxBoard[i] = new WWFBoard(board);
		}
		
		Thread horizontalTasks = new Thread(() -> {
			for (int i = 0; i < board.getBoard().length; i++) {
				for (int j = 0; j < board.getBoard()[0].length; j++) {
					if (board.getBoard()[i][j] != null) {
						hasSomething[0] = true;
						BoardScore a = solvePuzzleKernel(board, letters, false, j, j , i);
						if (a.getScore() > maxScore[0]) {
							maxScore[0] = a.getScore();
							maxBoard[0] = a.getBoard();
						}
					}
				}
			}
		});

		Thread verticalTasks = new Thread(() -> {
			for (int i = 0; i < board.getBoard().length; i++) {
				for (int j = 0; j < board.getBoard()[0].length; j++) {
					if (board.getBoard()[i][j] != null) {
						hasSomething[1] = true;
						BoardScore b = solvePuzzleKernel(board, letters, true, i, i, j);
						if (b.getScore() > maxScore[1]) {
							maxScore[1] = b.getScore();
							maxBoard[1] = b.getBoard();
						}
					}
				}
			}
		});
		
		horizontalTasks.start();
		verticalTasks.start();

		try {
			horizontalTasks.join();
			verticalTasks.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!hasSomething[0] && !hasSomething[1]) {
			char c = letters.removeLast();
			board.addTile(new Tile(c,true,true), 5, 5);
			BoardScore a = solvePuzzleKernel(board, letters, false, 5, 5 , 5);
			BoardScore b = solvePuzzleKernel(board, letters, true, 5, 5, 5);
			if (a.getScore() > maxScore[0]) {
				maxScore[0] = a.getScore();
				maxBoard[0] = a.getBoard();
			}
			if (b.getScore() > maxScore[1]) {
				maxScore[1] = b.getScore();
				maxBoard[1] = b.getBoard();
			}

		}

		return maxScore[0] > maxScore[1] ? new BoardScore(maxBoard[0], maxScore[0]) : new BoardScore(maxBoard[1],maxScore[1]);

	}

	/* Solves a board recursively. Will place a tile on each side of the current tiles inputed and return the max of
	 * the words formed by each addition and any further words that can be created by appending and calling the function recursivly. */
	public BoardScore solvePuzzleKernel(WWFBoard board, LinkedList<Character> letters, boolean isVertical,int start, int end, int index) {
		int maxScore = 0;
		WWFBoard maxBoard = new WWFBoard(board);
		for(char t : letters) {

			if (t == '?') {
				LinkedList<Character> newLetters = removeCharFromList(letters, t);

				for (char c = 'a'; c <= 'z'; c++) {
					
					WWFBoard beforeBoard = new WWFBoard(board);
					int beforeTileRow = isVertical? nextBefore(board, isVertical, start, index) : index;
					int beforeTileColumn = isVertical? index : nextBefore(board, isVertical, start, index);
					boolean hasSpaceForTileBefore = beforeTileRow != -1 && beforeTileColumn != -1;
					if (hasSpaceForTileBefore) {
						boolean canPutTileInSpaceBefore = checkSidesAdvanced(board, isVertical, beforeTileRow, beforeTileColumn, c);
						if (canPutTileInSpaceBefore) {
							beforeBoard.addTile(new Tile(c, false, true),beforeTileRow, beforeTileColumn);
							int beforeIndexOfTile = isVertical? beforeTileRow : beforeTileColumn;
							int beforeStartOfWord = startOfWord(beforeBoard, isVertical, beforeIndexOfTile, index);
							int beforeEndOfWord = endOfWord(beforeBoard, isVertical, beforeIndexOfTile, index);
							String beforeNewWord = getWord(beforeBoard, isVertical, beforeStartOfWord, beforeEndOfWord, index);
							if (dt.isValidWord(beforeNewWord)){
								int score = getWordScore(beforeBoard, isVertical, beforeStartOfWord, end, index, true);
								if (score > maxScore) {
									maxScore = score;
									maxBoard = beforeBoard;
								}
							}
							BoardScore maxFromBeforeFutureCalls = solvePuzzleKernel(beforeBoard, newLetters, isVertical, beforeStartOfWord, beforeEndOfWord, index);
							if (maxFromBeforeFutureCalls.getScore() > maxScore) {
								maxScore = maxFromBeforeFutureCalls.getScore();
								maxBoard = maxFromBeforeFutureCalls.getBoard();
							}
						}
					}
					
					WWFBoard afterBoard = new WWFBoard(board);
					int afterTileRow = isVertical? nextAfter(board,isVertical,start, index) : index;
					int afterTileColumn = isVertical? index : nextAfter(board,isVertical,start, index);
					boolean hasSpaceForTileAfter = afterTileRow != -1 && afterTileColumn != -1;
					if (hasSpaceForTileAfter) {
						boolean canPutTileInSpaceAfter = checkSidesAdvanced(board, isVertical, afterTileRow, afterTileColumn, c);
						if (canPutTileInSpaceAfter) {
							afterBoard.addTile(new Tile(c, false, true),afterTileRow, afterTileColumn);
							int newIndexOfTile = isVertical? afterTileRow : afterTileColumn;
							int afterEndOfWord = endOfWord(afterBoard, isVertical, newIndexOfTile, index);
							int afterStartOfWord = startOfWord(afterBoard, isVertical, newIndexOfTile, index);
							String afterNewWord = getWord(afterBoard, isVertical, afterStartOfWord, afterEndOfWord, index);
							if (dt.isValidWord(afterNewWord)){
								int score = getWordScore(afterBoard, isVertical, afterStartOfWord, afterEndOfWord, index, true);
								if (score > maxScore) {
									maxScore = score;
									maxBoard = afterBoard;
								}
							}
							BoardScore maxFromAfterFutureCalls = solvePuzzleKernel(afterBoard, newLetters, isVertical, start, afterEndOfWord, index);
							if (maxFromAfterFutureCalls.getScore() > maxScore) {
								maxScore = maxFromAfterFutureCalls.getScore();
								maxBoard = maxFromAfterFutureCalls.getBoard();
							}

						}
					}
				}
			}
			else {
				LinkedList<Character> newLetters = removeCharFromList(letters, t);
				
				WWFBoard beforeBoard = new WWFBoard(board);
				int beforeTileRow = isVertical? nextBefore(board, isVertical, start, index) : index;
				int beforeTileColumn = isVertical? index : nextBefore(board, isVertical, start, index);
				boolean hasSpaceForTileBefore = beforeTileRow != -1 && beforeTileColumn != -1;
				if (hasSpaceForTileBefore) {
					boolean canPutTileInSpaceBefore = checkSidesAdvanced(board, isVertical, beforeTileRow, beforeTileColumn, t);
					if (canPutTileInSpaceBefore) {
						beforeBoard.addTile(new Tile(t,true,true),beforeTileRow, beforeTileColumn);
						int beforeIndexOfTile = isVertical? beforeTileRow : beforeTileColumn;
						int beforeStartOfWord = startOfWord(beforeBoard, isVertical, beforeIndexOfTile, index);
						int beforeEndOfWord = endOfWord(beforeBoard, isVertical, beforeIndexOfTile, index);
						String beforeNewWord = getWord(beforeBoard, isVertical, beforeStartOfWord, beforeEndOfWord, index);
						if (dt.isValidWord(beforeNewWord)){
							int score = getWordScore(beforeBoard, isVertical, beforeStartOfWord, end, index, true);
							if (score > maxScore) {
								maxScore = score;
								maxBoard = beforeBoard;
							}
						}
						BoardScore maxFromBeforeFutureCalls = solvePuzzleKernel(beforeBoard, newLetters, isVertical, beforeStartOfWord, beforeEndOfWord, index);
						if (maxFromBeforeFutureCalls.getScore() > maxScore) {
							maxScore = maxFromBeforeFutureCalls.getScore();
							maxBoard = maxFromBeforeFutureCalls.getBoard();
						}
					}
				}
				
				WWFBoard afterBoard = new WWFBoard(board);
				int afterTileRow = isVertical? nextAfter(board,isVertical,start, index) : index;
				int afterTileColumn = isVertical? index : nextAfter(board,isVertical,start, index);
				boolean hasSpaceForTileAfter = afterTileRow != -1 && afterTileColumn != -1;
				if (hasSpaceForTileAfter) {
					boolean canPutTileInSpaceAfter = checkSidesAdvanced(board, isVertical, afterTileRow, afterTileColumn, t);
					if (canPutTileInSpaceAfter) {
						afterBoard.addTile(new Tile(t,true,true),afterTileRow, afterTileColumn);
						int newIndexOfTile = isVertical? afterTileRow : afterTileColumn;
						int afterEndOfWord = endOfWord(afterBoard, isVertical, newIndexOfTile, index);
						int afterStartOfWord = startOfWord(afterBoard, isVertical, newIndexOfTile, index);
						String afterNewWord = getWord(afterBoard, isVertical, afterStartOfWord, afterEndOfWord, index);
						if (dt.isValidWord(afterNewWord)){
							int score = getWordScore(afterBoard, isVertical, afterStartOfWord, afterEndOfWord, index, true);
							if (score > maxScore) {
								maxScore = score;
								maxBoard = afterBoard;
							}
						}
						BoardScore maxFromAfterFutureCalls = solvePuzzleKernel(afterBoard, newLetters, isVertical, start, afterEndOfWord, index);
						if (maxFromAfterFutureCalls.getScore() > maxScore) {
							maxScore = maxFromAfterFutureCalls.getScore();
							maxBoard = maxFromAfterFutureCalls.getBoard();
						}

					}
				}
			}
		}
		return new BoardScore(maxBoard,maxScore);
	}
	
	/* Returns new list where the specified letter is removed 1 time from the input list */
	public LinkedList<Character> removeCharFromList(LinkedList<Character> originalList, char characterToRemove) {
		LinkedList<Character> ret = new LinkedList<>();
		boolean hasAlreadyRemoved = false;
		for (char c : originalList) {
			if (characterToRemove != c) {
				ret.add(c);
			}
			else {
				if (hasAlreadyRemoved) {
					ret.add(c);
				}
				else {
					hasAlreadyRemoved = true;
				}
			}
		}
		return ret;
	}
	
	/* Finds the closest empty cell before the current given value */
	public static int nextBefore(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current - 1;
		while (i >= 0) {
			boolean isBeforeNull = isVertical? board.getBoard()[i][index] == null : board.getBoard()[index][i] == null;
			if (isBeforeNull) {
				return i;
			}
			i--;
		}
		return -1;
	}
	
	/* Finds the closest empty cell after the current given value */
	public static int nextAfter(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current + 1;
		while (i < 11) {
			boolean isAfterNull = isVertical? board.getBoard()[i][index] == null : board.getBoard()[index][i] == null;
			if (isAfterNull) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	/* Checks if the space does not have letters on either side of it, this allowing for placement */ 
	public static boolean checkSides(WWFBoard board, boolean isVertical, int i, int j) {
		int top = i-1;
		int bottom = i+1;
		int right = j+1;
		int left = j-1;
		boolean canCheckRight = (right) < board.getBoard()[0].length;
		boolean canCheckLeft = (left) >= 0;
		boolean canCheckTop = (top) > 0;
		boolean canCheckBottom = (bottom) < board.getBoard().length;

		if (isVertical) {
			if (canCheckRight) {
				if (board.getBoard()[i][right] != null){
					return false;
				}
			}
			if (canCheckLeft) {
				if (board.getBoard()[i][left] != null) {
					return false;
				}
			}
		}
		else {
			if (canCheckTop) {
				if (board.getBoard()[top][j] != null) {
					return false;
				}
			}
			if (canCheckBottom) {
				if (board.getBoard()[bottom][j] != null) {
					return false;
				}

			}
		}

		return true;
	}
	
	/* Checks to see if the word that is formed from placing a character in a place is allowed (perpedicular word formed is valid). */
	public boolean checkSidesAdvanced(WWFBoard board, boolean isVertical, int i, int j, char c) {
		int top = i-1;
		int bottom = i+1;
		int right = j+1;
		int left = j-1;
		boolean canCheckRight = (right) < board.getBoard()[0].length;
		boolean canCheckLeft = (left) >= 0;
		boolean canCheckTop = (top) > 0;
		boolean canCheckBottom = (bottom) < board.getBoard().length;

		if (isVertical) {
			int rightEndIndex = j;
			int leftStartIndex = j;
			if (canCheckRight) {
				if (board.getBoard()[i][right] != null){
					rightEndIndex = endOfWord(board, !isVertical, j, i);
				}
			}
			if (canCheckLeft) {
				if (board.getBoard()[i][left] != null) {
					leftStartIndex = startOfWord(board, !isVertical, j, i);
				}
			}
			if (rightEndIndex == j && leftStartIndex == j) {
				return true;
			}
			else {
				String leftWord = leftStartIndex == j ? "" : getWord(board, !isVertical, leftStartIndex, j - 1, i);
				String rightWord = rightEndIndex == j ? "" : getWord(board, !isVertical, j + 1, rightEndIndex, i);
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
				if (board.getBoard()[top][j] != null) {
					topStartIndex = startOfWord(board, !isVertical, i, j);
				}
			}
			if (canCheckBottom) {
				if (board.getBoard()[bottom][j] != null) {
					bottomEndIndex = endOfWord(board, !isVertical, i, j);
				}
			}
			if (topStartIndex == i && bottomEndIndex == i) {
				return true;
			}
			else {
				String topWord = topStartIndex == i ? "" : getWord(board, !isVertical, topStartIndex, i - 1, j);
				String bottomWord = bottomEndIndex == i ? "" : getWord(board, !isVertical, i + 1, bottomEndIndex, j);
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
	
	/* Finds the index of the start of the word. */
	public static int startOfWord(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current - 1;
		while (i >= 0) {
			boolean isBeforeNull = isVertical? board.getBoard()[i][index] == null : board.getBoard()[index][i] == null;
			if (isBeforeNull) {
				return i + 1;
			}
			i--;
		}
		return i + 1;
	}
	
	/* Finds the index of the end of the word */
	public static int endOfWord(WWFBoard board, boolean isVertical, int current, int index) {
		int i = current + 1;
		while ( i < 11) {
			boolean isAfterNull = isVertical? board.getBoard()[i][index] == null : board.getBoard()[index][i] == null;
			if (isAfterNull) {
				return i-1;
			}
			i++;
		}
		return i-1;
	}
	
	/* Gets the word from the board given indices. */
	public static String getWord(WWFBoard board, boolean isVertical, int min, int max, int index) {
		String ret = "";
		for (int i = min; i <= max; i++) {
			ret += isVertical? board.getBoard()[i][index].getLetter() : board.getBoard()[index][i].getLetter();
		}
		return ret;
	}
	
	/* Gets the score of the word specified by the given indices. */
	public int getWordScore(WWFBoard board, boolean isVertical, int min, int max, int index, boolean checkSides) {
		int score = 0;
		boolean doubleWord = false;
		boolean tripleWord = false;
		int additionalScores = 0;
		for (int i = min; i <= max; i++){
			char letter = isVertical? board.getBoard()[i][index].getLetter() : board.getBoard()[index][i].getLetter();
			boolean hasScore = isVertical? board.getBoard()[i][index].hasScore() : board.getBoard()[index][i].hasScore();
			boolean checkAdditionalScores = isVertical? board.getBoard()[i][index].isNew() : board.getBoard()[index][i].isNew();
			if (checkSides && checkAdditionalScores) {
				if (isVertical) {
					int leftStartIndex = startOfWord(board, !isVertical, index, i);
					int rightEndIndex = endOfWord(board, !isVertical, index, i);
					if (leftStartIndex == index && rightEndIndex == index) {
						//no additional points
					}
					else {
						int wordScore = getWordScore(board, !isVertical, leftStartIndex, rightEndIndex, i, false);
						additionalScores += wordScore;
					}

				}
				else {

					int topStartIndex = startOfWord(board, !isVertical, index, i);
					int bottomEndIndex = endOfWord(board, !isVertical, index, i);

					if (topStartIndex == index && bottomEndIndex == index) {

					}
					else {
						int wordScore = getWordScore(board, !isVertical, topStartIndex, bottomEndIndex, i, false);
						additionalScores += wordScore;
					}
				}
			}
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
		return score + additionalScores;
	}	
	
	/* Gets the array index corresponding a character (a -> 0, z -> 25). */
	public static int getPosition(char c) {
		return c - 'a';
	}
}

