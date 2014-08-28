import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates similarities between arrays.
 * @author dchouren
 *
 */
public class VectorSimilarity
{
	private static final Set<String> METRICS = new HashSet<String>(
			Arrays.asList(new String[] {"jaccard", "sorenson"}));
	
	private double[][] sortedMemberStats;
	private int y;
	private int x;
	private Map<String, List<Double>> similarityStats;
	private MCMatrix mcMatrix;
	private String metric;
	private double[] sampleCityVector;
	
	
	public VectorSimilarity(MCMatrix mcMatrix, double[] sampleCityVector,
			String whichMetric)
	{
		similarityStats = null;
		sortedMemberStats = null;
		x = 0;
		y = 0;
		this.mcMatrix = mcMatrix;
		this.metric = whichMetric;
                this.sampleCityVector = sampleCityVector;
		
	}
        	
	
	
	// XXX: maybe refactor standard deviation calculation into separate 
	// function and Map
	// XXX: maybe even refactor this into its own class
	/**
	 * Return a Map between members and a List<Double> containing their 
	 * similarity to a sampleCityVector and the standard deviation of their 
	 * score compared to other members. 
	 * @param sampleCityVector
	 * @param mcMatrix
	 * @param whichMetric
	 * @return
	 */
	private void calculateSimilarities()
	{
		assert METRICS.contains(this.metric);
		
		Map<Integer, String> memberIndexKeyMap = mcMatrix.getMemberIndexKeyMap();
		
		Map<String, List<Double>> memberStats = 
				new HashMap<String, List<Double>>();
		
		double meanTotal = 0;
		List<Double> similarityScores = new ArrayList<Double>();
		List<Integer> skippedMemberIndices = new ArrayList<Integer>();
		
		int y = mcMatrix.getY();
		int numMembers = y;
		for (int i = 0; i < y; i++) {
			
			double thisSimilarityScore = 0;
			String member = memberIndexKeyMap.get(i);
			double[] row = mcMatrix.getCityVector(member);
			
			if (this.metric == "jaccard") 
				thisSimilarityScore = jaccard(sampleCityVector, row);
			else if (this.metric == "sorenson")
				thisSimilarityScore = sorenson(sampleCityVector, row);
			
			if (Epsilon.areEqual(thisSimilarityScore, 1.0)) { // must be comparing to
				// ourselves
				numMembers--;
				skippedMemberIndices.add(i);
//				continue;
			}
			else {
				// will hold similarity score and z-score for each member
				
				
				meanTotal += thisSimilarityScore;
				similarityScores.add(thisSimilarityScore);
			}
			List<Double> stats = new ArrayList<Double>();
			stats.add(thisSimilarityScore);
			memberStats.put(member, stats);
		}
		
		// calculate z-scores for the similarity scores
		// XXX: should be in own function? maybe..
		double mean = meanTotal / numMembers;
		double std = std(similarityScores, mean);
		
		for (int i = 0; i < y; i++) {
//			if (skippedMemberIndices.contains(i)) {
//				continue;
//			}
			String member = memberIndexKeyMap.get(i);
			List<Double> stats = memberStats.get(member);
			double zscore = zscore(stats.get(0), mean, std);
			stats.add(zscore);
		}
		
		this.similarityStats = memberStats;
	}
	
	
	/**
	 * Create the sortedMemberStats array by sorting Map of member stats
	 * by similarityScore. member index, similarity score, stddev
	 * @param similarityStats
	 * @param mcMatrix
	 */
	private void sortedMemberStats()
	{
		Map<Integer, String> memberIndexKeyMap = 
				this.mcMatrix.getMemberIndexKeyMap();
		
		int y = this.mcMatrix.getY();
		
		// 3 columns are memberID, similarity score, std deviation
		double[][] memberStatsArray = new double[y][3];
		this.sortedMemberStats = memberStatsArray;
		this.y = y;
		this.x = 3;
		
		for (int i = 0; i < y; i++) {
			String member = memberIndexKeyMap.get(i);
			double similarityScore = getSimilarityScore(member);
			double stdDev = getStandardDeviation(member);
			memberStatsArray[i][0] = i;
			memberStatsArray[i][1] = similarityScore;
			memberStatsArray[i][2] = stdDev;
		}
		
		// sort on similarityScore
		ArrayHelper.sort2DArray(memberStatsArray, 1);
		
		this.sortedMemberStats = memberStatsArray;
	}
	
	
	/**
	 * Return sorted member statistics array according to similarity with 
	 * a sampleCityVector as calculating using whichMetric.
	 * @param sampleCityVector
	 * @param whichMetric
	 * @return
	 */
	public void initVectorSimilarity()
	{
		this.calculateSimilarities();
		this.sortedMemberStats();
	}
	
	
	/**
	 * Return the top k similar members.
	 * @param k
	 * @return
	 */
	public List<double[]> topK(int k)
	{
		double[][] sortedMemberStats = this.sortedMemberStats;
		List<double[]> topK = new ArrayList<double[]>();
		
		int rowsAdded = 0;
		for (double[] row : sortedMemberStats) {
			if (Epsilon.areEqual(row[1], 1.0))
				continue;
			topK.add(row);
			rowsAdded++;
			if (rowsAdded == k)
				break;
		}
		
		return topK;
	}
	
	
	/**
	 * Return the similar members whose standard deviations exceed specifiedttttttttttttttt
	 * threshold. Return at least the top member.
	 * @param stdThreshold
	 * @return
	 */
	public List<double[]> topStd(double stdThreshold)
	{
		double[][] sortedMemberStats = this.sortedMemberStats;
		List<double[]> topStd = new ArrayList<double[]>();
		
		int i = 0;
		double std = sortedMemberStats[i][2];;
		double score;
		
		do {
			score = sortedMemberStats[i][1];
			if (Epsilon.areEqual(score, 1.0)) {
				i++;
				continue;
			}

			topStd.add(sortedMemberStats[i]);
			
			if (i + 1 == sortedMemberStats.length)
				break;
			
			i++;
			std = sortedMemberStats[i][2];
		} while (std > stdThreshold);
		
		return topStd;
	}
	
	
	
