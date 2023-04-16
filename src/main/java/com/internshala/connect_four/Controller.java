package com.internshala.connect_four;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Label playerNameLabel;
	@FXML
	public TextField playerOneTextField, playerTwoTextField;
	@FXML
	public Button setNamesButton;

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24302E";
	private static final String discColor2 = "#4CAA88";
	private boolean isPlayerOneTurn = true;
	private static String PLAYER_ONE;
	private static String PLAYER_TWO;
	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];
	private boolean isAllowedToInsert = true;

	public void createPlayground(){
		Shape rectangleWithHole = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHole, 0, 1);
		List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle :
				rectangleList) {
			rootGridPane.add(rectangle, 0 ,1);
		}
		setNamesButton.setOnAction(actionEvent -> {
			PLAYER_ONE = playerOneTextField.getText();
			PLAYER_TWO = playerTwoTextField.getText();
			playerNameLabel.setText(PLAYER_ONE);
		});
	}

	private Shape createGameStructuralGrid(){
		Shape rectangleWithHole = new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER, (ROWS+1)*CIRCLE_DIAMETER);

		for(int row=0; row < ROWS; row++){
			for(int col=0; col < COLUMNS; col++){
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);
				circle.setTranslateX(col * (CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER / 4);

				rectangleWithHole = Shape.subtract(rectangleWithHole, circle);
			}
		}

		rectangleWithHole.setFill(Color.WHITE);
		return rectangleWithHole;
	}

	private List<Rectangle> createClickableColumns(){
		List<Rectangle> rectangleList = new ArrayList<>();
		for(int col=0; col < COLUMNS; col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER / 4);
			rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));
			final int finalCol = col;
			rectangle.setOnMouseClicked(mouseEvent -> {
				if(isAllowedToInsert) {
					isAllowedToInsert = false;
					insetDisc(new Disc(isPlayerOneTurn), finalCol);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insetDisc(Disc disc, int column) {
		int row = ROWS - 1;
		while(row >= 0){
			if(insertedDiscArray[row][column] == null)
				break;
			row--;
		}
		if(row < 0)
			return;

		int currentRow = row;
		insertedDiscArray[row][column] = disc;
		insertedDiscPane.getChildren().add(disc);
		disc.setTranslateX(column * (CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setByY(row * (CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER / 4);
		translateTransition.setOnFinished(actionEvent -> {
			isAllowedToInsert = true;
			if(gameEnded(currentRow, column)){
				gameOver();
				return;
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE: PLAYER_TWO);
		});
		translateTransition.play();
	}

	private boolean gameEnded(int row, int column) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
										.mapToObj(r -> new Point2D(r, column))
										.toList();

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
										.mapToObj(c -> new Point2D(row, c))
										.toList();

		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
										.mapToObj(i -> startPoint1.add(i, -i))
										.toList();

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
										.mapToObj(i -> startPoint2.add(i, i))
										.toList();

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
						|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

		return isEnded;
	}

	private void gameOver() {
		String winner = isPlayerOneTurn? PLAYER_ONE: PLAYER_TWO;
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The winner is "+winner);
		alert.setContentText("Want to play again?");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater(() ->{
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if(btnClicked.isPresent() && btnClicked.get() == yesBtn)
				resetGame();
			else{
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {
		insertedDiscPane.getChildren().clear();
		for (Disc[] discs : insertedDiscArray) {
			Arrays.fill(discs, null);
		}
		isPlayerOneTurn = true;
		playerNameLabel.setText(PLAYER_ONE);
		createPlayground();
	}

	public void newGame(){
		insertedDiscPane.getChildren().clear();
		for (Disc[] discs : insertedDiscArray) {
			Arrays.fill(discs, null);
		}
		isPlayerOneTurn = true;
		playerNameLabel.setText("Player One");
		playerOneTextField.setText("");
		playerTwoTextField.setText("");
		createPlayground();
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point : points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);
			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
				chain++;
				if(chain == 4)
					return true;
			} else {
				chain = 0;
			}
		}
		return false;
	}

	private Disc getDiscIfPresent(int row, int column){
		if(row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
			return null;
		return insertedDiscArray[row][column];
	}

	private static class Disc extends Circle{
		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1):Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}