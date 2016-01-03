package textmining;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.Set;

public class TextData {
    private String data;
    private Map<String,Integer> termFreq;
    private Set<String> uniqueTokens;
    private List<String> allWords;
    private Map<String,Double> tWeight;
    private static final String[] sws = {"a","all","an","and","any","are","as","be","been","but","by","few","for","have","he","her","here","him","his","how","i","in","is","it","its","many","me","my","none","of","on","or","our","she","some","the","their","them","there","they","that","this","us","was","what","when","where","which","who","why","will","with","you"};
    private static List<String> stopwords = Arrays.asList(sws);
    
    
    public Map<String, Double> getTWeight() {
        return tWeight;
    }
    
    public TextData(String data) {
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
        lemmatize();
        removeStopWords();
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
        	//allWords.addAll(processor.process(tokenizer.nextToken()));
        	String t = tokenizer.nextToken();
        	if(t!=null && t!="") {
                allWords.add(t);
        	}
        }
    }
    
    public void lemmatize() {
        List<String> lemmas = new ArrayList<String>();
        if (allWords == null) {
            tokenize();
        }
        StanfordLemmatizer lemtr = StanfordLemmatizer.getInstance();
        lemmas = lemtr.lemmatize(allWords);
        allWords = new ArrayList<String>();
        allWords.addAll(lemmas);
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
        	if(entry.getKey()!=null ||  entry.getKey()!="") {
                tWeight.put(entry.getKey(), entry.getValue()/length);
        	}
        }
    }
    
}
