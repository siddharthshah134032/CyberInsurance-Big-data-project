

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.Set;

public class TextObject3 {
	private String data;
	private Map<String,Integer> termFreq;
	private Set<String> uniqueTokens;
	private List<String> allWords;
	private Map<String,Double> tWeight;
	private List<String> stopwords;
	
	public Map<String, Double> getTWeight() {
		return tWeight;
	}

	public TextObject3(String data) {
		this.data = data;
	}
	
	public void calcUniqTokens() {
		uniqueTokens = new HashSet<String>();
		for (String s:allWords) {
			uniqueTokens.add(s);
		}
	}
	
	public void calcTF() {
		termFreq = new HashMap<String,Integer>();
		for (String t: allWords) {
			if (!termFreq.containsKey(t)) {
				termFreq.put(t, 1);
			}else {
				termFreq.put(t,termFreq.get(t)+1);
			}
		}
	}
	
	public String getData() {
		return data;
	}

	public Map<String, Integer> getTermFreq() {
		return termFreq;
	}
	
	public Set<String> getUniqueTokens() {
		return uniqueTokens;
	}


	public void process() {
		tokenize();
		//removeStopWords();
		calcUniqTokens();
		calcTF();
		calcNormlzdWeight();
	}

	private void removeStopWords() {
		Iterator<String> it = allWords.iterator();
		while(it.hasNext()) {
			String next = it.next();
			if (stopwords.contains(next)) {
				it.remove();
			}
		}
	}

	public void tokenize() {
		StringTokenizer tokenizer = new StringTokenizer(data);
		Processor processor = new Processor();
		allWords = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			allWords.addAll(processor.process(tokenizer.nextToken()));
		}
	}
	
	public void calcNormlzdWeight() {
		tWeight= new HashMap<String,Double>();
		Map<String,Double> tWt = new HashMap<String,Double>();
		for (Entry<String,Integer> entry: termFreq.entrySet()) {
			double raw = entry.getValue();
			double w = 1 + Math.log10(raw);
			tWt.put(entry.getKey(), w);
		}
		
		double length = 0;
		
		for (Entry<String,Double> entry: tWt.entrySet()) {
			length += Math.pow(entry.getValue(),2);
		}
		length = Math.sqrt(length);
		
		for (Entry<String,Double> entry: tWt.entrySet()) {
			tWeight.put(entry.getKey(), entry.getValue()/length);
		}
	}
	
	public void setStopWords(List<String> stopwords) {
		this.stopwords = stopwords;

	}

	
}
