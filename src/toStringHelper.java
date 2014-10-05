import java.util.List;

/**
 * Helper class for outputting.
 * @author dchouren
 *
 */
public class toStringHelper {
	
	/**
	 * Return newline separated List<String>.
	 * @param list
	 * @return
	 */
	public static String toStringCount(List<String> list) 
	{
		String output = "";
		String delim = "\t";
		int count = 1;
		for (String token : list) {
		    output += delim += count + "\t" + token;
		    delim = "\n\t";
		    count++;
		}
		
		return output;
	}
	
	public static String toString(List<String> list) 
	{
		String output = "";
		String delim = "";
		for (String token : list) {
		    output += delim += token;
		    delim = "\n";
		}
		
		return output;
	}
	
	
	/**
	 * Return comma separated List<String>.
	 * @param list
	 * @return
	 */
	public static String toOneString(List<String> list) 
	{
		String output = "";
		String delim = "\t";
		String delimOutput = "";
		int charCount = 0;
		for (String token : list) {
			delimOutput = delim + token;
			charCount += delimOutput.length();
		    output += delimOutput;
		    delim = "; ";
		    if (charCount > 150) {
		    	delim = ",\n\t";
		    	charCount = 0;
		    }
		}
		
		return output;
	}
}
