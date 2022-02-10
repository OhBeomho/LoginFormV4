package form.login.v4;

import java.io.*;
import java.net.URL;
import java.util.*;

import javafx.animation.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class Controller implements Initializable {
	@FXML
	private AnchorPane mainPane, loginPane, registerPane;
	@FXML
	private TextField idField, rIdField;
	@FXML
	private PasswordField passwordField, rPasswordField, checkPasswordField;
	@FXML
	private Label idLabel;

	private ArrayList<Account> accounts;
	private Timeline timeline;
	private Account currentAccount;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		accounts = loadAccounts();
		timeline = new Timeline();
	}

	private ArrayList<Account> loadAccounts() {
		ArrayList<Account> accounts = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(Account.ACCOUNTS_FILE));
			String line;

			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "/");
				String id = st.nextToken(), password = st.nextToken();

				accounts.add(new Account(id, password));
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return accounts;
	}

	private void deleteAccount(Account account) {
		accounts.remove(account);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(Account.ACCOUNTS_FILE));

			for (Account a : accounts) {
				writer.write(a.getId() + "/" + a.getPassword() + "\n");
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Alert dialog(AlertType type, String... thc) {
		String title = thc[0], header = thc[1], content = thc.length > 2 ? thc[2] : "";

		Alert dialog = new Alert(type);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);

		return dialog;
	}

	@FXML
	protected void setOnLogin() {
		String id = idField.getText(), password = passwordField.getText();

		if (id.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "ID를 입력해 주세요.");
			dialog.show();
			return;
		} else if (password.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "비밀번호를 입력해 주세요.");
			dialog.show();
			return;
		}

		for (Account a : accounts) {
			if (a.getId().equalsIgnoreCase(id)) {
				if (HexString.toNormalString(a.getPassword()).equals(password)) {
					timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
						new KeyValue(loginPane.translateYProperty(), -mainPane.getPrefHeight(), Interpolator.SPLINE(1, 0, 0, 1))));
					timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
					timeline.play();

					idLabel.setText(id);

					idField.clear();
					passwordField.clear();

					currentAccount = a;
				} else {
					Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "비밀번호가 일치하지 않습니다.", "다시 입력해 주세요.");
					dialog.show();
				}

				return;
			}
		}

		Alert dialog = dialog(AlertType.ERROR, "로그인 오류", "존재하지 않는 ID입니다.", "다시 입력해 주세요.");
		dialog.show();
	}

	@FXML
	protected void setOnStartRegister() {
		idField.clear();
		passwordField.clear();

		timeline.getKeyFrames()
			.add(new KeyFrame(Duration.seconds(1), new KeyValue(registerPane.translateXProperty(), 0, Interpolator.SPLINE(1, 0, 0, 1))));
		timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
		timeline.play();
	}

	@FXML
	protected void setOnRegister() {
		String id = rIdField.getText(), password = rPasswordField.getText(), cPassword = checkPasswordField.getText();

		if (id.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "ID를 입력하세요.");
			dialog.show();
			return;
		} else if (password.equals("") || cPassword.equals("")) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "비밀번호를 입력하세요.");
			dialog.show();
			return;
		}

		for (Account a : accounts) {
			if (a.getId().equalsIgnoreCase(id)) {
				Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "" + id + "는 이미 사용된 ID입니다.", "다른 ID를 입력하세요.");
				dialog.show();
				return;
			}
		}

		if (!password.equals(cPassword)) {
			Alert dialog = dialog(AlertType.ERROR, "회원가입 오류", "비밀번호가 일치하지 않습니다.");
			dialog.show();
			return;
		}

		Account newAcc = new Account(id, HexString.toHexString(password));
		accounts.add(newAcc);
		newAcc.writeToFile();

		setOnCancel();
	}

	@FXML
	protected void setOnCancel() {
		rIdField.clear();
		rPasswordField.clear();
		checkPasswordField.clear();

		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
			new KeyValue(registerPane.translateXProperty(), mainPane.getPrefWidth(), Interpolator.SPLINE(1, 0, 0, 1))));
		timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
		timeline.play();
	}

	@FXML
	protected void setOnLogout() {
		timeline.getKeyFrames()
			.add(new KeyFrame(Duration.seconds(1), new KeyValue(loginPane.translateYProperty(), 0, Interpolator.SPLINE(1, 0, 0, 1))));
		timeline.setOnFinished(e -> timeline.getKeyFrames().clear());
		timeline.play();

		currentAccount = null;
	}

	@FXML
	protected void setOnDelete() {
		Alert dialog = dialog(AlertType.CONFIRMATION, "삭제 확인", "이 계정을 삭제하겠습니까?");
		dialog.showAndWait().ifPresent(result -> {
			if (result == ButtonType.OK) {
				deleteAccount(currentAccount);
			}
		});

		setOnLogout();
	}
}
