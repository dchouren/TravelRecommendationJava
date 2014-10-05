import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Creates matrix representing member and city intersections.
 * @author dchouren
 *
 */
public class MCMatrix
{
	private final static int SECONDS_TO_NANO = 1000000000;
	private final static int MIN_CITIES = 50;
	private final static int MIN_PERCENT = 8;
	
	private final static String[] FORUMS = {
		"http://www.tripadvisor.com/ShowForum-g293910-i9303-Taiwan.html",
		"http://www.tripadvisor.com/ShowForum-g293915-i3686-Thailand.html",
		"http://www.tripadvisor.com/ShowForum-g293921-i8432-Vietnam.html",
		"http://www.tripadvisor.com/ShowForum-g293953-i7445-Maldives.html",
		"http://www.tripadvisor.com/ShowForum-g293951-i7006-Malaysia.html",
		"http://www.tripadvisor.com/ShowForum-g293889-i9243-Nepal.html",
		"http://www.tripadvisor.com/ShowForum-g293860-i511-India.html",
		"http://www.tripadvisor.com/ShowForum-g28926-i29-California.html",
		"http://www.tripadvisor.com/ShowForum-g28930-i18-Florida.html",
		"http://www.tripadvisor.com/ShowForum-g28927-i252-Colorado.html",
		"http://www.tripadvisor.com/ShowForum-g28943-i319-Michigan.html",
		"http://www.tripadvisor.com/ShowForum-g28951-i77-New_Jersey.html",
		"http://www.tripadvisor.com/ShowForum-g154967-i326-Nova_Scotia.html",
		"http://www.tripadvisor.com/ShowForum-g154922-i80-British_Columbia.html",
		"http://www.tripadvisor.com/ShowForum-g150805-i7-Yucatan_Peninsula.html",
		"http://www.tripadvisor.com/ShowForum-g190455-i550-Norway.html",
		"http://www.tripadvisor.com/ShowForum-g190372-i9179-Cyprus.html",
		"http://www.tripadvisor.com/ShowForum-g294451-i3250-Bulgaria.html",
		"http://www.tripadvisor.com/ShowForum-g189952-i223-Iceland.html",
		"http://www.tripadvisor.com/ShowForum-g294200-i9124-Egypt.html",
		"http://www.tripadvisor.com/ShowForum-g293740-i9186-South_Africa.html",
		"http://www.tripadvisor.com/ShowForum-g293794-i9249-Gambia.html",
		"http://www.tripadvisor.com/ShowForum-g294206-i9216-Kenya.html",
		"http://www.tripadvisor.com/ShowForum-g294291-i1357-Chile.html",
		"http://www.tripadvisor.com/ShowForum-g294311-i818-Peru.html",
		"http://www.tripadvisor.com/ShowForum-g294266-i977-Argentina.html",
		"http://www.tripadvisor.com/ShowForum-g294331-i883-Fiji.html",
		"http://www.tripadvisor.com/ShowForum-g294338-i867-French_Polynesia.html",
		"http://www.tripadvisor.com/ShowForum-g294006-i2046-Oman.html",
		"http://www.tripadvisor.com/ShowForum-g293977-i1733-Israel.html",
		"http://www.tripadvisor.com/ShowForum-g293985-i2131-Jordan.html"
	}; 
	private static float totalTimer = 0;


	private double[][] matrix;
	private int y; // number of rows in matrix
	private int x; // number of cols in matrix
	private Map<String, Integer> cityKeyIndexMap;
	private Map<String, Integer> memberKeyIndexMap;
	private Map<Integer, String> cityIndexKeyMap;
	private Map<Integer, String> memberIndexKeyMap;
	private Map<String, List<String>> memberSets;
	private Map<String, List<String>> citySets;
	private Map<String, Double> cityIdf;

