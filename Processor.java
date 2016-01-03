package textmining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processor {

	private static final String SGML_TAG_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
	private static final String specialChars="[\\(\\)%$&!@#+^:;,\\*\\?=\"]";
	private static final String slashesApostrophes = "[\\.\\/\']";


	public Set<String> process(String token) {
		Set<String> tokens = new HashSet<String>();
		token = token.toLowerCase();
		token = token.replaceAll(specialChars, " ");
		if (!isTag(token)) {
			token = trimNumbers(token);
			token = trimPossesives(token);
			if (isAcronym(token)){
				tokens.add(getAcronym(token));
			}
			if (token.contains("-")) {
				tokens.addAll(deHyphen(token));
			} else {
				tokens.add(token.replaceAll(slashesApostrophes, " "));
			}
		}
		return tokens;
	}
	
	public String trimNumbers(String token) {
		token = token.trim();
		Pattern pattern = Pattern.compile(".*([0-9])+.*");
		Matcher matcher	= pattern.matcher(token);
		if (matcher.matches()) {
			token = token.replaceAll("[^a-zA-Z]", "");
		}
		return token;
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
