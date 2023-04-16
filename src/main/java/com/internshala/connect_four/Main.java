package com.internshala.connect_four;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();

		controller = loader.getController();
		controller.createPlayground();

		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(stage.widthProperty());

		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);

		stage.setScene(scene);
		stage.setTitle("Connect Four");
		stage.setResizable(false);
		stage.show();
	}

	public MenuBar createMenu() {

		// file menu
		Menu fileMenu = new Menu("File");
		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(event -> controller.newGame());
		MenuItem resetGame = new MenuItem("Reset Game");
		resetGame.setOnAction(actionEvent -> controller.resetGame());
		SeparatorMenuItem separatorFileItem = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem("Exit Game");
		exitGame.setOnAction(actionEvent -> exitGame());

		fileMenu.getItems().addAll(newGame, resetGame, separatorFileItem, exitGame);

		// help menu
		Menu helpMenu = new Menu("Help");
		MenuItem aboutGame = new MenuItem("About Connect4");
		aboutGame.setOnAction(actionEvent -> aboutGame());
		SeparatorMenuItem separatorHelpItem = new SeparatorMenuItem();
		MenuItem aboutMe = new MenuItem("About Me");
		aboutMe.setOnAction(actionEvent -> aboutMe());

		helpMenu.getItems().addAll(aboutGame, separatorHelpItem, aboutMe);

		// adding menuBar
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		return menuBar;
	}

	private void aboutMe() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Developer");
		alert.setHeaderText("Ankeet Gogoi");
		alert.setContentText("Hey there! hope you enjoyed the game.");
		alert.show();
	}

	private void aboutGame() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("How to play?");
		alert.setContentText("Connect Four is a two-player connection game in which"+
							" the players first choose a color and then take turns"+
							" dropping colored discs from the top into a seven-column,"+
							" six-row vertically suspended grid. The pieces fall straight down,"+
							" occupying the next available space within the column."+
							" The objective of the game is to be the first to form a"+
							" horizontal, vertical, or diagonal line of four of one's own discs."+
							" Connect Four is a solved game."+
							" The first player can always win by playing the right moves.");
		alert.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}


	public static void main(String[] args) {
		launch();
	}
}