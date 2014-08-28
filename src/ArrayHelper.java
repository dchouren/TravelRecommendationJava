/**
 * Helper class 
 * @author dchouren
 *
 */
public class ArrayHelper {
	/**
	 * Reverse sort a 2d array by row on the column given by index.
	 * Helper method for sortedMemberStats().
	 */
	public static void sort2DArray(double[][] array, final int index)
	{
		java.util.Arrays.sort(array, new java.util.Comparator<double[]>() {
		    public int compare(double[] a, double[] b) {
		        return -1 * Double.compare(a[index], b[index]);
		    }
		});
	}
}
