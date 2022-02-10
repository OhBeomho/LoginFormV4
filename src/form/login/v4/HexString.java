package form.login.v4;

import java.nio.charset.StandardCharsets;

public class HexString {
	public static String toHexString(String str) {
		String hexString = "";

		char[] chars = str.toCharArray();

		for (char c : chars) {
			hexString += Integer.toHexString(c);
		}

		return hexString;
	}

	public static String toNormalString(String hexStr) {
		String string;

		int l = hexStr.length();
		byte[] data = new byte[l / 2];
		for (int i = 0; i < l; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character.digit(hexStr.charAt(i + 1), 16));
		}

		string = new String(data, StandardCharsets.UTF_8);

		return string;
	}
}