	/**
	 * Basic constructor for MCMatrix.
	 */
	public MCMatrix()
	{
		matrix = null;
		y = 0;
		x = 0;
		cityKeyIndexMap = null;
		memberKeyIndexMap = null;
		cityIndexKeyMap = null;
		memberIndexKeyMap = null;
		memberSets = new HashMap<String, List<String>>();
		citySets = new HashMap<String, List<String>>();
		cityIdf = null;
	}

//	public static String[] split(String strToSplit, String delimiter) {
//	    List<String> arr = new ArrayList<>();
//	    int foundPosition;
//	    int startIndex = 0;
//	    while ((foundPosition = strToSplit.indexOf(delimiter, startIndex)) > -1) {
//	        arr.add(strToSplit.substring(startIndex, foundPosition));
//	        startIndex = foundPosition + delimiter.length();
//	    }
//	    arr.add(strToSplit.substring(startIndex));
//	    System.out.println(arr.size());
//	    return arr.toArray(new String[arr.size()]);
//	}
	
	/**
	 * Return List<String> of cities that a member has visited. Update 
	 * memberSets and citySets.
	 * @param member
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public List<String> readVisitedCities(String member) 
			throws IOException, ParseException
	{
		List<String> cities = null;
		
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		String memberHtml = FileUtil.readFile(s + "/memberPages/" + member 
				+ ".txt", StandardCharsets.UTF_8);
		Document memberPage = Jsoup.parse(memberHtml);
		
		// make sure we have at least MIN_CITIES
		String pins = memberPage.select("span[class=pin_counts pc_all]").text();
		String strippedPins = pins.substring(1, pins.length() - 1);
		int pinCount = NumberFormat.getNumberInstance(
							java.util.Locale.US).parse(strippedPins).intValue();
		if (pinCount < MIN_CITIES)
			return null;
		
		// make sure we have at least MIN_PERCENT
		String worldTraveled = memberPage.select(
				"div[class=statNumber percentStat").text();
		if (worldTraveled.isEmpty() || worldTraveled == null)
			return null;
		int travelPercent = Integer.parseInt(
					worldTraveled.substring(0, worldTraveled.length() - 1));
		if (travelPercent < MIN_PERCENT)
			return null;

	
		Elements scripts = memberPage.getElementsByTag("script");
		for (Element script : scripts) {

			String scriptString = script.html();

			if (!scriptString.startsWith("DUST_GLOBAL")) {
				continue;
			}

			//			PrintWriter writer = new PrintWriter("test.txt", "UTF-8");
			//			writer.println(scriptString);
			//			writer.close();
			
//			Pattern mapPin = Pattern.compile(
//					"(been|fave|want)\"\\],\"name\":\"");
			String[] splitJson = memberPage.toString().split(
					"(been|fave)\"\\],\"name\":\"");
			
//			float startTime = System.nanoTime();
//			String[] splitJson = split(scriptString, 
//					"\"flags\":\\[\"been\"\\],\"name\":\"");
//			float endTime = System.nanoTime();
//			totalTimer += (endTime - startTime) / SECONDS_TO_NANO;
			
//			if (splitJson.length < MIN_CITIES + 1)
//				return null;
			
			
			cities = new ArrayList<String>(Arrays.asList(splitJson));

			// format city entries
			ListIterator<String> i = cities.listIterator(1);
			while (i.hasNext()) {
				String cityEntry = i.next();
				cityEntry = cityEntry.substring(0, cityEntry.indexOf('\"'));
				i.set(cityEntry);


				// update memberSets
				if (this.memberSets != null && 
						this.memberSets.get(cityEntry) != null) 
				{
					this.memberSets.get(cityEntry).add(member);
				}
				else {
					List<String> memberSet = new ArrayList<String>(
							Arrays.asList(member)); 
					this.memberSets.put(cityEntry, memberSet);
				}
			}

			// first entry is garbage javascript stuff
			cities.remove(0);  
		}

		// update citySets
		if (cities != null) {
//			System.out.println(member);
			this.citySets.put(member, cities);
		}

		return cities;
	}



	/**
	 * Scrape all cities that all members have visited. Update memberSets,
	 * citySets.
	 * @param forums
	 * @throws IOException 
	 * @throws ParseException 
	 */
	private void readAllCities() throws IOException, ParseException
	{
		List<String> experts = FileUtil.listFileBasenames("memberPages/");
		if (experts == null)
			return;
		
//		List<String> experts = TripAdvisorScraper.scrapeAllMembers();

		for (int j = 0; j < experts.size(); j++) {
			String thisExpert = experts.get(j);

			// does all the dirty work of creating sets
			readVisitedCities(thisExpert);
		}
	}

	
	
