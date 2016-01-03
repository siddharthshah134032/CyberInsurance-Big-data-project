package textmining;



import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dictionary {
    
    //terms and their doc frequency
    Map<String,Integer> terms;
    private int numEntries;
    
    public Dictionary() {
        terms = new HashMap<String,Integer>();
        this.numEntries=0;
    }
    
    public void processEntry(Entry e) {
        numEntries++;
        Set<String> allTokens = e.getAllTokens();
        for (String token:allTokens) {
            if (!terms.containsKey(token)){
                terms.put(token, 1);
            } else {
                terms.put(token, terms.get(token)+1);
            }
        }
    }
    
    public Set<String> getTokens() {
        return terms.keySet();
    }
    
    public int getDF(String token) {
        return terms.get(token);
    }
    
    public int getCount() {
        
        return numEntries;
    }
}
