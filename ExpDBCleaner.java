package textmining;

public class ExpDBCleaner {

	
	public String clean(String line,StanfordLemmatizer lemmatizer) {
		if (!line.endsWith("Exploit") && !line.endsWith("Vulnerability")) {
			line = line + " Exploit";
		}
		System.out.println(line);
		return (lemmatizer.run(line));
	}
	
	
}
