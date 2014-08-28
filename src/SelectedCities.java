import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates a city vector.
 * @author dchouren
 *
 */
public class SelectedCities {
	
	private List<String> cities;
	private double[] sampleCityVector;
	private MCMatrix mcMatrix;
	
	public SelectedCities() 
	{
		cities = null;
//		cities = ArrayList<String>(Arrays.asList(s))
		sampleCityVector = null;
		mcMatrix = null;
	}
	
	
	public void initSelectedCities(MCMatrix mcMatrix) throws IOException
	{
		this.mcMatrix = mcMatrix;
		this.cities = findCities();
		this.sampleCityVector = fillCityVector();
	}
	
	
	/**
	 * Return user-inputed list of cities. 
	 * @return
	 * @throws IOException 
	 */
	private List<String> findCities() throws IOException
	{
		ArrayList<String> inputCities = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String inputString = "";
		while (!inputString.equals("PREDICT")) {
			
			System.out.print("Enter next city: ");
			
	        inputString = br.readLine();
	        
	        if (inputString.equals("END ALL")) {
	        	return null;
	        }
	        
			inputCities.add(inputString);
			
//			System.out.println(inputString);
		}
		return inputCities;
		
//		return new ArrayList<String>(
//				Arrays.asList( 
//				"West Yellowstone, MT, USA",
//				"Gatlinburg, TN, USA",
//				"Yosemite Village, CA, USA",
//				"Denali National Park and Preserve, AK, USA",
//				"Crater Lake National Park, OR, USA",
//				"Kakadu National Park, Australia",
//				"Lake Manyara National Park, Tanzania",
//				"Queen Elizabeth National Park, Uganda",
//				"Dartmoor National Park, UK",
//				"Uluru-Kata Tjuta National Park, Australia",
//				"Tongariro National Park, New Zealand",
//				"Chitwan National Park, Nepal",
//				"Lauca National Park, Chile",
//				"Serengeti National Park, Tanzania",
//				"Sequoia and Kings Canyon National Park, CA, USA"
//				));
	}
	
	
	/**
	 * Fill sampleCityVector with 1.0 if city is in our list.
	 */
	private double[] fillCityVector()
	{
		double[] sampleCityVector = new double[this.mcMatrix.getX()];
		
		Map<String, Double> cityIdf = this.mcMatrix.getCityIdf();
		Map<String, Integer> cityKeyIndexMap = 
										this.mcMatrix.getCityKeyIndexMap();
		
		for (String city : this.cities) {
			Integer index = cityKeyIndexMap.get(city);
			if (index == null)
				continue;
			
			double thisIdf = cityIdf.get(city);
			sampleCityVector[index] = thisIdf;
			System.out.println(thisIdf);
			
		}
		
		return sampleCityVector;
	}
	
	
	/***********************************************************************
	 * GETTER METHODS                                                      *
	 ***********************************************************************/
	
	public double[] getSampleCityVector()
	{
		return this.sampleCityVector;
	}
	
	public List<String> getSampleCities()
	{
		return this.cities;
	}
	
	
	public static void main(String [] args) 
	{
		
	}
}












