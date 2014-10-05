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
	
	public void initSelectedCitiesFromArgs(MCMatrix mcMatrix, String citiesArgs) throws IOException
	{
		this.mcMatrix = mcMatrix;
		this.cities = findCitiesFromArgs(citiesArgs);
		this.sampleCityVector = fillCityVector();
	}
	
	
	private List<String> parseArgs(String citiesArgs)
	{
		ArrayList<String> inputCities = new ArrayList<String>(Arrays.asList(citiesArgs.split("\n")));
		return inputCities;
	}
	
	
	/**
	 * Return user-inputed list of cities. 
	 * @return
	 * @throws IOException 
	 */
	private List<String> findCities() throws IOException
	{
		List<String> inputCities = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String inputString = "";
		while (!inputString.equals("PREDICT")) {
			
//			System.out.print("Enter next city: ");
			
	        inputString = br.readLine();
	        
	        if (inputString.equals("END ALL")) {
	        	return null;
	        }
	        
			inputCities.add(inputString);
			
//			System.out.println(inputString);
		}
		
		return inputCities;
		
	}
	
	
	private List<String> findCitiesFromArgs(String citiesArgs)
	{
		List<String> inputCities = new ArrayList<String>();
		inputCities = parseArgs(citiesArgs);
		return inputCities;
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
//			System.out.println(thisIdf);
			
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












