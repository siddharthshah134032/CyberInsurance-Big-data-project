package textmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class Test3 {

	  public static Set<NVDEntry> nvdEntries = new HashSet<NVDEntry>();
      public static Dictionary dictionary;
      public static Matcher matcher;
      
	public static void main(String[] args) throws Exception {
		setup();
		System.out.println("END OF SETUP!!");
		Map<ExploitDBEntry,List<NVDEntry>> reducerMap = new HashMap<ExploitDBEntry,List<NVDEntry>>();
		FileReader fout = new FileReader("C:/Users/Manoj/workspace/TextMining/part-r-00000");
		BufferedReader reader = new BufferedReader(fout);
		String value;
		while ((value=reader.readLine())!=null) {
			String[] nist = value.toString().split("\t")[1].split(":::")[0].split("\\^");
        	String[] exploit = value.toString().split("\t")[1].split(":::")[1].split("\\^");
        	
	    	 TextData desc = new TextData(exploit[2]);	//based on current dataSet
	         TextData date = new TextData(exploit[3]);
	         ExploitDBEntry exEntry = new ExploitDBEntry(date, desc);
	         
	         desc = new TextData(nist[4]);	//for current dataSet
	         date = new TextData(nist[3]);
	         TextData prod = new TextData(nist[2]);
	         double rating = Double.parseDouble(nist[1]);
	         NVDEntry nvdEntry = new NVDEntry(date, desc,prod,rating);
	         System.out.println(matcher.match(nvdEntry, exEntry));}
	         /*
	          * 
	         if (reducerMap.containsKey(exEntry)) {
	        	 List<NVDEntry> list = reducerMap.get(exEntry);
	        	 list.add(nvdEntry);
	         } else {
	        	 List<NVDEntry> list = new ArrayList<NVDEntry>();
	        	 list.add(nvdEntry);
	        	 reducerMap.put(exEntry, list);
	         }
	         
		}
		reader.close();
        System.out.println("End of mapper");
		for (java.util.Map.Entry<ExploitDBEntry, List<NVDEntry>> ex1: reducerMap.entrySet()) {
			ExploitDBEntry key = ex1.getKey();
			List<NVDEntry> values = ex1.getValue();
			Map<Double,NVDEntry> allValues = new HashMap<Double,NVDEntry>();
			for (NVDEntry v:values) {
				allValues.put(v.getRating(), v);
			}
			System.out.println(allValues.size() + " many ratings to consider & " + ex1.getValue().size()+ " entries");
			List<java.util.Map.Entry<Double,NVDEntry>> sortedScores = new LinkedList
	                <java.util.Map.Entry<Double,NVDEntry>>(allValues.entrySet());
	                
	        Collections.sort(sortedScores, new Comparator<java.util.Map.Entry<Double, NVDEntry>>(){
	                    
	                    @Override
	                    public int compare(java.util.Map.Entry<Double, NVDEntry> o1,
	                                       java.util.Map.Entry<Double, NVDEntry> o2) {
	                        return (int) ((o2.getKey()-o1.getKey())*1000);
	                    }
	                });
	                
	                
            int c=0;
            double prediction = 0.0;
            double sum=0.0;
            int k=5;
            for (java.util.Map.Entry<Double, NVDEntry> e: sortedScores) {
                if(c++>k) break;
                prediction += e.getKey()*e.getValue().getRating();
                sum+=e.getKey();
            }
	                //context.write(key, new DoubleWritable(prediction/sum));
	       System.out.println(key.toString() + " ==> " + prediction/sum);
			
			
		}*/
       		
	}
	
	
	public static void setup() throws IOException,
    InterruptedException {
       
        FileReader fout = new FileReader("C:/Users/Manoj/workspace/TextMining/part-r-00000");
		BufferedReader reader = new BufferedReader(fout);
		String line;
		int c=0;
		while ((line=reader.readLine())!=null) {
			System.out.println(line);
			if (line!=null) {
				String split1=line.split("\t")[1];
				String[] arr = split1.split(":::")[0].split("\\^");
	            TextData desc = new TextData(arr[4]);	//for current dataSet
	            TextData date = new TextData(arr[3]);
	            TextData prod = new TextData(arr[2]);
	            double rating = Double.parseDouble(arr[1]);
	            NVDEntry entry = new NVDEntry(date, desc, prod, rating);
	            nvdEntries.add(entry);
			}
			System.out.println("Processing new line" + (c++) + "in setup");
        }
        dictionary = new Dictionary();
        for (NVDEntry entry: nvdEntries) {
            dictionary.processEntry(entry);
        }
        matcher = new Matcher(Matcher.TYPE.TFIDF);
        matcher.setDictionary(dictionary);
    }

}
