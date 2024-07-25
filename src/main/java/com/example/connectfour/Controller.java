package com.example.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int columns = 7;
	private static final int rows = 6;
	private static final int diameter = 80;
	private static final String color1="#24303E";
	private static final String color2="#4CAA88";
	public static String player_1="Player One";
	public static String player_2="Player Two";
	private boolean isP1Turn=true;
	private Disc[][] insertedDiscArray = new Disc[rows][columns];

	@FXML
	public TextField p1txt;
	@FXML
	public TextField p2txt;
	@FXML
	public GridPane gridpane;
	@FXML
	public Pane discpane;
	@FXML
	public Button setname;
	@FXML
	public Label playerone;
	@FXML
	public Label turn;
	private boolean isAllowedToInsert=true;

	public void createPlayground(){
		Shape rec =createCircles();
		gridpane.add(rec,0,1);
		List<Rectangle> reclist = clickableColumns();
		for (Rectangle i:reclist) {
			gridpane.add(i, 0, 1);
		}
		setname.setOnAction(event -> {
			setPlayerNames();
		});
	}
	private void setPlayerNames() {
		player_1=p1txt.getText();
		player_2=p2txt.getText();
	}
	private Shape createCircles(){
		Shape rec = new Rectangle((columns+1)*diameter,(rows+1)*diameter);
		for (int r=0; r<rows;r++){
			for (int c=0; c<columns;c++){
				Circle circle = new Circle();
				circle.setRadius((double) diameter / 2);
				circle.setCenterX( (double) (diameter) /2);
				circle.setCenterY( (double) (diameter) /2);
				circle.setSmooth(true);
				circle.setTranslateX(c*(diameter+5)+((double) diameter /4));
				circle.setTranslateY(r*(diameter+5)+((double) diameter /4));
				rec = Shape.subtract(rec,circle);
			}
		}
		rec.setFill(Color.WHITE);
		return rec;
	}
	private List<Rectangle> clickableColumns(){
		List<Rectangle> rlist = new ArrayList<>();
		for (int col=0; col<columns;col++) {
			Rectangle rect = new Rectangle(diameter, (rows + 1) * diameter);
			rect.setFill(Color.TRANSPARENT);
			rect.setTranslateX(col*(diameter+5)+(double) diameter / 4);
			rect.setOnMouseEntered(event -> rect.setFill(Color.valueOf("#eeeeee26")));
			rect.setOnMouseExited(event -> rect.setFill(Color.TRANSPARENT));
			final int cols=col;
			rect.setOnMouseClicked(event-> {
				if (isAllowedToInsert) {
					isAllowedToInsert=false;
					insertDisc(new Disc(isP1Turn), cols);
				}
			});
			rlist.add(rect);
		}
		return rlist;
	}
	private void insertDisc(Disc disc, int col) {

		int row = rows-1;
		while (row>=0) {
			if (getDiscIfPresent(row,col) == null)
				break;
			row--;
		}
		if (row<0)
			return;

		insertedDiscArray[row][col] = disc;
		discpane.getChildren().add(disc);
		int currentRow=row;
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3),disc);
		disc.setTranslateX(col*(diameter+5)+(double) diameter / 4);
		transition.setToY((row*(diameter+5)+(double) diameter / 4));
		transition.setOnFinished(event -> {
			isAllowedToInsert=true;
			if (eventEnded(currentRow,col)){
				gameOver();
				return;
			}
			isP1Turn=!isP1Turn;
			playerone.setText(isP1Turn? player_1:player_2);
		});
		transition.play();
	}
	private Disc getDiscIfPresent(int row, int col) {
		if (row>=rows || col>=columns || row<0 || col<0){
			return null;
		}
		return insertedDiscArray[row][col];
	}
	private boolean eventEnded(int currentRow, int col) {
		//vertical
		List<Point2D> vertical = IntStream.rangeClosed(currentRow-3,currentRow+3)
				.mapToObj(r -> new Point2D(r,col)).collect(Collectors.toList());
		//horizontal
		List<Point2D> horizontal = IntStream.rangeClosed(col-3,col+3)
				.mapToObj(c -> new Point2D(currentRow,c)).collect(Collectors.toList());
		//right diagonal
		Point2D startPoint1 = new Point2D(currentRow-3,col+3);
		List<Point2D> diagonalr = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint1.add(i,-i)).collect(Collectors.toList());
		//left diagonal
		Point2D startPoint2 = new Point2D(currentRow-3,col-3);
		List<Point2D> diagonall = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint2.add(i,i)).collect(Collectors.toList());
		boolean isEnded= checkCombinations(vertical) || checkCombinations(horizontal)
				           || checkCombinations(diagonalr) || checkCombinations(diagonall);
		return isEnded;
	}
	private boolean checkCombinations(List<Point2D> points){
		int chain = 0;
        for (Point2D point: points){
			int rowIndexArray = (int) point.getX();
			int columnIndexArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexArray,columnIndexArray);
			if (disc !=null && disc.isPlayer1==isP1Turn){
				chain++;
				if (chain==4){
					return true;
				}
			}else{
				chain=0;
			}
        }
		return false;
	}
	private void gameOver(){
		String winner = isP1Turn? player_1:player_2;
		Platform.runLater(() ->{
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Connect-4");
			alert.setHeaderText("Game Over");
			alert.setContentText("The winner is "+winner);
			ButtonType restart = new ButtonType("Restart Game");
			ButtonType exit = new ButtonType("Exit");
			alert.getButtonTypes().setAll(restart,exit);
			Optional<ButtonType> button = alert.showAndWait();
			if (button.isPresent() && button.get() == exit){
				Platform.exit();
				System.exit(0);
			}else{
				resetGame();
			}
		});
	}
	public void resetGame() {
		discpane.getChildren().clear();
		for (int row = 0; row < insertedDiscArray.length; row++) {
			for (int column = 0; column < insertedDiscArray[row].length; column++) {
				insertedDiscArray[row][column]=null;
			}
		}
		isP1Turn=true;
		p1txt.setText(player_1);
		createPlayground();
	}
	private static class Disc extends Circle{
		private final boolean isPlayer1;
		public Disc(boolean isPlayer1){
			this.isPlayer1=isPlayer1;
			setRadius((double) diameter /2);
			setFill(isPlayer1? Color.valueOf(color1):Color.valueOf(color2));
			setCenterX((double) diameter /2);
			setCenterY((double) diameter /2);
		}
	}
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}