	/**
	 * Initialize MCMatrix array's access key-index and index-key maps. 
	 * Initialize the actual array.
	 */
	public void initMatrix()
	{
		this.cityKeyIndexMap = MapUtil.keyIndexMap(memberSets);
		this.memberKeyIndexMap = MapUtil.keyIndexMap(citySets);
		this.cityIndexKeyMap = MapUtil.indexKeyMap(cityKeyIndexMap);
		this.memberIndexKeyMap = MapUtil.indexKeyMap(memberKeyIndexMap);
		
		this.x = cityIndexKeyMap.size();
		this.y = memberIndexKeyMap.size();

		double[][] matrix = new double[this.y][this.x];
		this.matrix = matrix;
	}
	
	
	/**
	 * Mark 1.0 matrix cells.
	 */
	public void fillMatrix()
	{
		for (int i = 0; i < this.matrix.length; i++) {
			String member = memberIndexKeyMap.get(i);
			
			List<String> theseCities = citySets.get(member);
			
			// find mark indices corresponding to theseCities
			for (int k = 0; k < theseCities.size(); k++) {
				int beenIndex = cityKeyIndexMap.get(theseCities.get(k));
				matrix[i][beenIndex] = 1.0;
			}		
		}
	}
	
	
	/**
	 * Helper function to help calculate tf-idf score.
	 * @param termFrequency
	 * @param inverseDocumentFrequency
	 * @return
	 */
	private static double tfIdf(double termFrequency, double inverseDocumentFrequency)
	{
		double tf = 0;
		if (!Epsilon.areEqual(0, termFrequency)) {
			tf = 1;
		}
		double idf = Math.log(inverseDocumentFrequency);
		
		return tf * idf;
	}
	
	
	/**
	 * Update matrix with tf-idf scores.
	 */
	public void tfIdfMatrix() 
	{
		int numCols = this.memberSets.size();
		int numRows = this.citySets.size();
		
		double[][] newMatrix = new double[numRows][numCols];
		double[][] matrix = this.matrix;
		
		Map<String, Double> cityIdf = new HashMap<String, Double>();
		
		for (int i = 0; i < numRows; i++) {
			double[] row = matrix[i];
			int rowTotal = SetUtils.getSize(row);
			
			for (int j = 0; j < numCols; j++) {
				double termFreq = (double)row[j] / rowTotal;
				
				String thisCity = this.cityIndexKeyMap.get(j);
				List<String> members = this.memberSets.get(thisCity); 
				double inverseDocFreq = (double) numRows / members.size();
				
				cityIdf.put(thisCity, tfIdf(1, inverseDocFreq));
				
				newMatrix[i][j] = tfIdf(termFreq, inverseDocFreq);
			}
		}
		
		this.cityIdf = cityIdf;
		this.matrix = newMatrix;
	}
	
	
	
	/**
	 * Map city List<Double> to their member. Called when tfidf scores have
	 * been calculated so effectively maps each tfidf score to the correct
	 * member.
	 * @param member
	 */
//	public void extractCityVectors()
//	{
//		Map<String, Integer> memberKeyIndexMap = this.memberKeyIndexMap;
//		for (String member : memberKeyIndexMap.keySet()) {
//			double[] rowVector = this.matrix[memberKeyIndexMap.get(member)];
//			
//			double[] cityVector = new double[this.cityKeyIndexMap.size];
//			
//			this.cityVectors.put(member, cityVector);
//		}
//	}
	
	
	/***********************************************************************
	 * GETTER METHODS                                                      *
	 ***********************************************************************/
	
