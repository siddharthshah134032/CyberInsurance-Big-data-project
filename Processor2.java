package textmining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processor2 {

	private static final String SGML_TAG_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
	private static final String specialChars="[\\(\\)%$&!@#+^:;,\\*\\?=\"]";
	private static final String slashesApostrophes = "[\\.\\/\']";
	

	public String process(String line) {
		line = trimSpecialChars(line);
		line = trimNumbers(line);
		line = line.replaceAll(slashesApostrophes, "");
		return line;
	}
	
	public String trimNumbers(String line) {
		line = line.trim();
		Pattern pattern = Pattern.compile(".*([0-9])+.*");
		Matcher matcher	= pattern.matcher(line);
		if (matcher.matches()) {
			line = line.replaceAll("[^a-zA-Z]", " ");
		}
		return line;
	}
	
	public String trimSpecialChars(String line) {
		line = line.trim();
		Pattern pattern = Pattern.compile(".*("+specialChars+")+.*");
		Matcher matcher	= pattern.matcher(line);
		if (matcher.matches()) {
			line = line.replaceAll("[^a-zA-Z]", "");
		}
		return line;
	}
	
	private String getAcronym(String token) {
		return token.replaceAll("\\.", "");
	}
	
	public static boolean isAcronym(String key) {
		String testString = key;
		// If acronym comes at the end of a sentence or at the begining
		if (key.endsWith("..")) {
			testString = key.substring(0, key.length()-1);
		} else if (key.startsWith(".")) {
			testString = key.substring(1,key.length());
		}
		Pattern pattern = Pattern.compile("(([a-zA-Z])\\.)(([a-zA-Z])\\.)+");
		Matcher matcher	= pattern.matcher(testString);
		return matcher.matches();
	}
	
	private boolean isTag(String token) {
		Pattern pattern = Pattern.compile(SGML_TAG_PATTERN);
		Matcher matcher	= pattern.matcher(token);
		return matcher.matches();
	}
	
	public static boolean isSGMLTag(String token) {
		Pattern pattern = Pattern.compile(SGML_TAG_PATTERN);
		Matcher matcher	= pattern.matcher(token);
		return matcher.matches();
	}
	private String trimPossesives(String token) {
		token = token.trim();
		token = isPossesive(token)?token.replaceFirst("'s", ""):token;
		return token;
	}
	
	public static boolean isPossesive(String token) {
		return token.endsWith("'s");
	}
	
	public List<String> deHyphen(String token) {
		List<String> tokens = new ArrayList<String>();
		if (token.contains("-")) {
			String[] words = token.split("-");
			for (String word: words) {
				word = word.replaceAll(slashesApostrophes, "");
				word = word.replaceAll(specialChars, "");
				if (word!="") {
					tokens.add(word);
				}
			}
		}
		return tokens;
	}
}
