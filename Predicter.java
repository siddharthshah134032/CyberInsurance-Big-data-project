package textmining;



import java.io.BufferedReader;
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
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Predicter extends Configured implements Tool {
    
    
    public static class Map extends Mapper<LongWritable, Text, Text, Text> {
        
        public static Set<NVDEntry> nvdEntries = new HashSet<NVDEntry>();
        public static Dictionary dictionary;
        public static Matcher matcher;
        

        @Override
        public void setup(Context context) throws IOException,
        InterruptedException {
            super.setup(context);
            
            //read data to memory on the mapper.
            Configuration conf = context.getConfiguration();
            //String myfilepath = conf.get("nvd_path");
           // String myfilepath = "vishwani/sortByDate/part-r-00000";
            Path path=new Path("hdfs://cshadoop1/vishwani/sortByDate/part-r-00000");	//Location of file in HDFS
            
            FileSystem fs = FileSystem.get(conf);
            FileStatus[] fss = fs.listStatus(path);
            for (FileStatus status : fss) {
                Path pt = status.getPath();
                
                BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
                String line;
                line=br.readLine();
                
                while (line != null){
                    String split1=line.split("\t")[1];
                    String[] arr = split1.split(":::")[0].split("\\^");
                    TextData desc = new TextData(arr[4]);	//for current dataSet
                    TextData date = new TextData(arr[3]);
                    TextData prod = new TextData(arr[2]);
                    double rating = Double.parseDouble(arr[1]);
                    NVDEntry entry = new NVDEntry(date, desc, prod, rating);
                    nvdEntries.add(entry);
                    line=br.readLine();
                }
                
            }
            dictionary = new Dictionary();
            for (NVDEntry entry: nvdEntries) {
                dictionary.processEntry(entry);
            }
            matcher = new Matcher(Matcher.TYPE.TFIDF);
            matcher.setDictionary(dictionary);
        }
        
        @Override
        protected void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
        	
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
	         
	         try {
				context.write(new Text(exEntry.toString()), new Text(nvdEntry.toString() + "|" 
						 + matcher.match(exEntry, nvdEntry)) );
			} catch (Exception e) {
				context.write(new Text(exEntry.toString()), new Text(nvdEntry.toString()+ "^msg = "+ e.toString()+ "|" 
						 + 0.0 ));
			}
        	
        	
            
        }
        
    }
    
    
    public static class Reduce extends Reducer<Text, Text, Text, DoubleWritable> {
    	
        public static final int k = 5;

    	 
        public void reduce(Text key, Iterable<Text> values, Context context) 
          throws IOException, InterruptedException {
            
        java.util.Map<Double, NVDEntry> allValues = new HashMap<Double,NVDEntry>();
        
       	Iterator<Text> it = values.iterator();
       	 while(it.hasNext()) {
       		 String line = it.next().toString();
       		 double score = Double.parseDouble(line.split("\\|")[1]);
       		 String data = line.split("\\|")[0];
       		 TextData desc = new TextData(data.split("\\^")[1]);	//for current dataSet
       		 TextData date = new TextData(data.split("\\^")[0]);
       		 TextData prod = new TextData(data.split("\\^")[2]);
       		 NVDEntry entry = new NVDEntry(date, desc, prod, score);
       		 allValues.put(score,entry);
       	 }
       	 
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
                for (java.util.Map.Entry<Double, NVDEntry> e: sortedScores) {
                    if(c++>k) break;
                    prediction += e.getKey()*(e.getValue().getRating());
                    sum+=e.getKey();
                }
                double finalVal = prediction/sum;
                context.write(key, new DoubleWritable(finalVal));
        }
     }
    
    public int run(String[] args) throws Exception{
        Configuration conf = super.getConf();
        
        Job job = new Job(conf, "Predicter");
        job.setMapperClass(Map.class); 
        //job.setNumReduceTasks(0);
        job.setReducerClass(Reduce.class);
        //set the HDFS path of the input data   
        FileInputFormat.addInputPath(job, new Path("/vishwani/sortByDate"));
        // set the HDFS path for the output
        FileOutputFormat.setOutputPath(job, new Path("/mxh133030/pout"));
        
        job.setJarByClass(Predicter.class);
        // set output value type 
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        //job.setOutputValueClass(Text.class);
        job.waitForCompletion(true);
        
        return job.waitForCompletion(true) ? 0 : 1;    
     }
    
    public static void main(String args[]) throws Exception {
    	int res = ToolRunner.run(new Configuration(), new Predicter(),args);
    	System.exit(res);
    }
    
}
