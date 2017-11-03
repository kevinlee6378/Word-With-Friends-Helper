package application;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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

			BorderPane root = new BorderPane();
			GridPane gridPane = new GridPane();
			gridPane.setHgap(hGap);
			gridPane.setVgap(vGap);
			gridPane.setPadding(new Insets(insets, insets, insets, insets));
			Scene scene = new Scene(gridPane,gridPaneXDim, gridPaneYDim);
			scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());


			addCellCodes(codesFile);
			createTextFieldsForCells();
			addCellsToGridPane(gridPane);
			createTextFieldsAndAddToGridPane(gridPane);

			Button go = new Button("GO!");
			gridPane.add(go, 5, 13);
			GridPane.setHalignment(go, HPos.CENTER);
			go.setOnAction(event -> {
				LinkedList<Character> inputList = createInputList();
				WWFBoard board = new WWFBoard(cellCodes);
				populateBoard(board, cells);
				changeCellCodesForRun(board);

				PuzzleSolver ps = new PuzzleSolver(dt,cellCodesForRun);
				long time_1 = System.currentTimeMillis();

				BoardScore bs = ps.solvePuzzle(board, inputList);
				System.out.println(bs.score);
				bs.board.printBoard();
				long time_2 = System.currentTimeMillis();
				System.out.println(time_2-time_1);

			});

			Button clear = new Button("Clear");
			gridPane.add(clear, 5, 14);
			clear.setOnAction(event -> {
				clearUI();
			});
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

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
	public static void createTextFieldsForCells() {
		for(int i = 0; i < dimX; i++) {
			for(int j = 0; j < dimY; j++) {
				final int accessible_i = i;
				final int accessible_j = j;
				TextField textField = new TextField();
				textField.setId(cellCodes[i][j]);
				textField.setPrefSize(textFieldSize, textFieldSize);
				textField.textProperty().addListener((obs,prev,next) -> {
					if (next.trim().isEmpty()) {
						textField.setId(cellCodes[accessible_i][accessible_j]);
					}
					else if (next.toLowerCase().charAt(0) == '?') {
						if (next.length() != 1) {
							if (Character.isLetter(next.toLowerCase().charAt(1))){
								textField.setId(Character.toString(next.toLowerCase().charAt(1)));
							}
						}
						else {
							textField.setId(cellCodes[accessible_i][accessible_j]);
						}
					}
					else if (Character.isLetter(next.toLowerCase().charAt(0))) {
						textField.setId(Character.toString(next.toLowerCase().charAt(0)));
					}


				});
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
			textField.setId("normal");
			textField.setPrefSize(textFieldSize, textFieldSize);
			textField.textProperty().addListener((obs,prev,next) -> {
				if (next.trim().isEmpty()) {
					textField.setId("normal");
				}
				else if (next.toLowerCase().charAt(0) == '?') {
					textField.setId("fill");
				}
				else if (Character.isLetter(next.toLowerCase().charAt(0))) {
					textField.setId(Character.toString(next.toLowerCase().charAt(0)));
				}


			});
			inputLetters[i] = textField;
			gridPane.add(inputLetters[i], i + 2, inputTextFieldRow);
		}
	}
	public static LinkedList<Character> createInputList() {
		LinkedList<Character> ret = new LinkedList<>();
		for(int i = 0; i < 7; i++) {
			if (inputLetters[i].getText().charAt(0) != '/') {
				ret.add(inputLetters[i].getText().toLowerCase().charAt(0));
			}
		}
		return ret;
	}
	public static void populateBoard(WWFBoard board, TextField[][] cells) {
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				String text = cells[i][j].getText();
				if (text.length() > 0) {
					if (text.charAt(0) == '?' && text.length() > 1) {
						char c = text.toLowerCase().charAt(1);
						if (Character.isLetter(c)){
							Tile t = new Tile(text.toLowerCase().charAt(1),false);
							board.addTile(t, i, j);
						}
					}
					else {
						char c = text.toLowerCase().charAt(0);
						if (Character.isLetter(c)) {
							Tile t = new Tile(c,true);
							board.addTile(t, i, j);
						}
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
	
	public static void main(String[] args) {
		launch(args);
	}
}
