import java.util.ArrayList;
import java.util.List;

/**
 * Return common items between Lists.
 * @author dchouren
 */
public class CommonItems {
	
	
	/**
	 * Return common items in two List<String>.
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List<String> commonStrings(List<String> list1, 
			List<String> list2)
	{
		List<String> longerList = new ArrayList<String>();
		List<String> shorterList = new ArrayList<String>();
		
		if (list2.size() > list1.size()) {
			shorterList = list1;
			longerList = list2;
		}
		else {
			shorterList = list2;
			longerList = list1;
		}
		
		List<String> commonItems = new ArrayList<String>();
		for (String item : longerList) {
			if (shorterList.contains(item)) {
				commonItems.add(item);
			}
		}
		
		return commonItems;
	}
	
	
	
	
}
