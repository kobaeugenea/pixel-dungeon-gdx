package java.util.regex;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.MatchResult;


public class Matcher {
	private final RegExp regExp;
	private final String input;
	private MatchResult matchResult;

	Matcher (Pattern pattern, CharSequence input) {
		this.regExp = pattern.regExp;
		this.input = String.valueOf(input);
		if(!pattern.regExp.getGlobal()) {
			matchResult = regExp.exec(this.input);
		}
	}

	public boolean find () {
		return (matchResult = regExp.exec(input)) != null;
	}

	public boolean matches () {
		return (matchResult = regExp.exec(input)) != null;
	}

	public String group (int group) {
		return matchResult.getGroup(group);
	}

	public int start(){
		return matchResult.getIndex();
	}

	public int end(){
		return regExp.getLastIndex();
	}

    public static String quoteReplacement(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1))
            return s;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '$') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}