import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class ProductMatch {
	public static String[] sws = {"a","all","an","and","any","are","as","be","been","but","by","few","for","have","he","her","here","him","his","how","i","in","is","it","its","many","me","my","none","of","on","or","our","she","some","the","their","them","there","they","that","this","us","was","what","when","where","which","who","why","will","with","you","vulnerability"};
	
	public static class Map0 extends Mapper<LongWritable, Text, Text, Text>{	
		Set<String> hs = new HashSet<String>();
		private Text keyword = new Text(); // NIST
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] mydata = value.toString().split("\\^"); 
			
			if(hs.add(mydata[2]))
				context.write(new Text(mydata[2]), null); // create a pair <keyword, 1> 
		}
	}

	public static class Map1 extends Mapper<LongWritable, Text, Text, Text>{	
		
		private Text keyword = new Text(); // NIST
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] mydata = value.toString().split("\\^"); 
			
			keyword.set(mydata[2].toString());   // product
			context.write(keyword, new Text("N_" + value)); // create a pair <keyword, 1> 
			
		}
	}
	
	public static class Map2 extends Mapper<LongWritable, Text, Text, Text>{	
		private List<String> myCenterList;
		private Text keyword = new Text(); // Exploit
		private static List<String> stopwords;
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] mydata = value.toString().split("\\^"); 

			double maxSim = 0.0;
			String correctMatch = null;
			String req = mydata[2].toString();
	
			for(String str:myCenterList){

				//COMPARISON
				TextObject3 text1 = new TextObject3(req);
				text1.setStopWords(stopwords);
				text1.process();
				TextObject3 text2 = new TextObject3(str);
				text2.setStopWords(stopwords);
				text2.process();
			
				double x = new Matcher2().match(text1, text2);

				if(x>maxSim){
					maxSim = x;
					correctMatch = str;
				}
			}
			
			if(maxSim > 0.2){
				keyword.set(correctMatch);
				context.write(keyword, new Text("E_" + value));
			}/*else{	//values that don't have a match
				keyword.set(req);
				context.write(keyword, new Text("elseE_" + value + " " + maxSim));
			}*/
			
		}
		
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			super.setup(context);
			//loadStopWords(context.getConfiguration());
			stopwords = Arrays.asList(sws);
			//read data to memory on the mapper.
			myCenterList = new ArrayList<String>();
			
			Configuration conf = context.getConfiguration();
			//String myfilepath = conf.get("inputFile2");
			Path part = new Path("hdfs://cshadoop1/vishwani/nistProdNames");//Location of file in HDFS	
			
			FileSystem fs = FileSystem.get(conf);
			FileStatus[] fss = fs.listStatus(part);
		    for (FileStatus status : fss) {
		        Path pt = status.getPath();
		        
		        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
		        String line;
		        line=br.readLine();
		        while (line != null){
		           myCenterList.add(line);
		           line=br.readLine();
		        }		       
		    }
	    }
		
		private static void loadStopWords(Configuration conf) throws IOException {
			
			Path part = new Path("hdfs://cshadoop1/vishwani/stopwords");//Location of file in HDFS	
			String line = "";
			FileSystem fs = FileSystem.get(conf);
			FileStatus[] fss = fs.listStatus(part);
		    for (FileStatus status : fss) {
		        Path pt = status.getPath();
		        
		        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
		        
		        line=br.readLine();
		        while (line != null){
		          
		           line+=br.readLine();
		        }		       
		    }
			
			StringTokenizer tk = new StringTokenizer(line);
			stopwords = new ArrayList<String>();
			while(tk.hasMoreTokens()) {
				stopwords.add(tk.nextToken());
			}
		}
	}
      
      public static class Reduce extends Reducer<Text,Text,Text,Text> {
    	  private Text result = new Text();
    	  @Override
    	  public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException { 
    		  Set<String> nist1 = new HashSet<String>();
    		  Set<String> exploit1 = new HashSet<String>();
    		  
    		  for(Text value:values){
    			  if(value.toString().startsWith("N_"))
    				  nist1.add(value.toString());
    			  else
    				  exploit1.add(value.toString());
    		  }
    		  
    		 
    		  List<String> nist = new ArrayList<String>(nist1);
    		  List<String> exploit = new ArrayList<String>(exploit1);
    		  
    		  for(int i=0; i< nist.size() ; i++){
    			  for(int j = 0; j < exploit.size() ; j++){
    				  context.write(key, new Text(nist.get(i)+":::"+exploit.get(j)));
    				  
    			  }
    		  }
            }
      }
      
      // Driver program
      public static void main(String[] args) throws Exception { 
    	Configuration conf = new Configuration();
    	//conf.set("inputFile2", args[0]);	//vishwani/nistProdNames
    	/*String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();		// get all args
		if (otherArgs.length < 2) {
			System.err.println("Usage: Ques3 <in> <in2><out>");
			System.exit(2);
		}*/
    	
      	Job job = new Job(conf, "ProdMatch");	//Job to do Map-side join
      	
      	/*job.setMapperClass(Map0.class); 
      	job.setNumReduceTasks(0);
      //set the HDFS path of the input data   
      	FileInputFormat.addInputPath(job, new Path("/vishwani/nvd_latest.csv"));
      // set the HDFS path for the output
      	FileOutputFormat.setOutputPath(job, new Path("/vishwani/nistProdNames"));	//vishwani/nistProdNames/part

      	job.setJarByClass(ProductMatch.class);
      	// set output value type 
   
      	job.setMapOutputKeyClass(Text.class);
      	job.setMapOutputValueClass(Text.class);

      	job.setOutputKeyClass(Text.class);
      	job.setOutputValueClass(Text.class);*/
      	
      	
		job.setJarByClass(ProductMatch.class);
	   
		MultipleInputs.addInputPath(job, new Path("/vishwani/nvd_latest.csv"),TextInputFormat.class,Map1.class);	//NIST

		MultipleInputs.addInputPath(job, new Path("/vishwani/ExploitCarrot.csv"),TextInputFormat.class,Map2.class);	//Exploit

		job.setReducerClass(Reduce.class);

		// set output key type 
		job.setOutputKeyClass(Text.class);
		// set output value type
		job.setOutputValueClass(Text.class);
		
		// set the HDFS path for the output 
		FileOutputFormat.setOutputPath(job, new Path("/vishwani/sortByProduct"));
	
		//Wait till job completion
		System.exit(job.waitForCompletion(true) ? 0 : 1);
      	}
}
