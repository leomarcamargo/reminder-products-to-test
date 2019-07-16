package util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class Mask {
	public static String unmask(String str) {
		return str.replaceAll("[:]", "").replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "")
				.replaceAll("[(]", "").replaceAll("[)]", "");
	}

	public static TextWatcher insert(final String mask, final EditText ediTxt) {
		return new TextWatcher() {
			boolean isUpdating;
			String old = "";

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = Mask.unmask(s.toString());
				String txt = "";
				if (isUpdating) {
					old = str;
					isUpdating = false;
					return;
				}
				int i = 0;
				boolean done = false;
				for (char m : mask.toCharArray()) {
					if (!done && m != '#' && str.length() > old.length()) {
						txt += m;
					} else if (!done && i < str.length()) {
						txt += str.charAt(i);
						i++;
					} else {
						done = true;
					}
				}
				isUpdating = true;
				ediTxt.setText(txt);
				ediTxt.setSelection(txt.length());
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		};
	}
}