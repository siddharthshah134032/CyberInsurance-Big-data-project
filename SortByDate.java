import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class SortByDate { 
	
	
	public static class Map1 extends Mapper<LongWritable, Text, Text, Text>{	
		
		private Text keyword = new Text(); 
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] keyValue = value.toString().split("\t"); 
			String prodName = keyValue[0];
			String stringToBeSplit = keyValue[1];
			String[] tempArr = stringToBeSplit.split(":::");
			
			String[] mydata = tempArr[0].toString().split("\\^"); //nist
			
			
			String date = mydata[3];
			String[] dataArr = date.toString().split("/");

			if(dataArr.length>1){
				keyword.set(prodName+":"+dataArr[0].toString());   // product
				context.write(keyword, new Text(tempArr[0])); // create a pair <keyword, 1> 
			}
			
			
		}
	}
	
	public static class Map2 extends Mapper<LongWritable, Text, Text, Text>{	
		
		private Text keyword = new Text(); // Exploit
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] keyValue = value.toString().split("\t"); 
			String prodName = keyValue[0];
			String stringToBeSplit = keyValue[1];
			String[] tempArr = stringToBeSplit.split(":::");
			
			//exploit
			String[] mydataE = tempArr[1].toString().split("\\^"); //exploit
			String dateE = mydataE[3];
			String[] dataArrE = dateE.toString().split("/");	
			
			if(dataArrE.length>1){
				int temp = Integer.parseInt(dataArrE[0]);
				keyword.set(prodName+":"+temp);   // product
				context.write(keyword, new Text(tempArr[1])); // create a pair <keyword, 1> 
			}
		}
	}
      
      public static class Reduce extends Reducer<Text,Text,Text,Text> {
    	 // private Text result = new Text();
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
    				  //prodName+":"+temp
    			  }
    		  }
            }
    		  
      }
      
      // Driver program
      public static void main(String[] args) throws Exception { 
    	Configuration conf = new Configuration();
      	
    	/*String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();		// get all args
		if (otherArgs.length < 2) {
			System.err.println("Usage: Ques3 <in> <in2><out>");
			System.exit(2);
		}*/
    	
      	Job job = new Job(conf, "Date");	//Job to do Map-side join
      	job.setJarByClass(SortByDate.class);
 	   
		MultipleInputs.addInputPath(job, new Path("/vishwani/sortByProduct"),TextInputFormat.class,Map1.class);	//NIST

		MultipleInputs.addInputPath(job, new Path("/vishwani/sortByProduct1"),TextInputFormat.class,Map2.class);	//Exploit

		job.setReducerClass(Reduce.class);

		// set output key type 
		job.setOutputKeyClass(Text.class);
		// set output value type
		job.setOutputValueClass(Text.class);
		
		// set the HDFS path for the output 
		FileOutputFormat.setOutputPath(job, new Path("/vishwani/sortByDate"));
      	
      	/*job.setMapperClass(Map2.class); 
      	job.setNumReduceTasks(0);
      //set the HDFS path of the input data   
      	FileInputFormat.addInputPath(job, new Path("/vishwani/sortByProduct"));
      // set the HDFS path for the output
      	FileOutputFormat.setOutputPath(job, new Path("/vishwani/out1"));

      	job.setJarByClass(SortByDate.class);
      	// set output value type 
   
      	job.setMapOutputKeyClass(Text.class);
      	job.setMapOutputValueClass(Text.class);

      	job.setOutputKeyClass(Text.class);
      	job.setOutputValueClass(Text.class);*/
	
		//Wait till job completion
		System.exit(job.waitForCompletion(true) ? 0 : 1);
      	}
}