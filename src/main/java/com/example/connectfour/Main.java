package com.example.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args){
		launch(args);
	}
	private Controller controller;
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGPane = loader.load();
		controller = loader.getController();
		controller.createPlayground();
		Pane menu = (Pane) rootGPane.getChildren().get(0);
		//
		MenuBar menubar = CreateMenuBar();
		menubar.prefWidthProperty().bind(stage.widthProperty());
		menu.getChildren().add(0,menubar);
		Scene scene = new Scene(rootGPane);
		stage.setScene(scene);
		stage.setTitle("Connect 4");
		stage.setResizable(false);
		stage.show();
	}
	private MenuBar CreateMenuBar() {
		SeparatorMenuItem sep = new SeparatorMenuItem();
		//file
		Menu file=new Menu("File");
		MenuItem New = new MenuItem("New Game");
		New.setOnAction(event -> resetGame());
		MenuItem reset = new MenuItem("Reset Game");
		reset.setOnAction(event -> resetGame());
		MenuItem exit = new MenuItem("Exit Game");
		exit.setOnAction(event -> exitGame());
		file.getItems().addAll(New,reset,sep,exit);
		//help
		SeparatorMenuItem sep2 = new SeparatorMenuItem();
		Menu help=new Menu("Help");
		MenuItem aboutBox = new MenuItem("About");
		aboutBox.setOnAction(event -> about());
		MenuItem play = new MenuItem("How to play");
		play.setOnAction(event -> playGame());
		help.getItems().addAll(aboutBox,sep2,play);
		//adding menus to the menu bar
		MenuBar menubar = new MenuBar();
		menubar.getMenus().addAll(file,help);
		return menubar;
	}
	//Menu items functions...
	private void playGame() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect-4");
		alert.setHeaderText("How to play?");
		alert.setContentText("Connect Four is a two-player connection game in which the players"+
				" first choose a color and then take turns dropping colored discs from the top "+
				"into a seven-column, six-row vertically suspended grid. The pieces fall straight "+
				"down, occupying the next available space within the column. The objective of the "+
				"game is to be the first to form a horizontal, vertical, or diagonal line of four of "+
				"one's own discs. Connect Four is a solved game. The first player can always win by "+
				"playing the right moves.");
		alert.show();
	}
	private void resetGame() {
		controller.resetGame();
	}
	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}
	private void about() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect-4");
		alert.setHeaderText("About the developer");
		alert.setContentText("Hi. My name is L.Yashaswini. This is my first desktop application."+
				" This game is known as the connect-4. Hope you enjoy playing this game!!!");
		alert.show();
	}
}