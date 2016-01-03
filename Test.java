package textmining;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Test {

	private static List<String> stopwords;
	
	public static void main(String args[]) throws IOException {
		loadStopWords();
		System.out.println("Enter string 1");
		TextData text1 = new TextData(new Scanner(System.in).nextLine());
		text1.process();
		System.out.println("Enter string 2");
		TextData text2 = new TextData(new Scanner(System.in).nextLine());
		text2.process();
		
		System.out.println("Unqiue chars1");
		for (String u: text1.getUniqueTokens()) {
			System.out.println(u);
		}
		
		
		System.out.println("Unqiue chars2");
		for (String u: text2.getUniqueTokens()) {
			System.out.println(u);
		}
		
		
		System.out.println(new Matcher(null).match(text1, text2));

	}
	
	
	private static void loadStopWords() throws IOException {
		//File sw = new File("/people/cs/s/sanda/cs6322/resourcesIR/stopwords");
		File sw = new File("C:/Manoj/Academia/Information Retrieval/HW2/stopwords");
		String data = Reader.readFromFile(sw);
		StringTokenizer tk = new StringTokenizer(data);
		stopwords = new ArrayList<String>();
		while(tk.hasMoreTokens()) {
			stopwords.add(tk.nextToken());
		}
	}
	
	
}
