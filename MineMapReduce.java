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
import org.apache.hadoop.io.NullWritable;
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

public class MineMapReduce extends Configured implements Tool {
	
	public static StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance();

	public static class MatchMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
		    
			public static int nvdDescIndex=3;
			public static List<String> stopwords = new ArrayList<String>();
			public static int exDescIndex=3;
			 
			public void map(LongWritable key, Text value, Context context) 
					throws IOException, InterruptedException {
		         String line = value.toString();
		         String exDBDesc = getExDesc(line,exDescIndex);
		         String nvdDBDesc = getNvdDesc(line,nvdDescIndex);
		         String cleanedExDesc = new ExpDBCleaner().clean(exDBDesc, lemmatizer);
		         
		         TextData ex = new TextData(cleanedExDesc);
		         ex.process();
		         TextData nvd = new TextData(nvdDBDesc);
		         nvd.process();
		         
		         double score = new Matcher(null).match(ex, nvd);
		         String date = getDate(line);
		         String rank = getRank(line);
		         String cveId = getCveId(line);
		         context.write(new Text(date + "^" +
		        		 		cveId + "^" + 
		        		 		cleanedExDesc + "^" +
		        		 		getNvdSummary(nvdDBDesc) + "^" +
		        		 		rank + "^" + 
		        		 		score), null);
		         
		        
	     }

	private String getCveId(String line) {
				return line.split("\t")[1].split(":::")[0].split("\\^")[0].substring(2);
		}

	private String getNvdSummary(String nvdDBDesc) {
			return nvdDBDesc;
			}

	private String getRank(String line) {
			return "0";
			}

	private String getDate(String line) {
			return line.split("\t")[1].split(":::")[0].split("\\^")[2];
		}

	private String getNvdDesc(String line, int nvdDescIndex2) {
				return line.split("\t")[1].split(":::")[0].split("\\^")[nvdDescIndex2];
			}

	private String getExDesc(String line, int exDescIndex2) {
			return line.split("\t")[1].split(":::")[1].split(",")[exDescIndex2];

			}

	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		String[] sws = {"a","all","an","and","any","are","as","be","been","but","by","few","for","have","he","her","here","him","his","how","i","in","is","it","its","many","me","my","none","of","on","or","our","she","some","the","their","them","there","they","that","this","us","was","what","when","where","which","who","why","will","with","you"};
		stopwords = Arrays.asList(sws);
	}
  } 
 

  public int run(String[] args) throws Exception {
     Configuration conf = super.getConf();
         
     Job job = new Job(conf, "MineMapReduce");
     
     job.setOutputKeyClass(Text.class);
     job.setOutputValueClass(NullWritable.class);
         
     job.setMapperClass(MatchMapper.class);
     job.setNumReduceTasks(0);
     
     job.setInputFormatClass(TextInputFormat.class);
     job.setOutputFormatClass(TextOutputFormat.class);
         
     FileInputFormat.setInputPaths(job, new Path("/vishwani/sortByDateOut"));
     FileOutputFormat.setOutputPath(job, new Path("/mxh133030/inpFiles/pout5"));
         
     job.setJarByClass(MineMapReduce.class);
     job.waitForCompletion(true);
     
     return job.waitForCompletion(true) ? 0 : 1;
  }
  
  public static void main(String[] args) throws Exception {
      int res = ToolRunner.run(new Configuration(), new MineMapReduce(), args);
      System.exit(res);
  }
         
 }
