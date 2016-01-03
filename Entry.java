package textmining;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Entry {
	
	protected abstract Set<TextData> getAllSummableFields();
	protected abstract Set<TextData> getAllFilterFields();
	
	public Set<String> getAllTokens() {
		Set<TextData> allTextData = getAllSummableFields();
		allTextData.addAll(getAllFilterFields());
		Set<String> allStrings = new HashSet<String>();
		for (TextData td: allTextData) {
			allStrings.addAll(td.getUniqueTokens());
		}
		return allStrings;
	}
	
	public void process() {
		for (TextData td: getAllSummableFields()) {
			td.process();
		}
		for (TextData td: getAllFilterFields()) {
			td.process();
		}
	}
	

}
