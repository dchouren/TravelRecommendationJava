import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to write and read files.
 */
public class FileUtil {
	
	/**
	 * Write a string to fileNa
	 * @param outputString
	 * @param fileName
	 */
	public static void writeToFile(String outputString, String fileName) 
	{
		Writer writer = null;
	
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
		    writer.write(outputString);
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	public static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	
	public static List<String> listFileBasenames(String directory)
	{
		List<String> results = new ArrayList<String>();


		File[] files = new File(directory).listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 

		for (File file : files) {
		    if (file.isFile()) {
		        results.add(file.getName().split("\\.(?=[^\\.]+$)")[0]);
		    }
		}
		
		return results;
	}
	
	/**
	 * Write keys of a Map to file.
	 */
	public static void writeKeys(Map<String, Integer> map, String outputFile)
	{
		Map<String, Integer> ourMap = map;
		
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(outputFile), "utf-8"));
		    for (String key : ourMap.keySet()) {
		    	writer.write(key + "\n");
		    }
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
}














