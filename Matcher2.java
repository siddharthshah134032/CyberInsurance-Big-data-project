

import java.util.Map.Entry;

public class Matcher2 {

	public double match(TextObject3 t1, TextObject3 t2) {
		double score=0;
		for (Entry<String,Double> e1: t1.getTWeight().entrySet()) {
			for (Entry<String,Double> e2: t2.getTWeight().entrySet()) {
				if(e1.getKey().equals(e2.getKey())) {
					score += e1.getValue()*e2.getValue();
				}
			}
		}
		return score;	
			
	}
}
