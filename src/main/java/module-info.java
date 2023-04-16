module com.internshala.connect_four {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.internshala.connect_four to javafx.fxml;
	exports com.internshala.connect_four;
}