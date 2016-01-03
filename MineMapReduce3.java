package textmining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MineMapReduce3 extends Configured implements Tool {
	
	public static StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance();


	public static class NvdMapper extends Mapper<LongWritable, Text, Text, Text> {
		    
			public static int nvdDescIndex=3;
			public static List<String> stopwords = new ArrayList<String>();
			 
			public void map(LongWritable key, Text value, Context context) 
					throws IOException, InterruptedException {
		         String line = value.toString();
		         String nvdDBDesc = line.split("\\^")[nvdDescIndex];
		         context.write(new Text(nvdDBDesc), new Text(""));

	     }

  } 

         
  public int run(String[] args) throws Exception {
     Configuration conf = super.getConf();
         
     Job job = new Job(conf, "MineMapReduce3");
     
     job.setOutputKeyClass(Text.class);
     job.setOutputValueClass(Text.class);
         
     job.setNumReduceTasks(0);
     job.setMapperClass(NvdMapper.class);
     
     job.setInputFormatClass(TextInputFormat.class);
     job.setOutputFormatClass(TextOutputFormat.class);
         
     FileInputFormat.setInputPaths(job, new Path("/mxh133030/inpFiles/nvdCarrot.csv"));
     FileOutputFormat.setOutputPath(job, new Path("/mxh133030/pout5"));
         
     job.setJarByClass(MineMapReduce3.class);
     job.waitForCompletion(true);
     
     return job.waitForCompletion(true) ? 0 : 1;
  }
  
  public static void main(String[] args) throws Exception {
      int res = ToolRunner.run(new Configuration(), new MineMapReduce3(), args);
      System.exit(res);
  }
         
 }
