package textmining;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Test2 {
    public static ArrayList<NVDEntry> nvdEntries = new ArrayList<NVDEntry>();
    public static Dictionary dictionary;
    public static final int k = 10;
    Matcher matcher;
    
    public static void main(String[] args) throws Exception{
        //new Test2().readFile();
        Test2 n = new Test2();
        n.setup();
        n.readExploitFile();
    }
    
    private void readExploitFile() throws Exception{
        //BufferedReader br = new BufferedReader(new FileReader("/Users/vishwaniarora/Desktop/Fall 2015/Big data/Project/files_ExploitOne.csv"));
        //String value = br.readLine();
        //String value = "37356^platforms/php/webapps/37356.txt^WordPress Email Newsletter Plugin 8.0 'option' Parameter Information Disclosure vulnerability^1/1/15^Sammy FORGIT^php^webapps^0";
        String value = "37543^platforms/linux/local/37543.c^Linux Kernel 2.6.x 'rds_recvmsg()' Function Local Information Disclosure Vulnerability^3/12/2015^Jay Fenlason^linux^local";
        String[] mydata = value.toString().split("\\^");
        TextData desc = new TextData(mydata[2]);	//based on current dataSet
        TextData date = new TextData(mydata[3]);
        ExploitDBEntry exEntry = new ExploitDBEntry(date, desc);
        java.util.Map<Double,NVDEntry> scoreMap = new HashMap<Double,NVDEntry>();
        for (NVDEntry nvdEntry: nvdEntries) {
            double matching = matcher.match(exEntry, nvdEntry);
            if(matching>=0.2 && isSameMonth(nvdEntry.getDate().getData(),exEntry.getDate().getData())) {
                scoreMap.put(matching, nvdEntry);
            }
        }
        LinkedList<java.util.Map.Entry<Double,NVDEntry>> allScores = new LinkedList
        <java.util.Map.Entry<Double,NVDEntry>>(scoreMap.entrySet());
        
        Collections.sort(allScores, new Comparator<java.util.Map.Entry<Double, NVDEntry>>(){
            
            @Override
            public int compare(java.util.Map.Entry<Double, NVDEntry> o1,
                               java.util.Map.Entry<Double, NVDEntry> o2) {
                return (int) ((o2.getKey()-o1.getKey())*1000);
            }
        });
        
        int c=0;
        double prediction = 0.0;
        double sum=0.0;
        for (java.util.Map.Entry<Double, NVDEntry> e: allScores) {
            if(c++>k) break;
            System.out.println("Rating : " + e.getValue().getRating());
            System.out.println("Prediction " + e.getKey());
            prediction += e.getKey()*e.getValue().getRating();
            sum+=e.getKey();
        }
        
        System.out.println("prediction " + prediction);
        System.out.println("sum " + sum);
        System.out.println("avg " + prediction/sum);
        
    }
    
    private boolean isSameMonth(String NVDDate, String ExploitDate) {
        String[] expdate = ExploitDate.split("\\/");
        String[] nvddate = NVDDate.split("\\/");
        if (expdate[0] == nvddate[0]) {
            return true;
        } else {
            return false;
        }
    }
    
    private void setup() throws Exception{	//NIST dataset
        BufferedReader br = new BufferedReader(new FileReader("/Users/vishwaniarora/Desktop/Fall 2015/Big data/Project/nvd_latestCopy.csv"));
        String line = br.readLine();
        //System.out.println(line);
        while (line != null) {
            String[] arr=line.split("\\^");
            TextData desc = new TextData(arr[4]);	//for current dataSet
            TextData date = new TextData(arr[3]);
            TextData prod = new TextData(arr[2]);
            double rating = Double.parseDouble(arr[1]);
            NVDEntry entry = new NVDEntry(date, desc, prod, rating);
            nvdEntries.add(entry);
            line=br.readLine();
        }
        
        dictionary = new Dictionary();
        for (NVDEntry entry: nvdEntries) {
            dictionary.processEntry(entry);
        }
        System.out.println("Dictionary size" + dictionary.getCount());
        matcher = new Matcher(Matcher.TYPE.TFIDF);
        matcher.setDictionary(dictionary);
    }
}
