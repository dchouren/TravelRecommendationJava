import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Makes city recommendations.
 * @author dchouren
 *
 */
public class Recommender 
{
	private MCMatrix mcMatrix;
	private List<String> recommendations;
	private String similarityMetric;
	private String memberOrdering;
	private int k;
	private double stdThreshold;
	private int numCitySetsThreshold;
	private static final Set<String> METRICS = new HashSet<String>(
			Arrays.asList(new String[] {"jaccard", "sorenson"}));
	private static final Set<String> ORDERINGS = new HashSet<String>(
			Arrays.asList(new String[] {"top k", "top std"}));
	private List<String> similarMembers;
	private Map<String, Double> sortedMemberStats;
	private Map<String, String> memberStdMap;
	private Map<String, Double> memberWeightMap;
	private List<String>recommendationWeights;
	
	public Recommender(MCMatrix mcMatrix) 
	{
		this.mcMatrix = mcMatrix;
		recommendations = null;
		similarityMetric = null;
		memberOrdering = null;
		similarMembers = null;
		k = 0;
		stdThreshold = 0;
		numCitySetsThreshold = 0;
		sortedMemberStats = null;
		memberStdMap = null;
		memberWeightMap = null;
		recommendationWeights = null;
	}
	
	
	private void setSimilarityMetric(String metric)
	{
		this.similarityMetric = metric;
	}
	
	private void setMemberOrdering(String ordering)
	{
		this.memberOrdering = ordering;
	}
	
	private void setK(int k)
	{
		this.k = k;
	}
	
	private void setStdThreshold(double stdThreshold) 
	{
		this.stdThreshold = stdThreshold;
	}
	
	private void setNumCitySetsThreshold(int numCitySetsThreshold)
	{
		this.numCitySetsThreshold = numCitySetsThreshold;
	}
	
	
	public void initRecommender(String whichMetric, String whichOrdering,
			int k, double stdThreshold, int numCitySetsThreshold)
	{
		assert(k < this.mcMatrix.getY());
		String ordering = whichOrdering.toLowerCase();
		String metric = whichMetric.toLowerCase();
		assert(ORDERINGS.contains(ordering));
		assert(METRICS.contains(metric));
		
		setSimilarityMetric(metric);
		setMemberOrdering(ordering);
		setStdThreshold(stdThreshold);
		setK(k);
		setNumCitySetsThreshold(numCitySetsThreshold);
	}
	
	
	/**
	 * Return a Set<Integer> of the city indices in sampleCityVector.
	 * @param sampleCityVector
	 * @return
	 */
	private Set<Integer> sampleCityIndices(double[] sampleCityVector) 
	{
		Set<Integer> sampleCityIndices = new HashSet<Integer>();
		
		for (int i = 0; i < sampleCityVector.length; i++) {
			if (!Epsilon.areEqual(sampleCityVector[i], 0))
			sampleCityIndices.add(i);
		}
		
		return sampleCityIndices;		
	}
	
	/**
	 * Get numRecs recommendations for sampleCityVector.
	 * @param sampleCityVector
	 * @param numRecs
	 */
	public void makeRecommendations(double[] sampleCityVector, 
			int numRecs) 
	{
		List<String> recommendationWeights = 
				new ArrayList<String>();
		List<double[]> similarRows = similarRows(sampleCityVector);
		this.similarMembers = extractSimilarMembers(similarRows);
		double[][] sortedCityWeights = sortedCityWeights(similarRows);
		
		if (sortedCityWeights.length < numRecs)
			numRecs = sortedCityWeights.length;
		
		Map<Integer, String> cityIndexKeyMap = 
				this.mcMatrix.getCityIndexKeyMap();
		
		Set<Integer> sampleCityIndices = sampleCityIndices(sampleCityVector);
		
		List<String> recommendations = new ArrayList<String>();
		int recsFound = 0;
		for (double[] row : sortedCityWeights) {
			
			if (recsFound == numRecs) // have found all our recs
				break;
			
			int cityIndex = (int)row[0];
			String weight = Double.toString(row[1]);
			int numCitySets = (int)row[2];
			
			// don't recommend this city since it's already in the 
			// sampleCityVector
			if (sampleCityIndices.contains(cityIndex)) {
				continue; 
			}
			
			if (numCitySets < this.numCitySetsThreshold) {
				continue;
			}
			
			// found a rec
			recommendations.add(cityIndexKeyMap.get(cityIndex));
			recommendationWeights.add(cityIndexKeyMap.get(cityIndex) + ": " 
					+ weight);
			recsFound++;
		}
		
		this.recommendations = recommendations;
		this.recommendationWeights = recommendationWeights;
	}
	
	
	
	/**
	 * Return members and their similarities to sampleCityVector.
	 * @param sampleCityVector
	 * @return
	 */
	private List<double[]> similarRows(double[] sampleCityVector)
	{
		VectorSimilarity vSimilarity = new VectorSimilarity(this.mcMatrix, 
				sampleCityVector, this.similarityMetric);
		vSimilarity.initVectorSimilarity();
		
//		double[][] sortedStats = vSimilarity.getSortedStats();
//		Map<String, Double> sortedStatsMap = new HashMap<String, Double>();
//		for (double[] row : sortedStats) {
//			String member = this.mcMatrix.getMemberIndexKeyMap().get(row[0]);
//			double stddev = row[2];
//			sortedStatsMap.put(member,  stddev);
//		}
//		this.sortedMemberStats = sortedStatsMap;
		
		String memberOrdering = this.memberOrdering;
		
		List<double[]> similarRows = new ArrayList<double[]>(); 		
		if (memberOrdering == "top std") {
			similarRows = vSimilarity.topStd(this.stdThreshold);
		}
		else if (memberOrdering == "top k") {
			similarRows = vSimilarity.topK(this.k);
		}
		
		return similarRows;
	}
	
	
	
