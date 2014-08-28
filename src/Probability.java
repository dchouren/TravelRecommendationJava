
public class Probability {
	/**
	 * Return value of n choose k.
	 * @param n
	 * @param k
	 * @return
	 */
	private static int nChooseK(int n, int k) 
	{
		// N! / K!
		int factorial = 1;
		for (int i = n; i > k; i--) {
			factorial *= i;
		}
		
		// (1 / (N-K)!)
		for (int i = 2; i <= n - k; i++) {
			factorial /= i;
		}
		
		return factorial;
	}
	
	/**
	 * Return probability that given totalOccurances occurrances in setSize
	 * events, EXACTLY subsetOccurances of these occur within a subset of 
	 * subsetSize.
	 * @param subsetOccurances
	 * @param totalOccurances
	 * @param subsetSize
	 * @param setSize
	 * @return
	 */
	public static double subsetOccuranceProbability(int subsetOccurances, 
			int totalOccurances, int subsetSize, int setSize)
	{
		double probability = (double) totalOccurances / setSize;
		double probabilityNot = 1 - probability;
		int numCombinations = nChooseK(subsetSize, subsetOccurances);
		
		double totalProbability = numCombinations 
					* Math.pow(probability, subsetOccurances)
					* Math.pow(probabilityNot, subsetSize - subsetOccurances);
		
//		System.out.println(numCombinations);
//		System.out.println("probability: " + probability);
//		 
//		System.out.println(totalProbability);
		return totalProbability;
	}
	
	
	/**
	 * Return probability that given totalOccurances occurrances in setSize
	 * events, AT LEAST subsetOccurances of these occur within a subset of 
	 * subsetSize.
	 * @param subsetOccurances
	 * @param totalOccurances
	 * @param subsetSize
	 * @param setSize
	 * @return
	 */
	public static double cumulativeSubsetOccuranceProbability(
			int subsetOccurances, int totalOccurances, int subsetSize, 
			int setSize)
	{
		
		double inSubset = 0.0;
		double probability = (double) totalOccurances / setSize;
		double probabilityNot = 1 - probability;
		int numCombinations = nChooseK(subsetSize, subsetOccurances);
		
		for (int i = 0; i < subsetSize - subsetOccurances; i++) {
			inSubset += numCombinations 
					* Math.pow(probability, subsetOccurances);
		}
		
		double numerator = 0.0;
		for (int i = 0; i < subsetSize - subsetOccurances; i++) {
			inSubset += subsetOccuranceProbability(subsetOccurances + i,
					totalOccurances, subsetSize, setSize);
		}
		double cumProbability = 0.0;
		for (int i = subsetOccurances; i <= Math.min(totalOccurances,subsetSize); i++) {
			cumProbability += subsetOccuranceProbability(subsetOccurances + i,
					totalOccurances, subsetSize, setSize);
		}
		
//		double cumProbability = numerator / inSubset;
		
		System.out.println(cumProbability);
		return cumProbability;
	}
}
