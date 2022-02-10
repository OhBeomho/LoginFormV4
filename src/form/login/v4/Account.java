package form.login.v4;

import java.io.*;

public class Account {
	public static final File ACCOUNTS_FILE = new File(Account.class.getResource("resources/accounts.txt").getFile());

	private String id, password;

	public Account(String id, String password) {
		this.id = id;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public void writeToFile() {
		String data = id + "/" + password + "\n";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE, true));
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
