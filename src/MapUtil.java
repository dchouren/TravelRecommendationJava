import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for key index mapping.
 * @author dchouren
 *
 */
public class MapUtil {

	/**
	 * Return a Map between keys in a Map that won't change and an incrementing
	 * Integer value that will correspond with the matrix entry.
	 * @param set
	 * @return
	 */
	public static Map<String, Integer> keyIndexMap(
			Map<String, List<String>> set)
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		int index = 0;
		for (String key : set.keySet()) {
			map.put(key, index);
			index++;
		}

		return map;
	}

	/**
	 * Reverse a keyIndexMap.
	 * @param keyIndexMap
	 * @return
	 */
	public static Map<Integer, String> indexKeyMap(
			Map<String, Integer> keyIndexMap)
	{
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (String key : keyIndexMap.keySet()) {
			map.put(keyIndexMap.get(key), key);
		}

		return map;
	}
}
