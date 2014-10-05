import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to write and read files.
 */
public class FileUtil {
	
	/**
	 * Write a string to fileNa
	 * @param outputString
	 * @param fileName
	 */
	public static void writeToFile(String outputString, String fileName) 
	{
		Writer writer = null;
	
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
		    writer.write(outputString);
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	public static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	
	public static List<String> listFileBasenames(String directory)
	{
		List<String> results = new ArrayList<String>();
		

		File[] files = new File(directory).listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 
		
		
		
		for (File file : files) {
		    if (file.isFile()) {
		        results.add(file.getName().split("\\.(?=[^\\.]+$)")[0]);
		    }
		}
		
		
		
//		/**
//		 * JAR VERSION
//		 */
//		String resultsString = "scottca075, OhLynne, LoisInsiderTips, Jadamros, bluewaterdiva, Shuffaluff, SingaporeNancy, Kbecjeans, debdestiny, MainerOnTheGo, deadheadcouple, Victorianlad, Swissdiver, rocking_pony, addisadvisor, rdglady, shake801, llanitaSunflower, amf1, Dotneko, Mofissio, Rashyroo, St-Lucia_Enthusiast, lobomago, mariatan, jimjade3, hopefulist, vter, Nice_French_Riviera, Fundygirlr, slrtravelplanner, nico_nc, lyonfish, Eirelover, Pigsterz, Angelina52, IslanderOnTheGo, CayManTA, AnnG62, Remay28, reedprincess, stokeygirl, AERULES, icelandicguide, xelas, Labatt, sasa73, JohntheFinn, greek-traveller, achnab, tommomelbourne, endosunshine, Gaby85, RedRox, The_Egyptian_Guide, arbyunc, rickb, nevera, Monica4, MaryGG, Arubalisa, greasy, francis_oconnor, ricardoBA, bleicherblog, civilengtiger, Cora_v, sandiaman, Lori12309, YouLiveOnlyOnce, MrsO_5, spammie, Asifiwe, MaryGo3, Karl_Gingrich, searose, grahamwitton, cricket123, denalicat, NorthWestLondoner, Miss_Chief1, brave1, Teach1977, LasVegasPatti, SunloverFrance, Stephane501, Northice, KthSch, azbuceadora, preziam, TranscendentalEye, huow, half-Brit, ALR1802, Mazza-Mru, g0akc, _de_last_one, Flowerfarm, myanmarguide, OzzieinMozzie, BarbiJKM, EIIen, kkym, SamSalmon, Texas123_7, IndigoGrrl, TaipeiAlive, Medic_5, luv4travelNJ, InBarbados, charley48, rongpuk, j_a70, NLgirl06, Morlaque, Dave148, NBPINVT, kendrick, sonnysullivan, butterflysue1, tymetraveler222, mousehunt, bellagio, davidlayba, jl, Pitzikat, SunjayJK, chelloveks, activegirl, nuttyazn, uktripfan, Bruce596, PRSV, roadwarriorafrica, Rozafa, john9159, LittleMissRed, hatchpal4u, tnhighroad, melissassm, Lynphd, STEFANILARDA, fti, kirimist, roadadvisor, Scubapro1957, eLaReF, SteveKent, Gib_Minty, RV429, vn_hoangnguyen, smudger888, oma14, Tihi33, Prolijo, UNCMickey, alli, JRN58, CB48, Me^Only, mdp963, AndrewGW, myrm, Sunniebgi, Tembofan, billa55, mostlybytrain, noTanMan, crfan, verobaires, Treshi, MitchHellman, davetheguide, kegler747, bryantpark88, DeeCapeTown, katievalk, StLuciaDebs, AshisRoy, 24fan, Bips, Beast666, Mikiduta, dancingmolly, Taksidiotis, JeffB32, sodomojo, Hambagahle, meherio, AKStafford, momstravel, polarbearfin, maca81, DXBINC, bwanadave, ShawnWithCaterina, travellerguy, PR-Nature-Lover, evening, Chris_and_Liz_VT, Mikey_Vienna, Oreets, GOPBI, Bohemien, dav4844, TowerofFlour, AmateurGO, ArubaAmy, amanx, VisitIsrael, GCEK, lighthouselovertoo, daffy2, TravelSense-Asia, TravellingMitch, JeffinPrague, playa4u, E&amp;CinReno, Schonefeld, nibrika, Discover_Namibia, IntheBurgh, hat776, TwigsOxford, SunshineOnCapeCod, traveller47, NPLGUY, alaskantiger, dyoll, blamona, Toid, PixieC, Londinense, syriafullbottle, camke, Scary, xochitl, SunL0ver57, AskCy, LivesInNewJersey, Clauds, happytraveler49, bornwithwheels, ErikdR, Toonman, Nowayellen, klimaflyktning, BGIWorldTraveller, Yankari, Expat1957, jpmaldivestraveller, spacefry, doodko, TomMcF, 6tareg, luv2snorkel61, BuRaiR, khal926, KentDougal, japantravelteacher, maryann-ns, PR_Bob, silverswimmer, PeaceAndLight, bbmars, H--Torfaen, raquel_z, shoshi, lucianmermaid, BooKat, taiwanDIY, Hans_DEN, SeeSea99, Maneki-neko, happybypleasure, sunny21_7, Deepa_Krishnan, mariaeugene, J_T_travellers, Mankster, Gengen, iverh, trizi01, ziggy60, Babiface, drr49, Kevin-sg, ddcuba, november_moon, Since_71, topfan, Vespucio, barkinmad, pausilypon, huub9999, himalayan_tiger, drill-n-fill, scubagirrl, PrestonGuild, JACombs, Safarichild, Paulhawtin, O.T., Alanyeti, SXMScubaman, mamiesgirl, shully, Bhutantraveller, FTHW, sweetpea6468, pelorrucho38, swissfan, visit-the-coqui, coalminer, CasaAzul, Twirl, Kathy4HIM, B0bK, SuziiW, MayFlower23958, Doggles10, arvind13, TimCullis, LondonChris_S, eeuunikkeiexpat, forestred, TumbleweedandCactus, Arubamasc, penny000, keenvisitor, 643amc, chefathome, Bryno, irishadrian, Nefertari, DrFeelAwesome, SableSeeker, dcmsteach, Innvic, tez823, Cassnu, blondie9093, JohnnyBgood, Sookyoung1101, blee246, lamlee, Brian_in_Brooklyn, joebwan, Poorjar, misspWestSlope_CO, barcann, shimmin2robertson, swandav, bradintaos, fatbear2000, RomanCitizen, 64novpt, Bernese, Avanti50, ralphy57, bluestreek, UruguayoErrante, Hamara, stefburgas, JJKorea, Cherries_Jubilee, noexpert, mmr1316, flicarose, kiddos, bronia, drakesdrum, johnb121, TetonBill, debb199, Testudo";
//		List<String> results = new ArrayList<String>();
//		String[] files = resultsString.split(", ");
//		for (String file : files) {
//			results.add(file);
//		}
		
		
		
		
		return results;
	}
	
	/**
	 * Write keys of a Map to file.
	 */
	public static void writeKeys(Map<String, Integer> map, String outputFile)
	{
		Map<String, Integer> ourMap = map;
		
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(outputFile), "utf-8"));
		    for (String key : ourMap.keySet()) {
		    	writer.write(key + "\n");
		    }
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
}














