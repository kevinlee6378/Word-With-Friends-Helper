package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Main extends Application {
	private static int[] tileScores = {1,4,4,2,1,4,3,3,1,10,5,2,4,2,1,4,10,1,1,1,2,5,4,8,3,10};
	private static BufferedReader b;
	private static int dimX = 11;
	private static int dimY = 11;
	private static int numInput = 7;
	private static DictionaryTree dt = new DictionaryTree();
	private static String[][] cellCodes = new String[dimX][dimY];
	private static String[][] cellCodesForRun = new String[dimX][dimY];
	private static TextField[][] cells = new TextField[dimX][dimY];
	private static TextField[] inputLetters = new TextField[numInput];


	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Words With Friends Solver");

			BorderPane root = new BorderPane();
			//Scene scene = new Scene(root,400,400);
			GridPane gridPane = new GridPane();
			gridPane.setHgap(1); //horizontal gap in pixels => that's what you are asking for
			gridPane.setVgap(1); //vertical gap in pixels
			gridPane.setPadding(new Insets(10, 10, 10, 10));
			Scene scene = new Scene(gridPane,700, 700); //580
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());


			addCellCodes();
			createTextFieldsForCells();
			addCellsToGridPane(gridPane);
			createTextFieldsAndAddToGridPane(gridPane);
			
			Button b = new Button("GO!");
			gridPane.add(b, 5, 13);
			b.setOnAction(event -> {
				
				LinkedList<Character> inputList = createInputList();
				WWFBoard board = new WWFBoard();
				populateBoard(board, cells);
				changeCellCodesForRun(board);
				
				BoardScore bs = solvePuzzle(board, inputList);
				System.out.println(bs.score);
				bs.board.printBoard();

			});

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void addCellCodes() {
		for(int i = 0; i < dimX; i++) {
			for(int j = 0; j < dimY; j++) {
				if( i == 0 || i == 10){
					if(j==0 || j==10) {
						cellCodes[i][j] = "tl";
					}
					else if(j==2 || j==8) {
						cellCodes[i][j] = "tw";
					}
					else {
						cellCodes[i][j] = "n";
					}
				}
				else if(i==1 || i==9) {
					if(j==1 || j==5 || j==9) {
						cellCodes[i][j] = "dw";
					}
					else {
						cellCodes[i][j] = "n";
					}
				}
				else if(i==2 || i==8){
					if(j==0 || j==10) {
						cellCodes[i][j] = "tw";
					}
					else if(j==2 || j==8) {
						cellCodes[i][j] = "tl";
					}
					else if(j==4 || j==6) {
						cellCodes[i][j] = "dl";
					}
					else {
						cellCodes[i][j] = "n";
					}
				}
				else if(i==3 || i==7) {
					if(j==3 || j==7) {
						cellCodes[i][j] = "tl";
					}
					else {
						cellCodes[i][j] = "n";
					}
				}
				else if(i==4 || i==6){
					if(j==2 || j==8) {
						cellCodes[i][j] = "dl";
					}
					else {
						cellCodes[i][j] = "n";
					}
				}
				else {
					if(j==1 || j==9) {
						cellCodes[i][j] = "dw";
					}
					else if (j==5) {
						cellCodes[i][j] = "middle";
					}
					else {
						cellCodes[i][j] = "n";
					}
				}
			}
		}
	}
	public static void createTextFieldsForCells() {
		for(int i = 0; i < dimX; i++) {
			for(int j = 0; j < dimY; j++) {
				TextField textField = new TextField();
				textField.setId(cellCodes[i][j]);
				textField.setPrefSize(50, 50);
				cells[i][j] = textField;
			}
		}
	}
	public static void addCellsToGridPane(GridPane gridPane) {
		for(int i = 0; i < dimX; i++) {
			for(int j = 0; j < dimY; j++) {
				gridPane.add(cells[i][j], j, i);
			}
		}
	}
	public static void createTextFieldsAndAddToGridPane(GridPane gridPane) {
		for (int i = 0; i < inputLetters.length; i++) {
			TextField textField = new TextField();
			textField.setId("n");
			textField.setPrefSize(50, 50);
			inputLetters[i] = textField;
			gridPane.add(inputLetters[i], i + 2, 12);
		}
	}
	public static LinkedList<Character> createInputList() {
		LinkedList<Character> ret = new LinkedList<>();
		for(int i = 0; i < 7; i++) {
			if (inputLetters[i].getText().charAt(0) != '/') {
				ret.add(inputLetters[i].getText().charAt(0));
			}
		}
		return ret;
	}
	public static void populateBoard(WWFBoard board, TextField[][] cells) {
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				String text = cells[i][j].getText();
				if (text.length() > 0) {
					if (text.charAt(0) == '?') {
						Tile t = new Tile(cells[i][j].getText().charAt(1),false);
						board.addTile(t, i, j);
					}
					else {
						Tile t = new Tile(cells[i][j].getText().charAt(0),true);
						board.addTile(t, i, j);
					}
				}

			}
		}

	}
	public static void changeCellCodesForRun(WWFBoard board) {
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				if (board.board[i][j] != null) {
					cellCodesForRun[i][j] = "n";
				}
				else {
					cellCodesForRun[i][j] = cellCodes[i][j];
				}
			}
		}
	}
	
	public static BoardScore solvePuzzle(WWFBoard board, LinkedList<Character> letters) {
		int maxScore = 0;
		WWFBoard maxBoard = new WWFBoard();
		boolean hasSomething = false;
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				if (board.board[i][j] != null) {
					hasSomething = true;
					//System.out.println(i + " " + j);
					BoardScore a = solvePuzzle3(board, letters, false, j, j , i);
					BoardScore b = solvePuzzle3(board, letters, true, i, i, j);

					if (a.score > maxScore) {
						maxScore = a.score;
						maxBoard = a.board;
					}
					if (b.score > maxScore) {
						maxScore = b.score;
						maxBoard = b.board;
					}

				}
			}
		}
		if (!hasSomething) {
			char c = letters.removeLast();
			board.addTile(new Tile(c,true), 5, 5);
			BoardScore a = solvePuzzle3(board, letters, false, 5, 5 , 5);
			BoardScore b = solvePuzzle3(board, letters, true, 5, 5, 5);
			if (a.score > maxScore) {
				maxScore = a.score;
				maxBoard = a.board;
			}
			if (b.score > maxScore) {
				maxScore = b.score;
				maxBoard = b.board;
			}

		}
		return new BoardScore(maxBoard, maxScore);
	}
	public static int getPosition(char c) {
		return c - 'a';
	}
	public static int getWordScore(WWFBoard board, boolean isVertical, int min, int max, int index) {
		//		boolean print = isVertical == false && min == 2 && max == 5 && index == 2;
		int score = 0;
		boolean doubleWord = false;
		boolean tripleWord = false;
		for (int i = min; i <= max; i++){
			char letter = isVertical? board.board[i][index].letter : board.board[index][i].letter;
			boolean hasScore = isVertical? board.board[i][index].hasScore : board.board[index][i].hasScore;
			int position = getPosition(letter);
			int letterScore = tileScores[position];
			String code = isVertical? cellCodesForRun[i][index] : cellCodesForRun[index][i];
			if (code == "dw") {
				doubleWord = true;
			}
			else if(code == "tw") {
				tripleWord = true;
			}
			else if(code == "dl") {
				letterScore *= 2;
			}
			else if(code == "tl") {
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
	public static int getScore(String word) {
		int score = 0;
		char[] asArray = word.toCharArray();
		for (char c : asArray) {
			int position = getPosition(c);
			score += tileScores[position];
		}
		return score;
	}
	
	public static boolean[][] placesToAppend(WWFBoard board) {
		boolean[][] ret = new boolean[11][11];
		// check inner squares
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++){
				ret[i][j] = canPutTile(i, j, board);
			}
		}
		return ret;
	}

	public static boolean canPutTile(int i, int j, WWFBoard board) {
		if (board.board[i][j] != null) {
			return false;
		}
		else if (i == 0) {
			if (j == 0) {
				return board.board[i][j+1] != null || board.board[i+1][j] != null;
			}
			else if (j == 10) {
				return board.board[i][j-1] != null || board.board[i+1][j] != null;
			}
			else {
				return board.board[i][j-1] != null || board.board[i][j+1] != null || board.board[i+1][j] != null;
			}
		}
		else if (i == 10) {
			if (j == 0) {
				return board.board[i][j+1] != null || board.board[i-1][j] != null;
			}
			else if (j == 10) {
				return board.board[i][j-1] != null || board.board[i-1][j] != null;
			}
			else {
				return board.board[i][j-1] != null || board.board[i][j+1] != null || board.board[i-1][j] != null;
			}
		}
		else if (j == 0) {
			if (i == 0) {
				return board.board[i][j+1] != null || board.board[i+1][j] != null;
			}
			else if (i == 10) {
				return board.board[i][j+1] != null || board.board[i-1][j] != null;
			}
			else {
				return board.board[i+1][j] != null || board.board[i-1][j] != null || board.board[i][j+1] != null;
			}
		}
		else if (j == 10) {
			if (i == 0) {
				return board.board[i][j-1] != null || board.board[i+1][j] != null;
			}
			else if (i == 10) {
				return board.board[i][j-1] != null || board.board[i-1][j] != null;
			}
			else {
				return board.board[i+1][j] != null || board.board[i-1][j] != null || board.board[i][j-1] != null;
			}
		}
		else {
			return board.board[i+1][j] != null || board.board[i-1][j] != null || board.board[i][j+1] != null || board.board[i][j-1] != null;
		}	
	}

	public static LinkedList<String> solvePuzzle2(WWFBoard board, LinkedList<Character> letters, boolean isVertical,int start, int end, int index) {
		LinkedList<String> ret = new LinkedList<>();
		//		System.out.println(start + " "  + end);
		for(char t : letters) {

			LinkedList<Character> newLetters = new LinkedList<>();
			for (char i : letters){
				if (i != t) {
					newLetters.add(i);
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
					String newWord = getWord(newBoard, isVertical, newStartOfWord, end, index);
					//System.out.println(newWord);
					//System.out.println(dt.isValidWord(newWord));
					if (dt.isValidWord(newWord)){
						ret.add(newWord);
					}

					//newBoard.printBoard();
					LinkedList<String> recurse = solvePuzzle2(newBoard, newLetters, isVertical, newStartOfWord, end, index);
					for (String s : recurse) {
						ret.add(s);
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
					String newWord2 = getWord(newBoard2, isVertical, start, newEndOfWord, index);
					//System.out.println(newWord2);
					//System.out.println(dt.isValidWord(newWord2));
					if (dt.isValidWord(newWord2)){
						ret.add(newWord2);
					}
					//newBoard2.printBoard();
					LinkedList<String> recurse2 = solvePuzzle2(newBoard2, newLetters, isVertical, start , newEndOfWord, index);
					for (String s : recurse2) {
						ret.add(s);
					}

				}
			}
		}
		return ret;
	}
	public static BoardScore solvePuzzle3(WWFBoard board, LinkedList<Character> letters, boolean isVertical,int start, int end, int index) {
		int maxScore = 0;
		WWFBoard maxBoard = new WWFBoard();
		//		System.out.println(start + " "  + end);
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
							//System.out.println(newWord);
							//System.out.println(dt.isValidWord(newWord));
							if (dt.isValidWord(newWord)){
								int score = getWordScore(newBoard, isVertical, newStartOfWord, end, index);
								if (score > maxScore) {
									maxScore = score;
									maxBoard = newBoard;
								}
							}
							BoardScore recurse = solvePuzzle3(newBoard, newLetters, isVertical, newStartOfWord, endOfWord, index);
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
							BoardScore recurse2 = solvePuzzle3(newBoard2, newLetters, isVertical, start, newEndOfWord, index);
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
						//System.out.println(newWord);
						//System.out.println(dt.isValidWord(newWord));
						if (dt.isValidWord(newWord)){
							int score = getWordScore(newBoard, isVertical, newStartOfWord, end, index);
							if (score > maxScore) {
								maxScore = score;
								maxBoard = newBoard;
							}
						}
						BoardScore recurse = solvePuzzle3(newBoard, newLetters, isVertical, newStartOfWord, endOfWord, index);
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
						BoardScore recurse2 = solvePuzzle3(newBoard2, newLetters, isVertical, start, newEndOfWord, index);
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
	public static boolean checkSides(WWFBoard board, boolean isVertical, int i, int j) {
		int top = i-1;
		int bottom = i+1;
		int right = j+1;
		int left = j-1;
		boolean canCheckRight = (right) < 11;
		boolean canCheckLeft = (left) >= 0;
		boolean canCheckTop = (top) > 0;
		boolean canCheckBottom = (bottom) < 11;

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
	public static LinkedList<String> solvePuzzle(WWFBoard board, LinkedList<Character> letters, int starti, int endi, int[] nexti, int[] nextj) {
		LinkedList<String> ret = new LinkedList<>();
		for(int x=0; x < nexti.length; x++) {
			if (nexti[x] >= 0 && nexti[x] < 11){
				for (char t: letters){
					WWFBoard newBoard = new WWFBoard(board);
					newBoard.addTile(new Tile(t, true), nexti[x], nextj[x]);
					LinkedList<Character> newList = new LinkedList<>(letters);
					newList.remove(t);
					int[] newi = nexti.clone();
					int newstart = starti;
					int newend = endi;
					if (nexti[x] < starti) {
						newi[x]--;
						newstart--;
					}
					else {
						newi[x]++;
						newend++;
					}
					String newWord = getWord(newBoard,true,starti,endi,nextj[x]);
					if (dt.isValidWord(newWord)){
						System.out.println(newWord);
						ret.add(newWord);
					}
					LinkedList<String> recurse = solvePuzzle(newBoard, newList, newstart, newend, newi, nextj);
					for(String s : recurse){
						ret.add(s);
					}
				}
			}
		}
		return ret;
	}
	public static String getWord(WWFBoard board, boolean isVertical, int min, int max, int index) {
		String ret = "";
		for (int i = min; i <= max; i++) {
			ret += isVertical? board.board[i][index].letter : board.board[index][i].letter;
		}
		return ret;
	}

	public static void main(String[] args) {
		try {
			File f = new File("src/application/wordlist.txt");
			b = new BufferedReader(new FileReader(f));
			String readLine = "";

			while ((readLine = b.readLine()) != null) {
				//System.out.println(readLine);
				dt.AddWord(readLine);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		launch(args);

	}
}
