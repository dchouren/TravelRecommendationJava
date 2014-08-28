import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.server.LoaderHandler;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.traversal.DocumentTraversal;


public class scrapeTripAdvisor 
{
	private static final String MEMBER_PAGE = 
			"http://www.tripadvisor.com/members/";
	private HashMap<String, List<String>> memberSets;
	private HashMap<String, List<String>> citySets;
	private static final String[] FORUMS = {
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
		    "http://www.tripadvisor.com/ShowForum-g294280-i1045-Brazil.html",
		    "http://www.tripadvisor.com/ShowForum-g294266-i977-Argentina.html",
		    "http://www.tripadvisor.com/ShowForum-g294331-i883-Fiji.html",
		    "http://www.tripadvisor.com/ShowForum-g294338-i867-French_Polynesia.html",
		    "http://www.tripadvisor.com/ShowForum-g294006-i2046-Oman.html",
		    "http://www.tripadvisor.com/ShowForum-g293977-i1733-Israel.html",
		    "http://www.tripadvisor.com/ShowForum-g293985-i2131-Jordan.html"
	};
	
	/**
	 * Return List<String> of cities that a member has visited.
	 * @param member
	 * @return
	 * @throws IOException
	 */
	public List<String> scrapeVisitedCities(String member) 
			throws IOException
	{
		List<String> cities = null;
		
		String memberUrl = MEMBER_PAGE + member;
		Document memberPage = Jsoup.connect(memberUrl).get();
		
		Elements scripts = memberPage.getElementsByTag("script");
		for (Element script : scripts) {
			String scriptString = script.html();
			
			if (!scriptString.startsWith("DUST_GLOBAL")) {
				continue;
			}
			
//			PrintWriter writer = new PrintWriter("test.txt", "UTF-8");
//			writer.println(scriptString);
//			writer.close();
			
			String[] splitJson = scriptString.split(
									"\"flags\":\\[\"been\"\\],\"name\":\"");
			
			cities = new ArrayList<String>(Arrays.asList(splitJson));
			
			// format city entries
			ListIterator<String> i = cities.listIterator();
			while (i.hasNext()) {
				String cityEntry = i.next();
				cityEntry = cityEntry.substring(0, cityEntry.indexOf('\"'));
				i.set(cityEntry);
				citySets.put
			}
		}
		// first entry is garbage javascript stuff
		cities.remove(0);  
		
		return cities;
	}

	
	/**
	 * Return List<String> containing the destination experts for this forum.
	 * @param forumUrl
	 * @return 
	 * @return
	 * @throws IOException 
	 */
	public static List<String> scrapeDestinationExperts(String forumUrl) 
			throws IOException 
	{
		List<String> experts = null;
		
		Document forumPage = Jsoup.connect(forumUrl).get();
		
		// jsoup only has getElement(s) function, should only be one such class
		Element expertDiv = forumPage.getElementsByClass("expertbox").first();
		String expertsString = expertDiv.html();
				
		String[] splitExperts = expertsString.split("alt=\"");
		
		experts = new ArrayList<String>(Arrays.asList(splitExperts));

		// format expert entries
		ListIterator<String> i = experts.listIterator(1);
		while (i.hasNext()) {
			String expertEntry = i.next();
			expertEntry = expertEntry.substring(0, expertEntry.indexOf(
																"\" width"));
			i.set(expertEntry);
		}
		// first entry is garbage javascript stuff
		experts.remove(0);  
		
		return experts;		
	}
	
	public static List<String> scrapeAllMembers()
	{
		List<String> allMembers = null;
		for (forumUrl : FORUMS) {
			allMembers.addAll(scrapeDestinationExperts(forum));
		}
		
		return allMembers;
	}
	
	
	public static void main(String[] args) throws IOException 
	{
//		System.out.println(scrapeVisitedCities("brave1"));
		System.out.println(scrapeDestinationExperts("http://www.tripadvisor.com/ShowForum-g293910-i9303-Taiwan.html"));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}