	public int getY()
	{
		return this.y;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	/**
	 * Return all members.
	 * @return
	 */
	public Set<String> getMembers() 
	{
		return this.memberKeyIndexMap.keySet();
	}
	
	/**
	 * Return all cities.
	 * @return
	 */
	public Set<String> getCities()
	{
		return this.cityKeyIndexMap.keySet();
	}
	
	public Map<Integer, String> getMemberIndexKeyMap()
	{
		return this.memberIndexKeyMap;
	}
	public Map<String, Integer> getMemberKeyIndexMap()
	{
		return this.memberKeyIndexMap;
	}
	public Map<Integer, String> getCityIndexKeyMap()
	{
		return this.cityIndexKeyMap;
	}
	public Map<String, Integer> getCityKeyIndexMap()
	{
		return this.cityKeyIndexMap;
	}
	public Map<String, Double> getCityIdf()
	{
		return this.cityIdf;
	}
	public Map<String, List<String>> getMemberSets()
	{
		return this.memberSets;
	}

	
	/**
	 * Return the city vector for a particular member.
	 * @param member
	 * @return
	 */
	public double[] getCityVector(String member)
	{
		return this.matrix[this.memberKeyIndexMap.get(member)];
	}
	
	
	
	
	/***********************************************************************
	 * MAIN METHOD                                                         
	 * @throws ParseException *
	 ***********************************************************************/

	public static void main(String [] args) throws IOException, ParseException
	{
//		TripAdvisorScraper.writeAllPages();
		
//		Probability.subsetOccuranceProbability(1, 2, 1,4);
//		Probability.cumulativeSubsetOccuranceProbability(0, 1, 10, 10);
		
		/**
		 * Initialize MCMatrix
		 */
		DecimalFormat df = new DecimalFormat("#.##");
		float startTime = System.nanoTime();

		MCMatrix mcMatrix = new MCMatrix();
		mcMatrix.readAllCities();
		mcMatrix.initMatrix();
		mcMatrix.fillMatrix();
		mcMatrix.tfIdfMatrix();
		
		float endTime = System.nanoTime();
		float totalTime = (endTime - startTime) / SECONDS_TO_NANO;
//		System.out.println("\nFinished parsing " + mcMatrix.memberSets.size() 
//				+ " unique cities and " + mcMatrix.citySets.size() 
//				+ " members in " + totalTime + " seconds.\n");
		
		
		// write all cities to a file
		FileUtil.writeKeys(mcMatrix.getCityKeyIndexMap(), "cities.txt");
		
		
		
		/**
		 * Search queries
		 */
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		Recommender recommender = new Recommender(mcMatrix);
		recommender.initRecommender("jaccard", "top std", 20, 2.0, 3);
		
		
		
		/**
		 * one shot query with cities passed all at once through args[0]
		 */
/*		SelectedCities sample = new SelectedCities();
		String citiesArgs = args[0];
		sample.initSelectedCitiesFromArgs(mcMatrix, citiesArgs);
		
//		float searchBeginTime = System.nanoTime();
		double[] sampleCityVector = sample.getSampleCityVector();
		List<String> sampleCities = sample.getSampleCities();
		
		recommender.makeRecommendations(sampleCityVector, 20);
		List<String> recommendedCities = recommender.getRecommendations();
		recommender.printRecommendationNames();
		*/
//		System.out.println("here");
		
		
		String inputString = "";
		while (!inputString.equals("END ALL")) {
			
//			System.out.println();
//			System.out.println("-----------------------");
			
			

			SelectedCities sample = new SelectedCities();
			sample.initSelectedCities(mcMatrix);
			
//			float searchBeginTime = System.nanoTime();
			double[] sampleCityVector = sample.getSampleCityVector();
//			List<String> sampleCities = sample.getSampleCities();
			
			
			recommender.makeRecommendations(sampleCityVector, 20);
//			float searchEndTime = System.nanoTime();
//			float totalSearchTime = (searchEndTime - searchBeginTime) 
//										/ SECONDS_TO_NANO;
//			
//			System.out.print("\nSimilar members:\n");
//			recommender.printSimilarMembers();
//			System.out.println();
//			System.out.println("Recommended cities:");
//			List<String> recommendedCities = recommender.getRecommendations();
			
			
			recommender.printRecommendationNames();
			
//			System.out.println();
//			System.out.println("Search took " + totalSearchTime + " seconds.");
//			
//			System.out.print("\nNext Command: ");
			
			
			/*
			inputString = br.readLine();*/
//			
//			List<String> similarMembers = recommender.getSimilarMembers();
//			Map<String, String> memberStdMap = recommender.getMemberStdMap();
//			Map<String, Double> memberWeightMap = 
//					recommender.getMemberWeightMap();

			
			
			/**
			 * get information about this query
			 */
			/*while (!inputString.equals("NEXT SEARCH") 
					&& !inputString.equals("END ALL")) {
				List<String> memberSets = mcMatrix.memberSets.get(inputString);
				List<String> citySets = mcMatrix.citySets.get(inputString);
				
				// if input is a city
				if (memberSets != null) {
					System.out.println();
					double tfIdf = tfIdf(1, memberSets.size());
					System.out.println("Tf-idf: " + df.format(tfIdf));
					System.out.println();
					System.out.println(
							"Member set (size " + memberSets.size() + "): ");
					System.out.println(toStringHelper.toOneString(memberSets));
					
					System.out.println();
					List<String> commonMembers = recommender.commonMembers(
							inputString, similarMembers);
					System.out.print(
						"Similar members (size " + commonMembers.size() 
									+ "):\n\t");
					
					for (String member : commonMembers) {
						System.out.print(member + " " 
									+ memberStdMap.get(member) + "; ");
					}
					System.out.println();
//					System.out.println(toStringHelper.toOneString(
//							commonMembers));
				}
				// if input is a member
				else if (citySets != null) {
					
					System.out.println();
					System.out.println(citySets.size() + " cities");
					if (memberStdMap.get(inputString) != null) {
						System.out.println("Std: " 
								+ memberStdMap.get(inputString));
					}
//					if (memberWeightMap.get(inputString) != null) {
//						System.out.println("Weight: " 
//								+ memberWeightMap.get(inputString));
//					}
					System.out.println();
					List<String> commonCities = CommonItems.commonStrings(
							citySets, sampleCities);
					System.out.print(
						"Common cities (size " + commonCities.size() + "):\n\t");
					
					for (String city : commonCities) {
						List<String> memberSet = mcMatrix.memberSets.get(city);
						double tfIdf = tfIdf(1, memberSet.size());
						System.out.print(city + ": " + df.format(tfIdf) + "; ");
					}
					
					System.out.println();
					System.out.println();
					List<String> memberRecommended = CommonItems.commonStrings(
							citySets, recommendedCities);
					System.out.print(
						"Recommended cities (size " + memberRecommended.size() 
						+ "):\n");
					int count = 0;
					for (String city : memberRecommended) {
						List<String> memberSet = mcMatrix.memberSets.get(city);
						if (count % 8 == 0)
							System.out.println('\t');
						double tfIdf = tfIdf(1, memberSet.size());
						System.out.print(city + ": " + df.format(tfIdf) + "; ");

						count++;
					}
					
					System.out.println();
//					System.out.println(toStringHelper.toOneString(
//							commonCities));
				}
				else if (inputString.equals("similar members")) {
					System.out.println("Similar members:");
					int count = 0;
					for (String member : similarMembers) {
						if (count % 8 == 0)
							System.out.println('\t');
						System.out.print(member + " " 
									+ memberStdMap.get(member) + "; ");

						count++;
					}
				}
				else if (inputString.equals("recs")) {
					System.out.println("Recommended cities:");
					recommender.printRecommendations();
				}
				else if (inputString.equals("sample cities")) {
					System.out.println(toStringHelper.toString(sampleCities));
				}
				System.out.println();
				System.out.println("**********************");
				System.out.print("Next Command: ");
				inputString = br.readLine();
			}*/
		}
		
//		System.out.println();
//		System.out.println("Terminated.");

	
	}
}