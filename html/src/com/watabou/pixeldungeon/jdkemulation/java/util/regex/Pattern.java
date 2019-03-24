package java.util.regex;

import com.google.gwt.regexp.shared.RegExp;
import com.watabou.pixeldungeon.utils.Utils;

public class Pattern {
	final RegExp regExp;

	private Pattern (String regExp, int flags) {
		if((flags & Utils.REGEXP_GLOBAL_FLAG) != 0){
			this.regExp = RegExp.compile(regExp, "g");
		} else {
			this.regExp = RegExp.compile(regExp);
		}
	}

	public static Pattern compile(String regExp, int flags) {
		return new Pattern(regExp, flags);
	}

	public static Pattern compile (String regExp) {
		return new Pattern(regExp, 0);
	}

	public Matcher matcher (CharSequence input) {
		return new Matcher(this, input);
	}
}