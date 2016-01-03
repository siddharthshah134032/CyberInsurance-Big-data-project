package textmining;

import java.util.Iterator;

public class Matcher {
    
    private static final int NUM_NVD_ENTRIES = 1291;	//10000;
    private Dictionary dictionary;
    public enum TYPE {
    	TFIDF,
    	TF,
    }
    private TYPE type;
    
    public Matcher(TYPE type) {
        this.type = type;
    }
    
    public void setDictionary(Dictionary d) {
    	this.dictionary = d;
    }
    
    public double match(TextData t1, TextData t2) {
        double score=0;
        for (java.util.Map.Entry<String,Double> e1: t1.getTWeight().entrySet()) {
            for (java.util.Map.Entry<String,Double> e2: t2.getTWeight().entrySet()) {
                if(e1.getKey().equals(e2.getKey())) {
                    score += e1.getValue()*e2.getValue();
                }
            }
        }
        return score;
        
    }
    
    public double match(Entry tuple1, Entry tuple2) throws Exception {
    	double score = 0.0;
		
		Iterator<TextData> it1 = tuple1.getAllSummableFields().iterator();
		
		while(it1.hasNext()) {
			TextData text1 = it1.next();
			Iterator<TextData> it2 = tuple2.getAllSummableFields().iterator();
			while(it2.hasNext()) {
				TextData text2 = it2.next();
				for (java.util.Map.Entry<String, Double> e1: text1.getTWeight().entrySet()) {
					for(java.util.Map.Entry<String, Double> e2: text2.getTWeight().entrySet()){
						if(e1.getKey()==null || e2.getKey()==null) {
							break;
						}
						if(e1.getKey().equals(e2.getKey())) {
							if(type.equals(TYPE.TFIDF)) {
								if(dictionary==null) {
									throw new Exception("Dictionary not set");
								}
								double idf = Math.log(dictionary.getCount()/dictionary.getDF(e1.getKey()));
								score += e1.getValue()*e2.getValue()*idf;
							} else {
								score += e1.getValue()*e2.getValue();
							}
						}
					}
				}
				
			}
		}
		
		double filter = 0.0;
		Iterator<TextData> it3 = tuple1.getAllFilterFields().iterator();
		while(it3.hasNext()) {
			TextData text1 = it3.next();
			Iterator<TextData> it4 = tuple2.getAllFilterFields().iterator();
			while(it4.hasNext()) {
				TextData text2 = it4.next();
				for (java.util.Map.Entry<String, Double> e1: text1.getTWeight().entrySet()) {
					for(java.util.Map.Entry<String, Double> e2: text2.getTWeight().entrySet()){
						if(e1.getKey()==null || e2.getKey()==null) {
							break;
						}
						if(e1.getKey().equals(e2.getKey())) {
							if(type.equals(TYPE.TFIDF)) {
								double idf = Math.log(dictionary.getCount()/dictionary.getDF(e1.getKey()));
								filter += e1.getValue()*e2.getValue()*idf;
							} else {
								filter += e1.getValue()*e2.getValue();
							}
						}
					}
				}
			}
		}
		return score; 
    }
}
