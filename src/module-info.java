module LoginFormV4 {
	requires javafx.controls;
	requires javafx.fxml;
	
	opens form.login.v4 to javafx.graphics, javafx.fxml;
}
