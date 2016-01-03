package textmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Reader {

	public static String readFromFile(File fileEntry) throws IOException {
		FileReader fout = new FileReader(fileEntry);
		BufferedReader reader = new BufferedReader(fout);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line=reader.readLine())!=null) {
			sb.append(line+" ");
		}
		reader.close();
		return sb.toString();
	}
}
