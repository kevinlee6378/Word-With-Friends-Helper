package application;
	
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
	        primaryStage.setTitle("GridPane Experiment");

			BorderPane root = new BorderPane();
			//Scene scene = new Scene(root,400,400);
	        GridPane gridPane = new GridPane();
	        gridPane.setHgap(1); //horizontal gap in pixels => that's what you are asking for
	        gridPane.setVgap(1); //vertical gap in pixels
	        gridPane.setPadding(new Insets(10, 10, 10, 10));
			Scene scene = new Scene(gridPane,700, 700); //580
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        
			String[][] cellCodes = new String[11][11];
			for(int i = 0; i < 11; i++) {
	        	for(int j = 0; j < 11; j++) {
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
			TextField[][] myCells = new TextField[11][11];
			for(int i = 0; i < 11; i++) {
	        	for(int j = 0; j < 11; j++) {
	        		TextField textField = new TextField();
	        		if(i==4 && j==6){
	        			textField.setText("hello");
	        		}
	        		textField.setId(cellCodes[i][j]);
	        		textField.setPrefSize(50, 50);
	        		myCells[i][j] = textField;
	        	}
	        }
	        
	        for(int i = 0; i < 11; i++) {
	        	for(int j = 0; j < 11; j++) {
	        		gridPane.add(myCells[i][j], j, i);
	        	}
	        }
	        //add texfields for current tiles
	        TextField[] myCurrentTiles = new TextField[7];
	        for (int i = 0; i < 7; i++) {
	        	TextField textField = new TextField();
	        	textField.setPrefSize(50, 50);
	        	myCurrentTiles[i] = textField;
	        	gridPane.add(myCurrentTiles[i], i + 2, 11);
	        }
	        Button b = new Button("GO!");
	        gridPane.add(b, 5, 12);
	        b.setOnAction(event -> {
	        	WWFBoard board = createBoard(myCells);
	        });
	        
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static WWFBoard createBoard(TextField[][] myCells) {
		WWFBoard myBoard = new WWFBoard();
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				String text = myCells[i][j].getText();
				if (text.length() > 0) {
					if (text.charAt(0) == '?') {
						Tile t = new Tile(myCells[i][j].getText().charAt(1),false);
						myBoard.addTile(t, i, j);
					}
					else {
						Tile t = new Tile(myCells[i][j].getText().charAt(0),true);
						myBoard.addTile(t, i, j);
					}
				}

			}
		}
		myBoard.printBoard();
		return myBoard;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
