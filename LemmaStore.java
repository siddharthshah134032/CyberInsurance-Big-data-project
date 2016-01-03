package textmining;

import java.util.HashMap;
import java.util.Map;

public class LemmaStore {
	
	private static LemmaStore instance;
	private Map<String,String> lemmas;
	
	private LemmaStore() {
		lemmas = new HashMap<String,String>();
	}
	
	public static LemmaStore getInstance() {
		if(instance==null) {
			instance = new LemmaStore();
		}
		return instance;
	}
	
	public boolean isLemmaAvailable(String term) {
		return lemmas.containsKey(term.toLowerCase().trim());
	}
	
	public void addLemma(String token, String lemma) {
		lemmas.put(token, lemma);
	}

	public String getLemma(String t) {
		return lemmas.get(t);
	}

}