	public List<String> topStdMembers(double stdThreshold)
	{
		List<String> topMembers = new ArrayList<String>();
		List<double[]> topStd = topStd(stdThreshold);
		Map<Integer, String> memberIndexKeyMap = 
				this.mcMatrix.getMemberIndexKeyMap();
		
		for (double[] row : topStd) {
			String member = memberIndexKeyMap.get(row[0]);
			topMembers.add(member);
		}
		
		return topMembers;
	}
	
	
	/***********************************************************************
	 * GETTERS                                                             *
	 ***********************************************************************/
	
	public int getY() {
		return this.y;
	}
	public int getX() {
		return this.x;
	}
	
	private Map<String, List<Double>> getSimilarityStats() 
	{
		return this.similarityStats;
	}
	
	public double getSimilarityScore(String member)
	{
//		System.out.println(member);
		return this.getSimilarityStats().get(member).get(0);
	}
	public double getStandardDeviation(String member)
	{
		return this.similarityStats.get(member).get(1);
	}
	
	public double[][] getSortedStats()
	{
		return this.sortedMemberStats;
	}
	
	
	public MCMatrix getMCMatrix()
	{
		return this.mcMatrix;
	}
	
	
	
	/***********************************************************************
	 * HELPERS                                                             *
	 ***********************************************************************/
	
	
	/**
	 * Return the sorenson similarity between two arrays.
	 * @param array1
	 * @param array2
	 * @return
	 */
	private static double sorenson(double[] array1, double[] array2) 
	{
		int intersection = SetUtils.intersection(array1, array2);
		int size1 = SetUtils.getSize(array1);
		int size2 = SetUtils.getSize(array2);
		
		double similarity = 2.0 * intersection / (size1 + size2);
		
		return similarity;
	}
	
	/**
	 * Return the jaccard similarity between two arrays.
	 * @param array1
	 * @param array2
	 * @return
	 */
	private static double jaccard(double[] array1, double[] array2)
	{
//		int intersection = SetUtils.intersection(array1, array2);

		int intersection = SetUtils.additiveIntersection(array1, array2);
		int union = SetUtils.union(array1, array2);
		
		if (union == 0)
			return 1;
		
		double similarity = (double) intersection / union;
		
		return similarity;
	}
	
	
	
	/**
	 * Return the standard deviation of an List of values.
	 * @param values
	 * @param mean
	 * @return
	 */
	private static double std(List<Double> values, double mean)
	{
		double sd = 0;
		double varianceSum = 0;
		
		for (int i = 0; i < values.size(); i++)
		{
		    varianceSum += Math.pow(values.get(i) - mean, 2);
	    }
         sd = Math.sqrt(varianceSum / values.size());
	
	    return sd;
	}
	
	
	/**
	 * Return the z-score of a number.
	 * @param value
	 * @param mean
	 * @param std
	 * @return
	 */
	private static double zscore(double value, double mean, double std)
	{
        return (value - mean) / std; 
	}
	
	
	
}










