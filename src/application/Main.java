package application;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Main extends Application {
	
	private static int hGap = 1;
	private static int vGap = 1;
	private static int insets = 10;
	private static int gridPaneXDim = 600;
	private static int gridPaneYDim = 700;
	private static String cssFile = "application.css";
	private static String codesFile = "src/application/codes.txt";
	private static int textFieldSize = 50;
	private static int inputTextFieldRow = 12;
	private static int dimX = 11;
	private static int dimY = 11;
	private static int numInput = 7;
	private static int goButtonColumn = 5;
	private static int clearButtonColumn = 5;
	private static int goButtonRow = 13;
	private static int clearButtonRow = 14;
	private static String dictionaryFile = "src/application/wordlist.txt";
	private static DictionaryTree dt = new DictionaryTree(dictionaryFile);
	private static String[][] cellCodes = new String[dimX][dimY];
	private static String[][] cellCodesForRun = new String[dimX][dimY];
	private static TextField[][] cells = new TextField[dimX][dimY];
	private static TextField[] inputLetters = new TextField[numInput];


	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Words With Friends Solver");

			GridPane gridPane = new GridPane();
			gridPane.setHgap(hGap);
			gridPane.setVgap(vGap);
			gridPane.setPadding(new Insets(insets, insets, insets, insets));
			Scene scene = new Scene(gridPane,gridPaneXDim, gridPaneYDim);
			scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());


			addCellCodes(codesFile);
			createTextFieldsForCells();
			addCellsToGridPane(gridPane);
			createInputTextFieldsAndAddToGridPane(gridPane);

			Button go = new Button("GO!");
			gridPane.add(go, goButtonColumn, goButtonRow);
			GridPane.setHalignment(go, HPos.CENTER);
			go.setOnAction(event -> {
				
				removeHighlights();
				LinkedList<Character> inputList = createInputList();
				WWFBoard board = new WWFBoard();
				populateBoard(board, cells);
				changeCellCodesForRun(board);

				PuzzleSolver ps = new PuzzleSolver(dt,cellCodesForRun);
				long time_1 = System.currentTimeMillis();

				BoardScore bs = ps.solvePuzzle(board, inputList);
				System.out.println(bs.getScore());
				bs.getBoard().printBoard();
				long time_2 = System.currentTimeMillis();
				System.out.println(time_2-time_1);
				fillTextFieldsFromBoard(bs.getBoard());

			});

			Button clear = new Button("Clear");
			gridPane.add(clear, clearButtonColumn, clearButtonRow);
			clear.setOnAction(event -> {
				clearUI();
			});

			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/* Takes in file with codes for each cell on the board and populates cellCodes. */
	public static void addCellCodes(String file) throws FileNotFoundException, IOException, ParseException {
		Object obj = new JSONParser().parse(new FileReader(file));
		JSONObject json = (JSONObject) obj;
		for (int i = 0; i < dimX; i++) {
			JSONArray ja = (JSONArray) json.get(Integer.toString(i));
			for (int j = 0; j < dimY; j++) {
				String currentCode = ja.get(j).toString();
				cellCodes[i][j] = currentCode;
			}
		}
	}
	
	/* Creates the TextField objects that will be in the cells array. Sets the id of each cell to its corresponding code
	 * and creates listeners to change cell id whenever a letter is entered or removed.*/
	public static void createTextFieldsForCells() {
		for(int i = 0; i < dimX; i++) {
			for(int j = 0; j < dimY; j++) {
				final int accessible_i = i;
				final int accessible_j = j;
				TextField textField = new TextField();
				textField.setId(cellCodes[i][j]);
				textField.setPrefSize(textFieldSize, textFieldSize);
				textField.textProperty().addListener((obs,prev,current) -> {
					if (current.trim().isEmpty()) {
						textField.setId(cellCodes[accessible_i][accessible_j]);
					}
					else if (current.toLowerCase().charAt(0) == '?') {
						if (current.length() != 1) {
							if (Character.isLetter(current.toLowerCase().charAt(1))){
								textField.setId(Character.toString(current.toLowerCase().charAt(1)));
							}
						}
						else {
							textField.setId(cellCodes[accessible_i][accessible_j]);
						}
					}
					else if (Character.isLetter(current.toLowerCase().charAt(0))) {
						textField.setId(Character.toString(current.toLowerCase().charAt(0)));
					}


				});
				cells[i][j] = textField;
			}
		}
	}
	
	/* Adds all TextFields to the GridPane. */
	public static void addCellsToGridPane(GridPane gridPane) {
		for(int i = 0; i < dimX; i++) {
			for(int j = 0; j < dimY; j++) {
				gridPane.add(cells[i][j], j, i);
			}
		}
	}
	
	/* Creates the TextField objects for the input letters and adds them to the GridPane. */
	public static void createInputTextFieldsAndAddToGridPane(GridPane gridPane) {
		for (int i = 0; i < inputLetters.length; i++) {
			TextField textField = new TextField();
			textField.setId("normal");
			textField.setPrefSize(textFieldSize, textFieldSize);
			textField.textProperty().addListener((obs,prev,current) -> {
				if (current.trim().isEmpty()) {
					textField.setId("normal");
				}
				else if (current.toLowerCase().charAt(0) == '?') {
					textField.setId("fill");
				}
				else if (Character.isLetter(current.toLowerCase().charAt(0))) {
					textField.setId(Character.toString(current.toLowerCase().charAt(0)));
				}


			});
			inputLetters[i] = textField;
			gridPane.add(inputLetters[i], i + 2, inputTextFieldRow);
		}
	}
	
	/* Creates the inputList to be fed to the PuzzleSolver from letters inputed in the UI. */
	public static LinkedList<Character> createInputList() {
		LinkedList<Character> ret = new LinkedList<>();
		for(int i = 0; i < inputLetters.length; i++) {
			if (!inputLetters[i].getText().trim().isEmpty()) {
				ret.add(inputLetters[i].getText().toLowerCase().charAt(0));
			}
		}
		return ret;
	}
	
	/* Creates the board to be fed to the PuzzleSolver from the letters inputed in the UI. */
	public static void populateBoard(WWFBoard board, TextField[][] cells) {
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				String text = cells[i][j].getText();
				if (text.length() > 0) {
					if (text.charAt(0) == '?' && text.length() > 1) {
						char c = text.toLowerCase().charAt(1);
						if (Character.isLetter(c)){
							Tile t = new Tile(text.toLowerCase().charAt(1), false, false);
							board.addTile(t, i, j);
						}
					}
					else {
						char c = text.toLowerCase().charAt(0);
						if (Character.isLetter(c)) {
							Tile t = new Tile(c, true, false);
							board.addTile(t, i, j);
						}
					}
				}

			}
		}

	}

	/* Creates the array of cell codes that will be fed to the PuzzleSolver. This function will change the cell codes for 
	 * tiles already placed on the board to normal so they do not affect score calculations. */
	public static void changeCellCodesForRun(WWFBoard board) {
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				if (board.getBoard()[i][j] != null) {
					cellCodesForRun[i][j] = "normal";
				}
				else {
					cellCodesForRun[i][j] = cellCodes[i][j];
				}
			}
		}
	}
	
	/* Resets all TextFields in the UI. */
	public static void clearUI() {
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				cells[i][j].setText("");
			}
		}
		for (int i = 0; i < numInput; i++) {
			inputLetters[i].setText("");
		}
	}
	
	/* Makes UI match a given board. Used to change the UI to show the solution after the PuzzleSolver returns the object.
	 * Will highlight TextFields where new letters have been placed (letters from the solution). */
	public static void fillTextFieldsFromBoard(WWFBoard board) {
		for(int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				Tile currentTile = board.getBoard()[i][j];
				if (currentTile != null) {
					char letter = currentTile.getLetter();
					cells[i][j].setText(String.valueOf(letter));
					if(currentTile.isNew()){
						cells[i][j].getStyleClass().add("new");
					}
				}
			}
		}
	}
	
	/* Clears all highlights. Used to set all TextFields back to normal when the solver is run after the first time. */
	public static void removeHighlights() {
		for(int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				cells[i][j].getStyleClass().remove("new");
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
