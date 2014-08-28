import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Scrape member and city sets from TripAdvisor member pages.
 * @author dchouren
 *
 */
public class TripAdvisorScraper {
	
	private static final String MEMBER_PAGE = 
			"http://www.tripadvisor.com/members/";
	private final static int TIMEOUT_TEN_SECONDS = 10 * 1000;
	private final static String htmlDirectory = "memberPages/";
	private final static String[] FORUMS = {
		"http://www.tripadvisor.com/ShowForum-g28922-i173-Alabama.html",
		"http://www.tripadvisor.com/ShowForum-g28923-i349-Alaska.html",
		"http://www.tripadvisor.com/ShowForum-g28924-i139-Arizona.html",
		"http://www.tripadvisor.com/ShowForum-g28925-i492-Arkansas.html",
		"http://www.tripadvisor.com/ShowForum-g28926-i29-California.html",
		"http://www.tripadvisor.com/ShowForum-g28927-i252-Colorado.html",
		"http://www.tripadvisor.com/ShowForum-g28928-i468-Connecticut.html",
		"http://www.tripadvisor.com/ShowForum-g28929-i294-Delaware.html",
		"http://www.tripadvisor.com/ShowForum-g28969-i39-District_of_Columbia.html",
		"http://www.tripadvisor.com/ShowForum-g28930-i18-Florida.html",
		"http://www.tripadvisor.com/ShowForum-g28931-i103-Georgia.html",
		"http://www.tripadvisor.com/ShowForum-g5503747-i30407-Greater_Cincinnati.html",
		"http://www.tripadvisor.com/ShowForum-g28932-i36-Hawaii.html",
		"http://www.tripadvisor.com/ShowForum-g28933-i214-Idaho.html",
		"http://www.tripadvisor.com/ShowForum-g28934-i31-Illinois.html",
		"http://www.tripadvisor.com/ShowForum-g28935-i280-Indiana.html",
		"http://www.tripadvisor.com/ShowForum-g28936-i639-Iowa.html",
		"http://www.tripadvisor.com/ShowForum-g28937-i536-Kansas.html",
		"http://www.tripadvisor.com/ShowForum-g28938-i451-Kentucky.html",
		"http://www.tripadvisor.com/ShowForum-g28939-i33-Louisiana.html",
		"http://www.tripadvisor.com/ShowForum-g28940-i175-Maine.html",
		"http://www.tripadvisor.com/ShowForum-g28941-i100-Maryland.html",
		"http://www.tripadvisor.com/ShowForum-g28942-i47-Massachusetts.html",
		"http://www.tripadvisor.com/ShowForum-g28943-i319-Michigan.html",
		"http://www.tripadvisor.com/ShowForum-g28944-i371-Minnesota.html",
		"http://www.tripadvisor.com/ShowForum-g28945-i195-Mississippi.html",
		"http://www.tripadvisor.com/ShowForum-g28946-i199-Missouri.html",
		"http://www.tripadvisor.com/ShowForum-g28947-i982-Montana.html",
		"http://www.tripadvisor.com/ShowForum-g28948-i455-Nebraska.html",
		"http://www.tripadvisor.com/ShowForum-g28949-i9-Nevada.html",
		"http://www.tripadvisor.com/ShowForum-g4672736-i29525-New_England.html",
		"http://www.tripadvisor.com/ShowForum-g28950-i526-New_Hampshire.html",
		"http://www.tripadvisor.com/ShowForum-g28951-i77-New_Jersey.html",
		"http://www.tripadvisor.com/ShowForum-g28952-i227-New_Mexico.html",
		"http://www.tripadvisor.com/ShowForum-g28953-i4-New_York.html",
		"http://www.tripadvisor.com/ShowForum-g28954-i250-North_Carolina.html",
		"http://www.tripadvisor.com/ShowForum-g28955-i1257-North_Dakota.html",
		"http://www.tripadvisor.com/ShowForum-g28956-i265-Ohio.html",
		"http://www.tripadvisor.com/ShowForum-g28957-i520-Oklahoma.html",
		"http://www.tripadvisor.com/ShowForum-g28958-i237-Oregon.html",
		"http://www.tripadvisor.com/ShowForum-g28959-i112-Pennsylvania.html",
		"http://www.tripadvisor.com/ShowForum-g28959-i112-Pennsylvania.html",
		"http://www.tripadvisor.com/ShowForum-g28960-i254-Rhode_Island.html",
		"http://www.tripadvisor.com/ShowForum-g28960-i254-Rhode_Island.html",
		"http://www.tripadvisor.com/ShowForum-g28961-i24-South_Carolina.html",
		"http://www.tripadvisor.com/ShowForum-g28962-i673-South_Dakota.html",
		"http://www.tripadvisor.com/ShowForum-g28963-i149-Tennessee.html",
		"http://www.tripadvisor.com/ShowForum-g28964-i75-Texas.html",
		"http://www.tripadvisor.com/ShowForum-g28965-i411-Utah.html",
		"http://www.tripadvisor.com/ShowForum-g28966-i656-Vermont.html",
		"http://www.tripadvisor.com/ShowForum-g28967-i62-Virginia.html",
		"http://www.tripadvisor.com/ShowForum-g28968-i73-Washington.html",
		"http://www.tripadvisor.com/ShowForum-g28971-i892-West_Virginia.html",
		"http://www.tripadvisor.com/ShowForum-g28972-i114-Wisconsin.html",
		"http://www.tripadvisor.com/ShowForum-g28973-i480-Wyoming.html",
		
		"http://www.tripadvisor.com/ShowForum-g294445-i4583-Albania.html",
		"http://www.tripadvisor.com/ShowForum-g190391-i1957-Andorra.html",
		"http://www.tripadvisor.com/ShowForum-g190410-i146-Austria.html",
		"http://www.tripadvisor.com/ShowForum-g294447-i5625-Belarus.html",
		"http://www.tripadvisor.com/ShowForum-g188634-i204-Belgium.html",
		"http://www.tripadvisor.com/ShowForum-g294449-i6233-Bosnia_and_Herzegovina.html",
		"http://www.tripadvisor.com/ShowForum-g294451-i3250-Bulgaria.html",
		"http://www.tripadvisor.com/ShowForum-g294453-i1554-Croatia.html",
		"http://www.tripadvisor.com/ShowForum-g190372-i9179-Cyprus.html",
		"http://www.tripadvisor.com/ShowForum-g274684-i94-Czech_Republic.html",
		"http://www.tripadvisor.com/ShowForum-g189512-i216-Denmark.html",
		"http://www.tripadvisor.com/ShowForum-g274952-i995-Estonia.html",
		"http://www.tripadvisor.com/ShowForum-g190329-i5368-Faroe_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g189896-i442-Finland.html",
		"http://www.tripadvisor.com/ShowForum-g187070-i12-France.html",
		"http://www.tripadvisor.com/ShowForum-g294194-i9343-Georgia.html",
		"http://www.tripadvisor.com/ShowForum-g187275-i116-Germany.html",
		"http://www.tripadvisor.com/ShowForum-g187510-i1247-Gibraltar.html",
		"http://www.tripadvisor.com/ShowForum-g189398-i192-Greece.html",
		"http://www.tripadvisor.com/ShowForum-g274881-i262-Hungary.html",
		"http://www.tripadvisor.com/ShowForum-g189952-i223-Iceland.html",
		"http://www.tripadvisor.com/ShowForum-g186591-i88-Ireland.html",
		"http://www.tripadvisor.com/ShowForum-g187768-i20-Italy.html",
		"http://www.tripadvisor.com/ShowForum-g304082-i9352-Kosovo.html",
		"http://www.tripadvisor.com/ShowForum-g274960-i1133-Latvia.html",
		"http://www.tripadvisor.com/ShowForum-g190357-i10308-Liechtenstein.html",
		"http://www.tripadvisor.com/ShowForum-g274947-i1313-Lithuania.html",
		"http://www.tripadvisor.com/ShowForum-g190340-i789-Luxembourg.html",
		"http://www.tripadvisor.com/ShowForum-g190311-i348-Malta.html",
		"http://www.tripadvisor.com/ShowForum-g294455-i7895-Moldova.html",
		"http://www.tripadvisor.com/ShowForum-g190405-i678-Monaco.html",
		"http://www.tripadvisor.com/ShowForum-g635648-i11070-Montenegro.html",
		"http://www.tripadvisor.com/ShowForum-g190455-i550-Norway.html",
		"http://www.tripadvisor.com/ShowForum-g274723-i959-Poland.html",
		"http://www.tripadvisor.com/ShowForum-g189100-i201-Portugal.html",
		"http://www.tripadvisor.com/ShowForum-g295109-i8578-Republic_of_Macedonia.html",
		"http://www.tripadvisor.com/ShowForum-g294457-i2816-Romania.html",
		"http://www.tripadvisor.com/ShowForum-g294459-i705-Russia.html",
		"http://www.tripadvisor.com/ShowForum-g187808-i2942-San_Marino.html",
		"http://www.tripadvisor.com/ShowForum-g294471-i4872-Serbia.html",
		"http://www.tripadvisor.com/ShowForum-g274922-i1155-Slovakia.html",
		"http://www.tripadvisor.com/ShowForum-g274862-i501-Slovenia.html",
		"http://www.tripadvisor.com/ShowForum-g187427-i42-Spain.html",
		"http://www.tripadvisor.com/ShowForum-g189806-i232-Sweden.html",
		"http://www.tripadvisor.com/ShowForum-g188045-i336-Switzerland.html",
		"http://www.tripadvisor.com/ShowForum-g188553-i58-The_Netherlands.html",
		"http://www.tripadvisor.com/ShowForum-g293969-i367-Turkey.html",
		"http://www.tripadvisor.com/ShowForum-g294473-i3662-Ukraine.html",
		"http://www.tripadvisor.com/ShowForum-g186216-i15-United_Kingdom.html",
		
		"http://www.tripadvisor.com/ShowForum-g154909-i219-Alberta.html",
		"http://www.tripadvisor.com/ShowForum-g154922-i80-British_Columbia.html",
		"http://www.tripadvisor.com/ShowForum-g154950-i358-Manitoba.html",
		"http://www.tripadvisor.com/ShowForum-g154956-i833-New_Brunswick.html",
		"http://www.tripadvisor.com/ShowForum-g154962-i754-Newfoundland_and_Labrador.html",
		"http://www.tripadvisor.com/ShowForum-g154965-i1933-Northwest_Territories.html",
		"http://www.tripadvisor.com/ShowForum-g154967-i326-Nova_Scotia.html",
		"http://www.tripadvisor.com/ShowForum-g154978-i2184-Nunavut.html",
		"http://www.tripadvisor.com/ShowForum-g154979-i54-Ontario.html",
		"http://www.tripadvisor.com/ShowForum-g155022-i619-Prince_Edward_Island.html",
		"http://www.tripadvisor.com/ShowForum-g155025-i50-Quebec.html",
		"http://www.tripadvisor.com/ShowForum-g155038-i896-Saskatchewan.html",
		"http://www.tripadvisor.com/ShowForum-g155045-i1403-Yukon.html",

		"http://www.tripadvisor.com/ShowForum-g659499-i13185-Afghanistan.html",
		"http://www.tripadvisor.com/ShowForum-g293931-i9477-Armenia.html",
		"http://www.tripadvisor.com/ShowForum-g293933-i9346-Azerbaijan.html",
		"http://www.tripadvisor.com/ShowForum-g293935-i9192-Bangladesh.html",
		"http://www.tripadvisor.com/ShowForum-g293844-i10038-Bhutan.html",
		"http://www.tripadvisor.com/ShowForum-g293937-i9175-Brunei_Darussalam.html",
		"http://www.tripadvisor.com/ShowForum-g293939-i9162-Cambodia.html",
		"http://www.tripadvisor.com/ShowForum-g294211-i642-China.html",
		"http://www.tripadvisor.com/ShowForum-g295117-i9393-East_Timor.html",
		"http://www.tripadvisor.com/ShowForum-g293860-i511-India.html",
		"http://www.tripadvisor.com/ShowForum-g670819-i28046-Indian_Ocean.html",
		"http://www.tripadvisor.com/ShowForum-g294225-i7219-Indonesia.html",
		"http://www.tripadvisor.com/ShowForum-g294232-i525-Japan.html",
		"http://www.tripadvisor.com/ShowForum-g293943-i9315-Kazakhstan.html",
		"http://www.tripadvisor.com/ShowForum-g293947-i9200-Kyrgyzstan.html",
		"http://www.tripadvisor.com/ShowForum-g293949-i9320-Laos.html",
		"http://www.tripadvisor.com/ShowForum-g293951-i7006-Malaysia.html",
		"http://www.tripadvisor.com/ShowForum-g293953-i7445-Maldives.html",
		"http://www.tripadvisor.com/ShowForum-g293955-i9382-Mongolia.html",
		"http://www.tripadvisor.com/ShowForum-g294190-i9408-Myanmar.html",
		"http://www.tripadvisor.com/ShowForum-g293889-i9243-Nepal.html",
		"http://www.tripadvisor.com/ShowForum-g294443-i9734-North_Korea.html",
		"http://www.tripadvisor.com/ShowForum-g293959-i9151-Pakistan.html",
		"http://www.tripadvisor.com/ShowForum-g294245-i3256-Philippines.html",
		"http://www.tripadvisor.com/ShowForum-g294262-i1747-Singapore.html",
		"http://www.tripadvisor.com/ShowForum-g294196-i8160-South_Korea.html",
		"http://www.tripadvisor.com/ShowForum-g293961-i8983-Sri_Lanka.html",
		"http://www.tripadvisor.com/ShowForum-g293910-i9303-Taiwan.html",
		"http://www.tripadvisor.com/ShowForum-g293963-i10646-Tajikistan.html",
		"http://www.tripadvisor.com/ShowForum-g293915-i3686-Thailand.html",
		"http://www.tripadvisor.com/ShowForum-g293965-i9795-Turkmenistan.html",
		"http://www.tripadvisor.com/ShowForum-g293967-i9281-Uzbekistan.html",
		"http://www.tripadvisor.com/ShowForum-g293921-i8432-Vietnam.html",
		
		"http://www.tripadvisor.com/ShowForum-g291959-i1455-Belize.html",
		"http://www.tripadvisor.com/ShowForum-g291982-i813-Costa_Rica.html",
		"http://www.tripadvisor.com/ShowForum-g294475-i3217-El_Salvador.html",
		"http://www.tripadvisor.com/ShowForum-g292002-i1599-Guatemala.html",
		"http://www.tripadvisor.com/ShowForum-g292016-i3574-Honduras.html",
		"http://www.tripadvisor.com/ShowForum-g294477-i2982-Nicaragua.html",
		"http://www.tripadvisor.com/ShowForum-g294479-i1193-Panama.html",
		"http://www.tripadvisor.com/ShowForum-g293717-i9843-Algeria.html",
		"http://www.tripadvisor.com/ShowForum-g293762-i9498-Angola.html",
		"http://www.tripadvisor.com/ShowForum-g293764-i10504-Benin.html",
		"http://www.tripadvisor.com/ShowForum-g293766-i9284-Botswana.html",
		"http://www.tripadvisor.com/ShowForum-g293768-i9304-Burkina_Faso.html",
		"http://www.tripadvisor.com/ShowForum-g293770-i10320-Burundi.html",
		"http://www.tripadvisor.com/ShowForum-g293772-i9741-Cameroon.html",
		"http://www.tripadvisor.com/ShowForum-g293774-i9523-Cape_Verde.html",
		"http://www.tripadvisor.com/ShowForum-g293776-i10927-Central_African_Republic.html",
		"http://www.tripadvisor.com/ShowForum-g293778-i9113-Chad.html",
		"http://www.tripadvisor.com/ShowForum-g294435-i10617-Comoros.html",
		"http://www.tripadvisor.com/ShowForum-g294192-i9911-Cote_d_Ivoire.html",
		"http://www.tripadvisor.com/ShowForum-g294186-i9962-Democratic_Republic_of_the_Congo.html",
		"http://www.tripadvisor.com/ShowForum-g293786-i10050-Djibouti.html",
		"http://www.tripadvisor.com/ShowForum-g294200-i9124-Egypt.html",
		"http://www.tripadvisor.com/ShowForum-g294437-i10979-Equatorial_Guinea.html",
		"http://www.tripadvisor.com/ShowForum-g293788-i9762-Eritrea.html",
		"http://www.tripadvisor.com/ShowForum-g293790-i9957-Ethiopia.html",
		"http://www.tripadvisor.com/ShowForum-g293792-i10416-Gabon.html",
		"http://www.tripadvisor.com/ShowForum-g293794-i9249-Gambia.html",
		"http://www.tripadvisor.com/ShowForum-g293796-i9182-Ghana.html",
		"http://www.tripadvisor.com/ShowForum-g293798-i9505-Guinea.html",
		"http://www.tripadvisor.com/ShowForum-g293800-i9390-Guinea_Bissau.html",
		"http://www.tripadvisor.com/ShowForum-g294206-i9216-Kenya.html",
		"http://www.tripadvisor.com/ShowForum-g293802-i12266-Lesotho.html",
		"http://www.tripadvisor.com/ShowForum-g293804-i9403-Liberia.html",
		"http://www.tripadvisor.com/ShowForum-g293806-i9127-Libya.html",
		"http://www.tripadvisor.com/ShowForum-g293808-i9291-Madagascar.html",
		"http://www.tripadvisor.com/ShowForum-g293810-i10033-Malawi.html",
		"http://www.tripadvisor.com/ShowForum-g293812-i9977-Mali.html",
		"http://www.tripadvisor.com/ShowForum-g293814-i10400-Mauritania.html",
		"http://www.tripadvisor.com/ShowForum-g293816-i9265-Mauritius.html",
		"http://www.tripadvisor.com/ShowForum-g295116-i11581-Mayotte.html",
		"http://www.tripadvisor.com/ShowForum-g293730-i9195-Morocco.html",
		"http://www.tripadvisor.com/ShowForum-g293818-i9167-Mozambique.html",
		"http://www.tripadvisor.com/ShowForum-g293820-i9680-Namibia.html",
		"http://www.tripadvisor.com/ShowForum-g293822-i9261-Niger.html",
		"http://www.tripadvisor.com/ShowForum-g293824-i9115-Nigeria.html",
		"http://www.tripadvisor.com/ShowForum-g294188-i10096-Republic_of_the_Congo.html",
		"http://www.tripadvisor.com/ShowForum-g293826-i10415-Reunion_Island.html",
		"http://www.tripadvisor.com/ShowForum-g293828-i9987-Rwanda.html",
		"http://www.tripadvisor.com/ShowForum-g295105-i14893-Saint_Helena.html",
		"http://www.tripadvisor.com/ShowForum-g294441-i10805-Sao_Tome_and_Principe.html",
		"http://www.tripadvisor.com/ShowForum-g293830-i9218-Senegal.html",
		"http://www.tripadvisor.com/ShowForum-g293738-i9311-Seychelles.html",
		"http://www.tripadvisor.com/ShowForum-g293832-i9353-Sierra_Leone.html",
		"http://www.tripadvisor.com/ShowForum-g294439-i9214-Somalia.html",
		"http://www.tripadvisor.com/ShowForum-g293740-i9186-South_Africa.html",
		"http://www.tripadvisor.com/ShowForum-g2201790-i24471-South_Sudan.html",
		"http://www.tripadvisor.com/ShowForum-g293834-i9634-Sudan.html",
		"http://www.tripadvisor.com/ShowForum-g293836-i9468-Swaziland.html",
		"http://www.tripadvisor.com/ShowForum-g293747-i9226-Tanzania.html",
		"http://www.tripadvisor.com/ShowForum-g293838-i9489-Togo.html",
		"http://www.tripadvisor.com/ShowForum-g293753-i9122-Tunisia.html",
		"http://www.tripadvisor.com/ShowForum-g293840-i9254-Uganda.html",
		"http://www.tripadvisor.com/ShowForum-g295118-i10130-Western_Sahara.html",
		"http://www.tripadvisor.com/ShowForum-g293842-i9277-Zambia.html",
		"http://www.tripadvisor.com/ShowForum-g293759-i9323-Zimbabwe.html",
		"http://www.tripadvisor.com/ShowForum-g147238-i388-Anguilla.html",
		"http://www.tripadvisor.com/ShowForum-g150894-i224-Antigua_and_Barbuda.html",
		"http://www.tripadvisor.com/ShowForum-g147247-i144-Aruba.html",
		"http://www.tripadvisor.com/ShowForum-g147414-i129-Bahamas.html",
		"http://www.tripadvisor.com/ShowForum-g147262-i230-Barbados.html",
		"http://www.tripadvisor.com/ShowForum-g147255-i208-Bermuda.html",
		"http://www.tripadvisor.com/ShowForum-g147267-i731-Bonaire.html",
		"http://www.tripadvisor.com/ShowForum-g147353-i545-British_Virgin_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g147364-i260-Cayman_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g147270-i91-Cuba.html",
		"http://www.tripadvisor.com/ShowForum-g147277-i583-Curacao.html",
		"http://www.tripadvisor.com/ShowForum-g147281-i801-Dominica.html",
		"http://www.tripadvisor.com/ShowForum-g147288-i27-Dominican_Republic.html",
		"http://www.tripadvisor.com/ShowForum-g147295-i518-Grenada.html",
		"http://www.tripadvisor.com/ShowForum-g147300-i1141-Guadeloupe.html",
		"http://www.tripadvisor.com/ShowForum-g147306-i747-Haiti.html",
		"http://www.tripadvisor.com/ShowForum-g147309-i69-Jamaica.html",
		"http://www.tripadvisor.com/ShowForum-g147327-i787-Martinique.html",
		"http://www.tripadvisor.com/ShowForum-g147333-i1913-Montserrat.html",
		"http://www.tripadvisor.com/ShowForum-g147319-i71-Puerto_Rico.html",
		"http://www.tripadvisor.com/ShowForum-g147335-i2073-Saba.html",
		"http://www.tripadvisor.com/ShowForum-g150742-i4796-St_Eustatius.html",
		"http://www.tripadvisor.com/ShowForum-g147373-i540-St_Kitts_and_Nevis.html",
		"http://www.tripadvisor.com/ShowForum-g147342-i247-St_Lucia.html",
		"http://www.tripadvisor.com/ShowForum-g147379-i1174-St_Vincent_and_the_Grenadines.html",
		"http://www.tripadvisor.com/ShowForum-g147338-i760-St_Barthelemy.html",
		"http://www.tripadvisor.com/ShowForum-g147346-i222-St_Maarten_St_Martin.html",
		"http://www.tripadvisor.com/ShowForum-g147387-i548-Trinidad_and_Tobago.html",
		"http://www.tripadvisor.com/ShowForum-g147395-i212-Turks_and_Caicos.html",
		"http://www.tripadvisor.com/ShowForum-g147400-i171-U_S_Virgin_Islands.html",
		
		"http://www.tripadvisor.com/ShowForum-g150769-i82-Baja_California.html",
		"http://www.tripadvisor.com/ShowForum-g150796-i163-Central_Mexico_and_Gulf_Coast.html",
		"http://www.tripadvisor.com/ShowForum-g1575477-i14532-Jalisco.html",
		"http://www.tripadvisor.com/ShowForum-g150778-i563-Northern_Mexico.html",
		"http://www.tripadvisor.com/ShowForum-g150786-i45-Pacific_Coast.html",
		"http://www.tripadvisor.com/ShowForum-g3194809-i30468-Southern_Mexico.html",
		"http://www.tripadvisor.com/ShowForum-g150805-i7-Yucatan_Peninsula.html",
		"http://www.tripadvisor.com/ShowForum-g60665-i4093-American_Samoa.html",
		"http://www.tripadvisor.com/ShowForum-g255055-i120-Australia.html",
		"http://www.tripadvisor.com/ShowForum-g294328-i2626-Cook_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g294198-i5825-Federated_States_of_Micronesia.html",
		"http://www.tripadvisor.com/ShowForum-g294331-i883-Fiji.html",
		"http://www.tripadvisor.com/ShowForum-g294338-i867-French_Polynesia.html",
		"http://www.tripadvisor.com/ShowForum-g1487275-i27862-Mariana_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g301392-i6131-Marshall_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g294127-i15974-Nauru.html",
		"http://www.tripadvisor.com/ShowForum-g294129-i4307-New_Caledonia.html",
		"http://www.tripadvisor.com/ShowForum-g255104-i125-New_Zealand.html",
		"http://www.tripadvisor.com/ShowForum-g294131-i10811-Niue.html",
		"http://www.tripadvisor.com/ShowForum-g294135-i4017-Palau.html",
		"http://www.tripadvisor.com/ShowForum-g294115-i3592-Papua_New_Guinea.html",
		"http://www.tripadvisor.com/ShowForum-g673774-i12548-Pitcairn_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g294121-i6904-Republic_of_Kiribati.html",
		"http://www.tripadvisor.com/ShowForum-g294137-i5760-Samoa.html",
		"http://www.tripadvisor.com/ShowForum-g294139-i6230-Solomon_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g295114-i14863-Tokelau.html",
		"http://www.tripadvisor.com/ShowForum-g294141-i5178-Tonga.html",
		"http://www.tripadvisor.com/ShowForum-g294481-i7574-Tuvalu.html",
		"http://www.tripadvisor.com/ShowForum-g294143-i3827-Vanuatu.html",
		"http://www.tripadvisor.com/ShowForum-g60667-i11694-Wake_Island.html",
		"http://www.tripadvisor.com/ShowForum-g294266-i977-Argentina.html",
		"http://www.tripadvisor.com/ShowForum-g294071-i4810-Bolivia.html",
		"http://www.tripadvisor.com/ShowForum-g294280-i1045-Brazil.html",
		"http://www.tripadvisor.com/ShowForum-g294291-i1357-Chile.html",
		"http://www.tripadvisor.com/ShowForum-g294073-i1708-Colombia.html",
		"http://www.tripadvisor.com/ShowForum-g294307-i2253-Ecuador.html",
		"http://www.tripadvisor.com/ShowForum-g294270-i10985-Falkland_Islands.html",
		"http://www.tripadvisor.com/ShowForum-g294075-i11080-French_Guiana.html",
		"http://www.tripadvisor.com/ShowForum-g294077-i5182-Guyana.html",
		"http://www.tripadvisor.com/ShowForum-g294079-i7050-Paraguay.html",
		"http://www.tripadvisor.com/ShowForum-g294311-i818-Peru.html",
		"http://www.tripadvisor.com/ShowForum-g294081-i7073-Suriname.html",
		"http://www.tripadvisor.com/ShowForum-g294064-i4294-Uruguay.html",
		"http://www.tripadvisor.com/ShowForum-g294324-i2023-Venezuela.html",
		"http://www.tripadvisor.com/ShowForum-g293996-i3669-Bahrain.html",
		"http://www.tripadvisor.com/ShowForum-g1389503-i13861-Golan_Heights.html",
		"http://www.tripadvisor.com/ShowForum-g293998-i9139-Iran.html",
		"http://www.tripadvisor.com/ShowForum-g294000-i13855-Iraq.html",
		"http://www.tripadvisor.com/ShowForum-g293977-i1733-Israel.html",
		"http://www.tripadvisor.com/ShowForum-g293985-i2131-Jordan.html",
		"http://www.tripadvisor.com/ShowForum-g294002-i4278-Kuwait.html",
		"http://www.tripadvisor.com/ShowForum-g294004-i2871-Lebanon.html",
		"http://www.tripadvisor.com/ShowForum-g294006-i2046-Oman.html",
		"http://www.tripadvisor.com/ShowForum-g660378-i12130-Palestinian_Territories.html",
		"http://www.tripadvisor.com/ShowForum-g294008-i3511-Qatar.html",
		"http://www.tripadvisor.com/ShowForum-g293991-i4477-Saudi_Arabia.html",
		"http://www.tripadvisor.com/ShowForum-g294010-i5486-Syria.html",
		"http://www.tripadvisor.com/ShowForum-g294012-i871-United_Arab_Emirates.html",
		"http://www.tripadvisor.com/ShowForum-g294014-i3240-Yemen.html",
	}; 
	private static final String[] FORUMS2 = {
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
	 * Write member html pages.
	 * @param member
	 * @return
	 * @throws IOException
	 */
	private static void writeMemberPage(String member) throws IOException
	{

		String memberUrl = MEMBER_PAGE + member;

		Document memberPage = Jsoup.connect(memberUrl).timeout(
											TIMEOUT_TEN_SECONDS).get();
		
		String writePath = htmlDirectory + member + ".txt";
		
		File fileToCreate = new File(writePath);
		
		if (!fileToCreate.exists()) {
//			System.out.println(member);
			FileUtil.writeToFile(memberPage.html(), writePath);		
		}
	}


	/**
	 * Return List<String> containing the destination experts for this forum.
	 * @param forumUrl
	 * @return
	 * @throws IOException
	 */
	private static List<String> scrapeDestinationExperts(String forumUrl) 
			throws IOException 
	{
		List<String> experts = null;

		Document forumPage = Jsoup.connect(forumUrl).timeout(10*1000).get();

		// jsoup only has getElement(s) function, should only be one such class
		Element expertDiv = forumPage.getElementsByClass("expertbox").first();
		if (expertDiv == null)
			return experts;

		String expertsString = expertDiv.html();

		String[] splitExperts = expertsString.split("alt=\"");

		experts = new ArrayList<String>(Arrays.asList(splitExperts));

		// format expert entries
		ListIterator<String> i = experts.listIterator(1);
		while (i.hasNext()) {
			String expertEntry = i.next();
			expertEntry = expertEntry.substring(0, expertEntry.indexOf(
					"\" width"));
			//TODO: check for other types of malformed urls, possibly make
			// own function 
			if (expertEntry.indexOf(' ') == -1)
				i.set(expertEntry);
			else
				i.remove();
		}
		// first entry is garbage javascript stuff
		experts.remove(0);  

		return experts;		
	}


	/**
	 * Scrape all cities that all members have visited. Update memberSets,
	 * citySets.
	 * @param forums
	 * @throws IOException 
	 */
	public static void writeAllPages() throws IOException
	{
		for (int i = 0; i < FORUMS.length; i++) {
			List<String> experts = scrapeDestinationExperts(FORUMS[i]);
			if (experts == null)
				continue;

			for (int j = 0; j < experts.size(); j++) {
				String thisExpert = experts.get(j);

				// does all the dirty work of creating sets
				writeMemberPage(thisExpert);
			}
		}
		System.out.println("finished");
	}
	
	
	/**
	 * Generate list of members from FORUMS2.
	 * @return
	 * @throws IOException
	 */
	public static List<String> scrapeAllMembers() throws IOException
	{
		List<String> allMembers = new ArrayList<String>();
		for (String forumUrl : FORUMS2) {
			List<String> experts = scrapeDestinationExperts(forumUrl);
			if (experts != null)
				allMembers.addAll(experts);
		}
		
		return allMembers;
	}
	
}