	/**
	 * Return sorted city weights for all member-city combinations.
	 * @param similarMembers
	 * @return
	 */
	private double[][] sortedCityWeights(List<double[]> similarRows)
	{
		MCMatrix mcMatrix = this.mcMatrix;
		// holds index of city, city's total weighting, how many city sets
		// it is in
		double[][] cityWeights = new double[mcMatrix.getX()][3];
		
		double memberWeight;
		int memberIndex;
		String member;
		double[] memberCityVector;
		Map<Integer, String> memberIndexKeyMap = 
				mcMatrix.getMemberIndexKeyMap();
		Map<Integer, String> cityIndexKeyMap = 
				mcMatrix.getCityIndexKeyMap();
		
//		final int memberIndex_Index = 0;
//		final int cityWeight_Index = 1;
//		final int stdIndex = 2;
		
		Map<String, List<String>> memberSets = mcMatrix.getMemberSets();
		
		Map<String, Double> memberWeightMap = new HashMap<String, Double>();
		
//		System.out.println(similarMembers.size());
		for (double[] row : similarRows) {
			memberWeight = row[2]; // TODO change so we have methods for this
			memberIndex = (int)row[0];
			member = memberIndexKeyMap.get(memberIndex);
			memberCityVector = mcMatrix.getCityVector(member);
			

			memberWeightMap.put(member, memberWeight);
			
			for (int i = 0; i < cityWeights.length; i++) {
				cityWeights[i][0] = i;
//				if (memberCityVector[memberIndex_Index] > 0)
//					cityWeights[i][cityWeight_Index] += memberWeight;
				double cityTfIdf = memberCityVector[i];
				
				
				if (!Epsilon.areEqual(cityTfIdf, 0)) {
					cityWeights[i][1] += memberWeight * cityTfIdf;
					cityWeights[i][2] += 1;
				}
			}
			
			/*for (int i = 0; i < cityWeights.length; i++) {
				int memberSetSize = memberSets.get(
					cityIndexKeyMap.get(i)).size();
			
				cityWeights[i][1] = cumulativeSubsetOccuranceProbability(
						(int)cityWeights[i][2], memberSetSize,
						similarRows.size(), memberIndexKeyMap.size());
				
//				System.out.println(cityWeights[i][1]);
//				System.out.println((int)cityWeights[i][2]);
//				System.out.println(memberSetSize);
//				System.out.println(similarRows.size());
//				System.out.println(memberIndexKeyMap.size());
			}*/
		}
		this.memberWeightMap = memberWeightMap;
		
		ArrayHelper.sort2DArray(cityWeights, 1);
//		for (int i = 0; i < cityWeights.length; i++) {
//			System.out.println(cityWeights[i][0]);
//		}
		return cityWeights;
	}
	
	

	/**
	 * Return List<String> of similar members from double[][] similarRows.
	 * @param similarRows
	 * @return
	 */
	private List<String> extractSimilarMembers(List<double[]> similarRows)
	{
		Map<Integer, String> memberIndexKeyMap = 
				mcMatrix.getMemberIndexKeyMap();
		List<String> similarMembers = new ArrayList<String>();
		
		Map<String, String> memberStdMap = new HashMap<String, String>();
		
		DecimalFormat df = new DecimalFormat("#.##");
		
		for (double[] row : similarRows) {
			String member = memberIndexKeyMap.get((int)row[0]);
			memberStdMap.put(member, df.format(row[2]));
			similarMembers.add(member);
		}
		
		this.memberStdMap = memberStdMap;
		
		return similarMembers;
	}
	
	
	/**
	 * Return similar members that have city. 
	 * @param city
	 * @param similarMembers
	 * @return
	 */
	public List<String> commonMembers(String city, 
			List<String> similarMembers)
	{
		Map<String, List<String>> memberSets = this.mcMatrix.getMemberSets();
		List<String> memberSet = memberSets.get(city);
		
		List<String> commonMembers = CommonItems.commonStrings(memberSet, 
				similarMembers);
		
		return commonMembers;
	}
	

	public List<String> getRecommendations()
	{
		return this.recommendations;
	}
	public List<String> getSimilarMembers()
	{
		return this.similarMembers;
	}
	
	public Map<String, Double> getSortedStats()
	{
		return this.sortedMemberStats;
	}
	
	public Map<String, String> getMemberStdMap()
	{
		return this.memberStdMap;
	}
	
	public Map<String, Double> getMemberWeightMap()
	{
		return this.memberWeightMap;
	}
	
	
	/**
	 * Print recommendations.
	 */
	public void printRecommendations()
	{
		System.out.println(toStringHelper.toString(this.recommendationWeights));
	}
	
	/**
	 * Print similar members.
	 */
	public void printSimilarMembers()
	{
		System.out.println(toStringHelper.toOneString(this.getSimilarMembers()));
	}
	
	
//	public double getSimilarityScore(String member)
//	{
////		System.out.println(member);
//		return this.vectorSimilarity.getSimilarityStats().get(member).get(0);
//	}
	
	
	
	public static void main(String [] args) 
	{
		
	}
}